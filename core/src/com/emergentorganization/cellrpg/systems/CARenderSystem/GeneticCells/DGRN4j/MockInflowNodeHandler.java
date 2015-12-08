package com.emergentorganization.cellrpg.systems.CARenderSystem.GeneticCells.DGRN4j;

/**
 * Created by 7yl4r on 10/4/2015.
 */
public class MockInflowNodeHandler implements InflowNodeHandler {
    String[] infoNodeList = new String[]{};
    public String[] getListOfInflowNodes(){
        return infoNodeList;
    }

    public void setInflowNodeList(String[] newNodeList){
        infoNodeList = newNodeList;
    }

    public int getInflowNodeValue(String key){
        return 1;
    }
}
