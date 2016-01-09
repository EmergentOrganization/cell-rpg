package com.emergentorganization.cellrpg.systems;

import com.artemis.Aspect;
import com.artemis.ComponentMapper;
import com.artemis.annotations.Wire;
import com.artemis.systems.IteratingSystem;
import com.badlogic.gdx.graphics.glutils.ShapeRenderer;
import com.emergentorganization.cellrpg.components.Bounds;
import com.emergentorganization.cellrpg.components.InputComponent;
import com.emergentorganization.cellrpg.components.Position;
import com.emergentorganization.cellrpg.components.Velocity;
import com.emergentorganization.cellrpg.core.entityfactory.EntityFactory;
import com.emergentorganization.cellrpg.input.InputProcessor;
import com.emergentorganization.cellrpg.input.player.PlayerInputProcessor;

import java.util.ArrayList;

/**
 * manages all InputProcessors which convert user input into various game happenings (eg player movement)
 *
 * Created by brian on 10/28/15.
 */
@Wire
public class InputSystem extends IteratingSystem {

    private ArrayList<InputProcessor> processors;
    private ComponentMapper<InputComponent> im;
    private ComponentMapper<Position> pm;
    private ComponentMapper<Bounds> bm;
    private CameraSystem camSys;
    @Wire private EntityFactory ef;

    private ShapeRenderer renderer;

    private static final int PLAYER_IN_PROC_INDEX = 0;  // careful not to add a processor and move this!

    public InputSystem() {
        super(Aspect.all(InputComponent.class, Velocity.class));
    }

    public PlayerInputProcessor getPlayerInputProcessor(){
        return (PlayerInputProcessor) processors.get(PLAYER_IN_PROC_INDEX);
    }

    @Override
    protected void initialize() {
        processors = new ArrayList<InputProcessor>();
        renderer = new ShapeRenderer();
        renderer.setAutoShapeType(true);
        processors.add(PLAYER_IN_PROC_INDEX, new PlayerInputProcessor(world, ef, im, pm, bm, renderer));
    }

    @Override
    protected void begin(){
        super.begin();
        renderer.setProjectionMatrix(camSys.getGameCamera().combined);
        renderer.begin();
    }

    @Override
    protected void end(){
        super.end();
        renderer.end();
    }
    @Override
    protected void process(int entityId) {
        for (InputProcessor p : processors) {
            p.process(entityId);
        }
    }
}
