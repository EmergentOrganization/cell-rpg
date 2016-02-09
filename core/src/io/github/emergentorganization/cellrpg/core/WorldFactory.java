package io.github.emergentorganization.cellrpg.core;

import com.artemis.World;
import com.artemis.WorldConfiguration;
import com.artemis.managers.TagManager;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.glutils.ShapeRenderer;
import com.badlogic.gdx.math.Vector3;
import com.badlogic.gdx.scenes.scene2d.Stage;
import io.github.emergentorganization.emergent2dcore.PixelonTransmission;
import io.github.emergentorganization.cellrpg.core.entityfactory.EntityFactory;
import io.github.emergentorganization.emergent2dcore.events.EventListener;
import io.github.emergentorganization.cellrpg.events.GameEvent;
import io.github.emergentorganization.cellrpg.events.SoundEventListener;
import io.github.emergentorganization.cellrpg.managers.AssetManager;
import io.github.emergentorganization.cellrpg.managers.EventManager;
import io.github.emergentorganization.cellrpg.managers.PhysicsSystem;
import io.github.emergentorganization.cellrpg.scenes.Scene;
import io.github.emergentorganization.cellrpg.systems.*;
import io.github.emergentorganization.cellrpg.systems.CASystems.CAGenerationSystem;
import io.github.emergentorganization.cellrpg.systems.CASystems.CAInteractionSystem;
import io.github.emergentorganization.cellrpg.systems.CASystems.CAPositionSystem;
import io.github.emergentorganization.cellrpg.systems.CASystems.CARenderSystem.CARenderSystem;
import io.github.emergentorganization.cellrpg.systems.CASystems.CASpontaneousGenerationSystem;
import io.github.emergentorganization.cellrpg.tools.postprocessing.TronShader;
import io.github.emergentorganization.emergent2dcore.systems.*;


public class WorldFactory {

    public static World standardGameWorld(PixelonTransmission pt,
                                          SpriteBatch batch, Stage stage, EntityFactory entityFactory,
                                          WorldConfiguration wc) {
        wc.register(entityFactory);

        // set up world systemss
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

        wc.setSystem(new WeaponSystem());
        wc.setSystem(new AISystem());
        wc.setSystem(new TimedDestructionSystem());
        wc.setSystem(new CollectibleSpawnSystem());
        wc.setSystem(new InputSystem());
        wc.setSystem(new MovementSystem()); // move after rendering
        wc.setSystem(new EntityLifecycleSystem());
        EventManager eventManager = new EventManager();
        wc.setSystem(eventManager); // needs to be near the end to be postured for sudden scene-change events
        wc.setSystem(new WindowSystem(stage, batch, pt.getSceneManager())); // render windows after everything else
        wc.setSystem(new FPSLoggerSystem());

        wc.setSystem(new MusicSystem(assetManager));

        // initialize world
        World world = new World(wc);
        entityFactory.initialize(world);

        physicsSystem.setContactListener(new PhysicsContactListener(world));

        eventManager.addListener(new SoundEventListener(assetManager));

        return world;
    }

    public static void setupStandardEventSystem(final World world, final PixelonTransmission pt) {
        // setup events
        world.getSystem(EventManager.class).addListener(new EventListener() {
            @Override
            public void notify(GameEvent event) {
                switch (event) {
                    case PLAYER_SHOOT:
                        break;
                    case PLAYER_HIT:
                        break;
                    case PLAYER_SHIELD_DOWN:
                        pt.getSceneManager().setScene(Scene.MAIN_MENU);
                        break;
                    case COLLISION_BULLET:
                        break;
                }
            }
        });
    }

    public static void setupStandardWorldEffects(World world) {
        world.getSystem(RenderSystem.class).setTronShader(
                new TronShader(new Vector3(1, 1, 1))
        );
    }

    public static void setupStdHUD(World world, Stage stage) {
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
        wc.setSystem(new WindowSystem(stage, batch, pt.getSceneManager())); // render windows after everything else

        World world = new World(wc);
        entityFactory.initialize(world);

        physicsSystem.setContactListener(new PhysicsContactListener(world));

        eventManager.addListener(new SoundEventListener(assetManager));

        return world;
    }
}
