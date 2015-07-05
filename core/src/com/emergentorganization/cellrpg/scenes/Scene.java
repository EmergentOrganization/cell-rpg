package com.emergentorganization.cellrpg.scenes;

/**
 * Created by BrianErikson on 6/2/2015.
 */

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.InputMultiplexer;
import com.badlogic.gdx.Screen;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.graphics.OrthographicCamera;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.glutils.ShapeRenderer;
import com.badlogic.gdx.math.Vector3;
import com.badlogic.gdx.scenes.scene2d.Stage;
import com.badlogic.gdx.utils.viewport.FitViewport;
import com.emergentorganization.cellrpg.components.GlobalComponent;
import com.emergentorganization.cellrpg.entities.Entity;
import com.emergentorganization.cellrpg.entities.EntitySort;
import com.emergentorganization.cellrpg.entities.characters.Player;
import com.emergentorganization.cellrpg.tools.physics.BodyLoader;
import org.dyn4j.dynamics.World;

import java.util.ArrayList;
import java.util.Collections;

/**
 * Think of this like the stage or level; used to update every entity in the stage, as well as render the world
 */
public abstract class Scene implements Screen {
    public static float scale = 1/10f;

    private ArrayList<Entity> entities;
    private ArrayList<Entity> removeQueue;
    private ArrayList<GlobalComponent> comps;

    private SpriteBatch batch; // sprite batch for entities
    private ShapeRenderer debugRenderer;
    private Vector3 clearColor;
    private Stage uiStage; // stage which handles all UI Actors
    private OrthographicCamera gameCamera;
    private World physWorld;
    private static final double WORLD_WIDTH = 10000d;
    private static final double WORLD_HEIGHT = 10000d;
    private InputMultiplexer input;

    private boolean isEditor = false;


    public void create() {
        entities = new ArrayList<Entity>();
        comps = new ArrayList<GlobalComponent>();
        removeQueue = new ArrayList<Entity>();

        batch = new SpriteBatch();
        debugRenderer = new ShapeRenderer();
        debugRenderer.setAutoShapeType(true);
        clearColor = new Vector3(0,0,0);
        uiStage = new Stage(new FitViewport(Gdx.graphics.getWidth(), Gdx.graphics.getHeight()));

        input = new InputMultiplexer();
        input.addProcessor(uiStage);

        Gdx.input.setInputProcessor(input);

        gameCamera = new OrthographicCamera(Gdx.graphics.getWidth() * scale, Gdx.graphics.getHeight() * scale);
        gameCamera.position.set(gameCamera.viewportWidth / 2f, gameCamera.viewportHeight / 2f, 0); // center camera with 0,0 in bottom left
        gameCamera.update();
        batch.setProjectionMatrix(gameCamera.combined);

        BodyLoader.fetch(); // initialize bodyLoader if it isn't already

        physWorld = new World();
        //physWorld.shiftCoordinates(new Vector2(WORLD_WIDTH / 2d, WORLD_HEIGHT / 2d));
    }

    @Override
    public void show() {
        // check gameState for android-app-hiding instances
        if (batch == null) {
            create();
        }
    }

    @Override
    public void resize(int width, int height) {
        // TODO
        // uiStage.getViewport().update(width, height, true);
    }

    /**
     * Updates all entities, and then renders all entities
     */
    @Override
    public void render(float delta) {
        Gdx.gl.glClearColor(clearColor.x, clearColor.y, clearColor.z, 1);
        Gdx.gl.glClear(GL20.GL_COLOR_BUFFER_BIT);

        if (!isEditor) physWorld.update(delta); // variable update rate. change to static if instability occurs

        handleQueue();

        for ( GlobalComponent comp : comps){
            comp.update(delta);
        }

        for (Entity entity : entities) {
            entity.update(delta);
        }

        uiStage.act();

        batch.setProjectionMatrix(gameCamera.combined);
        batch.begin();
        for (Entity entity : entities) {
            entity.render(batch);
        }
        batch.end();

        debugRenderer.setProjectionMatrix(gameCamera.combined);
        debugRenderer.begin();
        for (Entity entity : entities) {
            entity.debugRender(debugRenderer);
        }
        debugRenderer.end();
    }

    @Override
    public void pause() {
        // TODO
    }

    @Override
    public void resume() {
        // TODO
    }

    @Override
    public void dispose() {
        for (Entity entity : entities) {
            entity.dispose();
        }
        uiStage.dispose();
        physWorld.removeAllBodiesAndJoints();
    }

    public Entity getPlayer(){
        for (int i = 0; i < entities.size(); i++){
            if (entities.get(i) instanceof Player){
                return entities.get(i);
            }
        } // else
        throw new UnsupportedOperationException("cannot getPlayer(); no player in scene");
        // TODO: this should be changed to NoPlayerFoundException or similar...
    }

    public void drawUI() {
        uiStage.draw();
    }

    public void addEntity(Entity e) {
        e.setScene(this);
        System.out.println("adding entity...");
        entities.add(e);
        System.out.println(e);
        e.added();
        System.out.println("done");
        Collections.sort(entities, new EntitySort());
    }

    public void addEntities (Entity... e) {
        for (Entity entity : e) {
            addEntity(entity);
        }
    }

    public void addEntities(ArrayList<Entity> entities) {
        for (Entity entity : entities) {
            addEntity(entity);
        }
    }

    public void removeEntity(Entity e) {
        removeQueue.add(e);
    }

    public void addComponent(GlobalComponent comp){
        comps.add(comp);
    }

    protected void handleQueue(){
        for (Entity entity : removeQueue) {
            entity.setScene(null);
            entities.remove(entity);
            entity.dispose();
        }

        removeQueue.clear();
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

    public Stage getUiStage() {
        return uiStage;
    }

    public OrthographicCamera getGameCamera() {
        return gameCamera;
    }

    public World getWorld() {
        return physWorld;
    }

    public InputMultiplexer getInputMultiplexer() {
        return input;
    }

    protected void setToEditor() {
        isEditor = true;
    }

    public boolean isEditor() {
        return isEditor;
    }

}
