package com.emergentorganization.cellrpg.entities.ca.DGRN4j;

/**
 * Performs some action given output node keys and values
 *
 * Created by 7yl4r on 10/4/2015.
 */
public interface OutflowNodeHandler {
    public void handleOutputNodes(String key, int value);

    public String[] getListOfOutflowNodes();
}
