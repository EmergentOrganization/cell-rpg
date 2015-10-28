package com.emergentorganization.cellrpg.artemis.core;

import com.artemis.Archetype;
import com.artemis.ArchetypeBuilder;
import com.artemis.Entity;
import com.badlogic.gdx.math.Circle;
import com.badlogic.gdx.math.Polygon;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.physics.box2d.Body;
import com.badlogic.gdx.physics.box2d.BodyDef;
import com.badlogic.gdx.physics.box2d.FixtureDef;
import com.badlogic.gdx.physics.box2d.World;
import com.emergentorganization.cellrpg.artemis.components.*;
import com.emergentorganization.cellrpg.tools.physics.BodyEditorLoader;

/**
 * Created by brian on 10/28/15.
 */
public class EntityFactory {
    public static float SCALE_BOX_TO_WORLD = 32f;
    public static float SCALE_WORLD_TO_BOX = 0.03125f;
    private final com.artemis.World world;
    private BodyEditorLoader bodyEditorLoader;
    public Archetype base;
    public Archetype object;
    public Archetype collidable;
    public Archetype physical;

    public EntityFactory(com.artemis.World world, BodyEditorLoader bodyEditorLoader) {
        this.world = world;
        this.bodyEditorLoader = bodyEditorLoader;
        base = new ArchetypeBuilder().add(Position.class).build(world);
        object = new ArchetypeBuilder(base).add(Texture.class).add(Rotation.class).build(world);
        collidable = new ArchetypeBuilder(object).add(Collider.class).build(world);
        physical = new ArchetypeBuilder(collidable).add(Velocity.class).build(world);
    }

    public Entity createPlayer(World physWorld, float x, float y) {
        final Entity player = world.createEntity(physical);
        player.getComponent(Position.class).position = new Vector2(x, y);

        //player.getComponent(Texture.class).textureRegion = // TODO

        BodyDef bDef = new BodyDef();
        bDef.allowSleep = false;
        bDef.type = BodyDef.BodyType.DynamicBody;
        bDef.fixedRotation = true;
        bDef.position.set(x, y);
        Body body = physWorld.createBody(bDef);
        FixtureDef fDef = new FixtureDef();
        fDef.density = 1.0f;
        fDef.friction = 0.3f;
        fDef.restitution = 0.1f;
        //bodyEditorLoader.attachFixture(body, "char-player", fDef, /*Player tex * world_to_box*/);
        player.getComponent(Collider.class).body = body;

        return player;
    }
}
