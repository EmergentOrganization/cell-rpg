package com.emergentorganization.cellrpg.scenes.game;


import com.artemis.World;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.math.Vector3;
import com.emergentorganization.cellrpg.PixelonTransmission;
import com.emergentorganization.cellrpg.core.entityfactory.EntityFactory;
import com.emergentorganization.cellrpg.core.WorldFactory;
import com.emergentorganization.cellrpg.events.EventListener;
import com.emergentorganization.cellrpg.events.GameEvent;
import com.emergentorganization.cellrpg.managers.EventManager;
import com.emergentorganization.cellrpg.scenes.BaseScene;
import com.emergentorganization.cellrpg.scenes.Scene;
import com.emergentorganization.cellrpg.systems.*;
import com.emergentorganization.cellrpg.tools.mapeditor.map.MapTools;
import com.emergentorganization.cellrpg.tools.postprocessing.TronShader;

/**
 * Created by brian on 10/30/15.
 */
public class Story extends BaseScene {
    private World world;

    private SpriteBatch batch;
    private EntityFactory entityFactory;

    public Story(final PixelonTransmission pt) {
        super(pt);
        initArtemis();
        world.getSystem(RenderSystem.class).setTronShader(
                new TronShader(new Vector3(1, 1, 1))
        );

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

        MapTools.importMap("OneEachTestMap", entityFactory);
    }

    private void initArtemis() {
        batch = new SpriteBatch();
        entityFactory = new EntityFactory();
        world = WorldFactory.standardGameWorld(pt, batch, stage, entityFactory);
    }

    @Override
    public void render(float delta) {
        Gdx.gl.glClear(GL20.GL_COLOR_BUFFER_BIT);

        world.setDelta(delta);
        world.process();
    }

    @Override
    public void dispose() {
        super.dispose();
        world.dispose();
        batch.dispose();
    }

    @Override
    protected boolean shouldStash() {
        return false;
    }
}