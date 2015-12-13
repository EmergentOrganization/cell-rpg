package com.emergentorganization.cellrpg.core;

import java.util.EnumMap;
import java.util.Map;

/**
 * Created by brian on 11/8/15.
 */
public enum EntityID {
    BULLET, PLAYER, PLAYER_SHIELD, BUILDING_LARGE_ONE, BUILDING_ROUND_ONE,
    RIFT_ONE, RIFT_TWO, VYROID_BEACON, CIV_ONE_BLINKER, THE_EDGE;
    private static final EnumMap<EntityID, String> map = new EnumMap<EntityID, String>(EntityID.class);
    static {
        map.put(BULLET, "bullet");
        map.put(PLAYER, "char-player");
        map.put(PLAYER_SHIELD, "shield");
        map.put(BUILDING_LARGE_ONE, "building-large-1");
        map.put(BUILDING_ROUND_ONE, "building-round-1");
        map.put(RIFT_ONE, "rift1");
        map.put(RIFT_TWO, "rift2");
        map.put(VYROID_BEACON, "vyroid-generator");
        map.put(CIV_ONE_BLINKER, "char-civ1-blinker");
        map.put(THE_EDGE, "the_edge");
    }

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
        return map.get(this);
    }

    public static EntityID fromString(String type) {
        for (Map.Entry<EntityID, String> entry : map.entrySet()) {
            if (entry.getValue().equals(type))
                return entry.getKey();
        }

        throw new RuntimeException("ERROR: Could not find EntityID " + type + " in EntityID.fromString()");
    }
}
