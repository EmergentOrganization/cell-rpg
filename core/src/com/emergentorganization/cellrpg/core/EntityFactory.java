package com.emergentorganization.cellrpg.core;

import com.artemis.Archetype;
import com.artemis.ArchetypeBuilder;
import com.artemis.Entity;
import com.badlogic.gdx.physics.box2d.BodyDef;
import com.badlogic.gdx.physics.box2d.FixtureDef;
import com.emergentorganization.cellrpg.components.*;
import com.emergentorganization.cellrpg.managers.BodyManager;

/**
 * Created by brian on 10/28/15.
 */
public class EntityFactory {
    public static float SCALE_BOX_TO_WORLD = 40f;
    public static float SCALE_WORLD_TO_BOX = 0.025f;
    private final com.artemis.World world;

    public Archetype base;
    public Archetype object;
    public Archetype collidable;
    public Archetype physical;
    public Archetype character;
    private Archetype player;

    public EntityFactory(com.artemis.World world) {
        this.world = world;
        base = new ArchetypeBuilder().add(Position.class).build(world);
        object = new ArchetypeBuilder(base).add(Visual.class).add(Rotation.class).add(Scale.class).add(Bounds.class).build(world);
        collidable = new ArchetypeBuilder(object).add(Velocity.class).add(PhysicsBody.class).build(world);
        character = new ArchetypeBuilder(collidable).add(Health.class).build(world);
        player = new ArchetypeBuilder(character).add(Input.class).add(CameraFollow.class).build(world);
    }

    public int createPlayer(float x, float y) {
        final Entity player = world.createEntity(this.player);

        player.getComponent(Visual.class).setAnimation("char-player");
        player.getComponent(Bounds.class).set(40, 36);
        player.getComponent(Position.class).position.set(x, y);
        player.getComponent(Scale.class).scale = SCALE_WORLD_TO_BOX; // player ends up being 1 meter in size

        BodyDef bDef = new BodyDef();
        bDef.allowSleep = true;
        bDef.type = BodyDef.BodyType.DynamicBody;
        bDef.fixedRotation = true;
        bDef.position.set(x, y);
        FixtureDef fDef = new FixtureDef();
        fDef.density = 1.0f;
        fDef.friction = 0.3f;
        fDef.restitution = 0.1f;
        world.getSystem(BodyManager.class).createBody(player.getId(), "char-player", bDef, fDef);

        Input ic = player.getComponent(Input.class);
        ic.speed = 2f; // 2 meters per sec // a dedicated component?

        return player.getId();
    }
}
