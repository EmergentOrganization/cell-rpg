package com.emergentorganization.cellrpg.entities.ca;

import com.badlogic.gdx.graphics.Color;
import com.emergentorganization.cellrpg.CellRpg;
import it.uniroma1.dis.wsngroup.gexf4j.core.*;
import it.uniroma1.dis.wsngroup.gexf4j.core.data.*;
import it.uniroma1.dis.wsngroup.gexf4j.core.impl.GexfImpl;
import it.uniroma1.dis.wsngroup.gexf4j.core.impl.StaxGraphWriter;
import it.uniroma1.dis.wsngroup.gexf4j.core.impl.data.AttributeListImpl;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import javax.xml.crypto.KeySelectorException;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.io.Writer;
import java.util.Calendar;
import java.util.HashMap;
import java.util.List;

/**
 * CA grid cell which has a digital gene regulatory network (DGRN) to represent it's genome,
 * and traits which are extracted from this genome.
 *
 * Created by 7yl4r on 9/25/2015.
 */
public class GeneticCell extends BaseCell{
    private final Logger logger = LogManager.getLogger(getClass());

    public static class inflowNodes{
        public static final String ALWAYS_ON = "alwaysOn";

        public static String[] values(){
            return new String[]{ALWAYS_ON};
        }
    }

    public static class outflowNodes{
        public static final String COLOR_LIGHTEN = "COLOR_LIGHTEN";
        public static final String COLOR_DARKEN = "COLOR_DARKEN";
        public static final String COLOR_ADD_R = "COLOR_ADD_R";
        public static final String COLOR_ADD_G = "COLOR_ADD_G";
        public static final String COLOR_ADD_B = "COLOR_ADD_B";
        public static final String COLOR_SUB_R = "COLOR_SUB_R";
        public static final String COLOR_SUB_G = "COLOR_SUB_G";
        public static final String COLOR_SUB_B = "COLOR_SUB_B";

    }
    public static class nodeAttribute{
        public static final String ACTIVATION_VALUE = "activation level";
    }
    static final Color DEFAULT_COLOR = new Color(.7f,.7f,.7f,1f);
    private Color color = new Color(DEFAULT_COLOR);
    private Gexf gexf;
    protected Graph graph;
    private static AttributeList attrList = new AttributeListImpl(AttributeClass.NODE);

    protected static Attribute attr_ActivationValue = attrList.createAttribute(
            nodeAttribute.ACTIVATION_VALUE,
            AttributeType.INTEGER,
            "activation value"
    ).setDefaultValue("0");  // NOTE: default value doesn't seem to have intended effect?
    // active state of a node defines whether the node is

    public GeneticCell(int _state, GeneticCell[] parents, int mutateLevel){
        // builds cell network inherting from parent(s), mutated based on given level
        this(_state);
        // TODO: inherit + mutation
    }

    public GeneticCell(int _state, GeneticCell[] parents){
        // builds cell network inherting from parent(s) (no mutations)
        this(_state);
        // TODO: inherit network from parents
    }

    public GeneticCell(int _state){
        // sets up cell with empty DGRN
        super(_state);
        initGraph();
    }

    public void initGraph(){
        gexf = new GexfImpl();
        gexf.getMetadata()
                .setLastModified(Calendar.getInstance().getTime())
                .setCreator("Planiverse Bridge v" + CellRpg.VERSION)
                .setDescription("Digital Gene Regulatory Network");
        gexf.setVisualization(false);

        graph = gexf.getGraph();
        graph.getAttributeLists().add(attrList);

        graph.setDefaultEdgeType(EdgeType.DIRECTED)
                .setMode(Mode.STATIC);
    }

    public Color getColor(){
        return color;
    }

    public void tick() throws KeySelectorException{
        // computes one cycle through the DGRN
        HashMap<String, Integer> nodeUpdates = new HashMap<String, Integer>();
        for (Edge edge : graph.getAllEdges()){
            try{
                if (Integer.parseInt(getNodeAttributeValue(
                        edge.getSource(), nodeAttribute.ACTIVATION_VALUE)) > 0
                        && !isInflowNode(edge.getTarget().getId())){

//                      // FOR CUMULATIVE DGRN
//                    int i = getNodeAttributeIndex(edge.getTarget(), nodeAttribute.ACTIVATION_VALUE);
//                    AttributeValue targetAttr = edge.getTarget().getAttributeValues().get(i);
//                    int attrVal = Integer.parseInt(targetAttr.getValue()) + (int)edge.getWeight();
//                    edge.getTarget().getAttributeValues().get(i).setValue(Integer.toString(attrVal));

                    // for stabilizing DGRN, read all updates before applying
                    if (nodeUpdates.containsKey(edge.getTarget().getId())){
                        nodeUpdates.put(
                                edge.getTarget().getId(),
                                nodeUpdates.get(edge.getTarget().getId()) + (int)edge.getWeight()
                        );
                    } else {
                        nodeUpdates.put(
                                edge.getTarget().getId(),
                                (int)edge.getWeight()
                        );
                    }
                }

            } catch( KeySelectorException err){
                logger.error("node has no activation value attribute. attempting to add it.");
                edge.getSource().getAttributeValues().addValue(attr_ActivationValue, "0");
                edge.getTarget().getAttributeValues().addValue(attr_ActivationValue, "0");
                return;
            }
        }
        // now apply updates
        for (String key : nodeUpdates.keySet()){
            handleOutputNodes(key, nodeUpdates.get(key));
            //try {
                String newVal = Integer.toString(nodeUpdates.get(key));
                setNodeAttributeValue(
                        getNode(key),
                        nodeAttribute.ACTIVATION_VALUE,
                        newVal
                );
//            } catch (KeySelectorException err){
//                logger.error("node not found for key:" + key);
//            }
        }
    }

    public boolean isInflowNode(String id){
        // returns true if given id is id of inflow node
        for (String inNode : inflowNodes.values()){
            if (inNode == id){
                return true;
            }
        } // else
        return false;
    }

    public void handleOutputNodes(String key, int value){
        // handles special actions caused by outflow nodes
        final float COLOR_DELTA = .1f;
        if( key == outflowNodes.COLOR_LIGHTEN) {
            color.add(COLOR_DELTA, COLOR_DELTA, COLOR_DELTA, 0);
        } else if (key == outflowNodes.COLOR_DARKEN) {
            color.sub(COLOR_DELTA, COLOR_DELTA, COLOR_DELTA, 0);
        } else if (key == outflowNodes.COLOR_ADD_R) {
            color.add(COLOR_DELTA, 0, 0, 0);
        } else if (key == outflowNodes.COLOR_ADD_G) {
            color.add(0, COLOR_DELTA, 0, 0);
        } else if (key == outflowNodes.COLOR_ADD_B) {
            color.add(0, 0, COLOR_DELTA, 0);
        } else if (key == outflowNodes.COLOR_SUB_R) {
            color.sub(COLOR_DELTA, 0, 0, 0);
        } else if (key == outflowNodes.COLOR_SUB_G) {
            color.sub(0, COLOR_DELTA, 0, 0);
        } else if (key == outflowNodes.COLOR_SUB_B) {
            color.sub(0, 0, COLOR_DELTA, 0);
        } else { // not an output key
            return;
        }
    }

    public void saveGraph(Gexf gexf){
        StaxGraphWriter graphWriter = new StaxGraphWriter();
        File f = new File("static_graph_sample.gexf");
        Writer out;
        try {
            out =  new FileWriter(f, false);
            graphWriter.writeToStream(gexf, out, "UTF-8");
            System.out.println(f.getAbsolutePath());
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    protected static int getNodeAttributeIndex(Node node, String attributeId) throws KeySelectorException {
        // gets value from attribute with given Id from given node
        // throws KeySelectorException if node not found
        // TODO: this should should really be handled within the gexf4j library.
        AttributeValueList attributes = node.getAttributeValues();
        for (int i = 0; i < attributes.size(); i++) {
            if (attributes.get(i).getAttribute().getId() == attributeId) {
                return i;
            }
        } // else
        throw new KeySelectorException("attribute '" + attributeId + "' not found!");
    }

    protected static void setNodeAttributeValue(Node node, String attributeId, String newValue) throws KeySelectorException{
        // gets value from attribute with given Id from given node
        // throws KeySelectorException if node not found
        // TODO: this should should really be handled within the gexf4j library.
        for (AttributeValue attr : node.getAttributeValues()){
            //System.out.println("s_attrId:" + attr.getAttribute().getId());
            if (attr.getAttribute().getId() == attributeId){
                attr.setValue(newValue);
                return;
            }
        } // else
        throw new KeySelectorException("attribute '" + attributeId + "' not found!");
    }

    protected static String getNodeAttributeValue(Node node, String attributeId) throws KeySelectorException{
        // gets value from attribute with given Id from given node
        // throws KeySelectorException if node not found
        // TODO: this should should really be handled within the gexf4j library.
        for (AttributeValue attr : node.getAttributeValues()){
            //System.out.println("g_attrId:" + attr.getAttribute().getId());
            if (attr.getAttribute().getId() == attributeId){
                return attr.getValue();
            }
        } // else
        throw new KeySelectorException("attribute '" + attributeId + "' not found!");
    }

    protected Node getNode(String nodeId) throws KeySelectorException{
        // returns node found using given nodeId
        // throws KeySelectorException if node not found
        // TODO: this should should really be handled within the gexf4j library.
        List<Node> nodes = graph.getNodes();
        for( Node node : nodes){
            if (node.getId() == nodeId){
                return node;
            }
        } // else
        throw new KeySelectorException("node w/ id '" + nodeId + "' not found!");
    }
}
