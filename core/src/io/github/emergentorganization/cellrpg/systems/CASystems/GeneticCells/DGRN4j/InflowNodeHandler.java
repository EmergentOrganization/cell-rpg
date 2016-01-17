package io.github.emergentorganization.cellrpg.systems.CASystems.GeneticCells.DGRN4j;

import javax.xml.crypto.KeySelectorException;


public interface InflowNodeHandler {
    int getInflowNodeValue(String key) throws KeySelectorException;

    String[] getListOfInflowNodes();
}
