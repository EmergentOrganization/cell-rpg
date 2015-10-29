package com.emergentorganization.cellrpg.systems;

import com.artemis.Aspect;
import com.artemis.systems.IteratingSystem;
import com.badlogic.gdx.utils.Array;
import com.emergentorganization.cellrpg.components.Input;
import com.emergentorganization.cellrpg.components.Velocity;
import com.emergentorganization.cellrpg.input.InputProcessor;
import com.emergentorganization.cellrpg.input.player.PlayerInputProcessor;

import java.util.ArrayList;

/**
 * Created by brian on 10/28/15.
 */
public class InputSystem extends IteratingSystem {

    public InputSystem() {
        super(Aspect.all(Input.class, Velocity.class));
    }

    private ArrayList<InputProcessor> processors;

    @Override
    protected void initialize() {
        // I am using initiailze() cause this is called after the World object is injected to the systems.

        processors = new ArrayList<InputProcessor>();

        processors.add(new PlayerInputProcessor(world));
    }

    @Override
    protected void process(int entityId) {
        for(InputProcessor p : processors)
        {
            p.process(entityId);
        }
    }
}
