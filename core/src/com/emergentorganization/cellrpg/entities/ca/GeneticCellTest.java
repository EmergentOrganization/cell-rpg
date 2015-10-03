package com.emergentorganization.cellrpg.entities.ca;

import junit.framework.Assert;
import org.junit.Test;

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
}
