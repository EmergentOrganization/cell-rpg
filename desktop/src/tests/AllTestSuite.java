package tests;

import com.emergentorganization.cellrpg.desktop.TesterTest;
import com.emergentorganization.cellrpg.systems.CARenderSystem.CATestSuite;
import org.junit.BeforeClass;
import org.junit.runner.RunWith;
import org.junit.runners.Suite;

@RunWith(Suite.class)
@Suite.SuiteClasses({ TesterTest.class, CATestSuite.class} )
public final class AllTestSuite {
    @BeforeClass
    public static void initTestEnvironment() {
        // init application?
//        LwjglApplicationConfiguration config = new LwjglApplicationConfiguration();
//        config.width = 1024;
//        config.height = 768;
//        new LwjglApplication(new PixelonTransmission(), config);

        System.setProperty("log4j.configurationFile", "resources/log4j_debug.xml");
    }
}
