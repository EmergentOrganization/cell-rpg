package com.emergentorganization.cellrpg.core;

/**
 * Created by brian on 11/8/15.
 */
public enum EntityID {
    BULLET, PLAYER, PLAYER_SHIELD, BUILDING_LARGE_ONE, BUILDING_ROUND_ONE,
    RIFT_ONE, RIFT_TWO, VYROID_BEACON, CIV_ONE_BLINKER, THE_EDGE,
    CA_LAYER_VYROIDS, CA_LAYER_ENERGY;
    private static final String BULLET_STR = "bullet";

    private static final String PLAYER_STR = "char-player";
    private static final String PLAYER_SHIELD_STR = "shield";

    private static final String BUILDING_LARGE_ONE_STR = "building-large-1";
    private static final String BUILDING_ROUND_ONE_STR = "building-round-1";
    private static final String RIFT_ONE_STR = "rift1";
    private static final String RIFT_TWO_STR = "rift2";
    private static final String VYROID_BEACON_STR = "vyroid-generator";
    private static final String CIV_ONE_BLINKER_STR = "char-civ1-blinker";
    private static final String THE_EDGE_STR = "the_edge";

    public static final String CA_LAYER_VYROIDS_STR = "ca-layer-vyroids";
    public static final String CA_LAYER_ENERGY_STR = "ca-layer-energy";


    /**
     * Get all entity IDs in an array.
     * Implemented to iterate through all of the IDs for MapEditor usage (display them all in a list, etc)
     */
    public static String[] getIDs() {
        EntityID[] ids = values();
        String[] strs = new String[ids.length];
        for (int i = 0; i < ids.length; i++) {
            strs[i] = ids[i].toString();
        }
        return strs;
    }


    @Override
    public String toString() {
        String str;
        switch (this) {
            case BULLET:
                str = BULLET_STR;
                break;
            case PLAYER:
                str = PLAYER_STR;
                break;
            case PLAYER_SHIELD:
                str = PLAYER_SHIELD_STR;
                break;
            case BUILDING_LARGE_ONE:
                str = BUILDING_LARGE_ONE_STR;
                break;
            case BUILDING_ROUND_ONE:
                str = BUILDING_ROUND_ONE_STR;
                break;
            case RIFT_ONE:
                str = RIFT_ONE_STR;
                break;
            case RIFT_TWO:
                str = RIFT_TWO_STR;
                break;
            case VYROID_BEACON:
                str = VYROID_BEACON_STR;
                break;
            case CIV_ONE_BLINKER:
                str = CIV_ONE_BLINKER_STR;
                break;
            case THE_EDGE:
                str = THE_EDGE_STR;
                break;
            case CA_LAYER_VYROIDS:
                str = CA_LAYER_VYROIDS_STR;
                break;
            default:
                str = "ERROR: Could not find EntityID " + this.name() + " in EntityID.toString()";
        }

        return str;
    }
}
