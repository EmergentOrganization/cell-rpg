package com.emergentorganization.cellrpg.core;

import com.artemis.Archetype;
import com.artemis.ArchetypeBuilder;
import com.artemis.Entity;
import com.artemis.World;
import com.badlogic.gdx.math.MathUtils;
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

        Visual v = player.getComponent(Visual.class);
        v.setAnimation(EntityIDs.PLAYER);
        v.index = RenderIndex.PLAYER;
        player.getComponent(Bounds.class).setFromRegion(
                world.getSystem(AssetManager.class).getAnimation(EntityIDs.PLAYER).getKeyFrames()[0]
        );
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
        world.getSystem(BodyManager.class).createBody(player.getId(), EntityIDs.PLAYER, bDef, fDef);

        Input ic = player.getComponent(Input.class);
        ic.speed = 2f; // 2 meters per sec // a dedicated component?

        return player.getId();
    }

    public int createBullet(Vector2 pos, Vector2 dir) {
        Entity bullet = world.createEntity(object);
        final float speed = 10f;

        Visual v = bullet.getComponent(Visual.class);
        v.index = RenderIndex.BULLET;
        v.setTexture(EntityIDs.BULLET);
        bullet.getComponent(Bounds.class).setFromRegion(world.getSystem(AssetManager.class).getRegion(EntityIDs.BULLET));
        Vector2 position = bullet.getComponent(Position.class).position;
        position.set(pos);
        bullet.getComponent(Scale.class).scale = SCALE_WORLD_TO_BOX;
        Vector2 velocity = bullet.getComponent(Velocity.class).velocity;
        velocity.set(dir).scl(speed);

        return bullet.getId();
    }

    public int createCivOneBlinker(float x, float y) {
        final Entity civ = world.createEntity(character);

        Visual v = civ.getComponent(Visual.class);
        v.index = RenderIndex.NPC;
        v.setAnimation(EntityIDs.CIV_ONE_BLINKER);
        civ.getComponent(Bounds.class).setFromRegion(
                world.getSystem(AssetManager.class).getAnimation(EntityIDs.CIV_ONE_BLINKER).getKeyFrames()[0]
        );
        civ.getComponent(Position.class).position.set(x, y);
        civ.getComponent(Scale.class).scale = SCALE_WORLD_TO_BOX; // civ ends up being 1 meter in size

        BodyDef bDef = new BodyDef();
        bDef.allowSleep = true;
        bDef.type = BodyDef.BodyType.KinematicBody;
        bDef.fixedRotation = true;
        bDef.position.set(x, y);
        FixtureDef fDef = new FixtureDef();
        fDef.density = 1.0f;
        fDef.friction = 0.3f;
        fDef.restitution = 0.1f;
        world.getSystem(BodyManager.class).createBody(civ.getId(), EntityIDs.CIV_ONE_BLINKER, bDef, fDef);

        Input ic = civ.getComponent(Input.class);
        ic.speed = 2f; // 2 meters per sec // a dedicated component?

        return civ.getId();
    }

    public int createBuildingLargeOne(Vector2 pos, float angleDeg) {
        final String texPrefix = "game/buildings/";
        Entity bldg = world.createEntity(collidable);

        Visual v = bldg.getComponent(Visual.class);
        v.index = RenderIndex.BUILDING;
        v.setTexture(texPrefix + EntityIDs.BUILDING_LARGE_ONE);
        bldg.getComponent(Bounds.class).setFromRegion(
                world.getSystem(AssetManager.class).getRegion(texPrefix + EntityIDs.BUILDING_LARGE_ONE)
        );
        bldg.getComponent(Position.class).position.set(pos);
        bldg.getComponent(Rotation.class).angle = angleDeg;
        bldg.getComponent(Scale.class).scale = SCALE_WORLD_TO_BOX;

        BodyDef bDef = new BodyDef();
        bDef.allowSleep = true;
        bDef.type = BodyDef.BodyType.StaticBody;
        bDef.fixedRotation = true;
        bDef.position.set(pos);
        bDef.angle = MathUtils.degreesToRadians * angleDeg;
        FixtureDef fDef = new FixtureDef();
        fDef.density = 1.0f;
        fDef.friction = 0.7f;
        fDef.restitution = 0.1f;
        world.getSystem(BodyManager.class).createBody(bldg.getId(), EntityIDs.BUILDING_LARGE_ONE, bDef, fDef);

        return bldg.getId();
    }

    public int createBuildingRoundOne(Vector2 pos, float angleDeg) {
        // TODO: Tie GridSeed component to this somehow
        final String texPrefix = "game/buildings/";
        Entity bldg = world.createEntity(collidable);

        Visual v = bldg.getComponent(Visual.class);
        v.index = RenderIndex.BUILDING;
        v.setTexture(texPrefix + EntityIDs.BUILDING_ROUND_ONE);
        bldg.getComponent(Bounds.class).setFromRegion(
                world.getSystem(AssetManager.class).getRegion(texPrefix + EntityIDs.BUILDING_ROUND_ONE)
        );
        bldg.getComponent(Position.class).position.set(pos);
        bldg.getComponent(Rotation.class).angle = angleDeg;
        bldg.getComponent(Scale.class).scale = SCALE_WORLD_TO_BOX;

        BodyDef bDef = new BodyDef();
        bDef.allowSleep = true;
        bDef.type = BodyDef.BodyType.StaticBody;
        bDef.fixedRotation = true;
        bDef.position.set(pos);
        bDef.angle = MathUtils.degreesToRadians * angleDeg;
        FixtureDef fDef = new FixtureDef();
        fDef.density = 1.0f;
        fDef.friction = 0.7f;
        fDef.restitution = 0.1f;
        world.getSystem(BodyManager.class).createBody(bldg.getId(), EntityIDs.BUILDING_ROUND_ONE, bDef, fDef);

        return bldg.getId();
    }

    public int createRiftOne(Vector2 pos, float angleDeg) {
        final String texPrefix = "game/environment/";
        Entity bldg = world.createEntity(collidable);

        Visual v = bldg.getComponent(Visual.class);
        v.index = RenderIndex.BUILDING;
        v.setTexture(texPrefix + EntityIDs.RIFT_ONE);
        bldg.getComponent(Bounds.class).setFromRegion(
                world.getSystem(AssetManager.class).getRegion(texPrefix + EntityIDs.RIFT_ONE)
        );
        bldg.getComponent(Position.class).position.set(pos);
        bldg.getComponent(Rotation.class).angle = angleDeg;
        bldg.getComponent(Scale.class).scale = SCALE_WORLD_TO_BOX;

        BodyDef bDef = new BodyDef();
        bDef.allowSleep = true;
        bDef.type = BodyDef.BodyType.StaticBody;
        bDef.fixedRotation = true;
        bDef.position.set(pos);
        bDef.angle = MathUtils.degreesToRadians * angleDeg;
        FixtureDef fDef = new FixtureDef();
        fDef.density = 1.0f;
        fDef.friction = 0.7f;
        fDef.restitution = 0.1f;
        world.getSystem(BodyManager.class).createBody(bldg.getId(), EntityIDs.RIFT_ONE, bDef, fDef);

        return bldg.getId();
    }

    public int createRiftTwo(Vector2 pos, float angleDeg) {
        final String texPrefix = "game/environment/";
        Entity bldg = world.createEntity(collidable);

        Visual v = bldg.getComponent(Visual.class);
        v.index = RenderIndex.BUILDING;
        v.setTexture(texPrefix + EntityIDs.RIFT_TWO);
        bldg.getComponent(Bounds.class).setFromRegion(
                world.getSystem(AssetManager.class).getRegion(texPrefix + EntityIDs.RIFT_TWO)
        );
        bldg.getComponent(Position.class).position.set(pos);
        bldg.getComponent(Rotation.class).angle = angleDeg;
        bldg.getComponent(Scale.class).scale = SCALE_WORLD_TO_BOX;

        BodyDef bDef = new BodyDef();
        bDef.allowSleep = true;
        bDef.type = BodyDef.BodyType.StaticBody;
        bDef.fixedRotation = true;
        bDef.position.set(pos);
        bDef.angle = MathUtils.degreesToRadians * angleDeg;
        FixtureDef fDef = new FixtureDef();
        fDef.density = 1.0f;
        fDef.friction = 0.7f;
        fDef.restitution = 0.1f;
        world.getSystem(BodyManager.class).createBody(bldg.getId(), EntityIDs.RIFT_TWO, bDef, fDef);

        return bldg.getId();
    }

    public int createVyroidBeacon(Vector2 pos, float angleDeg) {
        final String texPrefix = "game/buildings/";
        Entity bldg = world.createEntity(collidable);

        Visual v = bldg.getComponent(Visual.class);
        v.index = RenderIndex.BUILDING;
        v.setTexture(texPrefix + EntityIDs.VYROID_BEACON);
        bldg.getComponent(Bounds.class).setFromRegion(
                world.getSystem(AssetManager.class).getRegion(texPrefix + EntityIDs.VYROID_BEACON)
        );
        bldg.getComponent(Position.class).position.set(pos);
        bldg.getComponent(Rotation.class).angle = angleDeg;
        bldg.getComponent(Scale.class).scale = SCALE_WORLD_TO_BOX;

        BodyDef bDef = new BodyDef();
        bDef.allowSleep = true;
        bDef.type = BodyDef.BodyType.StaticBody;
        bDef.fixedRotation = true;
        bDef.position.set(pos);
        bDef.angle = MathUtils.degreesToRadians * angleDeg;
        FixtureDef fDef = new FixtureDef();
        fDef.density = 1.0f;
        fDef.friction = 0.7f;
        fDef.restitution = 0.1f;
        world.getSystem(BodyManager.class).createBody(bldg.getId(), EntityIDs.VYROID_BEACON, bDef, fDef);

        return bldg.getId();
    }

    public int createBackgroundTheEdge(Vector2 pos) {
        final String texPrefix = "game/environment/";
        Entity bg = world.createEntity(object);

        Visual v = bg.getComponent(Visual.class);
        v.index = RenderIndex.BACKGROUND;
        bg.getComponent(Visual.class).setTexture(texPrefix + EntityIDs.THE_EDGE);
        bg.getComponent(Bounds.class).setFromRegion(
                world.getSystem(AssetManager.class).getRegion(texPrefix + EntityIDs.THE_EDGE)
        );
        bg.getComponent(Position.class).position.set(pos);
        bg.getComponent(Scale.class).scale = SCALE_WORLD_TO_BOX;

        return bg.getId();
    }
}
