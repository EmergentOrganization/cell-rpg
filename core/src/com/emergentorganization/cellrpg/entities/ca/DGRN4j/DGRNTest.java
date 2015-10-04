package com.emergentorganization.cellrpg.entities.ca.DGRN4j;

import com.badlogic.gdx.graphics.Color;
import com.emergentorganization.cellrpg.entities.ca.GeneticCell;
import it.uniroma1.dis.wsngroup.gexf4j.core.Graph;
import it.uniroma1.dis.wsngroup.gexf4j.core.Node;
import it.uniroma1.dis.wsngroup.gexf4j.core.data.Attribute;
import it.uniroma1.dis.wsngroup.gexf4j.core.data.AttributeClass;
import it.uniroma1.dis.wsngroup.gexf4j.core.data.AttributeList;
import it.uniroma1.dis.wsngroup.gexf4j.core.data.AttributeType;
import it.uniroma1.dis.wsngroup.gexf4j.core.impl.data.AttributeListImpl;
import junit.framework.Assert;
import org.junit.Test;

import javax.xml.crypto.KeySelectorException;

/**
 * Created by 7yl4r on 10/3/2015.
 */
public class DGRNTest {
    @Test
    public void testGetNode() throws KeySelectorException{
        AttributeList attrList = new AttributeListImpl(AttributeClass.NODE);
        Attribute attr_ActivationValue = attrList.createAttribute(
                "activation value id",
                AttributeType.INTEGER,
                "activation value"
        ).setDefaultValue("0");

        DGRN dgrn = new DGRN(
                "DGRNTest.java",
                "Test Digital Gene Regulatory Network",
                attrList,
                attr_ActivationValue,
                new MockOutflowNodeHandler(),
                new MockInflowNodeHandler()
        );
        GraphInitializer.buildLightenCellTestGraph(dgrn.graph);
        Assert.assertEquals(
                dgrn.getNode(GeneticCell.inflowNodes.ALWAYS_ON).getId(),
                GeneticCell.inflowNodes.ALWAYS_ON
        );
        Assert.assertEquals(dgrn.getNode("TF1").getId(), "TF1");
        Assert.assertEquals(
                dgrn.getNode(GeneticCell.outflowNodes.COLOR_LIGHTEN).getId(),
                GeneticCell.outflowNodes.COLOR_LIGHTEN
        );
    }
}
