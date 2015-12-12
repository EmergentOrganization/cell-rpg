package com.emergentorganization.cellrpg.core;

/**
 * Created by brian on 11/8/15.
 */
public enum EntityID {
    BULLET, PLAYER, PLAYER_SHIELD, BUILDING_LARGE_ONE, BUILDING_ROUND_ONE,
    RIFT_ONE, RIFT_TWO, VYROID_BEACON, CIV_ONE_BLINKER, THE_EDGE, INVISIBLE_WALL;
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
    private static final String INVISIBLE_WALL_STR = "invisible_wall";

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
            case INVISIBLE_WALL:
                str = INVISIBLE_WALL_STR;
                break;
            default:
                str = "ERROR: Could not find EntityID " + this.name() + " in EntityID.toString()";
        }

        return str;
    }

    public static EntityID fromString(String id) {
        if (id.equals(BULLET_STR))
            return BULLET;
        else if (id.equals(PLAYER_STR))
            return PLAYER;
        else if (id.equals(PLAYER_SHIELD_STR))
            return PLAYER_SHIELD;
        else if (id.equals(BUILDING_LARGE_ONE_STR))
            return BUILDING_LARGE_ONE;
        else if (id.equals(BUILDING_ROUND_ONE_STR))
            return BUILDING_ROUND_ONE;
        else if (id.equals(RIFT_ONE_STR))
            return RIFT_ONE;
        else if (id.equals(RIFT_TWO_STR))
            return RIFT_TWO;
        else if (id.equals(VYROID_BEACON_STR))
            return VYROID_BEACON;
        else if (id.equals(CIV_ONE_BLINKER_STR))
            return CIV_ONE_BLINKER;
        else if (id.equals(THE_EDGE_STR))
            return THE_EDGE;
        else if (id.equals(INVISIBLE_WALL_STR))
            return INVISIBLE_WALL;
        else
            throw new RuntimeException("ERROR: Could not find EntityID " + id + " in EntityID.fromString()");
    }
}
