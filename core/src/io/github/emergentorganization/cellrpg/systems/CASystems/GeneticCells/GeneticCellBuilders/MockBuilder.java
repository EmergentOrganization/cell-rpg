package com.emergentorganization.cellrpg.systems.CASystems.GeneticCells.GeneticCellBuilders;

import com.emergentorganization.cellrpg.systems.CASystems.CAs.CACell.GeneticCell;
import com.emergentorganization.cellrpg.systems.CASystems.GeneticCells.DGRN4j.DGRN;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;


public class MockBuilder implements GeneticNetworkBuilderInterface {
    private static final Logger logger = LogManager.getLogger(GeneticCell.class);

    public void buildNetwork(DGRN dgrn) {
        return;
    }
}
