package com.emergentorganization.cellrpg.systems.CASystems.GeneticCells.DGRN4j;

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
import java.util.*;


public class DGRN {
    public static Attribute attr_ActivationValue;
    public static Attribute attr_AlleleCount;
    private final Logger logger = LogManager.getLogger(getClass());
    public Random randomGenerator;
    public Graph graph;
    public String ACTIVATION_VALUE_ID;
    public String ALLELE_COUNT_ID = "# of alleles";
    private Gexf gexf;
    private OutflowNodeHandler handleOutputNodes;
    private InflowNodeHandler inflowNodeHandle;

    public DGRN(String creator, String description, AttributeList attrList, Attribute attributeActivationValue,
                OutflowNodeHandler outflowNodeHandler, InflowNodeHandler inflowNodeHandler) {
        this(creator, description, attrList, attributeActivationValue, outflowNodeHandler, inflowNodeHandler,
                new Random());
    }

    public DGRN(String creator, String description, AttributeList attrList, Attribute attributeActivationValue,
                OutflowNodeHandler outflowNodeHandler, InflowNodeHandler inflowNodeHandler, Random randomizer) {
        randomGenerator = randomizer;
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

    public static void setNodeAttributeValue(Node node, String attributeId, String newValue) throws KeySelectorException {
        // gets value from attribute with given Id from given node
        // throws KeySelectorException if node not found
        // TODO: this should should really be handled within the gexf4j library.
        for (AttributeValue attr : node.getAttributeValues()) {
            //System.out.println("s_attrId:" + attr.getAttribute().getId());
            if (attr.getAttribute().getId() == attributeId) {
                attr.setValue(newValue);
                return;
            }
        } // else
        throw new KeySelectorException("attribute '" + attributeId + "' not found!");
    }

    public static String getNodeAttributeValue(Node node, String attributeId) throws KeySelectorException {
        // gets value from attribute with given Id from given node
        // throws KeySelectorException if node not found
        // TODO: this should should really be handled within the gexf4j library.
        for (AttributeValue attr : node.getAttributeValues()) {
            //System.out.println("g_attrId:" + attr.getAttribute().getId());
            if (attr.getAttribute().getId() == attributeId) {
                return attr.getValue();
            }
        } // else
        throw new KeySelectorException("attribute '" + attributeId + "' not found!");
    }

    public List<Edge> getAllEdgesOf(Node node) {
        // returns list of all edges with given node as tgt or src
        List<Edge> edges = new ArrayList<Edge>();
        for (Edge edge : graph.getAllEdges()) {
            if (edge.getSource().getId() == node.getId()
                    || edge.getTarget().getId() == node.getId()) {
                edges.add(edge);
            }
        }
        return edges;
    }

    protected void primeInflowNodes() {
        // inserts appropriate values into inflow nodes
        for (String node : inflowNodeHandle.getListOfInflowNodes()) {
            try {
                int newValue = inflowNodeHandle.getInflowNodeValue(node);
                logger.trace("inflow " + node + " set to " + newValue);
                setNodeAttributeValue(getNode(node), ACTIVATION_VALUE_ID, Integer.toString(newValue));
            } catch (KeySelectorException err) {
                logger.error("inflow node '" + node + "' attr '" + ACTIVATION_VALUE_ID + "' not set; not found!");
            }
        }
    }

    public void initGraph(String creator, String description, AttributeList attrList) {
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

    public void addNodes(String[] nodeNameList) {
        for (String nodeName : nodeNameList) {
            Node colorAdd1 = graph.createNode(nodeName);
            colorAdd1
                    .setLabel(nodeName)
                    .getAttributeValues()
                    .addValue(attr_ActivationValue, "0")
                    .addValue(attr_AlleleCount, "1");
        }
    }

    public void connect(String nodeId1, String nodeId2, int connectionWeight) throws KeySelectorException {
        // connects n1 -> n2 with given weight
        Node n1 = getNode(nodeId1);
        Node n2 = getNode(nodeId2);
        if (n1.hasEdgeTo(nodeId2)) {
            throw new IllegalStateException("node '" + nodeId1 + "' is already connected to '" + nodeId2 + "'");
        } else {
            n1.connectTo(n2).setWeight(connectionWeight);
        }
    }

    private void deductEdgeWeightFromSrc(Edge edge) throws KeySelectorException {
        // deduct the magnitude of the edge weight from the src potential
        Node src = edge.getSource();
        int oldVal = Integer.parseInt(getNodeAttributeValue(src, ACTIVATION_VALUE_ID));
        int newVal = oldVal - Math.abs((int) edge.getWeight());
        setNodeAttributeValue(src, ACTIVATION_VALUE_ID, Integer.toString(newVal));
    }

    protected boolean edgePropagatesSignal(Edge edge) throws KeySelectorException {
        // returns true if the given node has a signal that should be passed along the given edge
        // assuming single-step weight-threshold-gate drain
        // check if src node has enough potential to traverse edge
        try {
            int edgeMagnitude = Math.abs((int) edge.getWeight());
            // potential must be positive to traverse edge, even if edge weight is negative
            //    thus, no abs() on the src potential
            int srcPotential = Integer.parseInt(getNodeAttributeValue(edge.getSource(), ACTIVATION_VALUE_ID));
            // traversing negative weights requires positive potential...
            logger.trace("(" + srcPotential + ")-" + edgeMagnitude + "->?");
            if (srcPotential >= edgeMagnitude) {
                return true;
            } else {
                return false;
            }
        } catch (IllegalStateException ex) {
            logger.error(edge.getSource().getId() + "->" + edge.getTarget().getId() + " has no weight? : " + ex.getMessage());
            return false; //throw ex;
        }
    }

    public void tick() {
        // computes one cycle through the DGRN
        // propagate signals through network
        HashMap<String, Integer> nodeUpdates = new HashMap<String, Integer>();
        for (Edge edge : graph.getAllEdges()) {
            try {
                if (edgePropagatesSignal(edge)) {
                    logger.trace("running " + edge.getSource().getId() + "->" + edge.getTarget().getId());
                    // must read all updates before applying additions, so they are stored in temp hashMap
                    if (nodeUpdates.containsKey(edge.getTarget().getId())) {
                        nodeUpdates.put(
                                edge.getTarget().getId(),
                                nodeUpdates.get(edge.getTarget().getId()) + (int) edge.getWeight()
                        );
                    } else {
                        nodeUpdates.put(
                                edge.getTarget().getId(),
                                (int) edge.getWeight()
                        );
                    }
                    deductEdgeWeightFromSrc(edge);
                }

            } catch (KeySelectorException err) {
                logger.error(edge.getSource().getId() +
                        " or " + edge.getTarget().getId() +
                        " node has no activation value attribute. attempting to add it.");
                edge.getSource().getAttributeValues().addValue(attr_ActivationValue, "0");
                edge.getTarget().getAttributeValues().addValue(attr_ActivationValue, "0");
                return;
            }
        }
        // now apply updates
        for (String key : nodeUpdates.keySet()) {
            try {
                // src nodes have already been reduced, but need to add weights to tgt nodes
                int oldVal = Integer.parseInt(getNodeAttributeValue(getNode(key), ACTIVATION_VALUE_ID));
                int delta = nodeUpdates.get(key);
                logger.trace("" + key + "=" + oldVal + "+" + delta);
                String newVal = Integer.toString(oldVal + delta);
                setNodeAttributeValue(
                        getNode(key),
                        ACTIVATION_VALUE_ID,
                        newVal
                );
                handleOutputNodes.handleOutputNode(key, nodeUpdates.get(key));
            } catch (KeySelectorException err) {
                logger.error("node not found for key:" + key);
            }
        }
        primeInflowNodes();
    }

    public void inheritGenes(DGRN parent, int maxAlleles) {
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
        logger.trace("inheriting from parent " + parent.toString());
        for (Node node : parent.graph.getNodes()) {
            if (isInflowNode(node.getId()) || isOutflowNode(node.getId())) {
                continue;  // don't inherit inflow/outflow nodes (these are in all by default)
            } else {
                try {
                    // choose if gene gets included based on node # of alleles attribute
                    int n_alleles = Integer.parseInt(getNodeAttributeValue(node, ALLELE_COUNT_ID));
                    // n_alleles/maxAlleles = chance of inheriting this gene
                    int diceRoll = randomGenerator.nextInt(maxAlleles + 1);
                    logger.debug("@ gene node " + node.getId() + " | " + n_alleles + "/" + maxAlleles + " alleles");
                    if (diceRoll <= n_alleles) {
                        // dice roll has determined that gene is inherited.
                        logger.trace("    inherited");
                        try {
                            // if gene already exists, # of alleles += 1
                            logger.trace("    adding to existing node");
                            Node childNode = getNode(node.getId());
                            int newAlleleCount = Integer.parseInt(getNodeAttributeValue(childNode, ALLELE_COUNT_ID));
                            newAlleleCount++;
                            setNodeAttributeValue(childNode, ALLELE_COUNT_ID, Integer.toString(newAlleleCount));
                        } catch (KeySelectorException err) {
                            // else add chosen gene w/ # of alleles = 1
                            logger.trace("    creating new gene node");
                            Node newNode = graph.createNode(node.getId());
                            newNode
                                    .setLabel(node.getLabel())
                                    .getAttributeValues()
                                    .addValue(attr_ActivationValue, "0")
                                    .addValue(attr_AlleleCount, "1");
                            // copy connections
                            List<Edge> edgesList = parent.getAllEdgesOf(node);
                            logger.trace("      found " + edgesList.size() + " edges.");  // TODO: this should != 0
                            for (Edge edge : edgesList) {
                                if (edge.getSource().equals(node)) {
                                    logger.trace("copying " + node.getId() + "->" + edge.getTarget().getId());
                                    // outgoing
                                    Node otherNode = getNode(edge.getTarget().getId());
                                    newNode.connectTo(otherNode).setWeight(edge.getWeight());
                                } else if (edge.getTarget().equals(node)) {
                                    // incoming
                                    logger.trace("copying " + edge.getSource().getId() + "->" + node.getId());
                                    Node otherNode = getNode(edge.getSource().getId());
                                    otherNode.connectTo(newNode).setWeight(edge.getWeight());
                                } else {
                                    throw new IllegalStateException("node is not src or tgt of edge?!?");
                                    // TODO: or maybe otherNode is not yet in grid!
                                }
                            }
                        }
                    } else { // dice roll determines gene not to be inherited
                        logger.trace("    not inherited");
                    }
                } catch (KeySelectorException err) {
                    logger.error("failed to inherit " + node.getId() + " gene: " + err.getMessage());
                }
            }
        }
    }

    public boolean isOutflowNode(String id) {
        // returns true if given id is id of an outflow node
        for (String outNode : handleOutputNodes.getListOfOutflowNodes()) {
            if (outNode == id) {
                return true;
            }
        } // else
        return false;
    }

    public boolean isInflowNode(String id) {
        // returns true if given id is id of inflow node
        for (String inNode : inflowNodeHandle.getListOfInflowNodes()) {
            if (inNode == id) {
                return true;
            }
        } // else
        return false;
    }

    public void saveGraph(Gexf gexf) {
        StaxGraphWriter graphWriter = new StaxGraphWriter();
        File f = new File("static_graph_sample.gexf");
        Writer out;
        try {
            out = new FileWriter(f, false);
            graphWriter.writeToStream(gexf, out, "UTF-8");
            logger.info("saved graph to " + f.getAbsolutePath());
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public Node getNode(String nodeId) throws KeySelectorException {
        // returns node found using given nodeId
        // throws KeySelectorException if node not found
        // TODO: this should should really be handled within the gexf4j library.
        List<Node> nodes = graph.getNodes();
        for (Node node : nodes) {
            if (node.getId() == nodeId) {
                return node;
            }
        } // else
        throw new KeySelectorException("node w/ id '" + nodeId + "' not found!");
    }
}
