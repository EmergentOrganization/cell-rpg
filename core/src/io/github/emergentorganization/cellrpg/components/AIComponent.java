package io.github.emergentorganization.cellrpg.components;

import com.artemis.Component;
import com.artemis.Entity;

public class AIComponent extends Component {
    public float period = 1;  // Time between AI runs [s]
    public aiType type;
    public float delay = 1;  // timer for tracking period [s]

    public Entity target;  // target entity (eg who to chase)

    public enum aiType {
        DUMBWALK, RANDWALK, CHASE
    }
}
