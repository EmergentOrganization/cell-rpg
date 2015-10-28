package com.emergentorganization.cellrpg.tools.mapeditor.ui;

import com.badlogic.gdx.utils.Array;
import com.emergentorganization.cellrpg.entities.backgrounds.TheEdge;
import com.emergentorganization.cellrpg.entities.buildings.*;
import com.emergentorganization.cellrpg.entities.characters.Player;
import com.emergentorganization.cellrpg.entities.characters.npcs.CharCiv1Blinker;

/**
 * Created by BrianErikson on 6/15/2015.
 */
public class EntityList {
    public static Array<EntityListNode> get() {
        Array<EntityListNode> list = new Array<EntityListNode>();

        list.add(new EntityListNode("The Edge", TheEdge.class));
        list.add(new EntityListNode("BuildingLarge1", BuildingLarge1.class));
        list.add(new EntityListNode("BuildingRound1", BuildingRound1.class));
        list.add(new EntityListNode("vyroid generator", VyroidBeacon.class));
        list.add(new EntityListNode("Player", Player.class));
        list.add(new EntityListNode("CharCiv1Blinker", CharCiv1Blinker.class));
        list.add(new EntityListNode("rift1", Rift1.class));
        list.add(new EntityListNode("rift2", Rift2.class));

        return list;
    }
}
