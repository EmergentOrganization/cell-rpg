package com.emergentorganization.cellrpg.managers;

import com.artemis.BaseSystem;
import com.artemis.ComponentMapper;
import com.artemis.annotations.Wire;
import com.badlogic.gdx.physics.box2d.Body;
import com.badlogic.gdx.physics.box2d.BodyDef;
import com.badlogic.gdx.physics.box2d.FixtureDef;
import com.badlogic.gdx.physics.box2d.World;
import com.emergentorganization.cellrpg.components.Bounds;
import com.emergentorganization.cellrpg.components.Visual;
import com.emergentorganization.cellrpg.core.EntityFactory;
import com.emergentorganization.cellrpg.tools.physics.BodyEditorLoader;

import java.util.HashMap;

/**
 * Created by brian on 10/29/15.
 */
@Wire
public class BodyManager extends BaseSystem {
    private final BodyEditorLoader bodyLoader;
    private final World physWorld;
    private AssetManager assetManager;
    private ComponentMapper<Visual> vm;
    private ComponentMapper<Bounds> bm;
    private HashMap<Integer, Body> bodies;

    public BodyManager(World world, BodyEditorLoader bodyLoader) {
        this.physWorld = world;
        this.bodyLoader = bodyLoader;
        bodies = new HashMap<Integer, Body>();
    }

    public void createBody(int entityId, String colliderId, BodyDef bd, FixtureDef fd) {
        Body body = physWorld.createBody(bd);
        body.setUserData(entityId);
        Bounds b = bm.get(entityId);
        bodyLoader.attachFixture(body, colliderId, fd, Math.min(b.width, b.height));
        bodies.put(entityId, body);
    }

    public Body getBody(int entityId) {
        return bodies.get(entityId);
    }

    @Override
    protected void processSystem() {

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
