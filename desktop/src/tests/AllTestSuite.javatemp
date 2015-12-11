package tests;

import com.emergentorganization.cellrpg.CellRpg;
import com.emergentorganization.cellrpg.desktop.TesterTest;
import com.emergentorganization.cellrpg.entities.ca.CATestSuite;
import org.junit.BeforeClass;
import org.junit.runner.RunWith;
import org.junit.runners.Suite;

@RunWith(Suite.class)
@Suite.SuiteClasses({ TesterTest.class, CATestSuite.class} )
public final class AllTestSuite {
    @BeforeClass
    public static void initTestEnvironment() {
        CellRpg cellRpg = new CellRpg();  // init singleton
        System.setProperty("log4j.configurationFile", "log4j_debug.xml");
    }
}
