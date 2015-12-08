package com.emergentorganization.cellrpg.systems.CARenderSystem.GeneticCells.DGRN4j;

import javax.xml.crypto.KeySelectorException;

/**
 * Performs some action given output node keys and values
 *
 * Created by 7yl4r on 10/4/2015.
 */
public interface OutflowNodeHandler {
    public void handleOutputNode(String key, int value) throws KeySelectorException;

    public String[] getListOfOutflowNodes();
}
