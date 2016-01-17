package io.github.emergentorganization.cellrpg.components;

import com.artemis.Component;


public class AIComponent extends Component {
    public float period = 1f;  // Time between AI runs
    public aiType type;
    public float delay = 1;  // timer for tracking period

    public enum aiType {
        DUMBWALK, RANDWALK
    }
}
