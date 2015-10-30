package com.emergentorganization.cellrpg.managers;

import com.artemis.BaseSystem;
import com.artemis.ComponentMapper;
import com.artemis.annotations.Wire;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.physics.box2d.Body;
import com.badlogic.gdx.physics.box2d.BodyDef;
import com.badlogic.gdx.physics.box2d.FixtureDef;
import com.badlogic.gdx.physics.box2d.World;
import com.emergentorganization.cellrpg.PixelonTransmission;
import com.emergentorganization.cellrpg.components.Bounds;
import com.emergentorganization.cellrpg.components.Scale;
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
    private AssetManager assetManager;
    private ComponentMapper<Visual> vm;
    private ComponentMapper<Bounds> bm;

    private final World physWorld;
    private HashMap<Integer, Body> bodies;

    public BodyManager(World world) {
        this.physWorld = world;
        this.bodyLoader = PixelonTransmission.fetch().getBodyLoader();
        bodies = new HashMap<Integer, Body>();
    }

    public void createBody(int entityId, String colliderId, BodyDef bd, FixtureDef fd) {
        Body body = physWorld.createBody(bd);
        Bounds b = bm.get(entityId);
        bodyLoader.attachFixture(body, colliderId, fd,
                (Math.max(b.width, b.height) * EntityFactory.SCALE_WORLD_TO_BOX) * 10
        );
        bodies.put(entityId, body);
    }

    public Body getBody(int entityId) {
        return bodies.get(entityId);
    }

    @Override
    protected void processSystem() {

    }
}
