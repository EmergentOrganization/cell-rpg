package io.github.emergentorganization.cellrpg.desktop;

import com.badlogic.gdx.Game;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.backends.lwjgl.LwjglApplication;
import com.badlogic.gdx.backends.lwjgl.LwjglApplicationConfiguration;
import com.kotcrab.vis.ui.VisUI;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.junit.Test;

import java.util.Timer;
import java.util.TimerTask;

/**
 * Tests creation of an application and resizing of the application for desktop system.
 */
public class DesktopResizeTest {
    static class TestGame extends Game{

        public TestGame(){
        }

        @Override
        public void create(){

            //version = getVersion();
            VisUI.load();

            logger.info("Game started");
        }
    }

    static class ResizeTask extends TimerTask {
        @Override
        public void run(){
            int w = 600, h=400;
            logger.info("resizing to " + w + "x" + h);
            Gdx.graphics.setWindowedMode(w, h);
            logger.debug("size changed to " + w + "x" + h);

            assert Gdx.graphics.getWidth() == w;
            assert Gdx.graphics.getHeight() == h;
        }
    }

    public static void main (String[] arg){
        mainTest();
    }

    @Test
    public static void mainTest () {
        LwjglApplicationConfiguration config = new LwjglApplicationConfiguration();
        config.width  = 100;
        config.height = 100;
        config.useGL30 = true;

        new LwjglApplication(new TestGame(), config);
        logger.debug("size initially 100x100");

        Timer timer = new Timer();
        timer.schedule(new ResizeTask(), 3000);
    }

    private static final Logger logger = LogManager.getLogger(DesktopResizeTest.class);
}
