package io.github.emergentorganization.cellrpg.core.entityfactory;

import com.artemis.Archetype;
import com.artemis.ArchetypeBuilder;
import com.artemis.Entity;
import com.artemis.World;
import com.artemis.managers.TagManager;
import com.badlogic.gdx.graphics.Camera;
import com.badlogic.gdx.graphics.g2d.Animation;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.physics.box2d.BodyDef;
import io.github.emergentorganization.cellrpg.components.*;
import io.github.emergentorganization.cellrpg.components.CAInteraction.CAInteraction;
import io.github.emergentorganization.cellrpg.components.CAInteraction.CAInteractionList;
import io.github.emergentorganization.cellrpg.components.SpontaneousGeneration.SpontaneousGenerationList;
import io.github.emergentorganization.cellrpg.components.Weapon.Powerup;
import io.github.emergentorganization.cellrpg.core.EntityID;
import io.github.emergentorganization.cellrpg.core.RenderIndex;
import io.github.emergentorganization.cellrpg.core.Tags;
import io.github.emergentorganization.cellrpg.core.components.*;
import io.github.emergentorganization.cellrpg.core.entityfactory.Entities.Bullet;
import io.github.emergentorganization.cellrpg.core.entityfactory.Entities.Player;
import io.github.emergentorganization.cellrpg.core.entityfactory.builder.EntityBuilder;
import io.github.emergentorganization.cellrpg.core.entityfactory.builder.componentbuilder.*;
import io.github.emergentorganization.cellrpg.core.events.EventListener;
import io.github.emergentorganization.cellrpg.core.systems.CameraSystem;
import io.github.emergentorganization.cellrpg.events.EntityEvent;
import io.github.emergentorganization.cellrpg.events.GameEvent;
import io.github.emergentorganization.cellrpg.managers.EventManager;
import io.github.emergentorganization.cellrpg.managers.PhysicsSystem;
import io.github.emergentorganization.cellrpg.systems.CASystems.CARenderSystem.CellRenderers.DecayCellRenderer;
import io.github.emergentorganization.cellrpg.systems.CASystems.CAs.CAStamps;
import io.github.emergentorganization.cellrpg.systems.CASystems.layers.CALayer;
import io.github.emergentorganization.cellrpg.tools.CGoLShapeConsts;
import io.github.emergentorganization.cellrpg.tools.Resources;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

public class EntityFactory {
    public static final float SCALE_BOX_TO_WORLD = 40f;
    public static final float SCALE_WORLD_TO_BOX = 0.025f;
    public static Archetype object;
    private static Archetype base;
    private static Archetype collidable;
    private static Archetype collectable;
    private static Archetype character;
    private static Archetype destructable;
    private static Archetype npc;
    public static Archetype player;
    public static Archetype bullet;
    private static Archetype ca_layer;
    private static Archetype invisibleObject;
    private TagManager tagManager;
    private World world;
    private EventManager eventManager;


    public void initialize(World world) {
        this.world = world;
        this.eventManager = world.getSystem(EventManager.class);
        base = new ArchetypeBuilder().add(Position.class).add(Name.class).add(Lifecycle.class).build(world);
        object = new ArchetypeBuilder(base).add(Visual.class).add(Rotation.class).add(Scale.class)
                .add(Bounds.class).add(Velocity.class).build(world);
        collidable = new ArchetypeBuilder(object).add(PhysicsBody.class).add(CAInteractionList.class).build(world);
        invisibleObject = new ArchetypeBuilder(object).remove(Visual.class).add(PhysicsBody.class).build(world);
        destructable = new ArchetypeBuilder(collidable).add(Health.class).build(world);
        collectable = new ArchetypeBuilder(destructable).add(DestructionTimer.class).build(world);
        bullet = new ArchetypeBuilder(destructable).add(CollideEffect.class).build(world);
        character = new ArchetypeBuilder(destructable).build(world);
        npc = new ArchetypeBuilder(character).add(AIComponent.class, InputComponent.class).build(world);
        player = new ArchetypeBuilder(character)
                .add(InputComponent.class)
                .add(CameraFollow.class)
                .add(EnergyLevel.class)
                .add(EquipmentList.class)
                .add(StatsTracker.class)
                .add(SpontaneousGenerationList.class)
                .add(EntitySpawnField.class)
                .remove(Health.class) // doesn't need health; has shield
                .build(world);
        ca_layer = new ArchetypeBuilder(base).add(CAGridComponents.class).build(world);

        tagManager = world.getSystem(TagManager.class);
    }

    public void addCALayers(Vector2 pos, int playerID) {
        logger.info("adding CA layers");
        // adds all ca layer entities to the scene.
        Camera camera = world.getSystem(CameraSystem.class).getGameCamera();

        Entity vyroidLayer = CALayerBuilder.buildLayer(world, pos,
                ca_layer, "std vyroids", CALayer.VYROIDS.getTag(), CALayer.VYROIDS
        );

        Entity energyLayer = CALayerBuilder.buildLayer(world, pos,
                ca_layer, "energy layer", CALayer.ENERGY.getTag(), CALayer.ENERGY
        );

        Entity geneticLayer = CALayerBuilder.buildLayer(world, pos,
                ca_layer, "genetic vyroids", CALayer.VYROIDS_GENETIC.getTag(), CALayer.VYROIDS_GENETIC
        );

        // add cellular automata grid interactions
        Entity player = world.getEntity(playerID);
        CAInteractionList interactList = player.getComponent(CAInteractionList.class);
        logger.debug("adding player-vyroid collision. ca grid id#" + vyroidLayer.getId());
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
                                .addCollisionImpactStamp(1, CGoLShapeConsts.BOOM(4 * 3, 4 * 3), energyLayer.getId())
                                .addCollisionImpactStamp(1, CGoLShapeConsts.EMPTY(4, 4), geneticLayer.getId())
                                .addEventTrigger(1, GameEvent.PLAYER_HIT)
                )
                .setColliderRadius(4)
        ;
    }

    private int createCivOneBlinker(float x, float y) {
        Vector2 pos = new Vector2(x, y);
        Entity civ = new EntityBuilder(world, character, "Civilian", EntityID.CIV_ONE_BLINKER.toString(), pos)
                .addBuilder(new VisualBuilder()
                        .renderIndex(RenderIndex.NPC)
                        .animation(Resources.ANIM_CIV1_BLINKER, Animation.PlayMode.LOOP_PINGPONG, 0.2f)
                )
                .addBuilder(new PhysicsBodyBuilder(world.getSystem(PhysicsSystem.class))
                        .bodyType(BodyDef.BodyType.KinematicBody)
                        .setFixedRotation(true)
                        .bodyFriction(0.3f)
                )
                .build();

        return civ.getId();
    }

    private int createBuildingLargeOne(Vector2 pos, float angleDeg) {
        Entity bldg = new EntityBuilder(world, collidable, "Large Building", EntityID.BUILDING_LARGE_ONE.toString(), pos)
                .addBuilder(new VisualBuilder()
                        .texture(Resources.TEX_BLDG_LRG_ONE)
                )
                .addBuilder(new PhysicsBodyBuilder(world.getSystem(PhysicsSystem.class))
                        .bodyType(BodyDef.BodyType.StaticBody)
                )
                .angle(angleDeg)
                .build();

        return bldg.getId();
    }

    private int createBuildingRoundOne(Vector2 pos, float angleDeg) {
        // TODO: Tie GridSeed component to this somehow
        Entity bldg = new EntityBuilder(world, collidable, "Round Building", EntityID.BUILDING_ROUND_ONE.toString(), pos)
                .addBuilder(new VisualBuilder()
                        .texture(Resources.TEX_BLDG_ROUND_ONE)
                )
                .addBuilder(new PhysicsBodyBuilder(world.getSystem(PhysicsSystem.class))
                        .bodyType(BodyDef.BodyType.StaticBody)
                )
                .angle(angleDeg)
                .build();

        return bldg.getId();
    }

    private int createRiftOne(Vector2 pos, float angleDeg) {
        Entity bldg = new EntityBuilder(world, collidable, "Rift1", EntityID.RIFT_ONE.toString(), pos)
                .addBuilder(new VisualBuilder()
                        .texture(Resources.TEX_RIFT_ONE)
                )
                .addBuilder(new PhysicsBodyBuilder(world.getSystem(PhysicsSystem.class))
                        .bodyType(BodyDef.BodyType.StaticBody)
                )
                .angle(angleDeg)
                .build();

        return bldg.getId();
    }

    private int createRiftTwo(Vector2 pos, float angleDeg) {
        Entity bldg = new EntityBuilder(world, collidable, "Rift2", EntityID.RIFT_TWO.toString(), pos)
                .addBuilder(new VisualBuilder()
                        .texture(Resources.TEX_RIFT_TWO)
                )
                .addBuilder(new PhysicsBodyBuilder(world.getSystem(PhysicsSystem.class))
                        .bodyType(BodyDef.BodyType.StaticBody)
                )
                .angle(angleDeg)
                .build();

        return bldg.getId();
    }

    private int createVyroidBeacon(Vector2 pos, float angleDeg) {
        Entity bldg = new EntityBuilder(world, collidable, "Vyroid Beacon", EntityID.VYROID_BEACON.toString(), pos)
                .addBuilder(new VisualBuilder()
                        .texture(Resources.TEX_VYROID_BEACON)
                )
                .addBuilder(new PhysicsBodyBuilder(world.getSystem(PhysicsSystem.class))
                        .bodyType(BodyDef.BodyType.StaticBody)
                )
                .angle(angleDeg)
                .build();

        return bldg.getId();
    }

    private int createBackgroundTheEdge(Vector2 pos) {
        Entity bg = new EntityBuilder(world, object, "The Edge Background", EntityID.THE_EDGE.toString(), pos)
                .addBuilder(new VisualBuilder()
                        .texture(Resources.TEX_THE_EDGE)
                        .renderIndex(RenderIndex.BACKGROUND)
                )
                .build();

        return bg.getId();
    }

    private int createPowerupPlus(Vector2 pos) {
        Entity powerup = new EntityBuilder(world, collectable, "plus powerup", EntityID.POWERUP_PLUS.toString(), pos)
                .addBuilder(new VisualBuilder()
                        .texture(Resources.TEX_POWERUP_PLUS)
                        .renderIndex(RenderIndex.BULLET)
                )
                .addBuilder(new PhysicsBodyBuilder(world.getSystem(PhysicsSystem.class)))
                .addBuilder(new DestructionTimerBuilder(5))
                .build();
        return powerup.getId();
    }

    private int createInvisibleWall(Vector2 size, Vector2 pos, float angleDeg) {
        Entity wall = new EntityBuilder(world, invisibleObject, "Invisible Wall", EntityID.INVISIBLE_WALL.toString(), pos)
                .angle(angleDeg)
                .addBuilder(new PhysicsBodyBuilder(world.getSystem(PhysicsSystem.class))
                        .bodyType(BodyDef.BodyType.StaticBody)
                        .boundsBody(size)
                )
                .build();
        return wall.getId();
    }

    private int createPowerupStar(Vector2 pos) {
        final Entity powerup = new EntityBuilder(world, collectable, "star powerup", EntityID.POWERUP_STAR.toString(), pos)
                .addBuilder(new VisualBuilder()
                        .texture(Resources.TEX_POWERUP_STAR)
                        .renderIndex(RenderIndex.BULLET)
                )
                .addBuilder(new PhysicsBodyBuilder(world.getSystem(PhysicsSystem.class)))
                .addBuilder(new DestructionTimerBuilder(10))
                .build();

        eventManager.addListener(new EventListener() {
            @Override
            public void notify(EntityEvent event) {
                if (event.event == GameEvent.POWERUP_STAR) {
                    tagManager.getEntity(Tags.PLAYER).getComponent(EquipmentList.class).powerUp(Powerup.FIRE_RATE);
                }
            }
        });

        return powerup.getId();
    }

    private int createVyrapuffer(Vector2 pos) {
        Entity puffer = new EntityBuilder(world, npc, "vyrapuffer", EntityID.VYRAPUFFER.toString(), pos)
                .addBuilder(new VisualBuilder()
                        .animation(Resources.ANIM_VYRAPUFFER, Animation.PlayMode.LOOP_PINGPONG, 0.7f)
                        .renderIndex(RenderIndex.NPC)
                )
                .addBuilder(new PhysicsBodyBuilder(world.getSystem(PhysicsSystem.class))
                        .bodyFriction(.5f)
                )
                .addBuilder(new HealthBuilder(3))
                .addBuilder(new InputBuilder()
                        .speed(10f)
                )
                .addBuilder(new AIComponentBuilder(AIComponent.aiType.DUMBWALK))
                .build();

        CAInteractionList interactList = puffer.getComponent(CAInteractionList.class);
//        System.out.println("adding player-vyroid collision. ca grid id#" + vyroidLayer.getId());
        Entity geneticLayer = tagManager.getEntity(CALayer.VYROIDS_GENETIC.getTag());
        interactList.addInteraction(
                geneticLayer.getId(),
                new CAInteraction().addCollisionImpactStamp(0, CAStamps.getVyrapuffer(), geneticLayer.getId())
        ).setColliderRadius(1);

        return puffer.getId();
    }

    private int createTubSnake(Vector2 pos) {
        Entity ent = new EntityBuilder(world, npc, "tub still life with snake behavior", EntityID.TUBSNAKE.toString(), pos)
                .addBuilder(new VisualBuilder()
                        .texture(Resources.TEX_TUBSNAKE)
                        .renderIndex(RenderIndex.NPC)
                )
                .addBuilder(new PhysicsBodyBuilder(world.getSystem(PhysicsSystem.class))
                        .bodyFriction(.5f)
                )
                .addBuilder(new HealthBuilder(1))
                .addBuilder(new InputBuilder()
                        .speed(.5f)
                )
                .addBuilder(new AIComponentBuilder(AIComponent.aiType.RANDWALK)
                        .AIPeriod(.5f)
                )
                .build();

        CAInteractionList interactList = ent.getComponent(CAInteractionList.class);
//        System.out.println("adding player-vyroid collision. ca grid id#" + vyroidLayer.getId());
        Entity vyroidLayer = tagManager.getEntity(CALayer.VYROIDS.getTag());
        Entity energyLayer = tagManager.getEntity(CALayer.ENERGY.getTag());
        interactList
                .addInteraction(
                        vyroidLayer.getId(),
                        new CAInteraction()
                                .addCollisionImpactStamp(0, CGoLShapeConsts.CELL, vyroidLayer.getId())
                                .addCollisionImpactStamp(
                                        0,
                                        CGoLShapeConsts.stateReplace(
                                                CGoLShapeConsts.TUB,
                                                DecayCellRenderer.getMaxOfColorGroup(DecayCellRenderer.colorGroupKeys.FIRE)
                                        ),
                                        energyLayer.getId()
                                )
                )
                .setColliderRadius(1)
        ;

        return ent.getId();
    }

    private int createPondBomb(Vector2 pos) {
        Entity playerEnt = tagManager.getEntity(Tags.PLAYER);
        Entity ent = new EntityBuilder(world, npc, "pond still life which chases player", EntityID.PONDBOMB.toString(), pos)
                .addBuilder(new VisualBuilder()
                        .animation(Resources.ANIM_PONDBOMB, Animation.PlayMode.LOOP, .1f)
                        .renderIndex(RenderIndex.NPC)
                )
                .addBuilder(new PhysicsBodyBuilder(world.getSystem(PhysicsSystem.class))
                        .bodyFriction(.5f)
                )
                .addBuilder(new HealthBuilder(1))
                .addBuilder(new InputBuilder()
                        .speed(1.2f)
                )
                .addBuilder(new AIComponentBuilder(AIComponent.aiType.CHASE)
                        .AIPeriod(.5f)
                        .AITarget(playerEnt)
                )
                .build();
        return ent.getId();
    }

    private int createGosper(Vector2 pos) {
        Entity playerEnt = tagManager.getEntity(Tags.PLAYER);
        Entity ent = new EntityBuilder(world, npc, "glider gun that shoots @ target", EntityID.GOSPER.toString(), pos)
                .angle(90)
                .addBuilder(new VisualBuilder()
                        .animation(Resources.ANIM_GOSPER, Animation.PlayMode.LOOP, .1f)
                        .renderIndex(RenderIndex.NPC)
                )
                .addBuilder(new PhysicsBodyBuilder(world.getSystem(PhysicsSystem.class))
                        .bodyFriction(1f)
                )
                .addBuilder(new HealthBuilder(5))
                .addBuilder(new InputBuilder()
                        .speed(0f)
                )
                .addBuilder(new AIComponentBuilder(AIComponent.aiType.RANDWALK)
                        .AIPeriod(.5f)
                        .AITarget(playerEnt)
                )
                .build();

        CAInteractionList interactList = ent.getComponent(CAInteractionList.class);
        Entity vyroidLayer = tagManager.getEntity(CALayer.VYROIDS.getTag());
        interactList
                .addInteraction(
                        vyroidLayer.getId(),
                        new CAInteraction()
                                .addCollisionImpactStamp(0, CGoLShapeConsts.GOSPER_DOWN_RIGHT, vyroidLayer.getId(), 44)
                                .addCollisionImpactStamp(1, CGoLShapeConsts.GOSPER_DOWN_RIGHT, vyroidLayer.getId(), 44)
                )
                .setColliderRadius(1)
        ;

        return ent.getId();
    }

    public int createEntityByID(EntityID id, Vector2 pos, float angleDeg) {
        // angleDeg: angle relative to +x axis
        try {
            switch (id) {
                case BULLET:
                    return new Bullet(world, pos, new Vector2(1, 0).rotate(angleDeg)).getId();
                case PLAYER:
                    return new Player(world, pos).getId();
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
                case VYRAPUFFER:
                    return createVyrapuffer(pos);
                case TUBSNAKE:
                    return createTubSnake(pos);
                case PONDBOMB:
                    return createPondBomb(pos);
                case GOSPER:
                    return createGosper(pos);
                case THE_EDGE:
                    return createBackgroundTheEdge(pos);
                case INVISIBLE_WALL:
                    return createInvisibleWall(new Vector2(1, 1), pos, angleDeg);
                case POWERUP_PLUS:
                    return createPowerupPlus(pos);
                case POWERUP_STAR:
                    return createPowerupStar(pos);
                default:
                    throw new RuntimeException("ERROR: enum instance missing in switch for id " + id);
            }
        } catch (Exception ex) {
            logger.error("failed to create entity of type " + id, ex);
            return -1;
        }
    }

    private final Logger logger = LogManager.getLogger(getClass());
}
