package com.emergentorganization.cellrpg.components;

import com.artemis.Component;

/**
 * Created by 7yl4r on 1/9/2016.
 */
public class AIComponent extends Component {
    public enum aiType {
        DUMBWALK, RANDWALK
    }

    public float period = 1f;  // Time between AI runs
    public aiType type;

    public float delay = 1;  // timer for tracking period
}
