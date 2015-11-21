package com.emergentorganization.cellrpg.core.entityfactory;

import com.artemis.Archetype;
import com.artemis.ArchetypeBuilder;
import com.artemis.Entity;
import com.artemis.World;
import com.badlogic.gdx.graphics.g2d.Animation;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.physics.box2d.BodyDef;
import com.emergentorganization.cellrpg.components.*;
import com.emergentorganization.cellrpg.core.EntityIDs;
import com.emergentorganization.cellrpg.core.RenderIndex;
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
    private Archetype bullet;

    public void initialize(World world) {
        this.world = world;
        base = new ArchetypeBuilder().add(Position.class).add(Name.class).build(world);
        object = new ArchetypeBuilder(base).add(Visual.class).add(SFX.class)
                .add(Rotation.class).add(Scale.class).add(Bounds.class).add(Velocity.class).build(world);
        collidable = new ArchetypeBuilder(object).add(PhysicsBody.class).build(world);
        bullet = new ArchetypeBuilder(collidable).add(BulletState.class).build(world);
        character = new ArchetypeBuilder(collidable).add(Health.class).build(world);
        player = new ArchetypeBuilder(character).add(Input.class).add(CameraFollow.class).add(Equipment.class).build(world);
    }

    public int createPlayer(float x, float y) {
        Vector2 pos = new Vector2(x, y);
        Entity player = new EntityBuilder(world, this.player, "Player", EntityIDs.PLAYER, pos)
                .tag("player")
                .animation(Resources.ANIM_PLAYER, Animation.PlayMode.LOOP_PINGPONG, 0.2f)
                .renderIndex(RenderIndex.PLAYER)
                .setFixedRotation(true)
                .bodyFriction(0.3f)
                .build();

        Input ic = player.getComponent(Input.class);
        ic.speed = 2f; // 2 meters per sec // a dedicated component?

        // Shield
        Entity shield = new EntityBuilder(world, object, "Energy Shield", EntityIDs.PLAYER_SHIELD, pos)
                .tag("shield")
                .texture(Resources.ANIM_PLAYER_SHIELD.get(0))
                .renderIndex(RenderIndex.PLAYER_SHIELD)
                .build();

        Equipment ec = player.getComponent(Equipment.class);
        ec.shieldEntity = shield.getId();

        return player.getId();
    }

    public int createBullet(Vector2 pos, Vector2 dir) {
        final float speed = 10f;
        Entity bullet = new EntityBuilder(world, this.bullet, "Bullet", EntityIDs.BULLET, pos)
                .texture(Resources.TEX_BULLET)
                .renderIndex(RenderIndex.BULLET)
                .velocity(speed, dir)
                .bodyFriction(0.0001f)
                .bodyRestitution(1.0f)
                .build();

        return bullet.getId();
    }

    public int createCivOneBlinker(float x, float y) {
        Vector2 pos = new Vector2(x, y);
        Entity civ = new EntityBuilder(world, character, "Civilian", EntityIDs.CIV_ONE_BLINKER, pos)
                .renderIndex(RenderIndex.NPC)
                .animation(Resources.ANIM_CIV1_BLINKER, Animation.PlayMode.LOOP_PINGPONG, 0.2f)
                .bodyType(BodyDef.BodyType.KinematicBody)
                .setFixedRotation(true)
                .bodyFriction(0.3f)
                .build();

        return civ.getId();
    }

    public int createBuildingLargeOne(Vector2 pos, float angleDeg) {
        Entity bldg = new EntityBuilder(world, collidable, "Large Building", EntityIDs.BUILDING_LARGE_ONE, pos)
                .texture(Resources.TEX_BLDG_LRG_ONE)
                .bodyType(BodyDef.BodyType.StaticBody)
                .angle(angleDeg)
                .build();

        return bldg.getId();
    }

    public int createBuildingRoundOne(Vector2 pos, float angleDeg) {
        // TODO: Tie GridSeed component to this somehow
        Entity bldg = new EntityBuilder(world, collidable, "Round Building", EntityIDs.BUILDING_ROUND_ONE, pos)
                .texture(Resources.TEX_BLDG_ROUND_ONE)
                .angle(angleDeg)
                .bodyType(BodyDef.BodyType.StaticBody)
                .build();

        return bldg.getId();
    }

    public int createRiftOne(Vector2 pos, float angleDeg) {
        Entity bldg = new EntityBuilder(world, collidable, "Rift1", EntityIDs.RIFT_ONE, pos) // TODO: Come up with a more ui-friendly name
                .texture(Resources.TEX_RIFT_ONE)
                .angle(angleDeg)
                .bodyType(BodyDef.BodyType.StaticBody)
                .build();

        return bldg.getId();
    }

    public int createRiftTwo(Vector2 pos, float angleDeg) {
        Entity bldg = new EntityBuilder(world, collidable, "Rift2", EntityIDs.RIFT_TWO, pos) // TODO: Come up with a more ui-friendly name
                .texture(Resources.TEX_RIFT_TWO)
                .angle(angleDeg)
                .bodyType(BodyDef.BodyType.StaticBody)
                .build();

        return bldg.getId();
    }

    public int createVyroidBeacon(Vector2 pos, float angleDeg) {
        Entity bldg = new EntityBuilder(world, collidable, "Vyroid Beacon", EntityIDs.VYROID_BEACON, pos)
                .texture(Resources.TEX_VYROID_BEACON)
                .angle(angleDeg)
                .bodyType(BodyDef.BodyType.StaticBody)
                .build();

        return bldg.getId();
    }

    public int createBackgroundTheEdge(Vector2 pos) {
        Entity bg = new EntityBuilder(world, object, "The Edge Background", EntityIDs.THE_EDGE, pos)
                .texture(Resources.TEX_THE_EDGE)
                .renderIndex(RenderIndex.BACKGROUND)
                .build();

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
