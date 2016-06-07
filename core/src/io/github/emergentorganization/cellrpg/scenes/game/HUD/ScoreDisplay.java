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


public class ScoreDisplay {
    private final World world;
    public final StatsTracker targetTracker;
    private final VisLabel scoreLabel;
    private int score = 0;

    public ScoreDisplay(World world, Stage stage, int entityId) {
        this.world = world;
        this.targetTracker = world.getEntity(entityId).getComponent(StatsTracker.class);
        VisWindow scoreWindow = new VisWindow("", true);
        scoreWindow.setPosition(0, 1000, Align.topRight);

        VisTable tabl = new VisTable();
        scoreWindow.add(tabl);
        scoreLabel = new VisLabel(getFormattedScoreStr());
        tabl.add(scoreLabel);

        scoreWindow.pack();

        stage.addActor(scoreWindow);

        EventManager eMan = world.getSystem(EventManager.class);
        eMan.addListener(new ScoreEventListener(this));
    }

    public void updateScore(float delta) {
        // get entities with statsTracker component
        targetTracker.timeSurvived += delta;
        score = targetTracker.getScore();
        scoreLabel.setText(getFormattedScoreStr());
        // scoreWindow.pack() repack? not needed as long as score stays same length.
    }

    private String getFormattedScoreStr() {
        String str = Integer.toString(score);
        return ("000000000" + str).substring(str.length());
    }
}
