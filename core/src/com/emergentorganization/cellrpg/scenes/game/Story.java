package com.emergentorganization.cellrpg.scenes.game;


import com.artemis.World;
import com.artemis.WorldConfiguration;
import com.artemis.managers.TagManager;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.graphics.g2d.Animation;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.math.Vector3;
import com.emergentorganization.cellrpg.PixelonTransmission;
import com.emergentorganization.cellrpg.core.EntityFactory;
import com.emergentorganization.cellrpg.core.SceneFactory;
import com.emergentorganization.cellrpg.managers.AssetManager;
import com.emergentorganization.cellrpg.managers.BodyManager;
import com.emergentorganization.cellrpg.scenes.BaseScene;
import com.emergentorganization.cellrpg.systems.*;
import com.emergentorganization.cellrpg.tools.mapeditor.map.MapTools;
import com.emergentorganization.cellrpg.tools.postprocessing.TronShader;

/**
 * Created by brian on 10/30/15.
 */
public class Story extends BaseScene {

    private final com.badlogic.gdx.physics.box2d.World physWorld;
    private World world;

    private SpriteBatch batch;
    private EntityFactory entityFactory;

    public Story(PixelonTransmission pt) {
        super(pt);
        physWorld = new com.badlogic.gdx.physics.box2d.World(new Vector2(), true);
        initArtemis(physWorld);
        world.getSystem(RenderSystem.class).setTronShader(
                new TronShader(new Vector3(1,1,1))
        );
        MapTools.importMap("OneEachTestMap", entityFactory);
    }

    private void initArtemis(com.badlogic.gdx.physics.box2d.World physWorld) {
        batch = new SpriteBatch();
        entityFactory = new EntityFactory();
        world = new World(SceneFactory.basicGameConfiguration(pt, physWorld, batch, stage, entityFactory));
        entityFactory.initialize(world);
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