package com.emergentorganization.cellrpg.events;

import com.artemis.World;
import com.badlogic.gdx.audio.Sound;
import com.emergentorganization.cellrpg.components.StatsTracker;
import com.emergentorganization.cellrpg.core.EntityID;
import com.emergentorganization.cellrpg.core.SoundEffect;
import com.emergentorganization.cellrpg.managers.AssetManager;
import com.emergentorganization.cellrpg.scenes.game.HUD.ScoreDisplay;

import java.util.Map;

/**
 * Created by brian on 11/21/15.
 */
public class ScoreEventListener implements EventListener {
    private ScoreDisplay display;

    public ScoreEventListener(ScoreDisplay _display) {
        display = _display;
    }

    @Override
    public void notify(GameEvent event) {
        // TODO: iff event applies to this entity
        StatsTracker stats = display.world.getEntity(display.targetEntityId).getComponent(StatsTracker.class);
        switch (event) {
            case PLAYER_HIT:
                stats.damageTaken += 1;
                break;
            case PLAYER_SHOOT:
                stats.shots += 1;
                break;
            default:
                return;  // return w/o updating display
        }
        display.updateScore();
    }
}
