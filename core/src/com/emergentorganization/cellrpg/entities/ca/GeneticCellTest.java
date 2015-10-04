package com.emergentorganization.cellrpg.entities.ca;

import junit.framework.Assert;
import org.junit.Test;

import javax.xml.crypto.KeySelectorException;

/**
 * Created by 7yl4r on 10/3/2015.
 */
public class GeneticCellTest {
    /*
    @Test(expected = IllegalArgumentException.class)
    public void testExceptionIsThrown() {
        MyClass tester = new MyClass();
        tester.multiply(1000, 5);
    }
    */

    @Test
    public void testCellStateIsSetByConstructor() {
        int INIT_STATE = 0;
        GeneticCell testCell = new GeneticCell(INIT_STATE);
        Assert.assertEquals("initial state should be set to " + INIT_STATE, INIT_STATE, testCell.getState());
    }

    @Test
    public void testDefaultGraphHasAlwaysOnNode() throws KeySelectorException {
        GeneticCell testCell = new GeneticCell(0);
        testCell.setGraphToDefault();
        testCell.getNode("alwaysOn");
    }

    @Test
    public void testDefaultGraphAlwaysOnNodeIsActive() throws KeySelectorException, Exception {
        GeneticCell testCell = new GeneticCell(0);
        testCell.setGraphToDefault();
        int TF = Integer.parseInt(GeneticCell.getNodeAttributeValue(
                testCell.getNode("alwaysOn"),
                GeneticCell.nodeAttribute.ACTIVATION_VALUE.toString()
        ));
        if (TF < 1){
            throw new Exception("alwaysOn.TF should be > 1, found:" + TF);
        }
    }
}
