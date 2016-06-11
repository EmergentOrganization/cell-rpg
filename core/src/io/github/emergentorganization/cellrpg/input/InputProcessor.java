package io.github.emergentorganization.cellrpg.input;

import com.artemis.ComponentMapper;
import com.artemis.World;
import io.github.emergentorganization.cellrpg.core.components.InputComponent;

/**
 * defines interface for inputProcessors (eg player controller or scripted input controllers)
 */
public abstract class InputProcessor {

    private final World world;
    private final ComponentMapper<InputComponent> im;

    protected InputProcessor(World world, ComponentMapper<InputComponent> im) {
        this.world = world;
        this.im = im;
    }

    public abstract void process(int entityId);
    // process input on given entity
}
