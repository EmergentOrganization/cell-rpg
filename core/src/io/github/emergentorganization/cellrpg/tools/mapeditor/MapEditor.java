package io.github.emergentorganization.cellrpg.tools.mapeditor;

import com.artemis.Aspect;
import com.artemis.ComponentMapper;
import com.artemis.Entity;
import com.artemis.World;
import com.artemis.utils.IntBag;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Input;
import com.badlogic.gdx.InputMultiplexer;
import com.badlogic.gdx.InputProcessor;
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
import io.github.emergentorganization.cellrpg.PixelonTransmission;
import io.github.emergentorganization.cellrpg.core.EntityID;
import io.github.emergentorganization.cellrpg.core.WorldFactory;
import io.github.emergentorganization.cellrpg.core.components.Bounds;
import io.github.emergentorganization.cellrpg.core.components.Name;
import io.github.emergentorganization.cellrpg.core.components.PhysicsBody;
import io.github.emergentorganization.cellrpg.core.components.Position;
import io.github.emergentorganization.cellrpg.core.entityfactory.EntityFactory;
import io.github.emergentorganization.cellrpg.core.systems.CameraSystem;
import io.github.emergentorganization.cellrpg.core.systems.InputSystem;
import io.github.emergentorganization.cellrpg.core.systems.RenderSystem;
import io.github.emergentorganization.cellrpg.managers.PhysicsSystem;
import io.github.emergentorganization.cellrpg.scenes.BaseScene;
import io.github.emergentorganization.cellrpg.tools.FileListNode;
import io.github.emergentorganization.cellrpg.tools.mapeditor.map.MapTools;
import io.github.emergentorganization.cellrpg.tools.mapeditor.renderables.BoundsGizmo;
import io.github.emergentorganization.cellrpg.tools.mapeditor.renderables.CornerGizmo;
import io.github.emergentorganization.cellrpg.tools.mapeditor.ui.EditorWindow;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.io.File;
import java.util.ArrayList;
import java.util.Collection;


public class MapEditor extends BaseScene implements InputProcessor {
    public static final float BB_THICKNESS = 0.05f; // Bounding box thickness of lines
    // TODO: move this with other scenes, and probably extend the WorldScene to reduce code duplication.
    private static final float AXIS_POLE_LENGTH = 10000.0f;
    private static final float MOVE_SPEED = 300.0f;
    private static final float MIN_ZOOM = 0.001f;
    private static final float ZOOM_AMT = 0.001f; // amount of zoom per key press
    private static final float AXIS_POLE_SIZE = 1.0f; // size of the axis poles denoting 0,0
    private static final float ZOOM_FACTOR = 0.001f;
    private static final float HIT_ACCURACY = 0.05f; // lower the value, the more accurate the hit detection
    private final OrthographicCamera gameCamera;
    private final InputMultiplexer multiplexer;
    private final EditorWindow window;
    private final Vector2 lastRMBClick = new Vector2(); // in UI space
    private final Vector2 lastLMBClick = new Vector2(); // in UI space
    private final ShapeRenderer shapeRenderer = new ShapeRenderer();
    private final Vector2 dragOffset = new Vector2();
    private final Vector3 dragPoint = new Vector3();
    private final Vector3 entityPos = new Vector3();
    private final Logger logger = LogManager.getLogger(getClass());
    private EditorTarget target;
    private CornerGizmo selectedGizmo;
    private boolean mapInputEnabled = true;
    private SpriteBatch batch;
    private World world;
    private EntityFactory entityFactory;
    private PhysicsSystem physicsSystem;
    private MapEditor editor;

    public MapEditor(final PixelonTransmission pt) {
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
        entityFactory.createEntityByID(EntityID.PLAYER, new Vector2(), 0);
        world.getSystem(PhysicsSystem.class).shouldRender(true);
        world.getSystem(InputSystem.class).setEnabled(false);
        world.getSystem(CameraSystem.class).setCamFollow(false);
    }

    public FileListNode[] getMaps() {
        final File folder = new File(MapTools.FOLDER_ROOT);

        if (folder.exists()) {
            final File[] files = folder.listFiles();
            if (files != null) {
                final FileListNode[] fileNodes = new FileListNode[files.length];
                for (int i = 0; i < files.length; i++) {
                    final File file = files[i];
                    if (file.getName().contains(MapTools.EXTENSION)) {
                        fileNodes[i] = new FileListNode(file);
                    }
                }

                return fileNodes;
            }
        }

        return null;
    }

    /**
     * Instantiates new entity
     *
     * @param pos Position in world-space
     */
    private void createNewEntity(final EntityID id, final Vector3 pos) {
        entityFactory.createEntityByID(id, new Vector2(pos.x, pos.y), 0.0f);
    }

    /**
     * Instantiates new entity
     *
     * @param pos Position in screen-space
     */
    public void createNewEntity(final EntityID id, final Vector2 pos) {
        final Vector3 worldSpace = stage.getCamera().unproject(new Vector3(lastRMBClick.x, lastRMBClick.y, 0.0f));
        final Vector3 unproject = gameCamera.unproject(new Vector3(worldSpace.x, worldSpace.y, 0.0f));
        createNewEntity(id, unproject);
    }

    @Override
    public void render(final float delta) {
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
            final Bounds bounds = target.getEntity().getComponent(Bounds.class);
            final Vector2 size = new Vector2(bounds.width, bounds.height); // stretch/shrink bounding box with rotation
            final Vector2 pos = target.getEntity().getComponent(Position.class).position.cpy();//.add(size.cpy().scl(0.5f));
            target.getBoundsGizmo().setPosition(pos);
            target.getBoundsGizmo().render(shapeRenderer);
        }

        shapeRenderer.end();

        super.render(delta);
    }

    public void clearMap() {
        for (final Integer id : world.getSystem(RenderSystem.class).getSortedEntityIds()) {
            world.delete(id);
        }

        target = null;
    }

    private void handleInput() {
        if (mapInputEnabled) {
            boolean update = false;

            final float speed = MOVE_SPEED * gameCamera.zoom * Gdx.graphics.getDeltaTime();
            if (Gdx.input.isKeyPressed(Input.Keys.W)) {
                update = true;
                gameCamera.position.add(0.0f, speed, 0.0f);
            } else if (Gdx.input.isKeyPressed(Input.Keys.S)) {
                update = true;
                gameCamera.position.add(0.0f, -speed, 0.0f);
            }

            if (Gdx.input.isKeyPressed(Input.Keys.D)) {
                update = true;
                gameCamera.position.add(speed, 0.0f, 0.0f);
            } else if (Gdx.input.isKeyPressed(Input.Keys.A)) {
                update = true;
                gameCamera.position.add(-speed, 0.0f, 0.0f);
            }

            if (Gdx.input.isKeyPressed(Input.Keys.Z)) { // zoom in
                update = true;
                gameCamera.zoom -= ZOOM_AMT;
            } else if (Gdx.input.isKeyPressed(Input.Keys.X)) { // zoom out
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

    private void setLastRMBClick(final Vector2 vec) {
        lastRMBClick.set(vec);
    }

    public Vector2 getLastLMBClick() {
        return lastLMBClick.cpy();
    }

    private void setLastLMBClick(final Vector2 vec) {
        lastLMBClick.set(vec);
    }

    private void setTarget(final EditorTarget target) {
        this.target = target;
        window.updateTransform(target.getEntity());
    }

    public void setMapInput(final boolean enable) {
        this.mapInputEnabled = enable;
    }

    private boolean isMapInputEnabled() {
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

    public void load(final String mapName) {
        clearMap();
        MapTools.importMap(mapName, entityFactory);
    }

    public EditorTarget getTarget() {
        return target;
    }

    private void setTarget(final Entity entity) {
        final Bounds bounds = entity.getComponent(Bounds.class);
        final Vector2 size = new Vector2(bounds.width, bounds.height);
        final Vector2 pos = entity.getComponent(Position.class).position.cpy();
        setTarget(new EditorTarget(new BoundsGizmo(size, pos), entity));
    }

    @Override
    public boolean keyDown(final int keycode) {
        return false;
    }

    @Override
    public boolean keyUp(final int keycode) {
        return false;
    }

    @Override
    public boolean keyTyped(final char character) {
        return false;
    }

    @Override
    public boolean touchDown(final int screenX, final int screenY, final int pointer, final int button) {
        if (isMapInputEnabled()) {
            final Vector2 screenPos = new Vector2(screenX, screenY);
            if (button == Input.Buttons.LEFT) {
                onLeftClick(screenPos);
            } else if (button == Input.Buttons.RIGHT) {
                onRightClick(screenPos);
            }
        }

        return false;
    }

    private void onLeftClick(final Vector2 screenPos) {
        if (mapInputEnabled) {
            final Vector3 uiVec = stage.getCamera().unproject(new Vector3(screenPos, 0f));
            final Vector2 click = new Vector2(uiVec.x, uiVec.y);
            setLastLMBClick(click);

            final Vector3 gameVec = gameCamera.unproject(new Vector3(screenPos.x, screenPos.y, 0f));
            final Rectangle hitBox = new Rectangle(gameVec.x - HIT_ACCURACY, gameVec.y - HIT_ACCURACY, HIT_ACCURACY, HIT_ACCURACY);

            if (target != null) {
                final CornerGizmo old = selectedGizmo;
                selectedGizmo = target.getBoundsGizmo().detectContains(hitBox);
                if (selectedGizmo != null) {
                    logger.trace("Gizmo selected"); //TODO
                } else if (old != null) {
                    logger.trace("Deselected Gizmo.");
                    detectNewTarget(hitBox);
                } else {
                    detectNewTarget(hitBox);
                }
            } else {
                detectNewTarget(hitBox);
            }
        }
    }

    /**
     * @param hitBox Hitbox in world space
     */
    private void detectNewTarget(final Rectangle hitBox) {
        final Entity entity = getEntityOnClick(hitBox);
        if (entity != null) {
            setTarget(entity);

            final Vector2 center = hitBox.getCenter(new Vector2());
            setDragOffset(dragPoint.set(center, 0), entityPos.set(entity.getComponent(Position.class).position, 0));
        } else {
            target = null;
        }
    }

    private void detectGizmoClick(final EditorTarget target, final Rectangle hitBox) {
        this.selectedGizmo = target.getBoundsGizmo().detectContains(hitBox);

    }

    private Entity getEntityOnClick(final Rectangle hitBox) {
        Entity entity;
        entity = detectPhysicsClick(hitBox);
        if (entity == null) {
            entity = detectImageClick(hitBox);
        }

        return entity;
    }

    private Entity detectPhysicsClick(final Rectangle hitBox) {
        final Collection<Integer> entities = new ArrayList<Integer>(5);
        physicsSystem.queryAABB(new QueryCallback() {
            @Override
            public boolean reportFixture(final Fixture fixture) {
                final Body body = fixture.getBody();
                entities.add((Integer) body.getUserData());

                return false;
            }
        }, hitBox.x, hitBox.y, hitBox.x + hitBox.getWidth(), hitBox.y + hitBox.getHeight());

        if (entities.size() > 0)
            return world.getEntity((Integer) entities.toArray()[0]);
        else
            return null;
    }

    private Entity detectImageClick(final Rectangle hitBox) {
        final ComponentMapper<Bounds> bm = world.getMapper(Bounds.class);
        final ComponentMapper<Position> pm = world.getMapper(Position.class);
        final IntBag bag = world.getAspectSubscriptionManager().get(Aspect.all().exclude(PhysicsBody.class)).getEntities();
        for (int i = 0; i < bag.size(); i++) {
            final int id = bag.get(i);
            final Bounds bounds = bm.get(id);
            logger.trace(world.getMapper(Name.class).get(id).friendlyName);
            final Vector2 pos = pm.get(id).position;
            final Rectangle rect = new Rectangle(pos.x, pos.y, bounds.width, bounds.height);
            if (rect.contains(hitBox))
                return world.getEntity(id);
        }
        return null;
    }

    private void onRightClick(final Vector2 screenPos) {
        if (mapInputEnabled) {
            final Vector3 worldPos = stage.getCamera().unproject(new Vector3(screenPos.x, screenPos.y, 0f));
            setLastRMBClick(new Vector2(worldPos.x, worldPos.y));
            window.openContextMenu(lastRMBClick);
        }
    }

    /**
     * @param mousePos   Mouse position in world space
     * @param startPoint Drag start position in world space
     */
    private void setDragOffset(final Vector3 mousePos, final Vector3 startPoint) {
        dragOffset.set(startPoint.x - mousePos.x, startPoint.y - mousePos.y);
    }

    @Override
    public boolean touchUp(final int screenX, final int screenY, final int pointer, final int button) {
        selectedGizmo = null;
        return false;
    }

    @Override
    public boolean touchDragged(final int screenX, final int screenY, final int pointer) {
        if (mapInputEnabled) {
            final Vector3 gameVec = gameCamera.unproject(new Vector3(screenX, screenY, 0f));

            if (target != null) {
                final Entity mapTarget = target.getEntity();

                if (selectedGizmo != null) {
                    logger.trace("Scaling entity by " + dragOffset);
                } else {
                    final Body body = world.getSystem(PhysicsSystem.class).getBody(mapTarget.getId());
                    if (body != null) {
                        body.setTransform(gameVec.x + dragOffset.x, gameVec.y + dragOffset.y, body.getAngle());
                    } else
                        mapTarget.getComponent(Position.class).position.set(gameVec.x + dragOffset.x, gameVec.y + dragOffset.y);
                    window.updateTransform(mapTarget);
                }
            }
        }
        return false;
    }

    @Override
    public boolean mouseMoved(final int screenX, final int screenY) {
        return false;
    }

    @Override
    public boolean scrolled(final int amount) {
        if (mapInputEnabled) {
            gameCamera.zoom += amount * ZOOM_FACTOR;
            if (gameCamera.zoom <= 0) gameCamera.zoom = MIN_ZOOM;
            gameCamera.update();
        }
        return false;
    }
}
