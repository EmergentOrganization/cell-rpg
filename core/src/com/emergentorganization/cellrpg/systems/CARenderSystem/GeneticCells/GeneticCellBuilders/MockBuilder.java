package com.emergentorganization.cellrpg.systems.CARenderSystem.GeneticCells.GeneticCellBuilders;

import com.emergentorganization.cellrpg.systems.CARenderSystem.CACell.GeneticCell;
import com.emergentorganization.cellrpg.systems.CARenderSystem.GeneticCells.DGRN4j.DGRN;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

/**
 * Created by 7yl4r on 10/6/2015.
 */
public class MockBuilder implements GeneticNetworkBuilderInterface {
    private static final Logger logger = LogManager.getLogger(GeneticCell.class);

    public void buildNetwork(DGRN dgrn){
        return;
    }
}
