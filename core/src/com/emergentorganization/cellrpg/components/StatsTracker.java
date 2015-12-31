package com.emergentorganization.cellrpg.components;

import com.artemis.Component;

/**
 * Component for tracking performance of an entity.
 * Used on the player class, but could also be useful for some NPCs
 * or player companions.
 *
 * TODO: connect this with game events using EventManager.
 *
 * Created by 7yl4r on 12/31/2015.
 */
public class StatsTracker extends Component {
    public int damageTaken;
    public int kills;
    public int powerUpsCollected;

    private static int POINTS_PER_KILL    = 100;
    private static int POINTS_PER_POWERUP = 100;

    public int getScore(){
        return kills * POINTS_PER_KILL
                + powerUpsCollected * POINTS_PER_POWERUP;
    }
}
