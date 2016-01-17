package com.emergentorganization.cellrpg.systems.CASystems.GeneticCells.DGRN4j;

import javax.xml.crypto.KeySelectorException;

/**
 * Performs some action given output node keys and values
 */
public interface OutflowNodeHandler {
    public void handleOutputNode(String key, int value) throws KeySelectorException;

    public String[] getListOfOutflowNodes();
}
