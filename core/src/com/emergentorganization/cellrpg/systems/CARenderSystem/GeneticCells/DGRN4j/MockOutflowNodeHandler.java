package com.emergentorganization.cellrpg.systems.CARenderSystem.GeneticCells.DGRN4j;

/**
 * Created by 7yl4r on 10/4/2015.
 */
public class MockOutflowNodeHandler implements OutflowNodeHandler {
    public String[] getListOfOutflowNodes(){
        return new String[]{};
    }

    public void handleOutputNode(String key, int val){
        return;
    }
}
