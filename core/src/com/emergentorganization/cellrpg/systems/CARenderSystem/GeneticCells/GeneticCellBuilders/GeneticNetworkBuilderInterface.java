package com.emergentorganization.cellrpg.systems.CARenderSystem.GeneticCells.GeneticCellBuilders;

import com.emergentorganization.cellrpg.systems.CARenderSystem.GeneticCells.DGRN4j.DGRN;

/**
 * Created by 7yl4r on 10/6/2015.
 */
public interface GeneticNetworkBuilderInterface {
    void buildNetwork(DGRN dgrn);
    // construct some network on given (assumed empty) network
}
