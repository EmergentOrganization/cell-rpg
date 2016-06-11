package io.github.emergentorganization.cellrpg.systems.CASystems.GeneticCells.DGRN4j;


public class MockInflowNodeHandler implements InflowNodeHandler {
    private String[] infoNodeList = new String[]{};

    public String[] getListOfInflowNodes() {
        return infoNodeList;
    }

    public void setInflowNodeList(String[] newNodeList) {
        infoNodeList = newNodeList;
    }

    public int getInflowNodeValue(String key) {
        return 1;
    }
}
