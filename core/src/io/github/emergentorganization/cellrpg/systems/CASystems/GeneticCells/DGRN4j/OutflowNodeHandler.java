package io.github.emergentorganization.cellrpg.systems.CASystems.GeneticCells.DGRN4j;

import javax.xml.crypto.KeySelectorException;

/**
 * Performs some action given output node keys and values
 */
public interface OutflowNodeHandler {
    void handleOutputNode(String key, int value);

    String[] getListOfOutflowNodes();
}
