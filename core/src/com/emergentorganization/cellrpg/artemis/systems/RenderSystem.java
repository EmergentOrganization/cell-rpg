package com.emergentorganization.cellrpg.artemis.systems;

import com.artemis.Aspect;
import com.artemis.ComponentMapper;
import com.artemis.annotations.Wire;
import com.artemis.systems.IteratingSystem;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.emergentorganization.cellrpg.artemis.components.Position;
import com.emergentorganization.cellrpg.artemis.components.Rotation;
import com.emergentorganization.cellrpg.artemis.components.Scale;
import com.emergentorganization.cellrpg.artemis.components.Visual;
import com.emergentorganization.cellrpg.artemis.managers.AssetManager;

/**
 * Created by brian on 10/28/15.
 */
@Wire
public class RenderSystem extends IteratingSystem {

    private ComponentMapper<Visual> vm;
    private ComponentMapper<Position> pm;
    private ComponentMapper<Scale> sm;
    private ComponentMapper<Rotation> rm;

    private AssetManager assetManager; // being a registered system, it is injected on runtime

    private SpriteBatch batch;

    public RenderSystem(SpriteBatch batch) {
        super(Aspect.all(Position.class, Rotation.class, Scale.class, Visual.class));

        this.batch = batch;
    }

    @Override
    protected void begin() {
        batch.begin();
    }

    @Override
    protected void end() {
        batch.end();
    }

    @Override
    protected void process(int entityId) {
        Visual v = vm.get(entityId);
        Position p = pm.get(entityId);
        Scale s = sm.get(entityId);
        Rotation r = rm.get(entityId);

        Texture t = assetManager.getTexture(v.id);
        if(t != null)
        {
            // temporary draw call
            batch.draw(t, p.position.x, p.position.y, 0, 0, t.getWidth(), t.getHeight(), s.scale, s.scale, r.angle, 0, 0, t.getWidth(), t.getHeight(), false, false );
        }
    }
}
