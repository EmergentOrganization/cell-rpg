package com.emergentorganization.cellrpg.systems.CARenderSystem.GeneticCells.DGRN4j;

import javax.xml.crypto.KeySelectorException;

/**
 * Created by 7yl4r on 10/4/2015.
 */
public interface InflowNodeHandler {
    int getInflowNodeValue(String key) throws KeySelectorException;

    String[] getListOfInflowNodes();
}
