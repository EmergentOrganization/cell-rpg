package com.emergentorganization.cellrpg.core.entityfactory;

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
import com.emergentorganization.cellrpg.core.EntityIDs;
import com.emergentorganization.cellrpg.core.RenderIndex;
import com.emergentorganization.cellrpg.managers.AssetManager;
import com.emergentorganization.cellrpg.managers.BodyManager;
import com.emergentorganization.cellrpg.tools.Resources;

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
        object = new ArchetypeBuilder(base).add(Visual.class).add(SFX.class)
                .add(Rotation.class).add(Scale.class).add(Bounds.class).add(Velocity.class).build(world);
        collidable = new ArchetypeBuilder(object).add(PhysicsBody.class).build(world);
        character = new ArchetypeBuilder(collidable).add(Health.class).build(world);
        player = new ArchetypeBuilder(character).add(Input.class).add(CameraFollow.class).build(world);
    }

    public int createPlayer(float x, float y) {
        final Entity player = createEntity(this.player, "Player", EntityIDs.PLAYER);
        world.getSystem(TagManager.class).register("player", player);

        Visual v = player.getComponent(Visual.class);
        v.setAnimation(EntityIDs.PLAYER);
        v.index = RenderIndex.PLAYER;

        Animation animation = world.getSystem(AssetManager.class).defineAnimation(EntityIDs.PLAYER, 0.2f,
                Resources.ANIM_PLAYER.toArray(new String[Resources.ANIM_PLAYER.size()]), Animation.PlayMode.LOOP);

        player.getComponent(Bounds.class).setFromRegion(
                animation.getKeyFrames()[0]
        );
        player.getComponent(Position.class).position.set(x, y);
        player.getComponent(Scale.class).scale = SCALE_WORLD_TO_BOX; // player ends up being 1 meter in size

        new PhysicsBuilder(world, player, new Vector2(x, y), EntityIDs.PLAYER)
                .setFixedRotation(true)
                .bodyFriction(0.3f)
                .build();

        Input ic = player.getComponent(Input.class);
        ic.speed = 2f; // 2 meters per sec // a dedicated component?

        return player.getId();
    }

    public int createBullet(Vector2 pos, Vector2 dir) {
        Entity bullet = createEntity(object, "Bullet", EntityIDs.BULLET);
        final float speed = 10f;

        Visual v = bullet.getComponent(Visual.class);
        v.index = RenderIndex.BULLET;
        v.setTexture(Resources.TEX_BULLET);
        bullet.getComponent(Bounds.class).setFromRegion(
                world.getSystem(AssetManager.class).getRegion(Resources.TEX_BULLET)
        );
        Vector2 position = bullet.getComponent(Position.class).position;
        position.set(pos);
        bullet.getComponent(Scale.class).scale = SCALE_WORLD_TO_BOX;
        Vector2 velocity = bullet.getComponent(Velocity.class).velocity;
        velocity.set(dir).scl(speed);

        return bullet.getId();
    }

    public int createCivOneBlinker(float x, float y) {
        Entity civ = createEntity(character, "Civilian", EntityIDs.CIV_ONE_BLINKER);

        Visual v = civ.getComponent(Visual.class);
        v.index = RenderIndex.NPC;
        v.setAnimation(EntityIDs.CIV_ONE_BLINKER);

        Animation animation = world.getSystem(AssetManager.class).defineAnimation(EntityIDs.CIV_ONE_BLINKER, 0.2f,
                Resources.ANIM_CIV1_BLINKER.toArray(new String[Resources.ANIM_CIV1_BLINKER.size()]), Animation.PlayMode.LOOP);

        civ.getComponent(Bounds.class).setFromRegion(
                world.getSystem(AssetManager.class).getAnimation(EntityIDs.CIV_ONE_BLINKER).getKeyFrames()[0]
        );
        civ.getComponent(Position.class).position.set(x, y);
        civ.getComponent(Scale.class).scale = SCALE_WORLD_TO_BOX; // civ ends up being 1 meter in size

        new PhysicsBuilder(world, civ, new Vector2(x, y), EntityIDs.CIV_ONE_BLINKER)
                .bodyType(BodyDef.BodyType.KinematicBody)
                .setFixedRotation(true)
                .bodyFriction(0.3f)
                .build();

        return civ.getId();
    }

    public int createBuildingLargeOne(Vector2 pos, float angleDeg) {
        Entity bldg = createEntity(collidable, "Large Building", EntityIDs.BUILDING_LARGE_ONE);

        Visual v = bldg.getComponent(Visual.class);
        v.index = RenderIndex.BUILDING;
        v.setTexture(Resources.TEX_BLDG_LRG_ONE);
        bldg.getComponent(Bounds.class).setFromRegion(
                world.getSystem(AssetManager.class).getRegion(Resources.TEX_BLDG_LRG_ONE)
        );
        bldg.getComponent(Position.class).position.set(pos);
        bldg.getComponent(Rotation.class).angle = angleDeg;
        bldg.getComponent(Scale.class).scale = SCALE_WORLD_TO_BOX;

        new PhysicsBuilder(world, bldg, pos, EntityIDs.BUILDING_LARGE_ONE)
                .bodyType(BodyDef.BodyType.StaticBody)
                .setAngle(angleDeg)
                .build();

        return bldg.getId();
    }

    public int createBuildingRoundOne(Vector2 pos, float angleDeg) {
        // TODO: Tie GridSeed component to this somehow
        Entity bldg = createEntity(collidable, "Round Building", EntityIDs.BUILDING_ROUND_ONE);

        Visual v = bldg.getComponent(Visual.class);
        v.index = RenderIndex.BUILDING;
        v.setTexture(Resources.TEX_BLDG_ROUND_ONE);
        bldg.getComponent(Bounds.class).setFromRegion(
                world.getSystem(AssetManager.class).getRegion(Resources.TEX_BLDG_ROUND_ONE)
        );
        bldg.getComponent(Position.class).position.set(pos);
        bldg.getComponent(Rotation.class).angle = angleDeg;
        bldg.getComponent(Scale.class).scale = SCALE_WORLD_TO_BOX;

        new PhysicsBuilder(world, bldg, pos, EntityIDs.BUILDING_ROUND_ONE)
                .bodyType(BodyDef.BodyType.StaticBody)
                .setAngle(angleDeg)
                .build();

        return bldg.getId();
    }

    public int createRiftOne(Vector2 pos, float angleDeg) {
        Entity bldg = createEntity(collidable, "Rift1", EntityIDs.RIFT_ONE); // TODO: Come up with a more ui-friendly name

        Visual v = bldg.getComponent(Visual.class);
        v.index = RenderIndex.BUILDING;
        v.setTexture(Resources.TEX_RIFT_ONE);
        bldg.getComponent(Bounds.class).setFromRegion(
                world.getSystem(AssetManager.class).getRegion(Resources.TEX_RIFT_ONE)
        );
        bldg.getComponent(Position.class).position.set(pos);
        bldg.getComponent(Rotation.class).angle = angleDeg;
        bldg.getComponent(Scale.class).scale = SCALE_WORLD_TO_BOX;

        new PhysicsBuilder(world, bldg, pos, EntityIDs.BUILDING_ROUND_ONE)
                .bodyType(BodyDef.BodyType.StaticBody)
                .setAngle(angleDeg)
                .build();

        return bldg.getId();
    }

    public int createRiftTwo(Vector2 pos, float angleDeg) {
        Entity bldg = createEntity(collidable, "Rift2", EntityIDs.RIFT_TWO); // TODO: Come up with a more ui-friendly name

        Visual v = bldg.getComponent(Visual.class);
        v.index = RenderIndex.BUILDING;
        v.setTexture(Resources.TEX_RIFT_TWO);
        bldg.getComponent(Bounds.class).setFromRegion(
                world.getSystem(AssetManager.class).getRegion(Resources.TEX_RIFT_TWO)
        );
        bldg.getComponent(Position.class).position.set(pos);
        bldg.getComponent(Rotation.class).angle = angleDeg;
        bldg.getComponent(Scale.class).scale = SCALE_WORLD_TO_BOX;

        new PhysicsBuilder(world, bldg, pos, EntityIDs.BUILDING_ROUND_ONE)
                .bodyType(BodyDef.BodyType.StaticBody)
                .setAngle(angleDeg)
                .build();

        return bldg.getId();
    }

    public int createVyroidBeacon(Vector2 pos, float angleDeg) {
        Entity bldg = createEntity(collidable, "Vyroid Beacon", EntityIDs.VYROID_BEACON);

        Visual v = bldg.getComponent(Visual.class);
        v.index = RenderIndex.BUILDING;
        v.setTexture(Resources.TEX_VYROID_BEACON);
        bldg.getComponent(Bounds.class).setFromRegion(
                world.getSystem(AssetManager.class).getRegion(Resources.TEX_VYROID_BEACON)
        );
        bldg.getComponent(Position.class).position.set(pos);
        bldg.getComponent(Rotation.class).angle = angleDeg;
        bldg.getComponent(Scale.class).scale = SCALE_WORLD_TO_BOX;

        new PhysicsBuilder(world, bldg, pos, EntityIDs.BUILDING_ROUND_ONE)
                .bodyType(BodyDef.BodyType.StaticBody)
                .setAngle(angleDeg)
                .build();

        return bldg.getId();
    }

    public int createBackgroundTheEdge(Vector2 pos) {
        Entity bg = createEntity(object, "The Edge Background", EntityIDs.THE_EDGE);

        Visual v = bg.getComponent(Visual.class);
        v.index = RenderIndex.BACKGROUND;
        bg.getComponent(Visual.class).setTexture(Resources.TEX_THE_EDGE);
        bg.getComponent(Bounds.class).setFromRegion(
                world.getSystem(AssetManager.class).getRegion(Resources.TEX_THE_EDGE)
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

    private Entity createEntity(Archetype archetype, String friendlyName, String internalId) {
        Entity entity = world.createEntity(archetype);
        Name name = entity.getComponent(Name.class);
        name.friendlyName = friendlyName;
        name.internalID = internalId;
        return entity;
    }
}
