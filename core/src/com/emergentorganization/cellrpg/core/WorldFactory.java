package com.emergentorganization.cellrpg.core;

import com.artemis.World;
import com.artemis.WorldConfiguration;
import com.artemis.managers.TagManager;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.glutils.ShapeRenderer;
import com.badlogic.gdx.scenes.scene2d.Stage;
import com.emergentorganization.cellrpg.PixelonTransmission;
import com.emergentorganization.cellrpg.core.entityfactory.EntityFactory;
import com.emergentorganization.cellrpg.events.SoundEventListener;
import com.emergentorganization.cellrpg.managers.AssetManager;
import com.emergentorganization.cellrpg.managers.PhysicsSystem;
import com.emergentorganization.cellrpg.managers.EventManager;
import com.emergentorganization.cellrpg.systems.*;
import com.emergentorganization.cellrpg.systems.CARenderSystem.CAGenerationSystem;
import com.emergentorganization.cellrpg.systems.CARenderSystem.CAPositionSystem;
import com.emergentorganization.cellrpg.systems.CARenderSystem.CARenderSystem;

/**
 * Created by brian on 11/7/15.
 */
public class WorldFactory {

    public static World standardGameWorld(PixelonTransmission pt,
                                          SpriteBatch batch, Stage stage, EntityFactory entityFactory) {
        WorldConfiguration wc = new WorldConfiguration();
        wc.register(entityFactory);

        // set up world systemss
        wc.setSystem(new TagManager()); // useful for tagging unique entities

        AssetManager assetManager = new AssetManager(pt.getGdxAssetManager());
        wc.setSystem(assetManager);



        wc.setSystem(new CameraSystem());
        wc.setSystem(new RenderSystem(batch));
        PhysicsSystem physicsSystem = new PhysicsSystem(pt.getBodyLoader(), null);
        wc.setSystem(physicsSystem);

        wc.setSystem(new InputSystem());
        wc.setSystem(new MovementSystem()); // move after rendering
        wc.setSystem(new EntityLifecycleSystem());
        EventManager eventManager = new EventManager();
        wc.setSystem(eventManager); // needs to be near the end to be postured for sudden scene-change events
        wc.setSystem(new WindowSystem(stage, batch, pt.getSceneManager())); // render windows after everything else

        wc.setSystem(new CAGenerationSystem());
        wc.setSystem(new CAPositionSystem());
        wc.setSystem(new CARenderSystem(new ShapeRenderer()));

        // initialize world
        World world = new World(wc);
        entityFactory.initialize(world);

        physicsSystem.setContactListener(new PhysicsContactListener(world));

        eventManager.addListener(new SoundEventListener(assetManager));

        return world;
    }
}
