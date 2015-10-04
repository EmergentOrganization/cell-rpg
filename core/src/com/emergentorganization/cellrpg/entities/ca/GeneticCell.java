package com.emergentorganization.cellrpg.entities.ca;

import com.emergentorganization.cellrpg.CellRpg;
import it.uniroma1.dis.wsngroup.gexf4j.core.*;
import it.uniroma1.dis.wsngroup.gexf4j.core.data.*;
import it.uniroma1.dis.wsngroup.gexf4j.core.impl.GexfImpl;
import it.uniroma1.dis.wsngroup.gexf4j.core.impl.StaxGraphWriter;
import it.uniroma1.dis.wsngroup.gexf4j.core.impl.data.AttributeListImpl;

import javax.xml.crypto.KeySelectorException;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.io.Writer;
import java.util.Calendar;
import java.util.List;

/**
 * CA grid cell which has a digital gene regulatory network (DGRN) to represent it's genome,
 * and traits which are extracted from this genome.
 *
 * Created by 7yl4r on 9/25/2015.
 */
public class GeneticCell extends BaseCell{
    public enum nodeAttribute{
        ACTIVATION_VALUE
    }
    private Gexf gexf;
    private Graph graph;
    // === SETUP NODE ATTRIBUTES ===
    private static AttributeList attrList = new AttributeListImpl(AttributeClass.NODE);

    private static Attribute attr_ActivationValue = attrList.createAttribute(
            nodeAttribute.ACTIVATION_VALUE.toString(),
            AttributeType.INTEGER,
            "activation value"
    ).setDefaultValue("0");  // NOTE: default value doesn't seem to have indended effect?
    // active state of a node defines whether the node is

    // === END SETUP NOTE ATTR ===

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

    public void setGraphToDefault() {
        // Create test graph of shape:
        //   (on) -> (TF1) -> (colorAdd)
        //  (onClick) -^

        Node alwaysOn = graph.createNode("alwaysOn");
        alwaysOn
                .setLabel("alwaysOn")
                .getAttributeValues()
                .addValue(attr_ActivationValue, "100");

        Node TF1 = graph.createNode("TF1");
        TF1
                .setLabel("TF1")
                .getAttributeValues()
                .addValue(attr_ActivationValue, "0");

        Node colorAdd1 = graph.createNode("colorAdd(50,50,50)");
        colorAdd1
                .setLabel("colorAdd(50,50,50)")
                .getAttributeValues()
                .addValue(attr_ActivationValue, "0");


        alwaysOn.connectTo("0", TF1).setWeight(2);
        TF1.connectTo("1", colorAdd1).setWeight(1);
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

    protected static String getNodeAttributeValue(Node node, String attributeId) throws KeySelectorException{
        // gets value from attribute with given Id from given node
        // throws KeySelectorException if node not found
        // TODO: this should should really be handled within the gexf4j library.
        for (AttributeValue attr : node.getAttributeValues()){
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
