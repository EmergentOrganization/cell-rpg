package com.emergentorganization.cellrpg.managers;

import com.artemis.BaseSystem;
import com.artemis.ComponentMapper;
import com.artemis.annotations.Wire;
import com.badlogic.gdx.physics.box2d.Body;
import com.badlogic.gdx.physics.box2d.BodyDef;
import com.badlogic.gdx.physics.box2d.FixtureDef;
import com.badlogic.gdx.physics.box2d.World;
import com.emergentorganization.cellrpg.PixelonTransmission;
import com.emergentorganization.cellrpg.components.Scale;
import com.emergentorganization.cellrpg.tools.physics.BodyEditorLoader;

import java.util.HashMap;

/**
 * Created by brian on 10/29/15.
 */
@Wire
public class BodyManager extends BaseSystem {
    private final BodyEditorLoader bodyLoader;
    private ComponentMapper<Scale> sm;

    private final World physWorld;
    private HashMap<Integer, Body> bodies;

    public BodyManager(World world) {
        this.physWorld = world;
        this.bodyLoader = PixelonTransmission.fetch().getBodyLoader();
        bodies = new HashMap<Integer, Body>();
    }

    public void createBody(int entityId, String colliderId, BodyDef bd, FixtureDef fd) {
        Body body = physWorld.createBody(bd);
        bodyLoader.attachFixture(body, colliderId, fd, sm.get(entityId).scale);
        bodies.put(entityId, body);
    }

    public Body getBody(int entityId) {
        return bodies.get(entityId);
    }

    @Override
    protected void processSystem() {

    }
}
