package com.emergentorganization.cellrpg.entities.ca;

import com.emergentorganization.cellrpg.CellRpg;
import it.uniroma1.dis.wsngroup.gexf4j.core.*;
import it.uniroma1.dis.wsngroup.gexf4j.core.data.Attribute;
import it.uniroma1.dis.wsngroup.gexf4j.core.data.AttributeClass;
import it.uniroma1.dis.wsngroup.gexf4j.core.data.AttributeList;
import it.uniroma1.dis.wsngroup.gexf4j.core.data.AttributeType;
import it.uniroma1.dis.wsngroup.gexf4j.core.impl.GexfImpl;
import it.uniroma1.dis.wsngroup.gexf4j.core.impl.StaxGraphWriter;
import it.uniroma1.dis.wsngroup.gexf4j.core.impl.data.AttributeListImpl;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.io.Writer;
import java.util.Calendar;

/**
 * CA grid cell which has a digital gene regulatory network (DGRN) to represent it's genome,
 * and traits which are extracted from this genome.
 *
 * Created by 7yl4r on 9/25/2015.
 */
public class GeneticCell extends BaseCell{
    Gexf gexf;

    public GeneticCell(int _state){
        super(_state);
        test();
    }

    public void initGraph(){
        gexf = new GexfImpl();
        Calendar date = Calendar.getInstance();

        gexf.getMetadata()
                .setLastModified(date.getTime())
                .setCreator("Planiverse Bridge v" + CellRpg.VERSION)
                .setDescription("Digital Gene Regulatory Network");
        gexf.setVisualization(false);

    }

    public void test() {
        initGraph();
        Graph graph = gexf.getGraph();
        graph.setDefaultEdgeType(EdgeType.DIRECTED)
                .setMode(Mode.STATIC);

        // === SETUP NODE ATTRIBUTES ===
        AttributeList attrList = new AttributeListImpl(AttributeClass.NODE);
        graph.getAttributeLists().add(attrList);

        Attribute attNodeType = attrList.createAttribute("0", AttributeType.STRING, "nodetype");
        // node type defines how a node behaves when using the DGRN.

        // === NODE TYPES ===  TODO: these could be in an enum, but there's no AttributeType.ENUM
        // NOTE: this may not be the appropriate formulation, since these are not mutually exclusive types.
        //           maybe better to have several booleans isGene, isTransFact, etc.

        String nodeType_gene = "gene";
        // node which expresses something (color, behavior). cell "outflow"
        //     genes generally don't have outgoing connections (though they could if allowed to also be a TF)

        String nodeType_transcriptionFactor = "transcriptionFactor";
        // node that affects expression of another node.
        //     DGRN "hidden" nodes. TFs have incoming & outgoing connections.

        String nodeType_externalProtein = "externalProtein";
        // node that has a value set from the cell environment
        //     (onClick, neighborCount, alwaysOn). cell "inflow"
        //     external proteins should have no inflows... though they could, if a TF was allowed to "fake" user input.

        // === END NODE TYPES ===

        Attribute attActive = attrList.createAttribute("1", AttributeType.BOOLEAN, "activated")
                .setDefaultValue("false");
        // active state of a node defines wheter the node is

        Attribute attTranscriptionFactorValue = attrList.createAttribute("2", AttributeType.INTEGER, "tf_value")
                .setDefaultValue("1");
        // === END SETUP NOTE ATTR ===



        // Create test graph of shape:
        //   (on) -> (TF1) -> (colorAdd)
        //  (onClick) -^

        Node alwaysOn = graph.createNode("alwaysOn");
        alwaysOn
                .setLabel("alwaysOn")
                .getAttributeValues()
                .addValue(attNodeType, nodeType_externalProtein)
                .addValue(attActive, "true");

        Node TF1 = graph.createNode("TF1");
        TF1
                .setLabel("TF1")
                .getAttributeValues()
                .addValue(attNodeType, nodeType_transcriptionFactor);

        Node colorAdd1 = graph.createNode("colorAdd(50,50,50)");
        colorAdd1
                .setLabel("colorAdd(50,50,50)")
                .getAttributeValues()
                .addValue(attNodeType, nodeType_gene);


        alwaysOn.connectTo("0", TF1);
        TF1.connectTo("1", colorAdd1);
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
}
