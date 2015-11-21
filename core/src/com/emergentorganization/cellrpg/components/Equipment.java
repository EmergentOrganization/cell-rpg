package com.emergentorganization.cellrpg.components;

import com.artemis.Component;

/**
 * Created by brian on 11/21/15.
 */
public class Equipment extends Component {
    public int shieldEntity = -1;

    public void removeShield() {
        shieldEntity = -1;
    }
}
