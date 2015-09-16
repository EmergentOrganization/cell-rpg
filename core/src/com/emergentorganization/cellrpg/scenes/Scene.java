package com.emergentorganization.cellrpg.scenes;

/**
 * Created by BrianErikson on 6/2/2015.
 */

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.InputMultiplexer;
import com.badlogic.gdx.Screen;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.graphics.OrthographicCamera;
import com.badlogic.gdx.graphics.Pixmap;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.graphics.glutils.FrameBuffer;
import com.badlogic.gdx.graphics.glutils.ShapeRenderer;
import com.badlogic.gdx.math.Vector3;
import com.badlogic.gdx.scenes.scene2d.Stage;
import com.badlogic.gdx.utils.viewport.FitViewport;
import com.emergentorganization.cellrpg.components.GlobalComponent;
import com.emergentorganization.cellrpg.entities.Entity;
import com.emergentorganization.cellrpg.entities.EntitySort;
import com.emergentorganization.cellrpg.scenes.submenus.ProfilerHUD;
import com.emergentorganization.cellrpg.entities.characters.Player;
import com.emergentorganization.cellrpg.physics.listeners.StaticCollisionListener;
import com.emergentorganization.cellrpg.scenes.listeners.EntityActionListener;
import com.emergentorganization.cellrpg.tools.physics.BodyLoader;
import com.emergentorganization.cellrpg.tools.postprocessing.PostProcessor;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.dyn4j.dynamics.World;

import java.util.ArrayList;
import java.util.Collections;

/**
 * Think of this like the stage or level; used to update every entity in the stage, as well as render the world
 */
public abstract class Scene implements Screen {
    public static float scale = 1/10f;
    public long renderTime = 0;  // est time used to render

    private final Logger logger = LogManager.getLogger(getClass());

    private ArrayList<Entity> entities;
    private ArrayList<GlobalComponent> comps;

    private ArrayList<EntityActionListener> entityActionListeners;

    private ArrayList<Entity> entityAddQueue;
    private ArrayList<Entity> entityRemoveQueue;

    private SpriteBatch batch; // sprite batch for entities
    private SpriteBatch outBatch;
    private ShapeRenderer debugRenderer;
    private Vector3 clearColor;
    private FrameBuffer frameBuffer;
    private ArrayList<PostProcessor> postProcessors;

    private Stage uiStage; // stage which handles all UI Actors
    private OrthographicCamera gameCamera;
    private World physWorld;
    private static final double WORLD_WIDTH = 10000d;
    private static final double WORLD_HEIGHT = 10000d;
    private InputMultiplexer input;

    private boolean isEditor = false;
    private TextureRegion fboRegion;


    public void create() {
        logger.info("Creating scene");
        entities = new ArrayList<Entity>();
        comps = new ArrayList<GlobalComponent>();

        entityActionListeners = new ArrayList<EntityActionListener>();

        entityAddQueue = new ArrayList<Entity>();
        entityRemoveQueue = new ArrayList<Entity>();

        batch = new SpriteBatch();
        outBatch = new SpriteBatch();
        debugRenderer = new ShapeRenderer();
        debugRenderer.setAutoShapeType(true);
        clearColor = new Vector3(0,0,0);
        frameBuffer = new FrameBuffer(Pixmap.Format.RGBA8888, Gdx.graphics.getWidth(), Gdx.graphics.getHeight(), true);
        postProcessors = new ArrayList<PostProcessor>();
        Texture cb = frameBuffer.getColorBufferTexture();
        fboRegion = new TextureRegion(cb, 0, 0, cb.getWidth(), cb.getHeight());
        fboRegion.flip(false, true); // FBO uses lower left, TextureRegion uses upper-left
        uiStage = new Stage(new FitViewport(Gdx.graphics.getWidth(), Gdx.graphics.getHeight()));

        input = new InputMultiplexer();
        input.addProcessor(uiStage);

        Gdx.input.setInputProcessor(input);

        gameCamera = new OrthographicCamera(Gdx.graphics.getWidth() * scale, Gdx.graphics.getHeight() * scale);
        gameCamera.position.set(gameCamera.viewportWidth / 2f, gameCamera.viewportHeight / 2f, 0); // center camera with 0,0 in bottom left
        gameCamera.update();
        batch.setProjectionMatrix(gameCamera.combined);
        logger.info("init game camera " + gameCamera.viewportWidth + "x" + gameCamera.viewportHeight);


        logger.info("parsing external physics meshes");
        BodyLoader.fetch(); // initialize bodyLoader if it isn't already

        physWorld = new World();
        physWorld.addListener(new StaticCollisionListener());
        //physWorld.shiftCoordinates(new Vector2(WORLD_WIDTH / 2d, WORLD_HEIGHT / 2d));

        getUiStage().addActor(new ProfilerHUD(this));
    }

    @Override
    public void show() {
        // check gameState for android-app-hiding instances
        logger.info("show event triggered");
        if (batch == null) {
            create();
        }
    }

    @Override
    public void resize(int width, int height) {
        // TODO
        logger.info("resize event triggered");
        // uiStage.getViewport().update(width, height, true);
    }

    /**
     * Updates all entities, and then renders all entities
     */
    @Override
    public void render(float delta) {
        long now = System.currentTimeMillis();

        if (!isEditor) physWorld.update(delta); // variable update rate. change to static if instability occurs

        handleQueue();

        for ( GlobalComponent comp : comps){
            comp.update(delta);
        }

        for (Entity entity : entities) {
            entity.update(delta);
        }

        uiStage.act();

        frameBuffer.begin();
        Gdx.gl.glClearColor(clearColor.x, clearColor.y, clearColor.z, 1);
        Gdx.gl.glClear(GL20.GL_COLOR_BUFFER_BIT);

        batch.setProjectionMatrix(gameCamera.combined);
        batch.begin();
        for (Entity entity : entities) {
            entity.render(batch);
        }
        batch.end();

        debugRenderer.setProjectionMatrix(gameCamera.combined);  // this should be uncommented, but doing so breaks cagrid...
        debugRenderer.begin();
        for (Entity entity : entities) {
            entity.debugRender(debugRenderer);
        }
        debugRenderer.end();
        frameBuffer.end();

        for (PostProcessor pp : postProcessors) {
            pp.render(frameBuffer);
        }

        // Render final texture to screen
        outBatch.begin();
        Gdx.gl.glClearColor(clearColor.x, clearColor.y, clearColor.z, 1);
        Gdx.gl.glClear(GL20.GL_COLOR_BUFFER_BIT);
        outBatch.draw(fboRegion, 0, 0);
        outBatch.end();

        renderTime = System.currentTimeMillis() - now;
    }

    @Override
    public void hide() {
        // TODO
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
        logger.info("disposing");
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
        logger.info("adding entity " + e.getClass());
        entityAddQueue.add(e);
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
        logger.info("removing entity " + e.getClass());
        entityRemoveQueue.add(e);
    }

    public void addComponent(GlobalComponent comp){
        logger.info("adding GlobalComponent " + comp.getClass());
        comps.add(comp);
        comp.setScene(this);
        comp.added();
    }

    private void handleQueue() {
        // Using standard for-loop to avoid ConcurrentModificationException when listeners modify the queues
        for (int i = 0; i < entityAddQueue.size(); i++) {
            Entity e = entityAddQueue.get(i);
            e.setScene(this);
            e.added();
            entities.add(e);

            for (EntityActionListener listener : entityActionListeners) {
                if (listener.getEntityClass() == e.getClass()) {
                    listener.onAdd();
                }
            }

            Collections.sort(entities, new EntitySort());
        }

        for (int i = 0; i < entityRemoveQueue.size(); i++) {
            Entity e = entityRemoveQueue.get(i);
            e.setScene(null);
            entities.remove(e);
            e.dispose();

            for (EntityActionListener listener : entityActionListeners) {
                if (listener.getEntityClass() == e.getClass()) {
                    listener.onRemove();
                }
            }
        }

        entityAddQueue.clear();
        entityRemoveQueue.clear();
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

    public ArrayList<EntityActionListener> getEntityActionListeners() {
        return entityActionListeners;
    }

    public void addEntityListener(EntityActionListener listener) {
        entityActionListeners.add(listener);
    }

    public void removeEntityActionListener(EntityActionListener listener) {
        entityActionListeners.remove(listener);
    }

    public ArrayList<PostProcessor> getPostProcessors() {
        return postProcessors;
    }

    public void addPostProcessor(PostProcessor... postProcessors) {
        Collections.addAll(this.postProcessors, postProcessors);
    }
}