package io.github.emergentorganization.cellrpg.components;

import com.artemis.Component;
import com.artemis.Entity;


public class AIComponent extends Component {
    public float period = 1f;  // Time between AI runs
    public aiType type;
    public float delay = 1;  // timer for tracking period

    public Entity target;  // target entity (eg who to chase)

    public enum aiType {
        DUMBWALK, RANDWALK, CHASE
    }
}
