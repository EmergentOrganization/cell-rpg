package io.github.emergentorganization.cellrpg.core;

import com.artemis.World;
import com.artemis.WorldConfiguration;
import com.artemis.managers.TagManager;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.glutils.ShapeRenderer;
import com.badlogic.gdx.math.Vector3;
import com.badlogic.gdx.scenes.scene2d.Stage;
import io.github.emergentorganization.cellrpg.PixelonTransmission;
import io.github.emergentorganization.cellrpg.core.entityfactory.EntityFactory;
import io.github.emergentorganization.cellrpg.core.events.EventListener;
import io.github.emergentorganization.cellrpg.core.systems.*;
import io.github.emergentorganization.cellrpg.core.systems.MusicSystem.MusicSystem;
import io.github.emergentorganization.cellrpg.events.EntityEvent;
import io.github.emergentorganization.cellrpg.events.SoundEventListener;
import io.github.emergentorganization.cellrpg.managers.AssetManager;
import io.github.emergentorganization.cellrpg.managers.EventManager;
import io.github.emergentorganization.cellrpg.managers.PhysicsSystem;
import io.github.emergentorganization.cellrpg.managers.RegionManager.LeveledRegionSwitcher;
import io.github.emergentorganization.cellrpg.scenes.game.regions.WarpInEventRegion;
import io.github.emergentorganization.cellrpg.systems.*;
import io.github.emergentorganization.cellrpg.systems.CASystems.CAGenerationSystem;
import io.github.emergentorganization.cellrpg.systems.CASystems.CAInteractionSystem;
import io.github.emergentorganization.cellrpg.systems.CASystems.CAPositionSystem;
import io.github.emergentorganization.cellrpg.systems.CASystems.CARenderSystem.CARenderSystem;
import io.github.emergentorganization.cellrpg.systems.CASystems.CASpontaneousGenerationSystem;
import io.github.emergentorganization.cellrpg.tools.postprocessing.TronShader;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;


public class WorldFactory {
    private static final Logger logger = LogManager.getLogger(WorldFactory.class);

    public static World createWorld(WorldType type, PixelonTransmission pt,
                                    SpriteBatch batch, Stage stage, EntityFactory entityFactory) {

        switch (type) {
            case STANDARD:
                return standardGameWorld(pt, batch, stage, entityFactory);
            case ARCADE:
                return arcadeGameWorld(pt, batch, stage, entityFactory);
            case EDITOR:
                return editorGameWorld(pt, batch, stage, entityFactory);
        }

        // Should never get to this point
        throw new RuntimeException("Could not create world " + type.name() + "! It doesn't exist?");
    }

    public static World arcadeGameWorld(PixelonTransmission pt,
                                          SpriteBatch batch, Stage stage, EntityFactory entityFactory) {

        WorldConfiguration wc = new WorldConfiguration();
        wc.register(entityFactory);

        // set up world systems
        wc.setSystem(new TagManager()); // useful for tagging unique entities

        AssetManager assetManager = new AssetManager(pt.getGdxAssetManager());
        wc.setSystem(assetManager);

        wc.setSystem(new CameraSystem());
        wc.setSystem(new RenderSystem(batch));
        PhysicsSystem physicsSystem = new PhysicsSystem(pt.getBodyLoader(), null);
        wc.setSystem(physicsSystem);

        wc.setSystem(new CAGenerationSystem());
        wc.setSystem(new CAPositionSystem());
        wc.setSystem(new CARenderSystem(new ShapeRenderer()));
        wc.setSystem(new CAInteractionSystem());
        wc.setSystem(new CASpontaneousGenerationSystem());

        wc.setSystem(new AISystem());
        wc.setSystem(new TimedDestructionSystem());
        wc.setSystem(new SpawningSystem());
        // for using WarpInEventRegions:
        int maxTimeInRegion = 3 * 60 * 1000;  // max time before region moves ahead anyway [ms]
        wc.setSystem(new LeveledRegionSwitcher(maxTimeInRegion, 0));  // -1 to use test region, 0 is typical starting wave
        //        // for using SingleShapeWarp and SingleEntityWarp Regions:
        //        wc.setSystem(new LeveledRegionSwitcher(new SingleShapeWarpRegion(
        //                this, 10*1000, CGoLShapeConsts.BLINKER_H, .5f, CALayer.vyroid_values()
        //        )));
        wc.setSystem(new InputSystem());
        wc.setSystem(new MovementSystem()); // move after rendering
        wc.setSystem(new EntityLifecycleSystem());
        EventManager eventManager = new EventManager();
        wc.setSystem(eventManager); // needs to be near the end to be postured for sudden scene-change events
        wc.setSystem(new WindowSystem(pt, stage, batch)); // render windows after everything else
        wc.setSystem(new FPSLoggerSystem());

        wc.setSystem(new MoodSystem(eventManager));
        wc.setSystem(new TimingSystem());
        wc.setSystem(new MusicSystem(assetManager));
        wc.setSystem(new EnergySystem());

        // initialize world
        World world = new World(wc);
        entityFactory.initialize(world);

        physicsSystem.setContactListener(new PhysicsContactListener(world));

        eventManager.addListener(new SoundEventListener(assetManager));

        return world;
    }

    public static World standardGameWorld(PixelonTransmission pt,
                                          SpriteBatch batch, Stage stage, EntityFactory entityFactory) {

        WorldConfiguration wc = new WorldConfiguration();
        wc.register(entityFactory);

        // set up world systems
        wc.setSystem(new TagManager()); // useful for tagging unique entities

        AssetManager assetManager = new AssetManager(pt.getGdxAssetManager());
        wc.setSystem(assetManager);

        wc.setSystem(new CameraSystem());
        wc.setSystem(new RenderSystem(batch));
        PhysicsSystem physicsSystem = new PhysicsSystem(pt.getBodyLoader(), null);
        wc.setSystem(physicsSystem);

        wc.setSystem(new CAGenerationSystem());
        wc.setSystem(new CAPositionSystem());
        wc.setSystem(new CARenderSystem(new ShapeRenderer()));
        wc.setSystem(new CAInteractionSystem());
        wc.setSystem(new CASpontaneousGenerationSystem());

        wc.setSystem(new AISystem());
        wc.setSystem(new TimedDestructionSystem());
        wc.setSystem(new SpawningSystem());
        wc.setSystem(new InputSystem());
        wc.setSystem(new MovementSystem()); // move after rendering
        wc.setSystem(new EntityLifecycleSystem());
        EventManager eventManager = new EventManager();
        wc.setSystem(eventManager); // needs to be near the end to be postured for sudden scene-change events
        wc.setSystem(new WindowSystem(pt, stage, batch)); // render windows after everything else
        wc.setSystem(new FPSLoggerSystem());

        wc.setSystem(new MoodSystem(eventManager));
        wc.setSystem(new TimingSystem());
        wc.setSystem(new MusicSystem(assetManager));
        wc.setSystem(new EnergySystem());

        // initialize world
        World world = new World(wc);
        entityFactory.initialize(world);

        physicsSystem.setContactListener(new PhysicsContactListener(world));

        eventManager.addListener(new SoundEventListener(assetManager));

        return world;
    }

    public static void setupStandardEventSystem(final World world, final PixelonTransmission pt) {
        // setup events
        logger.info("setting up std event system");
        world.getSystem(EventManager.class).addListener(new EventListener() {
            @Override
            public void notify(EntityEvent event) {
                switch (event.event) {
                    case PLAYER_SHOOT:
                        break;
                    case PLAYER_HIT:
                        break;
                    case PLAYER_SHIELD_DOWN:
                        pt.gameOver(world);
                        break;
                    case COLLISION_BULLET:
                        break;
                }
            }
        });
    }

    public static void setupStandardWorldEffects(World world) {
        logger.info("setting up shaders");
        world.getSystem(RenderSystem.class)
                .setTronShader(new TronShader(new Vector3(1, 1, 1)));
        //.setBackgroundShader(new BackgroundShader(new Vector3(0.37f, 0.43f, 0.50f)));
    }

    public static void setupStdHUD(World world, Stage stage) {
        logger.info("setting up std HUD...");
    }

    public static World editorGameWorld(PixelonTransmission pt, SpriteBatch batch, Stage stage, EntityFactory entityFactory) {
        WorldConfiguration wc = new WorldConfiguration();
        wc.register(entityFactory);

        wc.setSystem(new TagManager()); // useful for tagging unique entities

        AssetManager assetManager = new AssetManager(pt.getGdxAssetManager());
        wc.setSystem(assetManager);


        wc.setSystem(new CameraSystem());
        wc.setSystem(new RenderSystem(batch));
        PhysicsSystem physicsSystem = new PhysicsSystem(pt.getBodyLoader(), batch);
        wc.setSystem(physicsSystem);

        wc.setSystem(new InputSystem());
        wc.setSystem(new MovementSystem()); // move after rendering
        wc.setSystem(new EntityLifecycleSystem());
        EventManager eventManager = new EventManager();
        wc.setSystem(eventManager); // needs to be near the end to be postured for sudden scene-change events
        wc.setSystem(new WindowSystem(pt, stage, batch)); // render windows after everything else

        World world = new World(wc);
        entityFactory.initialize(world);

        physicsSystem.setContactListener(new PhysicsContactListener(world));

        eventManager.addListener(new SoundEventListener(assetManager));

        return world;
    }
}
