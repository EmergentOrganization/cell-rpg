package com.emergentorganization.cellrpg.managers;

import com.artemis.Aspect;
import com.artemis.BaseEntitySystem;
import com.artemis.ComponentMapper;
import com.artemis.annotations.Wire;
import com.badlogic.gdx.physics.box2d.Body;
import com.badlogic.gdx.physics.box2d.BodyDef;
import com.badlogic.gdx.physics.box2d.FixtureDef;
import com.badlogic.gdx.physics.box2d.World;
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

    public void createBody(int entityId, String colliderId, BodyDef bd, FixtureDef fd) {
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
