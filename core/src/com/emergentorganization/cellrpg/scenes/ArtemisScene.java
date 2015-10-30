package com.emergentorganization.cellrpg.scenes;

import com.artemis.World;
import com.artemis.WorldConfiguration;
import com.artemis.managers.TagManager;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.ScreenAdapter;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.graphics.g2d.Animation;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.math.Vector2;
import com.emergentorganization.cellrpg.PixelonTransmission;
import com.emergentorganization.cellrpg.core.EntityFactory;
import com.emergentorganization.cellrpg.managers.AssetManager;
import com.emergentorganization.cellrpg.managers.BodyManager;
import com.emergentorganization.cellrpg.systems.*;
import com.emergentorganization.cellrpg.tools.physics.BodyEditorLoader;

/**
 * Created by orelb on 10/28/2015.
 */
public class ArtemisScene extends ScreenAdapter {

    private final com.badlogic.gdx.physics.box2d.World physWorld;
    private World world;
    private PixelonTransmission pt;

    private SpriteBatch batch;

    public ArtemisScene(PixelonTransmission pt) {
        this.pt = pt;
        physWorld = new com.badlogic.gdx.physics.box2d.World(new Vector2(), true);
        initArtemis(physWorld);
    }

    private void initArtemis(com.badlogic.gdx.physics.box2d.World physWorld) {
        WorldConfiguration wc = new WorldConfiguration();
        batch = new SpriteBatch();

        wc.setSystem(new TagManager()); // useful for tagging unique entities
        wc.setSystem(new AssetManager(pt.getAssetManager()));

        wc.setSystem(new BodyManager(physWorld));

        wc.setSystem(new CameraSystem());
        wc.setSystem(new RenderSystem(batch));
        wc.setSystem(new PhysicsRenderSystem(batch, physWorld));

        wc.setSystem(new InputSystem());
        wc.setSystem(new MovementSystem()); // move after rendering

        world = new World(wc);

        // we need a dedicated class to define assets
        world.getSystem(AssetManager.class).defineAnimation("char-player", 0.2f,
                new String[]{"game/char-player/0",
                        "game/char-player/1",
                        "game/char-player/2",
                        "game/char-player/3",
                        "game/char-player/4",
                        "game/char-player/5",
                        "game/char-player/6",
                        "game/char-player/7",
                        "game/char-player/8",
                        "game/char-player/9"}, Animation.PlayMode.LOOP);

        EntityFactory entityFactory = new EntityFactory(world);

        int player = entityFactory.createPlayer(0, 0);
        world.getSystem(TagManager.class).register("player", player);
    }

    @Override
    public void render(float delta) {
        Gdx.gl.glClear(GL20.GL_COLOR_BUFFER_BIT);

        world.setDelta(delta);
        world.process();

        physWorld.step(PixelonTransmission.PHYSICS_TIMESTEP, 6, 2);
    }

    @Override
    public void dispose() {
        world.dispose();
        batch.dispose();
    }
}
