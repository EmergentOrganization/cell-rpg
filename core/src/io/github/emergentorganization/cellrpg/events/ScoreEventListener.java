package io.github.emergentorganization.cellrpg.events;

import io.github.emergentorganization.cellrpg.components.StatsTracker;
import io.github.emergentorganization.cellrpg.scenes.game.HUD.ScoreDisplay;
import io.github.emergentorganization.cellrpg.systems.CASystems.layers.CALayer;
import io.github.emergentorganization.cellrpg.core.events.EventListener;


public class ScoreEventListener implements EventListener {
    private ScoreDisplay display;

    public ScoreEventListener(ScoreDisplay _display) {
        display = _display;
    }

    @Override
    public void notify(EntityEvent event) {
        // TODO: iff event applies to this entity
        StatsTracker stats = display.targetTracker;
        switch (event.event) {
            case PLAYER_HIT:
                stats.damageTaken += 1;
                break;
            case PLAYER_SHOOT:
                stats.shots += 1;
                break;
            case VYROID_KILL_GENETIC:
                stats.addKill(CALayer.VYROIDS_GENETIC);
                break;
            case VYROID_KILL_STD:
                stats.addKill(CALayer.VYROIDS);
                break;
            default:
                return;  // return w/o updating display
        }
        //display.updateScore(0);  // don't need to worry about this b/c it's updated every render
    }
}
