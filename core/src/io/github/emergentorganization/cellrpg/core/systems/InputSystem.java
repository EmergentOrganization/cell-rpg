package io.github.emergentorganization.cellrpg.core.systems;

import com.artemis.Aspect;
import com.artemis.ComponentMapper;
import com.artemis.annotations.Profile;
import com.artemis.annotations.Wire;
import com.artemis.systems.IteratingSystem;
import com.badlogic.gdx.graphics.glutils.ShapeRenderer;
import io.github.emergentorganization.cellrpg.core.components.Bounds;
import io.github.emergentorganization.cellrpg.core.components.InputComponent;
import io.github.emergentorganization.cellrpg.core.components.Position;
import io.github.emergentorganization.cellrpg.core.components.Velocity;
import io.github.emergentorganization.cellrpg.core.entityfactory.EntityFactory;
import io.github.emergentorganization.cellrpg.input.InputProcessor;
import io.github.emergentorganization.cellrpg.input.player.PlayerInputProcessor;
import io.github.emergentorganization.cellrpg.tools.profiling.EmergentProfiler;

import java.util.ArrayList;

/**
 * manages all InputProcessors which convert user input into various game happenings (eg player movement)
 */
@Wire
@Profile(using=EmergentProfiler.class, enabled=true)
public class InputSystem extends IteratingSystem {

    private static final int PLAYER_IN_PROC_INDEX = 0;  // careful not to add a processor and move this!
    private ArrayList<InputProcessor> processors;
    private ComponentMapper<InputComponent> im;
    private ComponentMapper<Position> pm;
    private ComponentMapper<Bounds> bm;
    private CameraSystem camSys;
    @Wire
    private EntityFactory ef;
    private ShapeRenderer renderer;

    public InputSystem() {
        super(Aspect.all(InputComponent.class, Velocity.class));
    }

    public PlayerInputProcessor getPlayerInputProcessor() {
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
    protected void begin() {
        super.begin();
        renderer.setProjectionMatrix(camSys.getGameCamera().combined);
        renderer.begin();
    }

    @Override
    protected void end() {
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
