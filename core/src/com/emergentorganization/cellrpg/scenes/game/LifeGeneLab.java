package com.emergentorganization.cellrpg.scenes.game;

import com.artemis.World;
import com.artemis.managers.TagManager;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.graphics.g2d.Animation;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.math.Vector2;
import com.emergentorganization.cellrpg.PixelonTransmission;
import com.emergentorganization.cellrpg.core.EntityFactory;
import com.emergentorganization.cellrpg.core.SceneFactory;
import com.emergentorganization.cellrpg.managers.AssetManager;
import com.emergentorganization.cellrpg.scenes.BaseScene;

/**
 * Created by brian on 10/30/15.
 */
public class LifeGeneLab extends BaseScene {

    private final com.badlogic.gdx.physics.box2d.World physWorld;
    private World world;

    private SpriteBatch batch;

    public LifeGeneLab(PixelonTransmission pt) {
        super(pt);
        physWorld = new com.badlogic.gdx.physics.box2d.World(new Vector2(), true);
        initArtemis(physWorld);

    }

    private void initArtemis(com.badlogic.gdx.physics.box2d.World physWorld) {
        batch = new SpriteBatch();
        EntityFactory entityFactory = new EntityFactory();
        world = new World(SceneFactory.basicGameConfiguration(pt, physWorld, batch, stage, entityFactory));
        entityFactory.initialize(world);

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
        physWorld.dispose();
    }
}
