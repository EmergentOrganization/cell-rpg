package com.emergentorganization.cellrpg.core;

import com.artemis.Archetype;
import com.artemis.ArchetypeBuilder;
import com.artemis.Entity;
import com.artemis.World;
import com.artemis.managers.TagManager;
import com.badlogic.gdx.graphics.g2d.Animation;
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
        base = new ArchetypeBuilder().add(Position.class).add(Name.class).build(world);
        object = new ArchetypeBuilder(base).add(Visual.class).add(Rotation.class).add(Scale.class).add(Bounds.class).add(Velocity.class).build(world);
        collidable = new ArchetypeBuilder(object).add(PhysicsBody.class).build(world);
        character = new ArchetypeBuilder(collidable).add(Health.class).build(world);
        player = new ArchetypeBuilder(character).add(Input.class).add(CameraFollow.class).build(world);
    }

    public int createPlayer(float x, float y) {
        final Entity player = world.createEntity(this.player);
        Name name = player.getComponent(Name.class);
        name.friendlyName = "Player";
        name.internalID = EntityIDs.PLAYER;
        world.getSystem(TagManager.class).register("player", player);

        Visual v = player.getComponent(Visual.class);
        v.setAnimation(EntityIDs.PLAYER);
        v.index = RenderIndex.PLAYER;

        Animation animation = world.getSystem(AssetManager.class).defineAnimation(EntityIDs.PLAYER, 0.2f,
                new String[]{"game/char-player/0",
                        "game/char-player/1",
                        "game/char-player/2",
                        "game/char-player/3",
                        "game/char-player/4",
                        "game/char-player/5",
                        "game/char-player/6",
                        "game/char-player/7",
                        "game/char-player/8",
                        "game/char-player/9"}, Animation.PlayMode.LOOP);

        player.getComponent(Bounds.class).setFromRegion(
                animation.getKeyFrames()[0]
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
        Name name = bullet.getComponent(Name.class);
        name.friendlyName = "Bullet";
        name.internalID = EntityIDs.BULLET;
        final float speed = 10f;

        Visual v = bullet.getComponent(Visual.class);
        v.index = RenderIndex.BULLET;
        v.setTexture("game/" + EntityIDs.BULLET);
        bullet.getComponent(Bounds.class).setFromRegion(
                world.getSystem(AssetManager.class).getRegion("game/" + EntityIDs.BULLET)
        );
        Vector2 position = bullet.getComponent(Position.class).position;
        position.set(pos);
        bullet.getComponent(Scale.class).scale = SCALE_WORLD_TO_BOX;
        Vector2 velocity = bullet.getComponent(Velocity.class).velocity;
        velocity.set(dir).scl(speed);

        return bullet.getId();
    }

    public int createCivOneBlinker(float x, float y) {
        final Entity civ = world.createEntity(character);
        Name name = civ.getComponent(Name.class);
        name.friendlyName = "Civilian";
        name.internalID = EntityIDs.CIV_ONE_BLINKER;

        Visual v = civ.getComponent(Visual.class);
        v.index = RenderIndex.NPC;
        v.setAnimation(EntityIDs.CIV_ONE_BLINKER);

        Animation animation = world.getSystem(AssetManager.class).defineAnimation(EntityIDs.CIV_ONE_BLINKER, 0.2f,
                new String[]{
                        "game/char-civ1-blinker/0",
                        "game/char-civ1-blinker/1"
                }, Animation.PlayMode.LOOP);

        civ.getComponent(Bounds.class).setFromRegion(
                world.getSystem(AssetManager.class).getAnimation(EntityIDs.CIV_ONE_BLINKER).getKeyFrames()[0]
        );
        civ.getComponent(Position.class).position.set(x, y);
        civ.getComponent(Scale.class).scale = SCALE_WORLD_TO_BOX; // civ ends up being 1 meter in size

        BodyDef bDef = new BodyDef();
        bDef.allowSleep = true;
        bDef.type = BodyDef.BodyType.DynamicBody;
        bDef.fixedRotation = true;
        bDef.position.set(x, y);
        FixtureDef fDef = new FixtureDef();
        fDef.density = 1.0f;
        fDef.friction = 0.3f;
        fDef.restitution = 0.1f;
        world.getSystem(BodyManager.class).createBody(civ.getId(), EntityIDs.CIV_ONE_BLINKER, bDef, fDef);

        return civ.getId();
    }

    public int createBuildingLargeOne(Vector2 pos, float angleDeg) {
        final String texPrefix = "game/buildings/";
        Entity bldg = world.createEntity(collidable);
        Name name = bldg.getComponent(Name.class);
        name.friendlyName = "Large Building";
        name.internalID = EntityIDs.BUILDING_LARGE_ONE;

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
        Name name = bldg.getComponent(Name.class);
        name.friendlyName = "Round Building";
        name.internalID = EntityIDs.BUILDING_ROUND_ONE;

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
        Name name = bldg.getComponent(Name.class);
        name.friendlyName = "Rift1"; // TODO: Come up with a more ui-friendly name
        name.internalID = EntityIDs.RIFT_ONE;

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
        Name name = bldg.getComponent(Name.class);
        name.friendlyName = "Rift2"; // TODO: Come up with a more ui-friendly name
        name.internalID = EntityIDs.RIFT_TWO;

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
        Name name = bldg.getComponent(Name.class);
        name.friendlyName = "Vyroid Beacon";
        name.internalID = EntityIDs.VYROID_BEACON;

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
        Name name = bg.getComponent(Name.class);
        name.friendlyName = "The Edge Background";
        name.internalID = EntityIDs.THE_EDGE;

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

    public int createEntityByID(String id, Vector2 pos, float angleDeg) {
        if (id.equals(EntityIDs.BUILDING_LARGE_ONE)) {
            return createBuildingLargeOne(pos, angleDeg);
        } else if (id.equals(EntityIDs.BUILDING_ROUND_ONE)) {
            return createBuildingRoundOne(pos, angleDeg);
        } else if (id.equals(EntityIDs.BULLET)) {
            return createBullet(pos, new Vector2().rotate(angleDeg));
        } else if (id.equals(EntityIDs.CIV_ONE_BLINKER)) {
            return createCivOneBlinker(pos.x, pos.y);
        } else if (id.equals(EntityIDs.PLAYER)) {
            return createPlayer(pos.x, pos.y);
        } else if (id.equals(EntityIDs.RIFT_ONE)) {
            return createRiftOne(pos, angleDeg);
        } else if (id.equals(EntityIDs.RIFT_TWO)) {
            return createRiftTwo(pos, angleDeg);
        } else if (id.equals(EntityIDs.THE_EDGE)) {
            return createBackgroundTheEdge(pos);
        } else if (id.equals(EntityIDs.VYROID_BEACON)) {
            return createVyroidBeacon(pos, angleDeg);
        } else {
            throw new RuntimeException("Error: Could not find entity by ID '" + id + "'");
        }
    }
}
