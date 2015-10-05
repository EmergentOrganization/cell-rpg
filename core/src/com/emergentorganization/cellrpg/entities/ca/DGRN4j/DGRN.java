package com.emergentorganization.cellrpg.entities.ca.DGRN4j;

import com.emergentorganization.cellrpg.scenes.CAScene;
import it.uniroma1.dis.wsngroup.gexf4j.core.*;
import it.uniroma1.dis.wsngroup.gexf4j.core.data.*;
import it.uniroma1.dis.wsngroup.gexf4j.core.impl.GexfImpl;
import it.uniroma1.dis.wsngroup.gexf4j.core.impl.StaxGraphWriter;
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
import java.util.Random;

/**
 * Created by 7yl4r on 10/4/2015.
 */

public class DGRN {
    // NOTE: these vars should be the only connection back to cellRPG
    private final Logger logger = LogManager.getLogger(getClass());
    Random randomGenerator = CAScene.randomGenerator;
    // END cellRPG dependent vars

    private Gexf gexf;
    public Graph graph;
    private String ACTIVATION_VALUE_ID;
    private String ALLELE_COUNT_ID = "# of alleles";
    public static Attribute attr_ActivationValue;
    public static Attribute attr_AlleleCount;
    private OutflowNodeHandler handleOutputNodes;
    private InflowNodeHandler inflowNodeHandle;

    public DGRN(String creator, String description, AttributeList attrList, Attribute attributeActivationValue,
                OutflowNodeHandler outflowNodeHandler, InflowNodeHandler inflowNodeHandler) {
        attr_AlleleCount = attrList.createAttribute(
                ALLELE_COUNT_ID,
                AttributeType.INTEGER,
                ALLELE_COUNT_ID
        );
        attr_ActivationValue = attributeActivationValue;
        ACTIVATION_VALUE_ID = attr_ActivationValue.getId();
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
                setNodeAttributeValue(getNode(node), ACTIVATION_VALUE_ID, Integer.toString(newValue));
            } catch( KeySelectorException err){
                logger.error("inflow node '" + node + "' attr '" + ACTIVATION_VALUE_ID + "' not set; not found!");
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
                    .addValue(attr_ActivationValue, "0")
                    .addValue(attr_AlleleCount, "1");
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
                        edge.getSource(), ACTIVATION_VALUE_ID)) > 0){

//                      // FOR CUMULATIVE DGRN
//                    int i = getNodeAttributeIndex(edge.getTarget(), nodeAttribute.ACTIVATION_VALUE_ID);
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
                logger.error(edge.getSource().getId() +
                        " or " + edge.getTarget().getId() +
                        " node has no activation value attribute. attempting to add it.");
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
                        ACTIVATION_VALUE_ID,
                        newVal
                );
            } catch (KeySelectorException err){
                logger.error("node not found for key:" + key);
            }
        }
    }

    public void inheritGenes(DGRN parent, int maxAlleles){
        // inherits genes from given parent assuming parent will represent 1/maxAlleles in genetic material
        //     example: maxAlleles=2 (haploid)
        //      parent_1: gene1(alleles:2), gene2(alleles:1)
        //      patent_2: gene1(alleles:1), gene2(alleles:1), gene3(alleles:1)
        //      child   : gene1(1 or 2),    gene2(0, 1, 2)  , gene3(0 or 1)

        //     example2: maxAlleles=2 (triploid)
        //      parent_1: gene1(alleles:2), gene2(alleles:1)
        //      parent_2: gene1(alleles:1), gene2(alleles:1), gene3(alleles:1)
        //      parent_3: gene1( 0       ), gene2(   3     ), gene3(   0     ), gene4( 3 )
        //      child   : gene1(1 or 2),    gene2(1, 2, 3)  , gene3(0 or 1)   , gene4( 1 )

        //     example3: maxAlleles=1 (mitosis)
        //      child == parent_1

        // for each gene pair
        //logger.info("inheriting from parent " + parent.toString());
        for (Node node : parent.graph.getNodes()){
            if (isInflowNode(node.getId()) || isOutflowNode(node.getId())){
                continue;  // don't inherit inflow/outflow nodes (these are in all by default)
            } else {
                //logger.info("gene node " + node.getId());
                try {
                    // choose if gene gets included based on node # of alleles attribute
                    int n_alleles = Integer.parseInt(getNodeAttributeValue(node, ALLELE_COUNT_ID));
                    // n_alleles/maxAlleles = chance of inheriting this gene
                    if (randomGenerator.nextInt(maxAlleles + 1) < n_alleles) {
                        // dice roll has determined that gene is inherited.
                        try {
                            // if gene already exists, # of alleles += 1
                            Node childNode = getNode(node.getId());
                            int newAlleleCount = Integer.parseInt(getNodeAttributeValue(childNode, ALLELE_COUNT_ID));
                            newAlleleCount++;
                            setNodeAttributeValue(childNode, ALLELE_COUNT_ID, Integer.toString(newAlleleCount));
                        } catch (KeySelectorException err) {
                            // else add chosen gene w/ # of alleles = 1
                            Node newNode = graph.createNode(node.getId());
                            newNode
                                    .setLabel(node.getLabel())
                                    .getAttributeValues()
                                    .addValue(attr_ActivationValue, "0")
                                    .addValue(attr_AlleleCount, "1");
                            // copy connections
                            for (Edge edge : node.getAllEdges()) {  // or node.getEdges()?
                                if (edge.getSource().equals(node)) {
                                    // outgoing
                                    Node otherNode = getNode(edge.getTarget().getId());
                                    newNode.connectTo(otherNode);
                                } else if (edge.getTarget().equals(node)) {
                                    // incoming
                                    Node otherNode = getNode(edge.getSource().getId());
                                    otherNode.connectTo(newNode);
                                } else {
                                    throw new IllegalStateException("node is not src or tgt of edge?!?");
                                }
                            }
                        }
                    } // else dice roll determines gene not to be inherited

                } catch (KeySelectorException err) {
                    logger.error("failed to inherit " + node.getId() + " gene: " + err.getMessage());
                }
            }
        }
    }

    public boolean isOutflowNode(String id){
        // returns true if given id is id of an outflow node
        for (String outNode : handleOutputNodes.getListOfOutflowNodes()){
            if (outNode == id){
                return true;
            }
        } // else
        return false;
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
