package io.github.emergentorganization.cellrpg.scenes.game.HUD;

import com.artemis.World;
import com.badlogic.gdx.scenes.scene2d.Stage;
import com.badlogic.gdx.utils.Align;
import com.kotcrab.vis.ui.widget.VisLabel;
import com.kotcrab.vis.ui.widget.VisTable;
import com.kotcrab.vis.ui.widget.VisWindow;
import io.github.emergentorganization.cellrpg.components.StatsTracker;
import io.github.emergentorganization.cellrpg.events.ScoreEventListener;
import io.github.emergentorganization.cellrpg.managers.EventManager;
import io.github.emergentorganization.cellrpg.systems.TimingSystem;
import io.github.emergentorganization.emergent2dcore.systems.MoodSystem;


public class DebugDisplay {
    public World world;
    private Stage stage;
    private VisLabel measureLabel;
    private VisLabel moodLabel;
    private VisWindow scoreWindow;

    public DebugDisplay(World world, Stage stage) {
        this.world = world;
        this.stage = stage;
        scoreWindow = new VisWindow("debug HUD", true);
        scoreWindow.setPosition(9999, 9999, Align.topRight);

        VisTable tabl = new VisTable();
        scoreWindow.add(tabl);

        measureLabel = new VisLabel("---s");
        tabl.add(measureLabel).row();

        moodLabel = new VisLabel("mood:---%");
        tabl.add(moodLabel).row();

        scoreWindow.pack();
        stage.addActor(scoreWindow);

//        EventManager eMan = world.getSystem(EventManager.class);
//        eMan.addListener(new ScoreEventListener(this));
    }

    public void update(float delta){
        TimingSystem timeSys = world.getSystem(TimingSystem.class);
        measureLabel.setText("nxtMeasure:" + Long.toString(timeSys.getTimeToNextMeasure()/1000) + "s");

        int mood = world.getSystem(MoodSystem.class).scoreIntensityLevelOutOf(100);
        moodLabel.setText("mood:" + Integer.toString(mood) + "%");

        scoreWindow.pack();
    }
}
