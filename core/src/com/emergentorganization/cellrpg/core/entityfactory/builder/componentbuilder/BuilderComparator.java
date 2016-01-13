package com.emergentorganization.cellrpg.core.entityfactory.builder.componentbuilder;

import java.util.Comparator;


public class BuilderComparator implements Comparator<IComponentBuilder> {
    @Override
    public int compare(IComponentBuilder o1, IComponentBuilder o2) {
        return o1.getSortIndex() > o2.getSortIndex() ? 1 : 0;
    }
}
