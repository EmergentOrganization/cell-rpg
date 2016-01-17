package com.emergentorganization.cellrpg.core;

import com.emergentorganization.cellrpg.systems.CASystems.layers.CALayer;

import java.util.EnumMap;
import java.util.Map;


public enum EntityID {
    BULLET,
    PLAYER, PLAYER_SHIELD,
    BUILDING_LARGE_ONE, BUILDING_ROUND_ONE,
    RIFT_ONE, RIFT_TWO, VYROID_BEACON,
    CIV_ONE_BLINKER,
    VYRAPUFFER, TUBSNAKE,
    THE_EDGE, BG_ARCADE, INVISIBLE_WALL,
    POWERUP_PLUS, POWERUP_STAR,
    CA_LAYER_VYROIDS, CA_LAYER_ENERGY, CA_LAYER_GENETIC;

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

        map.put(VYRAPUFFER, "vyrapuffer");
        map.put(TUBSNAKE, "tubsnake");

        map.put(POWERUP_PLUS, "powerup-plus");
        map.put(POWERUP_STAR, "powerup-star");

        map.put(THE_EDGE, "the_edge");
        map.put(BG_ARCADE, "bg-arcade");
        map.put(INVISIBLE_WALL, "invisible_wall");

        map.put(CA_LAYER_VYROIDS, CALayer.VYROIDS.toString());
        map.put(CA_LAYER_ENERGY, CALayer.ENERGY.toString());
        map.put(CA_LAYER_GENETIC, CALayer.VYROIDS_GENETIC.toString());
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

    public static EntityID fromString(String type) {
        // convert string key to entityId enum
        for (Map.Entry<EntityID, String> entry : map.entrySet()) {
            if (entry.getValue().equals(type))
                return entry.getKey();

        }

        throw new RuntimeException("ERROR: Could not find EntityID " + type + " in EntityID.fromString()");
    }

    @Override
    public String toString() {
        // convert entityId enum to string
        return map.get(this);
    }
}
