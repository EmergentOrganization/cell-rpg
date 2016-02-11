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
import io.github.emergentorganization.cellrpg.components.Weapon.WeaponComponent;
import io.github.emergentorganization.cellrpg.core.EntityID;
import io.github.emergentorganization.cellrpg.core.RenderIndex;
import io.github.emergentorganization.cellrpg.core.Tags;
import io.github.emergentorganization.cellrpg.core.entityfactory.builder.EntityBuilder;
import io.github.emergentorganization.cellrpg.core.entityfactory.builder.componentbuilder.*;
import io.github.emergentorganization.cellrpg.events.EntityEvent;
import io.github.emergentorganization.emergent2dcore.events.EventListener;
import io.github.emergentorganization.cellrpg.events.GameEvent;
import io.github.emergentorganization.cellrpg.managers.EventManager;
import io.github.emergentorganization.cellrpg.managers.PhysicsSystem;
import io.github.emergentorganization.cellrpg.systems.CASystems.CARenderSystem.CellRenderers.DecayCellRenderer;
import io.github.emergentorganization.cellrpg.systems.CASystems.CAs.CAStamps;
import io.github.emergentorganization.cellrpg.systems.CASystems.layers.CALayer;
import io.github.emergentorganization.emergent2dcore.systems.CameraSystem;
import io.github.emergentorganization.cellrpg.tools.CGoLShapeConsts;
import io.github.emergentorganization.cellrpg.tools.Resources;
import io.github.emergentorganization.emergent2dcore.components.*;
import io.github.emergentorganization.emergent2dcore.systems.MoodSystem;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;


public class EntityFactory {
    private final Logger logger = LogManager.getLogger(getClass());

    public static float SCALE_BOX_TO_WORLD = 40f;
    public static float SCALE_WORLD_TO_BOX = 0.025f;
    public static Archetype object;
    public Archetype base;
    public Archetype collidable;
    public Archetype collectable;
    public Archetype character;
    public Archetype destructable;
    public Archetype npc;
    private TagManager tagManager;
    private World world;
    private EventManager eventManager;
    private Archetype player;
    private Archetype bullet;
    private Archetype ca_layer;
    private Archetype invisibleObject;

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
                .add(Equipment.class)
                .add(WeaponComponent.class)
                .add(StatsTracker.class)
                .add(SpontaneousGenerationList.class)
                .add(CollectibleSpawnField.class)
                .remove(Health.class) // doesn't need health; has shield
                .build(world);
        ca_layer = new ArchetypeBuilder(base).add(CAGridComponents.class).build(world);

        tagManager = world.getSystem(TagManager.class);
    }

    public void addCALayers(Vector2 pos, int playerID) {
        // adds all ca layer entities to the scene.
        Camera camera = world.getSystem(CameraSystem.class).getGameCamera();
        Entity vyroidLayer = new EntityBuilder(world, ca_layer, "Standard Vyroid CA Layer",
                EntityID.CA_LAYER_VYROIDS.toString(), pos)
                .tag(Tags.CA_VYROIDS_STD)
                .build();
        CAGridComponents vyroidLayerStuff = vyroidLayer.getComponent(CAGridComponents.class);
        CALayerFactory.initLayerComponentsByType(vyroidLayerStuff, CALayer.VYROIDS, camera);
        vyroidLayerStuff.intensityPerCell = MoodSystem.CA_INTENSITY_MAP.get(Tags.CA_VYROIDS_STD);

        Entity energyLayer = new EntityBuilder(world, ca_layer, "Energy CA Layer",
                EntityID.CA_LAYER_ENERGY.toString(), pos)
                .tag(Tags.CA_ENERGY)
                .build();
        CAGridComponents energyLayerStuff = energyLayer.getComponent(CAGridComponents.class);
        CALayerFactory.initLayerComponentsByType(energyLayerStuff, CALayer.ENERGY, camera);

        Entity geneticLayer = new EntityBuilder(world, ca_layer, "genetic CA Layer",
                EntityID.CA_LAYER_GENETIC.toString(), pos)
                .tag(Tags.CA_VYROIDS_GENETIC)
                .build();
        CAGridComponents geneticLayerStuff = geneticLayer.getComponent(CAGridComponents.class);
        CALayerFactory.initLayerComponentsByType(geneticLayerStuff, CALayer.VYROIDS_GENETIC, camera);
        geneticLayerStuff.intensityPerCell = MoodSystem.CA_INTENSITY_MAP.get(Tags.CA_VYROIDS_GENETIC);

        // add cellular automata grid interactions
        Entity player = world.getEntity(playerID);
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
                                .addCollisionImpactStamp(1, CGoLShapeConsts.BOOM(4 * 3, 4 * 3), energyLayer.getId())
                                .addCollisionImpactStamp(1, CGoLShapeConsts.EMPTY(4, 4), geneticLayer.getId())
                                .addEventTrigger(1, GameEvent.PLAYER_HIT)
                )
                .setColliderRadius(4)
        ;
    }

    public int createPlayer(float x, float y) {
        Vector2 pos = new Vector2(x, y);

        final Entity player = new EntityBuilder(world, this.player, "Player", EntityID.PLAYER.toString(), pos)
                .tag(Tags.PLAYER)
                .addBuilder(new VisualBuilder()
                        .animation(Resources.ANIM_PLAYER, Animation.PlayMode.LOOP_PINGPONG, 0.2f)
                        .renderIndex(RenderIndex.PLAYER)
                )
                .addBuilder(new PhysicsBodyBuilder(world.getSystem(PhysicsSystem.class))
                        .setFixedRotation(true)
                        .bodyFriction(0.3f)
                )
                .addBuilder(new InputBuilder()
                        .speed(2f)
                )
                .addBuilder(new SpontaneousGenerationListBuilder(10))// TODO: not sure what this value should be... could use Bounds?
                .addBuilder(new CollectibleSpawnFieldBuilder(10))// TODO: not sure what this should be either
                //.health(1) // shield takes care of this instead
                .build();

        // Shield
        final int MAX_SHIELD_STATE = Resources.ANIM_PLAYER_SHIELD.size() - 1;
        final Entity shield = new EntityBuilder(world, object, "Energy Shield", EntityID.PLAYER_SHIELD.toString(), pos)
                .tag("shield")
                .addBuilder(new VisualBuilder()
                        .texture(Resources.ANIM_PLAYER_SHIELD.get(MAX_SHIELD_STATE))
                        .renderIndex(RenderIndex.PLAYER_SHIELD)
                )
                .build();

        final Equipment ec = player.getComponent(Equipment.class);
        ec.shieldEntity = shield.getId();
        ec.shieldState = MAX_SHIELD_STATE;

        eventManager.addListener(new EventListener() {
            @Override
            public void notify(EntityEvent event) {
                switch (event.event) {
                    case PLAYER_HIT:
                        ec.shieldState--;
                        if (ec.shieldState < 0) {
                            ec.shieldState = 0;
                            eventManager.pushEvent(new EntityEvent(EntityEvent.NO_ENTITY, GameEvent.PLAYER_SHIELD_DOWN));
                        } else {
                            shield.getComponent(Visual.class).setTexture(Resources.ANIM_PLAYER_SHIELD.get(ec.shieldState));
                        }
                        break;
                    case POWERUP_PLUS:
//                        System.out.println("shield (" + ec.shieldState + ") powerup");
                        if (ec.shieldState < (MAX_SHIELD_STATE)) {
                            ec.shieldState++;
//                            System.out.println("shield++");
                            shield.getComponent(Visual.class).setTexture(Resources.ANIM_PLAYER_SHIELD.get(ec.shieldState));
                        }
                        break;
                    case POWERUP_STAR:
                        Vector2 cen = player.getComponent(Position.class).getCenter(player.getComponent(Bounds.class), 0);
                        Entity vyroidLayer = tagManager.getEntity(Tags.CA_VYROIDS_STD);
                        Entity geneticLayer = tagManager.getEntity(Tags.CA_VYROIDS_GENETIC);
                        Entity energyLayer = tagManager.getEntity(Tags.CA_ENERGY);
                        vyroidLayer.getComponent(CAGridComponents.class).stampCenteredAt(CGoLShapeConsts.EMPTY(210, 210), cen);
                        geneticLayer.getComponent(CAGridComponents.class).stampCenteredAt(CGoLShapeConsts.EMPTY(70, 70), cen);
                        energyLayer.getComponent(CAGridComponents.class).stampCenteredAt(CGoLShapeConsts.BOOM(210, 210), cen);
                }
            }
        });

        return player.getId();
    }

    public int createBullet(Vector2 pos, Vector2 dir) {
        final float speed = 10f;
        Entity bullet = new EntityBuilder(world, this.bullet, "Bullet", EntityID.BULLET.toString(), pos)
                .addBuilder(new VisualBuilder()
                        .texture(Resources.TEX_BULLET)
                        .renderIndex(RenderIndex.BULLET)
                )
                .addBuilder(new PhysicsBodyBuilder(world.getSystem(PhysicsSystem.class))
                        .bodyFriction(0.0001f)
                        .bodyRestitution(1.0f)
                        .bullet(true)
                )
                .addBuilder(new HealthBuilder(3))
                .addBuilder(new CollideEffectBuilder()
                        .collideDamage(1)
                        .collideSelfDamage(1)
                )
                .addBuilder(new LifecycleBuilder(20f))
                .velocity(speed, dir)
                .build();

        // add cellular automata grid interactions
        Entity vyroidLayer = tagManager.getEntity(Tags.CA_VYROIDS_STD);
        Entity geneticLayer = tagManager.getEntity(Tags.CA_VYROIDS_GENETIC);
        Entity energyLayer = tagManager.getEntity(Tags.CA_ENERGY);
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

    public int createBuildingLargeOne(Vector2 pos, float angleDeg) {
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

    public int createBuildingRoundOne(Vector2 pos, float angleDeg) {
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

    public int createRiftOne(Vector2 pos, float angleDeg) {
        Entity bldg = new EntityBuilder(world, collidable, "Rift1", EntityID.RIFT_ONE.toString(), pos) // TODO: Come up with a more ui-friendly name
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

    public int createRiftTwo(Vector2 pos, float angleDeg) {
        Entity bldg = new EntityBuilder(world, collidable, "Rift2", EntityID.RIFT_TWO.toString(), pos) // TODO: Come up with a more ui-friendly name
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

    public int createVyroidBeacon(Vector2 pos, float angleDeg) {
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

    public int createBackgroundTheEdge(Vector2 pos) {
        Entity bg = new EntityBuilder(world, object, "The Edge Background", EntityID.THE_EDGE.toString(), pos)
                .addBuilder(new VisualBuilder()
                                .texture(Resources.TEX_THE_EDGE)
                                .renderIndex(RenderIndex.BACKGROUND)
                )
                .build();

        return bg.getId();
    }

    public int createPowerupPlus(Vector2 pos) {
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

    public int createInvisibleWall(Vector2 size, Vector2 pos, float angleDeg) {
        Entity wall = new EntityBuilder(world, invisibleObject, "Invisible Wall", EntityID.INVISIBLE_WALL.toString(), pos)
                .angle(angleDeg)
                .addBuilder(new PhysicsBodyBuilder(world.getSystem(PhysicsSystem.class))
                        .bodyType(BodyDef.BodyType.StaticBody)
                        .boundsBody(size)
                )
                .build();
        return wall.getId();
    }

    public int createPowerupStar(Vector2 pos) {
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
                if (event.event == GameEvent.POWERUP_STAR){
                    tagManager.getEntity(Tags.PLAYER).getComponent(WeaponComponent.class).powerUp(Powerup.FIRE_RATE);
                }
            }
        });

        return powerup.getId();
    }

    public int createVyrapuffer(Vector2 pos) {
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
        Entity geneticLayer = tagManager.getEntity(Tags.CA_VYROIDS_GENETIC);
        interactList.addInteraction(
                geneticLayer.getId(),
                new CAInteraction().addCollisionImpactStamp(0, CAStamps.getVyrapuffer(), geneticLayer.getId())
        ).setColliderRadius(1);

        return puffer.getId();
    }

    public int createTubSnake(Vector2 pos) {
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
                        .speed(.2f)
                )
                .addBuilder(new AIComponentBuilder(AIComponent.aiType.RANDWALK)
                        .AIPeriod(.1f)
                )
                .build();

        CAInteractionList interactList = ent.getComponent(CAInteractionList.class);
//        System.out.println("adding player-vyroid collision. ca grid id#" + vyroidLayer.getId());
        Entity vyroidLayer = tagManager.getEntity(Tags.CA_VYROIDS_STD);
        Entity energyLayer = tagManager.getEntity(Tags.CA_ENERGY);
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
            case VYRAPUFFER:
                return createVyrapuffer(pos);
            case TUBSNAKE:
                return createTubSnake(pos);
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
    }
}
