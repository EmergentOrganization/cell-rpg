package com.emergentorganization.cellrpg.core.entityfactory;

import com.artemis.Archetype;
import com.artemis.ArchetypeBuilder;
import com.artemis.Entity;
import com.artemis.World;
import com.badlogic.gdx.Game;
import com.badlogic.gdx.graphics.Camera;
import com.badlogic.gdx.graphics.g2d.Animation;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.physics.box2d.BodyDef;
import com.emergentorganization.cellrpg.components.*;
import com.emergentorganization.cellrpg.components.CAInteraction.CAInteraction;
import com.emergentorganization.cellrpg.components.CAInteraction.CAInteractionList;
import com.emergentorganization.cellrpg.components.SpontaneousGeneration.SpontaneousGenerationList;
import com.emergentorganization.cellrpg.core.EntityID;
import com.emergentorganization.cellrpg.core.RenderIndex;
import com.emergentorganization.cellrpg.core.Tags;
import com.emergentorganization.cellrpg.events.EventListener;
import com.emergentorganization.cellrpg.events.GameEvent;
import com.emergentorganization.cellrpg.managers.EventManager;
import com.emergentorganization.cellrpg.systems.CASystems.CARenderSystem.CellRenderers.DecayCellRenderer;
import com.emergentorganization.cellrpg.systems.CASystems.layers.CALayer;
import com.emergentorganization.cellrpg.systems.CameraSystem;
import com.emergentorganization.cellrpg.tools.CGoLShapeConsts;
import com.emergentorganization.cellrpg.tools.Resources;

/**
 * Created by brian on 10/28/15.
 */
public class EntityFactory {
    public static float SCALE_BOX_TO_WORLD = 40f;
    public static float SCALE_WORLD_TO_BOX = 0.025f;

    public static float BULLET_MAX_DIST = 20f;

    private World world;
    private EventManager eventManager;

    public Archetype base;
    public Archetype object;
    public Archetype collidable;
    public Archetype character;
    private Archetype player;
    private Archetype bullet;
    private Archetype ca_layer;

    // TODO: add CAManager & get CA layers using , not this:
    Entity vyroidLayer;
    Entity energyLayer;
    Entity geneticLayer;


    public void initialize(World world) {
        this.world = world;
        this.eventManager = world.getSystem(EventManager.class);
        base = new ArchetypeBuilder().add(Position.class).add(Name.class).build(world);
        object = new ArchetypeBuilder(base).add(Visual.class).add(Rotation.class).add(Scale.class)
                .add(Bounds.class).add(Velocity.class).build(world);
        collidable = new ArchetypeBuilder(object).add(PhysicsBody.class).add(CAInteractionList.class).build(world);
        bullet = new ArchetypeBuilder(collidable).add(BulletState.class).build(world);
        character = new ArchetypeBuilder(collidable).add(Health.class).build(world);
        player = new ArchetypeBuilder(character)
                .add(Input.class)
                .add(CameraFollow.class)
                .add(Equipment.class)
                .add(StatsTracker.class)
                .add(SpontaneousGenerationList.class)
                .build(world);
        ca_layer = new ArchetypeBuilder(base).add(CAGridComponents.class).build(world);
    }

    private void addCALayers(Vector2 pos){
        // adds all ca layer entities to the scene.
        Camera camera = world.getSystem(CameraSystem.class).getGameCamera();
        vyroidLayer = new EntityBuilder(world, ca_layer, "Standard Vyroid CA Layer",
                EntityID.CA_LAYER_VYROIDS.toString(), pos)
                .renderIndex(RenderIndex.CA)
                .tag(Tags.CA_VYROIDS_STD)
                .build();
        CAGridComponents vyroidLayerStuff = vyroidLayer.getComponent(CAGridComponents.class);
        CALayerFactory.initLayerComponentsByType(vyroidLayerStuff, CALayer.VYROIDS, camera);

        energyLayer = new EntityBuilder(world, ca_layer, "Energy CA Layer",
                EntityID.CA_LAYER_ENERGY.toString(), pos)
                .renderIndex(RenderIndex.CA)
                .tag(Tags.CA_VYROIDS_GENETIC)
                .build();
        CAGridComponents energyLayerStuff = energyLayer.getComponent(CAGridComponents.class);
        CALayerFactory.initLayerComponentsByType(energyLayerStuff, CALayer.ENERGY, camera);

        geneticLayer = new EntityBuilder(world, ca_layer, "genetic CA Layer",
                EntityID.CA_LAYER_GENETIC.toString(), pos)
                .renderIndex(RenderIndex.CA)
                .build();
        CAGridComponents geneticLayerStuff = geneticLayer.getComponent(CAGridComponents.class);
        CALayerFactory.initLayerComponentsByType(geneticLayerStuff, CALayer.VYROIDS_GENETIC, camera);
    }

    public int createPlayer(float x, float y) {
        Vector2 pos = new Vector2(x, y);

        final Entity player = new EntityBuilder(world, this.player, "Player", EntityID.PLAYER.toString(), pos)
                .tag(Tags.PLAYER)
                .animation(Resources.ANIM_PLAYER, Animation.PlayMode.LOOP_PINGPONG, 0.2f)
                .renderIndex(RenderIndex.PLAYER)
                .setFixedRotation(true)
                .bodyFriction(0.3f)
                .build();

        Input ic = player.getComponent(Input.class);
        ic.speed = 2f; // 2 meters per sec // a dedicated component?

        // Shield
        final Entity shield = new EntityBuilder(world, object, "Energy Shield", EntityID.PLAYER_SHIELD.toString(), pos)
                .tag("shield")
                .texture(Resources.ANIM_PLAYER_SHIELD.get(Resources.ANIM_PLAYER_SHIELD.size() - 1))
                .renderIndex(RenderIndex.PLAYER_SHIELD)
                .build();

        final Equipment ec = player.getComponent(Equipment.class);
        ec.shieldEntity = shield.getId();
        ec.shieldState = Resources.ANIM_PLAYER_SHIELD.size() - 1;

        eventManager.addListener(new EventListener() {
            @Override
            public void notify(GameEvent event) {
                switch (event) {
                    case PLAYER_HIT:
                        ec.shieldState--;
                        if (ec.shieldState < 0) {
                            ec.shieldState = 0;
                            eventManager.pushEvent(GameEvent.PLAYER_SHIELD_DOWN);
                        } else {
                            shield.getComponent(Visual.class).setTexture(Resources.ANIM_PLAYER_SHIELD.get(ec.shieldState));
                        }
                        break;
                }
            }
        });

        addCALayers(pos);  // TODO: this should be somewhere else

        // add cellular automata grid interactions
        CAInteractionList interactList = player.getComponent(CAInteractionList.class);
//        System.out.println("adding player-vyroid collision. ca grid id#" + vyroidLayer.getId());
        interactList
            .addInteraction(
                vyroidLayer.getId(),
                new CAInteraction()
                    // vyroid damage
                    .addCollisionImpactStamp(1, CGoLShapeConsts.BOOM(10, 10), energyLayer.getId())
                    .addCollisionImpactStamp(1, CGoLShapeConsts.EMPTY(10, 10), vyroidLayer.getId())
                    .addEventTrigger(1, GameEvent.PLAYER_HIT)
                    // constant visual effect
                    .addCollisionImpactStamp(0, CGoLShapeConsts.SQUARE(
                            3,
                            3,
                            DecayCellRenderer.getMaxOfColorGroup(DecayCellRenderer.colorGroupKeys.BLUE)
                    ), energyLayer.getId())
            )
            .addInteraction(  // genetic vyroids damage
                geneticLayer.getId(),
                new CAInteraction()
                    .addCollisionImpactStamp(1, CGoLShapeConsts.BOOM(4*3,4*3), energyLayer.getId())
                    .addCollisionImpactStamp(1, CGoLShapeConsts.EMPTY(4, 4), geneticLayer.getId())
                    .addEventTrigger(1, GameEvent.PLAYER_HIT)
            )
            .setColliderRadius(4)
        ;

        return player.getId();
    }

    public int createBullet(Vector2 pos, Vector2 dir) {
        final float speed = 10f;
        Entity bullet = new EntityBuilder(world, this.bullet, "Bullet", EntityID.BULLET.toString(), pos)
                .texture(Resources.TEX_BULLET)
                .renderIndex(RenderIndex.BULLET)
                .velocity(speed, dir)
                .bodyFriction(0.0001f)
                .bodyRestitution(1.0f)
                .bullet(true)
                .build();

        // add cellular automata grid interactions
        CAInteractionList interactList = bullet.getComponent(CAInteractionList.class);
        interactList
            .addInteraction(
                vyroidLayer.getId(),
                new CAInteraction()
                    .addCollisionImpactStamp(1, CGoLShapeConsts.BOOM(9, 9), energyLayer.getId())
                    .addCollisionImpactStamp(1, CGoLShapeConsts.EMPTY(6, 6), vyroidLayer.getId())
                    // constant visual effect
                    .addCollisionImpactStamp(0, CGoLShapeConsts.SQUARE(
                            1,
                            1,
                            DecayCellRenderer.getMaxOfColorGroup(DecayCellRenderer.colorGroupKeys.WHITE)
                    ), energyLayer.getId())
                    .addEventTrigger(1, GameEvent.VYROID_KILL_STD)
            )
            .addInteraction(
                    geneticLayer.getId(),
                    new CAInteraction()
                            .addCollisionImpactStamp(1, CGoLShapeConsts.BOOM(9, 9), energyLayer.getId())
                            .addCollisionImpactStamp(1, CGoLShapeConsts.EMPTY(3, 3), geneticLayer.getId())
                            .addEventTrigger(1, GameEvent.VYROID_KILL_GENETIC)
            )
            .setColliderRadius(2)
        ;

        return bullet.getId();
    }

    public int createCivOneBlinker(float x, float y) {
        Vector2 pos = new Vector2(x, y);
        Entity civ = new EntityBuilder(world, character, "Civilian", EntityID.CIV_ONE_BLINKER.toString(), pos)
                .renderIndex(RenderIndex.NPC)
                .animation(Resources.ANIM_CIV1_BLINKER, Animation.PlayMode.LOOP_PINGPONG, 0.2f)
                .bodyType(BodyDef.BodyType.KinematicBody)
                .setFixedRotation(true)
                .bodyFriction(0.3f)
                .build();

        return civ.getId();
    }

    public int createBuildingLargeOne(Vector2 pos, float angleDeg) {
        Entity bldg = new EntityBuilder(world, collidable, "Large Building", EntityID.BUILDING_LARGE_ONE.toString(), pos)
                .texture(Resources.TEX_BLDG_LRG_ONE)
                .bodyType(BodyDef.BodyType.StaticBody)
                .angle(angleDeg)
                .build();

        return bldg.getId();
    }

    public int createBuildingRoundOne(Vector2 pos, float angleDeg) {
        // TODO: Tie GridSeed component to this somehow
        Entity bldg = new EntityBuilder(world, collidable, "Round Building", EntityID.BUILDING_ROUND_ONE.toString(), pos)
                .texture(Resources.TEX_BLDG_ROUND_ONE)
                .angle(angleDeg)
                .bodyType(BodyDef.BodyType.StaticBody)
                .build();

        return bldg.getId();
    }

    public int createRiftOne(Vector2 pos, float angleDeg) {
        Entity bldg = new EntityBuilder(world, collidable, "Rift1", EntityID.RIFT_ONE.toString(), pos) // TODO: Come up with a more ui-friendly name
                .texture(Resources.TEX_RIFT_ONE)
                .angle(angleDeg)
                .bodyType(BodyDef.BodyType.StaticBody)
                .build();

        return bldg.getId();
    }

    public int createRiftTwo(Vector2 pos, float angleDeg) {
        Entity bldg = new EntityBuilder(world, collidable, "Rift2", EntityID.RIFT_TWO.toString(), pos) // TODO: Come up with a more ui-friendly name
                .texture(Resources.TEX_RIFT_TWO)
                .angle(angleDeg)
                .bodyType(BodyDef.BodyType.StaticBody)
                .build();

        return bldg.getId();
    }

    public int createVyroidBeacon(Vector2 pos, float angleDeg) {
        Entity bldg = new EntityBuilder(world, collidable, "Vyroid Beacon", EntityID.VYROID_BEACON.toString(), pos)
                .texture(Resources.TEX_VYROID_BEACON)
                .angle(angleDeg)
                .bodyType(BodyDef.BodyType.StaticBody)
                .build();

        return bldg.getId();
    }

    public int createBackgroundTheEdge(Vector2 pos) {
        Entity bg = new EntityBuilder(world, object, "The Edge Background", EntityID.THE_EDGE.toString(), pos)
                .texture(Resources.TEX_THE_EDGE)
                .renderIndex(RenderIndex.BACKGROUND)
                .build();

        return bg.getId();
    }

    public int createEntityByID(EntityID id, Vector2 pos, float angleDeg) {
        switch (id) {
            case BULLET:
                return createBullet(pos, new Vector2().rotate(angleDeg));
            case PLAYER:
                return createPlayer(pos.x, pos.y);
            case PLAYER_SHIELD:
                throw new RuntimeException("ERROR: Cannot initialize player shield on it's own.");
            case BUILDING_LARGE_ONE:
                return createBuildingLargeOne(pos, angleDeg);
            case BUILDING_ROUND_ONE:
                return createBuildingRoundOne(pos, angleDeg);
            case RIFT_ONE:
                return createRiftOne(pos, angleDeg);
            case RIFT_TWO:
                return createRiftTwo(pos, angleDeg);
            case VYROID_BEACON:
                return createVyroidBeacon(pos, angleDeg);
            case CIV_ONE_BLINKER:
                return createCivOneBlinker(pos.x, pos.y);
            case THE_EDGE:
                return createBackgroundTheEdge(pos);
            default:
                throw new RuntimeException("ERROR: enum instance missing in switch for id " + id);
        }
    }
}
