package com.emergentorganization.cellrpg.core;

/**
 * Created by brian on 11/8/15.
 */
public class EntityIDs {
    public static final String BULLET = "bullet";

    public static final String PLAYER = "char-player";
    public static final String PLAYER_SHIELD = "shield";

    public static final String BUILDING_LARGE_ONE = "building-large-1";
    public static final String BUILDING_ROUND_ONE = "building-round-1";
    public static final String RIFT_ONE = "rift1";
    public static final String RIFT_TWO = "rift2";
    public static final String VYROID_BEACON = "vyroid-generator";
    public static final String CIV_ONE_BLINKER = "char-civ1-blinker";
    public static final String THE_EDGE = "the_edge";

    /**
     * Get all entity IDs in an array.
     * Implemented to iterate through all of the IDs for MapEditor usage (display them all in a list, etc)
     */
    public static String[] getIDs() {
        return new String[] {
                BULLET,
                PLAYER,
                PLAYER_SHIELD,
                BUILDING_LARGE_ONE,
                BUILDING_ROUND_ONE,
                RIFT_ONE,
                RIFT_TWO,
                VYROID_BEACON,
                CIV_ONE_BLINKER,
                THE_EDGE
        };
    }
}
