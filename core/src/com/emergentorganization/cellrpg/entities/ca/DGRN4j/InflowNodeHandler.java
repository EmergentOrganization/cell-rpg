package com.emergentorganization.cellrpg.entities.ca.DGRN4j;

import javax.xml.crypto.KeySelectorException;

/**
 * Created by 7yl4r on 10/4/2015.
 */
public interface InflowNodeHandler {
    public int getInflowNodeValue(String key) throws KeySelectorException;

    public String[] getListOfInflowNodes();
}
