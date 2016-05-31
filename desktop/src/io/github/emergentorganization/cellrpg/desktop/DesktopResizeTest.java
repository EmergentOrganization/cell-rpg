package io.github.emergentorganization.cellrpg.desktop;

import com.badlogic.gdx.Game;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.backends.lwjgl.LwjglApplication;
import com.badlogic.gdx.backends.lwjgl.LwjglApplicationConfiguration;
import com.kotcrab.vis.ui.VisUI;
import io.github.emergentorganization.cellrpg.scenes.SceneManager;
import org.junit.Test;

import java.util.Timer;
import java.util.TimerTask;

/**
 * Tests creation of an application and resizing of the application for desktop system.
 */
public class DesktopResizeTest {
    static class TestGame extends Game{
        private SceneManager sceneManager;

        public TestGame(){
        }

        @Override
        public void create(){

            //version = getVersion();
            VisUI.load();

            System.out.println("Game started");
        }
    }

    static class ResizeTask extends TimerTask {
        @Override
        public void run(){
            int w = 600, h=400;
            System.out.println("resizing to "+w+"x"+h);
            Gdx.graphics.setDisplayMode(w, h, false);
            System.out.println("size changed to "+w+"x"+h);

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
        System.out.println("size initially 100x100");

        Timer timer = new Timer();
        timer.schedule(new ResizeTask(), 3000);
    }
}
