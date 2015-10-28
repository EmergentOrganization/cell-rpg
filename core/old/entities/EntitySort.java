package com.emergentorganization.cellrpg.entities;

import java.util.Comparator;

/**
 * Created by BrianErikson on 6/12/2015.
 */
public class EntitySort implements Comparator<Entity> {
    @Override
    public int compare(Entity o1, Entity o2) {
        return o1.zIndex.ordinal() - o2.zIndex.ordinal();
    }
}
