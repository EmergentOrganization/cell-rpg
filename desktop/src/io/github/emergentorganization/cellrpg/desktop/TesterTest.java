package io.github.emergentorganization.cellrpg.desktop;

import junit.framework.Assert;
import org.junit.Test;


public class TesterTest {
    @Test
    public void testPrintMessage() {
        System.out.println("checking testing framework...");
        Assert.assertEquals("test", "test");
    }
}
