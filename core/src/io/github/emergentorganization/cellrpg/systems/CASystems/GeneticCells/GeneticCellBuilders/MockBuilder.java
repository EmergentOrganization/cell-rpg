package io.github.emergentorganization.cellrpg.systems.CASystems.GeneticCells.GeneticCellBuilders;

import io.github.emergentorganization.cellrpg.systems.CASystems.CAs.CACell.GeneticCell;
import io.github.emergentorganization.cellrpg.systems.CASystems.GeneticCells.DGRN4j.DGRN;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;


public class MockBuilder implements GeneticNetworkBuilderInterface {
    private static final Logger logger = LogManager.getLogger(GeneticCell.class);

    public void buildNetwork(DGRN dgrn) {
        return;
    }
}
