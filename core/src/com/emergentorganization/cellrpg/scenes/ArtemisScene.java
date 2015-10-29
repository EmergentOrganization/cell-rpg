package com.emergentorganization.cellrpg.scenes;

import com.artemis.World;
import com.artemis.WorldConfiguration;
import com.artemis.managers.TagManager;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.ScreenAdapter;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.emergentorganization.cellrpg.PixelonTransmission;
import com.emergentorganization.cellrpg.core.EntityFactory;
import com.emergentorganization.cellrpg.managers.AssetManager;
import com.emergentorganization.cellrpg.systems.AnimationSystem;
import com.emergentorganization.cellrpg.systems.InputSystem;
import com.emergentorganization.cellrpg.systems.MovementSystem;
import com.emergentorganization.cellrpg.systems.RenderSystem;

/**
 * Created by orelb on 10/28/2015.
 */
public class ArtemisScene extends ScreenAdapter {

    private World world;
    private PixelonTransmission pt;

    public ArtemisScene(PixelonTransmission pt){
        this.pt = pt;
        initArtemis();
    }

    private void initArtemis(){
        WorldConfiguration wc = new WorldConfiguration();
        SpriteBatch batch = new SpriteBatch();

        wc.setSystem(new TagManager()); // useful for tagging unique entities
        wc.setSystem(new AssetManager(pt.getAssetManager()));

        wc.setSystem(new InputSystem());
        wc.setSystem(new MovementSystem());
        wc.setSystem(new AnimationSystem());
        wc.setSystem(new RenderSystem(batch));

        world = new World(wc);

        EntityFactory entityFactory = new EntityFactory(world, null);

        int player = entityFactory.createPlayer(null, 100, 100);
        world.getSystem(TagManager.class).register("player", player);
    }

    @Override
    public void render(float delta) {
        Gdx.gl.glClear(GL20.GL_COLOR_BUFFER_BIT);

        world.setDelta(delta);
        world.process();
    }
}
