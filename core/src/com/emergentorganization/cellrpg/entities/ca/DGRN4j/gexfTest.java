package com.emergentorganization.cellrpg.entities.ca.DGRN4j;

import it.uniroma1.dis.wsngroup.gexf4j.core.*;
import it.uniroma1.dis.wsngroup.gexf4j.core.data.*;
import it.uniroma1.dis.wsngroup.gexf4j.core.impl.GexfImpl;
import it.uniroma1.dis.wsngroup.gexf4j.core.impl.data.AttributeListImpl;
import junit.framework.Assert;
import org.junit.Test;

import javax.xml.crypto.KeySelectorException;
import java.util.Calendar;

/**
 * Created by 7yl4r on 10/3/2015.
 */
public class gexfTest {

    // TODO: compare node.getAllEdges() and node.getEdges()
    // TODO: do either of the above include inflowing edges AND outflowing edges?

    @Test
    public void testCellStateIsSetByConstructor() {
        Gexf gexf = new GexfImpl();
        Graph graph = gexf.getGraph();
        graph.setDefaultEdgeType(EdgeType.UNDIRECTED).setMode(Mode.STATIC);

        AttributeList attrList = new AttributeListImpl(AttributeClass.NODE);
        graph.getAttributeLists().add(attrList);

        final String DEFAULT_VALUE = "true";
        Attribute attFrog = attrList.createAttribute("2", AttributeType.BOOLEAN, "frog")
                .setDefaultValue(DEFAULT_VALUE);


        Node gephi = graph.createNode("0");
        gephi
                .setLabel("Gephi");

        // gephi should have default value for attFrog?
        // but it doesn't, the following throws indexError, node has no attributes (default was not added)
        AttributeValue att1 = graph.getNodes().get(0).getAttributeValues().get(1);
        String v1 = att1.getValue();
        String t1 = att1.getAttribute().getTitle();
        System.out.println("attFrog value:" + v1 + " (" + t1 + ")");
        Assert.assertEquals(v1, DEFAULT_VALUE);
    }
}
