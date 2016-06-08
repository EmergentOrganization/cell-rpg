package io.github.emergentorganization.cellrpg.scenes.game.HUD;

import com.artemis.World;
import com.badlogic.gdx.scenes.scene2d.Stage;
import com.badlogic.gdx.utils.Align;
import com.kotcrab.vis.ui.widget.VisTable;
import com.kotcrab.vis.ui.widget.VisWindow;
import io.github.emergentorganization.cellrpg.core.systems.MoodSystem;
import io.github.emergentorganization.cellrpg.managers.PhysicsSystem;
import io.github.emergentorganization.cellrpg.systems.TimingSystem;

import java.util.ArrayList;


public class DebugDisplay {
    private final World world;
    private final VisWindow debugWindow;
    private final ArrayList<DebugElement> elements = new ArrayList<DebugElement>();

    public DebugDisplay(World world, Stage stage) {
        this.world = world;
        debugWindow = new VisWindow("debug HUD", true);
        debugWindow.setPosition(9999, 9999, Align.topRight);

        VisTable tabl = new VisTable();
        debugWindow.add(tabl);

        // === === === === === === DEBUG ELEMENTS === === === === === ===

        elements.add(new DebugElement(tabl, "nxtMeasure:", "s") {
            @Override
            public String getText(World world) {
                TimingSystem timeSys = world.getSystem(TimingSystem.class);
                return Long.toString(timeSys.getTimeToNextMeasure() / 1000);
            }
        });

        elements.add(new DebugElement(tabl, "mood:", "%") {
            @Override
            public String getText(World world) {
                int mood = world.getSystem(MoodSystem.class).scoreIntensityLevelOutOf(100);
                return Integer.toString(mood);
            }
        });

        elements.add(new DebugElement(tabl, "entCount:", "") {
            @Override
            public String getText(World world) {
                PhysicsSystem physicsSystem = world.getSystem(PhysicsSystem.class);
                if (physicsSystem != null && physicsSystem.isAvailable()) {
                    return Integer.toString(physicsSystem.getBodies().size());
                }
                else {
                    return "0";
                }
            }
        });

        // === === === === === === END DEBUG ELEMENTS === === === === === ===

        debugWindow.pack();
        stage.addActor(debugWindow);

//        EventManager eMan = world.getSystem(EventManager.class);
//        eMan.addListener(new ScoreEventListener(this));
    }

    public void update(float delta) {

        for (DebugElement elem : elements) {
            elem.update(world);
        }

        debugWindow.pack();
    }
}
