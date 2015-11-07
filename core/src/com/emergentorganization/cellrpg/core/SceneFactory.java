package com.emergentorganization.cellrpg.core;

import com.artemis.WorldConfiguration;
import com.artemis.managers.TagManager;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.physics.box2d.World;
import com.badlogic.gdx.scenes.scene2d.Stage;
import com.emergentorganization.cellrpg.PixelonTransmission;
import com.emergentorganization.cellrpg.managers.AssetManager;
import com.emergentorganization.cellrpg.managers.BodyManager;
import com.emergentorganization.cellrpg.systems.*;

/**
 * Created by brian on 11/7/15.
 */
public class SceneFactory {

    public static WorldConfiguration basicGameConfiguration(PixelonTransmission pt, World physWorld, SpriteBatch batch, Stage stage, EntityFactory entityFactory) {
        WorldConfiguration wc = new WorldConfiguration();
        wc.register(entityFactory);

        wc.setSystem(new TagManager()); // useful for tagging unique entities
        wc.setSystem(new AssetManager(pt.getGdxAssetManager()));

        wc.setSystem(new BodyManager(physWorld, pt.getBodyLoader()));

        wc.setSystem(new CameraSystem());
        wc.setSystem(new RenderSystem(batch));
        wc.setSystem(new PhysicsRenderSystem(batch, physWorld));

        wc.setSystem(new InputSystem());
        wc.setSystem(new MovementSystem()); // move after rendering
        wc.setSystem(new WindowSystem(stage, batch, pt.getSceneManager())); // render windows after everything else

        return wc;
    }
}
