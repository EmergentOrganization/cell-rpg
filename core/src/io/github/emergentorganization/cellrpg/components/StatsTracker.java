package com.emergentorganization.cellrpg.components;

import com.artemis.Component;
import com.emergentorganization.cellrpg.systems.CASystems.layers.CALayer;

/**
 * Component for tracking performance of an entity.
 * Used on the player class, but could also be useful for some NPCs
 * or player companions.
 */
public class StatsTracker extends Component {
    private static int POINTS_PER_KILL = 100;
    private static int POINTS_PER_SEC = 10;
    private static int COST_OF_SHOTS = 1;
    private static int COST_OF_DAMAGE = 100;
    public int damageTaken = 0;
    public int vyroidKills = 0;
    public int shots = 0;
    public float timeSurvived = 0f;
    private int[] kills = new int[CALayer.values().length];

    public int getScore() {
        return vyroidKills * POINTS_PER_KILL
                + (int) timeSurvived * POINTS_PER_SEC
                - shots * COST_OF_SHOTS
                - damageTaken * COST_OF_DAMAGE;
    }

    public void addKill(CALayer type) {
        kills[type.ordinal()] += 1;
        vyroidKills += 1;
    }
}
