package io.github.emergentorganization.cellrpg.input;

import com.artemis.ComponentMapper;
import com.artemis.World;
import io.github.emergentorganization.engine.components.InputComponent;

/**
 * defines interface for inputProcessors (eg player controller or scripted input controllers)
 */
public abstract class InputProcessor {

    protected World world;
    protected ComponentMapper<InputComponent> im;

    public InputProcessor(World world, ComponentMapper<InputComponent> im) {
        this.world = world;
        this.im = im;
    }

    public abstract void process(int entityId);
    // process input on given entity
}
