package com.emergentorganization.cellrpg.managers;

import com.artemis.Aspect;
import com.artemis.BaseEntitySystem;
import com.artemis.ComponentMapper;
import com.artemis.annotations.Wire;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.physics.box2d.*;
import com.emergentorganization.cellrpg.components.Bounds;
import com.emergentorganization.cellrpg.components.PhysicsBody;
import com.emergentorganization.cellrpg.components.Visual;
import com.emergentorganization.cellrpg.tools.physics.BodyEditorLoader;

import java.util.HashMap;

/**
 * Created by brian on 10/29/15.
 */
@Wire
public class BodyManager extends BaseEntitySystem {
    private final BodyEditorLoader bodyLoader;
    private final World physWorld;
    private AssetManager assetManager;
    private ComponentMapper<Visual> vm;
    private ComponentMapper<Bounds> bm;
    private ComponentMapper<PhysicsBody> pm;
    private HashMap<Integer, Body> bodies;

    public BodyManager(World world, BodyEditorLoader bodyLoader) {
        super(Aspect.all(PhysicsBody.class));
        this.physWorld = world;
        this.bodyLoader = bodyLoader;
        bodies = new HashMap<Integer, Body>();
    }

    public Body createBody(int entityId, String colliderId, BodyDef bd, FixtureDef fd) {
        Body body = createEmptyBody(entityId, bd);
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

    private Body createEmptyBody(int entityId, BodyDef bd) {
        if (!pm.has(entityId))
            throw new RuntimeException("Cannot create a body for an entity without a PhysicsBody component");
        Body body = physWorld.createBody(bd);
        body.setUserData(entityId);
        return body;
    }

    public Body createBoundsBody(int entityId, BodyDef bd, FixtureDef fd) {
        Body body = createEmptyBody(entityId, bd);
        Bounds b = bm.get(entityId);
        PolygonShape rect = new PolygonShape();
        rect.setAsBox(b.width * 0.5f, b.height * 0.5f);
        fd.shape = rect;
        body.createFixture(fd);
        bodies.put(entityId, body);
        return body;
    }

    public Body updateBoundsBody(int entityId) {
        if (physWorld.isLocked())
            throw new RuntimeException("ERROR: Cannot update bounds body in physics loop");

        Body body = bodies.get(entityId);
        for (Fixture fixture : body.getFixtureList()) {
            body.destroyFixture(fixture);
        }

        Bounds b = bm.get(entityId);
        PolygonShape polygonShape = new PolygonShape();
        polygonShape.setAsBox(b.width * 0.5f, b.height * 0.5f);
        body.createFixture(polygonShape, 1.0f);
        return body;
    }

    public Body getBody(int entityId) {
        return bodies.get(entityId);
    }

    @Override
    protected void processSystem() {}

    @Override
    protected void removed(int entityId) {
        removeBody(entityId);
    }

    public void removeBody(int entityId) {
        Body body = getBody(entityId);
        bodies.remove(entityId);
        physWorld.destroyBody(body);
    }

    public HashMap<Integer, Body> getBodies() {
        return bodies;
    }
}
