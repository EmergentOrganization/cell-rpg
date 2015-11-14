package com.emergentorganization.cellrpg.tools.mapeditor.ui;

import com.badlogic.gdx.utils.Array;

/**
 * Created by BrianErikson on 6/15/2015.
 */
public class EntityList {
    public static Array<String> get() {
        Array<String> list = new Array<String>();

        list.add("The Edge");
        list.add("BuildingLarge1");
        list.add("BuildingRound1");
        list.add("vyroid generator");
        list.add("Player");
        list.add("CharCiv1Blinker");
        list.add("rift1");
        list.add("rift2");

        return list;
    }
}
