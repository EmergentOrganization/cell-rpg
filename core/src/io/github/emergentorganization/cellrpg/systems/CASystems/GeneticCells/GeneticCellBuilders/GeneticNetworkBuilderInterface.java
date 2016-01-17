package io.github.emergentorganization.cellrpg.systems.CASystems.GeneticCells.GeneticCellBuilders;

import io.github.emergentorganization.cellrpg.systems.CASystems.GeneticCells.DGRN4j.DGRN;


public interface GeneticNetworkBuilderInterface {
    void buildNetwork(DGRN dgrn);
    // construct some network on given (assumed empty) network
}
