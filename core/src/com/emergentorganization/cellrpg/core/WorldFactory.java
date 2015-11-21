package com.emergentorganization.cellrpg.core;

import com.artemis.World;
import com.artemis.WorldConfiguration;
import com.artemis.managers.TagManager;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.scenes.scene2d.Stage;
import com.emergentorganization.cellrpg.PixelonTransmission;
import com.emergentorganization.cellrpg.core.entityfactory.EntityFactory;
import com.emergentorganization.cellrpg.events.EventListener;
import com.emergentorganization.cellrpg.events.GameEvent;
import com.emergentorganization.cellrpg.events.SoundEventListener;
import com.emergentorganization.cellrpg.managers.AssetManager;
import com.emergentorganization.cellrpg.managers.BodyManager;
import com.emergentorganization.cellrpg.managers.EventManager;
import com.emergentorganization.cellrpg.systems.*;

/**
 * Created by brian on 11/7/15.
 */
public class WorldFactory {

    public static World standardGameWorld(PixelonTransmission pt, com.badlogic.gdx.physics.box2d.World physWorld,
                                          SpriteBatch batch, Stage stage, EntityFactory entityFactory) {
        WorldConfiguration wc = new WorldConfiguration();
        wc.register(entityFactory);

        wc.setSystem(new TagManager()); // useful for tagging unique entities

        AssetManager assetManager = new AssetManager(pt.getGdxAssetManager());
        wc.setSystem(assetManager);

        wc.setSystem(new BodyManager(physWorld, pt.getBodyLoader()));

        EventManager eventManager = new EventManager();
        wc.setSystem(eventManager);

        wc.setSystem(new CameraSystem());
        wc.setSystem(new RenderSystem(batch));

        PhysicsRenderSystem phs = new PhysicsRenderSystem(batch, physWorld);
        phs.setEnabled(false);
        wc.setSystem(phs);

        wc.setSystem(new InputSystem());
        wc.setSystem(new MovementSystem()); // move after rendering
        wc.setSystem(new EntityLifecycleSystem());
        wc.setSystem(new WindowSystem(stage, batch, pt.getSceneManager())); // render windows after everything else

        World world = new World(wc);
        entityFactory.initialize(world);
        physWorld.setContactListener(new PhysicsContactListener(world));
        eventManager.addListener(new SoundEventListener(assetManager));

        return world;
    }
}
