package com.emergentorganization.cellrpg.managers;

import com.artemis.Aspect;
import com.artemis.BaseEntitySystem;
import com.artemis.ComponentMapper;
import com.artemis.annotations.Wire;
import com.badlogic.gdx.graphics.g2d.Batch;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.math.Vector3;
import com.badlogic.gdx.physics.box2d.*;
import com.emergentorganization.cellrpg.PixelonTransmission;
import com.emergentorganization.cellrpg.components.Bounds;
import com.emergentorganization.cellrpg.components.PhysicsBody;
import com.emergentorganization.cellrpg.components.Visual;
import com.emergentorganization.cellrpg.systems.CameraSystem;
import com.emergentorganization.cellrpg.tools.physics.BodyEditorLoader;

import javax.annotation.Nullable;
import java.util.HashMap;

/**
 * Created by brian on 10/29/15.
 */
@Wire
public class PhysicsSystem extends BaseEntitySystem {
    private CameraSystem cs;

    private final BodyEditorLoader bodyLoader;
    private final World physWorld;
    private final Box2DDebugRenderer renderer;
    private Batch batch;
    private AssetManager assetManager;
    private ComponentMapper<Visual> vm;
    private ComponentMapper<Bounds> bm;
    private ComponentMapper<PhysicsBody> pm;
    private HashMap<Integer, Body> bodies;
    private boolean shouldRender = false;

    public PhysicsSystem(BodyEditorLoader bodyLoader, @Nullable Batch batch) {
        super(Aspect.all(PhysicsBody.class));
        this.physWorld = new World(Vector2.Zero, true);
        this.batch = batch;
        renderer = new Box2DDebugRenderer(true, true, true, true, true, true);
        this.bodyLoader = bodyLoader;
        bodies = new HashMap<Integer, Body>();
    }

    public Body createBody(int entityId, String colliderId, BodyDef bd, FixtureDef fd) {
        if (!pm.has(entityId))
            throw new RuntimeException("Cannot create a body for an entity without a PhysicsBody component");
        Body body = physWorld.createBody(bd);
        body.setUserData(entityId);
        Bounds b = bm.get(entityId);
        float scale;
        if (b.height < b.width)
            scale = (Math.max(b.width, b.height) / Math.min(b.width, b.height)) * Math.min(b.width, b.height);
        else
            scale = Math.min(b.width, b.height);
        bodyLoader.attachFixture(body, colliderId, fd, scale);
        bodies.put(entityId, body);
        return body;
    }

    public Body getBody(int entityId) {
        return bodies.get(entityId);
    }

    @Override
    protected void processSystem() {
        if (shouldRender) {
            batch.begin();
            renderer.render(physWorld, cs.getGameCamera().combined);
            batch.end();
        }

        physWorld.step(PixelonTransmission.PHYSICS_TIMESTEP, 6, 2);
    }

    @Override
    protected void removed(int entityId) {
        removeBody(entityId);
    }

    public void removeBody(int entityId) {
        Body body = getBody(entityId);
        bodies.remove(entityId);
        physWorld.destroyBody(body);
    }

    @Override
    protected void dispose() {
        physWorld.dispose();
    }

    public void setContactListener(ContactListener listener) {
        physWorld.setContactListener(listener);
    }

    public HashMap<Integer, Body> getBodies() {
        return bodies;
    }

    public void shouldRender(boolean render) {
        if (batch != null)
            this.shouldRender = render;
        else
            System.out.println("Cannot enable physics rendering without a specifying a batch in the constructor!");
    }
}