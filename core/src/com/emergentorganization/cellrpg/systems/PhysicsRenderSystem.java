package com.emergentorganization.cellrpg.systems;

import com.artemis.BaseSystem;
import com.artemis.annotations.Wire;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.physics.box2d.Box2DDebugRenderer;
import com.badlogic.gdx.physics.box2d.World;

/**
 * Created by brian on 10/29/15.
 */
@Wire
public class PhysicsRenderSystem extends BaseSystem {
    private final Box2DDebugRenderer renderer;
    private final SpriteBatch batch;
    private final World physWorld;
    private CameraSystem cs;


    public PhysicsRenderSystem(SpriteBatch batch, World world) {
        this.physWorld = world;
        this.batch = batch;
        renderer = new Box2DDebugRenderer(true, true, true, true, true, true);
    }

    @Override
    protected void processSystem() {
        batch.begin();
        renderer.render(physWorld, cs.getCam().combined);
        batch.end();
    }
}
