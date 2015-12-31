package com.emergentorganization.cellrpg.components;

import com.artemis.Component;
import com.emergentorganization.cellrpg.systems.CASystems.layers.CALayer;

import java.util.EnumMap;
import java.util.HashMap;

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
    public int vyroidKills;
    public int shots;
    public float timeSurvived;
    private int[] kills = new int[CALayer.values().length];

    private static int POINTS_PER_KILL    = 100;
    private static int POINTS_PER_SEC = 10;
    private static int COST_OF_SHOTS = 1;
    private static int COST_OF_DAMAGE = 100;

    public int getScore(){
        return vyroidKills * POINTS_PER_KILL
                + (int)timeSurvived * POINTS_PER_SEC
                - shots * COST_OF_SHOTS
                - damageTaken * COST_OF_DAMAGE;
    }

    public void addKill(CALayer type) {
        kills[type.ordinal()] += 1;
        vyroidKills += 1;
    }
}
