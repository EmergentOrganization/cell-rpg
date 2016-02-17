package io.github.emergentorganization.cellrpg.tools.ApparitionCreator;

import io.github.emergentorganization.cellrpg.components.CAGridComponents;
import io.github.emergentorganization.cellrpg.components.SpontaneousGeneration.SpontaneousGeneration;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.util.TimerTask;

/**
 * defines the appearance of a given pattern onto a CA grid.
 */
public class CAApparitionTask extends TimerTask {
    private final Logger logger = LogManager.getLogger(getClass());

    SpontaneousGeneration spontGen;
    CAGridComponents targetGrid;

    public CAApparitionTask(SpontaneousGeneration spontGen, CAGridComponents targetGrid){
        this.spontGen = spontGen;
        this.targetGrid = targetGrid;
    }

    public void run(){
        targetGrid.stampCenteredAt(spontGen.stamp, spontGen.position);
    }
}
