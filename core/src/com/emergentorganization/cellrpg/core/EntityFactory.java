package com.emergentorganization.cellrpg.core;

import com.artemis.Archetype;
import com.artemis.ArchetypeBuilder;
import com.artemis.Entity;
import com.artemis.World;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.physics.box2d.BodyDef;
import com.badlogic.gdx.physics.box2d.FixtureDef;
import com.emergentorganization.cellrpg.components.*;
import com.emergentorganization.cellrpg.managers.AssetManager;
import com.emergentorganization.cellrpg.managers.BodyManager;

/**
 * Created by brian on 10/28/15.
 */
public class EntityFactory {
    public static float SCALE_BOX_TO_WORLD = 40f;
    public static float SCALE_WORLD_TO_BOX = 0.025f;

    public static float BULLET_MAX_DIST = 20f;

    private World world;

    public Archetype base;
    public Archetype object;
    public Archetype collidable;
    public Archetype physical;
    public Archetype character;
    private Archetype player;

    public void initialize(World world) {
        this.world = world;
        base = new ArchetypeBuilder().add(Position.class).build(world);
        object = new ArchetypeBuilder(base).add(Visual.class).add(Rotation.class).add(Scale.class).add(Bounds.class).add(Velocity.class).build(world);
        collidable = new ArchetypeBuilder(object).add(PhysicsBody.class).build(world);
        character = new ArchetypeBuilder(collidable).add(Health.class).build(world);
        player = new ArchetypeBuilder(character).add(Input.class).add(CameraFollow.class).build(world);
    }

    public int createPlayer(float x, float y) {
        final Entity player = world.createEntity(this.player);
        final String playerID = "char-player";

        player.getComponent(Visual.class).setAnimation(playerID);
        player.getComponent(Bounds.class).setFromRegion(world.getSystem(AssetManager.class).getAnimation(playerID).getKeyFrames()[0]);
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
        world.getSystem(BodyManager.class).createBody(player.getId(), playerID, bDef, fDef);

        Input ic = player.getComponent(Input.class);
        ic.speed = 2f; // 2 meters per sec // a dedicated component?

        return player.getId();
    }

    public int createBullet(Vector2 pos, Vector2 dir) {
        Entity bullet = world.createEntity(object);
        final float speed = 10f;

        bullet.getComponent(Visual.class).setTexture(EntityIDs.BULLET);
        bullet.getComponent(Bounds.class).setFromRegion(world.getSystem(AssetManager.class).getRegion(EntityIDs.BULLET));
        Vector2 position = bullet.getComponent(Position.class).position;
        position.set(pos);
        bullet.getComponent(Scale.class).scale = SCALE_WORLD_TO_BOX;
        Vector2 velocity = bullet.getComponent(Velocity.class).velocity;
        velocity.set(dir).scl(speed);
        System.out.println(position + " " + velocity);

        return bullet.getId();
    }
}
