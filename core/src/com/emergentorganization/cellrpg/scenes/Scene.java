package com.emergentorganization.cellrpg.scenes;

/**
 * Created by BrianErikson on 6/2/2015.
 */

import com.badlogic.gdx.ApplicationAdapter;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.InputMultiplexer;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.graphics.OrthographicCamera;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.math.Vector3;
import com.badlogic.gdx.scenes.scene2d.Stage;
import com.badlogic.gdx.utils.viewport.FitViewport;
import com.emergentorganization.cellrpg.entities.Entity;
import com.emergentorganization.cellrpg.tools.BodyLoader;
import org.dyn4j.collision.AxisAlignedBounds;
import org.dyn4j.dynamics.World;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.LinkedHashMap;
import java.util.Map;

/**
 * Think of this like the stage or level; used to update every entity in the stage, as well as render the world
 */
public abstract class Scene extends ApplicationAdapter {
    private ArrayList<Entity> entities;

    private static final int ENTITY_INSERT = 1;
    private static final int ENTITY_REMOVE = 2;
    private HashMap<Entity, Integer> entityQueue;

    private SpriteBatch batch; // sprite batch for entities
    private Vector3 clearColor;
    private Stage uiStage; // stage which handles all UI Actors
    private OrthographicCamera gameCamera;
    private World physWorld;
    private static final double WORLD_WIDTH = 10000d;
    private static final double WORLD_HEIGHT = 10000d;
    private InputMultiplexer input; // Not sure if should keep a reference for this

    @Override
    public void create() {
        super.create();

        entities = new ArrayList<Entity>();
        entityQueue = new LinkedHashMap<Entity, Integer>();

        batch = new SpriteBatch();
        clearColor = new Vector3(0,0,0);
        uiStage = new Stage(new FitViewport(Gdx.graphics.getWidth(), Gdx.graphics.getHeight()));

        input = new InputMultiplexer();
        input.addProcessor(uiStage);

        Gdx.input.setInputProcessor(input);

        gameCamera = new OrthographicCamera(Gdx.graphics.getWidth(), Gdx.graphics.getHeight());
        gameCamera.position.set(Gdx.graphics.getWidth() / 2f, Gdx.graphics.getHeight() / 2f, 0); // center camera with 0,0 in bottom left
        gameCamera.update();
        batch.setProjectionMatrix(gameCamera.combined);

        BodyLoader.fetch(); // initialize bodyLoader if it isn't already
        physWorld = new World(new AxisAlignedBounds(WORLD_WIDTH, WORLD_HEIGHT));
    }

    @Override
    public void resize(int width, int height) {
        super.resize(width, height);
    }

    /**
     * Updates all entities, and then renders all entities
     */
    @Override
    public void render() {
        super.render();


        Gdx.gl.glClearColor(clearColor.x, clearColor.y, clearColor.z, 1);
        Gdx.gl.glClear(GL20.GL_COLOR_BUFFER_BIT);

        physWorld.update(Gdx.graphics.getDeltaTime()); // variable update rate. change to static if instability occurs

        handleQueue();

        float deltaTime = Gdx.graphics.getDeltaTime();
        for (Entity entity : entities) {
            entity.update(deltaTime);
        }

        uiStage.act();

        batch.setProjectionMatrix(gameCamera.combined);
        batch.begin();
        for (Entity entity : entities) {
            entity.render(batch);
        }
        batch.end();

        uiStage.draw();
    }

    @Override
    public void pause() {
        super.pause();
    }

    @Override
    public void resume() {
        super.resume();
    }

    @Override
    public void dispose() {
        super.dispose();

        for (Entity entity : entities) {
            entity.dispose();
        }
        uiStage.dispose();
        physWorld.removeAllBodiesAndJoints();
    }

    public void addEntity(Entity e) {
        entityQueue.put(e, ENTITY_INSERT);
    }

    public void removeEntity(Entity e) {
        entityQueue.put(e, ENTITY_REMOVE);
    }

    private void handleQueue(){
        Iterator it = entityQueue.entrySet().iterator();
        while(it.hasNext()){
            Map.Entry<Entity, Integer> entry = (Map.Entry) it.next();

            Entity e = entry.getKey();
            Integer type = entry.getValue();

            if(type == ENTITY_INSERT){
                e.setScene(this);
                e.added();
                entities.add(e);
            }

            if (type == ENTITY_REMOVE) {
                e.setScene(null);
                entities.remove(e);
                e.dispose();
            }

            it.remove();
        }
    }

    public ArrayList<Entity> getEntities() {
        return entities;
    }

    protected void setClearColor(Vector3 color) {
        this.clearColor = color;
    }

    protected SpriteBatch getSpriteBatch() {
        return batch;
    }

    protected Stage getUiStage() {
        return uiStage;
    }

    public OrthographicCamera getGameCamera() {
        return gameCamera;
    }

    public World getWorld() {
        return physWorld;
    }
}
