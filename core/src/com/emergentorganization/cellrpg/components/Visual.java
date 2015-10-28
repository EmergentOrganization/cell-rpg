package com.emergentorganization.cellrpg.components;

import com.artemis.Component;

/**
 * This is used for both animation and static images.
 *
 * Created by orelb on 10/28/2015.
 */
public class Visual extends Component {

    public String id; // the visual id, this is resolved through a manager.

    public float d; // animation timestep

}