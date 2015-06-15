package com.emergentorganization.cellrpg.tools.mapeditor;

import com.badlogic.gdx.utils.Array;
import com.emergentorganization.cellrpg.entities.backgrounds.TheEdge;
import com.emergentorganization.cellrpg.entities.buildings.BuildingLarge1;
import com.emergentorganization.cellrpg.entities.buildings.BuildingRound1;
import com.emergentorganization.cellrpg.entities.characters.Player;
import com.emergentorganization.cellrpg.entities.characters.npcs.CharCiv1Blinker;

/**
 * Created by BrianErikson on 6/15/2015.
 */
public class EntityList {
    public static Array<EntityListNode> getList() {
        Array<EntityListNode> list = new Array<EntityListNode>();

        list.add(new EntityListNode("The Edge", TheEdge.class));
        list.add(new EntityListNode("BuildingLarge1", BuildingLarge1.class));
        list.add(new EntityListNode("BuildingRound1", BuildingRound1.class));
        list.add(new EntityListNode("Player", Player.class));
        list.add(new EntityListNode("CharCiv1Blinker", CharCiv1Blinker.class));

        return list;
    }
}
