package com.emergentorganization.cellrpg.entities.ca.DGRN4j;

import it.uniroma1.dis.wsngroup.gexf4j.core.*;
import it.uniroma1.dis.wsngroup.gexf4j.core.data.Attribute;
import it.uniroma1.dis.wsngroup.gexf4j.core.data.AttributeList;
import it.uniroma1.dis.wsngroup.gexf4j.core.data.AttributeValue;
import it.uniroma1.dis.wsngroup.gexf4j.core.data.AttributeValueList;
import it.uniroma1.dis.wsngroup.gexf4j.core.impl.GexfImpl;
import it.uniroma1.dis.wsngroup.gexf4j.core.impl.StaxGraphWriter;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import javax.xml.crypto.KeySelectorException;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.io.Writer;
import java.lang.reflect.InvocationTargetException;
import java.util.Calendar;
import java.util.HashMap;
import java.util.List;

/**
 * Created by 7yl4r on 10/4/2015.
 */

public class DGRN {
    private final Logger logger = LogManager.getLogger(getClass());

    private Gexf gexf;
    public Graph graph;
    private String ACTIVATION_VALUE;
    protected static Attribute attr_ActivationValue;
    private OutflowNodeHandler handleOutputNodes;
    private InflowNodeHandler inflowNodeHandle;

    public DGRN(String creator, String description, AttributeList attrList, Attribute attributeActivationValue,
                OutflowNodeHandler outflowNodeHandler, InflowNodeHandler inflowNodeHandler) {
        attr_ActivationValue = attributeActivationValue;
        ACTIVATION_VALUE = attr_ActivationValue.getId();
        inflowNodeHandle = inflowNodeHandler;
        handleOutputNodes = outflowNodeHandler;
        initGraph(creator, description, attrList);
        addNodes(inflowNodeHandler.getListOfInflowNodes());
        addNodes(outflowNodeHandler.getListOfOutflowNodes());
        primeInflowNodes();
    }

    private void primeInflowNodes(){
        // inserts appropriate values into inflow nodes
        for (String node : inflowNodeHandle.getListOfInflowNodes()){
            try {
                int newValue = inflowNodeHandle.getInflowNodeValue(node);
                setNodeAttributeValue(getNode(node), ACTIVATION_VALUE, Integer.toString(newValue));
            } catch( KeySelectorException err){
                logger.error("inflow node '" + node + "' attr '" + ACTIVATION_VALUE + "' not set; not found!");
            }
        }
    }

    public void initGraph(String creator, String description, AttributeList attrList){
        gexf = new GexfImpl();
        gexf.getMetadata()
                .setLastModified(Calendar.getInstance().getTime())
                .setCreator(creator)
                .setDescription(description);
        gexf.setVisualization(false);

        graph = gexf.getGraph();
        graph.getAttributeLists().add(attrList);

        graph.setDefaultEdgeType(EdgeType.DIRECTED)
                .setMode(Mode.STATIC);
    }

    public void addNodes(String[] nodeNameList){
        for (String nodeName : nodeNameList) {
            Node colorAdd1 = graph.createNode(nodeName);
            colorAdd1
                    .setLabel(nodeName)
                    .getAttributeValues()
                    .addValue(attr_ActivationValue, "0");
        }
    }

    public void connect(String nodeId1, String nodeId2, int connectionWeight) throws KeySelectorException{
        // connects n1 -> n2 with given weight
        Node n1 = getNode(nodeId1);
        Node n2 = getNode(nodeId2);
        if (n1.hasEdgeTo(nodeId2)){
            throw new IllegalStateException("node '" + nodeId1 + "' is already connected to '" + nodeId2 + "'");
        } else {
            n1.connectTo(n2).setWeight(connectionWeight);
        }
    }

    public void tick() {
        // computes one cycle through the DGRN
        primeInflowNodes();

        // propagate signals through network
        HashMap<String, Integer> nodeUpdates = new HashMap<String, Integer>();
        for (Edge edge : graph.getAllEdges()){
            try{
                if (Integer.parseInt(getNodeAttributeValue(
                        edge.getSource(), ACTIVATION_VALUE)) > 0){

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
            try {
                handleOutputNodes.handleOutputNode(key, nodeUpdates.get(key));
                String newVal = Integer.toString(nodeUpdates.get(key));
                setNodeAttributeValue(
                        getNode(key),
                        ACTIVATION_VALUE,
                        newVal
                );
            } catch (KeySelectorException err){
                logger.error("node not found for key:" + key);
            }
        }
    }

    private void applyUpdates(){

    }

    public boolean isInflowNode(String id){
        // returns true if given id is id of inflow node
        for (String inNode : inflowNodeHandle.getListOfInflowNodes()){
            if (inNode == id){
                return true;
            }
        } // else
        return false;
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

    public static int getNodeAttributeIndex(Node node, String attributeId) throws KeySelectorException {
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

    public static void setNodeAttributeValue(Node node, String attributeId, String newValue) throws KeySelectorException{
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

    public static String getNodeAttributeValue(Node node, String attributeId) throws KeySelectorException{
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

    public Node getNode(String nodeId) throws KeySelectorException{
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
