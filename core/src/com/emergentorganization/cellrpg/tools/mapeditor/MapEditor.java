package com.emergentorganization.cellrpg.tools.mapeditor;

import com.artemis.Aspect;
import com.artemis.ComponentMapper;
import com.artemis.Entity;
import com.artemis.World;
import com.artemis.utils.IntBag;
import com.badlogic.gdx.*;
import com.badlogic.gdx.Input;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.graphics.OrthographicCamera;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.glutils.ShapeRenderer;
import com.badlogic.gdx.math.Rectangle;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.math.Vector3;
import com.badlogic.gdx.physics.box2d.Body;
import com.badlogic.gdx.physics.box2d.Fixture;
import com.badlogic.gdx.physics.box2d.QueryCallback;
import com.badlogic.gdx.scenes.scene2d.Stage;
import com.emergentorganization.cellrpg.PixelonTransmission;
import com.emergentorganization.cellrpg.components.*;
import com.emergentorganization.cellrpg.core.EntityID;
import com.emergentorganization.cellrpg.core.entityfactory.EntityFactory;
import com.emergentorganization.cellrpg.core.WorldFactory;
import com.emergentorganization.cellrpg.managers.PhysicsSystem;
import com.emergentorganization.cellrpg.scenes.BaseScene;
import com.emergentorganization.cellrpg.systems.CameraSystem;
import com.emergentorganization.cellrpg.systems.InputSystem;
import com.emergentorganization.cellrpg.systems.RenderSystem;
import com.emergentorganization.cellrpg.tools.FileListNode;
import com.emergentorganization.cellrpg.tools.mapeditor.map.MapTools;
import com.emergentorganization.cellrpg.tools.mapeditor.renderables.BoundsGizmo;
import com.emergentorganization.cellrpg.tools.mapeditor.ui.*;

import java.io.File;
import java.util.ArrayList;
import java.util.Collection;

/**
 * Created by BrianErikson on 6/14/2015.
 */
public class MapEditor extends BaseScene implements InputProcessor {
    public static final float AXIS_POLE_LENGTH = 10000.0f;
    private final OrthographicCamera gameCamera;
    private final InputMultiplexer multiplexer;
    private final EditorWindow window;

    public static final float MOVE_SPEED = 300.0f;
    public static final float MIN_ZOOM = 0.001f;
    public static final float ZOOM_AMT = 0.001f; // amount of zoom per keypress

    private static final float AXIS_POLE_SIZE = 1.0f; // size of the axis poles denoting 0,0
    public static final float BB_THICKNESS = 0.05f; // Bounding box thickness of lines

    private final Vector2 lastRMBClick = new Vector2(); // in UI space
    private final Vector2 lastLMBClick = new Vector2(); // in UI space
    private final ShapeRenderer shapeRenderer = new ShapeRenderer();

    private EditorTarget target;

    private boolean mapInputEnabled = true;
    private SpriteBatch batch;
    private World world;
    private EntityFactory entityFactory;
    private PhysicsSystem physicsSystem;


    private static final float ZOOM_FACTOR = 0.001f;
    private MapEditor editor;
    public static final float HIT_ACCURACY =  0.05f; // lower the value, the more accurate the hit detection
    private Vector2 dragOffset = new Vector2();

    public MapEditor(PixelonTransmission pt) {
        super(pt);

        initArtemis();

        window = new EditorWindow(pt, this, stage, world);

        gameCamera = (OrthographicCamera) world.getSystem(CameraSystem.class).getGameCamera();

        multiplexer = new InputMultiplexer(stage, this);
        Gdx.input.setInputProcessor(multiplexer);
    }

    @Override
    public void show() {
        super.show();
        Gdx.input.setInputProcessor(multiplexer);
    }

    private void initArtemis() {
        batch = new SpriteBatch();
        entityFactory = new EntityFactory();
        world = WorldFactory.editorGameWorld(pt, batch, stage, entityFactory);

        physicsSystem = world.getSystem(PhysicsSystem.class);
        entityFactory.createPlayer(0, 0);
        world.getSystem(PhysicsSystem.class).shouldRender(true);
        world.getSystem(InputSystem.class).setEnabled(false);
        world.getSystem(CameraSystem.class).setCamFollow(false);
    }

    public FileListNode[] getMaps() {
        File folder = new File(MapTools.FOLDER_ROOT);

        if (folder.exists()) {
            File[] files = folder.listFiles();
            FileListNode[] fileNodes = new FileListNode[files.length];
            for (int i = 0; i < files.length; i++) {
                File file = files[i];
                if (file.getName().contains(MapTools.EXTENSION)) {
                    fileNodes[i] = new FileListNode(file);
                }
            }

            return fileNodes;
        }

        return null;
    }

    /**
     * Instantiates new entity
     * @param pos Position in world-space
     */
    public void createNewEntity(EntityID id, Vector3 pos) {
        entityFactory.createEntityByID(id, new Vector2(pos.x, pos.y), 0.0f);
    }

    /**
     * Instantiates new entity
     * @param pos Position in screen-space
     */
    public void createNewEntity(EntityID id, Vector2 pos) {
        Vector3 worldSpace = stage.getCamera().unproject(new Vector3(lastRMBClick.x, lastRMBClick.y, 0.0f));
        Vector3 unproject = gameCamera.unproject(new Vector3(worldSpace.x, worldSpace.y, 0.0f));
        createNewEntity(id, unproject);
    }

    @Override
    public void render(float delta) {
        handleInput();

        Gdx.gl.glClear(GL20.GL_COLOR_BUFFER_BIT);

        world.setDelta(delta);
        world.process();

        shapeRenderer.setProjectionMatrix(gameCamera.combined);
        shapeRenderer.begin(ShapeRenderer.ShapeType.Filled);

        // x / y axis
        shapeRenderer.rect(0.0f, 0.0f, AXIS_POLE_LENGTH, AXIS_POLE_SIZE * gameCamera.zoom, Color.GRAY, Color.GRAY, Color.GRAY, Color.GRAY);
        shapeRenderer.rect(0.0f, 0.0f, AXIS_POLE_SIZE * gameCamera.zoom, AXIS_POLE_LENGTH, Color.GRAY, Color.GRAY, Color.GRAY, Color.GRAY);

        // selected object bounds
        if (target != null) {
            Bounds bounds = target.getEntity().getComponent(Bounds.class);
            Vector2 size = new Vector2(bounds.width, bounds.height); // stretch/shrink bounding box with rotation
            Vector2 pos = target.getEntity().getComponent(Position.class).position.cpy();//.add(size.cpy().scl(0.5f));
            target.getBoundsGizmo().setPosition(pos);
            target.getBoundsGizmo().render(shapeRenderer);
        }

        shapeRenderer.end();

        super.render(delta);
    }

    @Override
    protected boolean shouldStash() {
        return false;
    }

    public void clearMap() {
        for (Integer id : world.getSystem(RenderSystem.class).getSortedEntityIds()) {
            world.delete(id);
        }

        target = null;
    }

    private void handleInput() {
        if (mapInputEnabled) {
            boolean update = false;

            float speed = MOVE_SPEED * gameCamera.zoom * Gdx.graphics.getDeltaTime();
            if (Gdx.input.isKeyPressed(Input.Keys.W)) {
                update = true;
                gameCamera.position.add(0.0f, speed, 0.0f);
            }
            else if (Gdx.input.isKeyPressed(Input.Keys.S)) {
                update = true;
                gameCamera.position.add(0.0f, -speed, 0.0f);
            }

            if (Gdx.input.isKeyPressed(Input.Keys.D)) {
                update = true;
                gameCamera.position.add(speed, 0.0f, 0.0f);
            }
            else if (Gdx.input.isKeyPressed(Input.Keys.A)) {
                update = true;
                gameCamera.position.add(-speed, 0.0f, 0.0f);
            }

            if (Gdx.input.isKeyPressed(Input.Keys.Z)) { // zoom in
                update = true;
                gameCamera.zoom -= ZOOM_AMT;
            }
            else if (Gdx.input.isKeyPressed(Input.Keys.X)) { // zoom out
                update = true;
                gameCamera.zoom += ZOOM_AMT;
            }

            if (update) gameCamera.update();

            if (Gdx.input.isKeyJustPressed(Input.Keys.FORWARD_DEL) && target != null) {
                target.getEntity().deleteFromWorld();
                target = null;
            }
        }
    }

    @Override
    public void hide() {

    }

    public Vector2 getLastRMBClick() {
        return lastRMBClick.cpy();
    }

    public void setLastRMBClick(Vector2 vec) {
        lastRMBClick.set(vec);
    }

    public Vector2 getLastLMBClick() {
        return lastLMBClick.cpy();
    }

    public void setLastLMBClick(Vector2 vec) {
        lastLMBClick.set(vec);
    }

    public void setTarget(EditorTarget target) {
        this.target = target;
        window.updateTransform(target.getEntity());
    }

    private void setTarget(Entity entity) {
        Bounds bounds = entity.getComponent(Bounds.class);
        Vector2 size = new Vector2(bounds.width, bounds.height);
        Vector2 pos = entity.getComponent(Position.class).position.cpy();
        setTarget(new EditorTarget(new BoundsGizmo(size, pos), entity));
    }

    public void setMapInput(boolean enable) {
        this.mapInputEnabled = enable;
    }

    public boolean isMapInputEnabled() {
        return mapInputEnabled;
    }

    public Stage getUiStage() {
        return stage;
    }

    public OrthographicCamera getGameCamera() {
        return gameCamera;
    }

    public World getWorld() {
        return world;
    }

    public PhysicsSystem getPhysicsSystem() {
        return physicsSystem;
    }


    public void load(String mapName) {
        clearMap();
        MapTools.importMap(mapName, entityFactory);
    }

    public EditorTarget getTarget() {
        return target;
    }

    @Override
    public boolean keyDown(int keycode) {
        return false;
    }

    @Override
    public boolean keyUp(int keycode) {
        return false;
    }

    @Override
    public boolean keyTyped(char character) {
        return false;
    }

    @Override
    public boolean touchDown(int screenX, int screenY, int pointer, int button) {
        if (isMapInputEnabled()) {
            Vector2 screenCoords = new Vector2(screenX, screenY);
            if (button == Input.Buttons.LEFT) {
                onLeftClick(screenCoords);
            }
            else if (button == Input.Buttons.RIGHT) {
                onRightClick(screenCoords);
            }
        }

        return false;
    }

    private void onLeftClick(Vector2 screenCoords) {
        if (mapInputEnabled) {
            Vector3 uiVec = stage.getCamera().unproject(new Vector3(screenCoords, 0f));
            Vector2 click = new Vector2(uiVec.x, uiVec.y);
            setLastLMBClick(click);

            Vector3 gameVec = gameCamera.unproject(new Vector3(screenCoords.x, screenCoords.y, 0f));
            Rectangle hitBox = new Rectangle(gameVec.x - HIT_ACCURACY, gameVec.y - HIT_ACCURACY, HIT_ACCURACY, HIT_ACCURACY);

            if (target != null) {
                BoundsGizmo.GizmoTrigger trigger = detectGizmoClick(target, hitBox);
                if (trigger != null) {
                    System.out.println("GIZMO!");
                } else {
                    detectNewTarget(hitBox, screenCoords);
                }
            } else {
                detectNewTarget(hitBox, screenCoords);
            }
        }
    }

    private void detectNewTarget(Rectangle hitBox, Vector2 screenCoords) {
        Entity entity = detectEntityClick(hitBox);
        if (entity != null) {
            setTarget(entity);
            setDragOffset(screenCoords);
        } else {
            target = null;
        }
    }

    private BoundsGizmo.GizmoTrigger detectGizmoClick(EditorTarget target, Rectangle hitBox) {
        BoundsGizmo.GizmoTrigger gizmoTrigger = target.getBoundsGizmo().detectContains(hitBox);

        if (gizmoTrigger != null) {
            return gizmoTrigger;
        }

        return null;
    }

    private Entity detectEntityClick(Rectangle hitBox) {
        Entity entity;
        entity = detectPhysicsClick(hitBox);
        if (entity == null) {
            entity = detectImageClick(hitBox);
        }

        return entity;
    }

    private Entity detectPhysicsClick(Rectangle hitBox) {
        final Collection<Integer> entities = new ArrayList<Integer>(5);
        physicsSystem.queryAABB(new QueryCallback() {
            @Override
            public boolean reportFixture(Fixture fixture) {
                Body body = fixture.getBody();
                entities.add((Integer) body.getUserData());

                return false;
            }
        }, hitBox.x, hitBox.y, hitBox.x + hitBox.getWidth(), hitBox.y + hitBox.getHeight());

        if (entities.size() > 0)
            return world.getEntity((Integer)entities.toArray()[0]);
        else
            return null;
    }

    private Entity detectImageClick(Rectangle hitBox) {
        ComponentMapper<Bounds> bm = world.getMapper(Bounds.class);
        ComponentMapper<Position> pm = world.getMapper(Position.class);
        IntBag bag = world.getAspectSubscriptionManager().get(Aspect.all().exclude(PhysicsBody.class)).getEntities();
        for (int i = 0; i < bag.size(); i++) {
            int id = bag.get(i);
            Bounds bounds = bm.get(id);
            Vector2 pos = pm.get(id).position;
            Rectangle rect = new Rectangle(pos.x, pos.y, bounds.width, bounds.height);
            if (rect.contains(hitBox))
                return world.getEntity(id);
        }
        return null;
    }

    private void onRightClick(Vector2 screenPos) {
        if (mapInputEnabled) {
            Vector3 worldPos = stage.getCamera().unproject(new Vector3(screenPos.x, screenPos.y, 0f));
            setLastRMBClick(new Vector2(worldPos.x, worldPos.y));
            window.openContextMenu(lastRMBClick);
        }
    }

    private void setDragOffset(Vector2 mousePos) {
        Vector3 gameVec = gameCamera.unproject(new Vector3(mousePos.x, mousePos.y, 0f));

        if (target != null) {
            Entity mapTarget = target.getEntity();
            Vector2 targetPos = mapTarget.getComponent(Position.class).position;
            dragOffset = new Vector2(targetPos.x - gameVec.x, targetPos.y - gameVec.y);
        }
        else {
            throw new RuntimeException("Cannot set drag offset when the editor has no target");
        }
    }

    @Override
    public boolean touchUp(int screenX, int screenY, int pointer, int button) {
        return false;
    }

    @Override
    public boolean touchDragged(int screenX, int screenY, int pointer) {
        if (mapInputEnabled) {
            Vector3 gameVec = gameCamera.unproject(new Vector3(screenX, screenY, 0f));

            if (target != null) {
                Entity mapTarget = target.getEntity();
                Body body = world.getSystem(PhysicsSystem.class).getBody(mapTarget.getId());
                if (body != null) {
                    body.setTransform(gameVec.x + dragOffset.x, gameVec.y + dragOffset.y, body.getAngle());
                }
                else
                    mapTarget.getComponent(Position.class).position.set(gameVec.x + dragOffset.x, gameVec.y + dragOffset.y);
                window.updateTransform(mapTarget);
            }
        }
        return false;
    }

    @Override
    public boolean mouseMoved(int screenX, int screenY) {
        return false;
    }

    @Override
    public boolean scrolled(int amount) {
        if (mapInputEnabled) {
            gameCamera.zoom += amount * ZOOM_FACTOR;
            if (gameCamera.zoom <= 0) gameCamera.zoom = MIN_ZOOM;
            gameCamera.update();
        }
        return false;
    }
}
