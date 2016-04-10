package io.github.emergentorganization.cellrpg.managers;

import com.artemis.Aspect;
import com.artemis.BaseEntitySystem;
import com.artemis.ComponentMapper;
import com.artemis.annotations.Wire;
import com.badlogic.gdx.graphics.g2d.Batch;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.physics.box2d.*;
import io.github.emergentorganization.emergent2dcore.PixelonTransmission;
import io.github.emergentorganization.emergent2dcore.components.Bounds;
import io.github.emergentorganization.emergent2dcore.components.PhysicsBody;
import io.github.emergentorganization.emergent2dcore.components.Visual;
import io.github.emergentorganization.emergent2dcore.systems.CameraSystem;
import io.github.emergentorganization.cellrpg.tools.physics.BodyEditorLoader;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import javax.annotation.Nullable;
import java.util.HashMap;


@Wire
public class PhysicsSystem extends BaseEntitySystem {
    private final BodyEditorLoader bodyLoader;
    private final World physWorld;
    private final Box2DDebugRenderer renderer;
    private CameraSystem cs;
    private Batch batch;
    private AssetManager assetManager;
    private ComponentMapper<Visual> vm;
    private ComponentMapper<Bounds> bm;
    private ComponentMapper<PhysicsBody> pm;
    private HashMap<Integer, Body> bodies;
    private boolean shouldRender = false;

    private final Logger logger = LogManager.getLogger(getClass());

    public PhysicsSystem(BodyEditorLoader bodyLoader, @Nullable Batch batch) {
        super(Aspect.all(PhysicsBody.class));
        this.physWorld = new World(Vector2.Zero, true);
        this.batch = batch;
        renderer = new Box2DDebugRenderer(true, true, true, true, true, true);
        this.bodyLoader = bodyLoader;
        bodies = new HashMap<Integer, Body>();
    }

    public Body createBody(int entityId, String colliderId, BodyDef bd, FixtureDef fd) {
        Body body = createEmptyBody(entityId, bd);
        Bounds b = bm.get(entityId);
        float scale;
        if (b.height < b.width)
            scale = (Math.max(b.width, b.height) / Math.min(b.width, b.height)) * Math.min(b.width, b.height);
        else
            scale = Math.min(b.width, b.height);
        bodyLoader.attachFixture(body, colliderId, fd, scale);
        bodies.put(entityId, body);
        return body;
    }

    private Body createEmptyBody(int entityId, BodyDef bd) throws IllegalArgumentException{
        if (!pm.has(entityId))
            throw new RuntimeException("Cannot create a body for an entity without a PhysicsBody component");

        if (bd == null){
            throw new IllegalArgumentException("cannot create body with null bodyDef");
        }

        Body body = physWorld.createBody(bd);
        body.setUserData(entityId);
        return body;
    }

    public Body createBoundsBody(int entityId, BodyDef bd, FixtureDef fd) {
        if (!pm.has(entityId))
            throw new RuntimeException("Cannot create a body for an entity without a PhysicsBody component");
        Body body = createEmptyBody(entityId, bd);
        Bounds b = bm.get(entityId);
        PolygonShape rect = new PolygonShape();
        final Vector2 origin = new Vector2(-0.5f, -0.5f);
        rect.set(new float[]{
                origin.x, origin.y,
                b.width + origin.x, origin.y,
                b.width + origin.x, b.height + origin.y,
                origin.x, b.height + origin.y
        });
        fd.shape = rect;
        body.createFixture(fd);
        bodies.put(entityId, body);
        return body;
    }

    public Body updateBoundsBody(int entityId) {
        if (physWorld.isLocked())
            throw new RuntimeException("ERROR: Cannot update bounds body in physics loop");

        Body body = bodies.get(entityId);
        for (Fixture fixture : body.getFixtureList()) {
            body.destroyFixture(fixture);
        }

        Bounds b = bm.get(entityId);
        PolygonShape polygonShape = new PolygonShape();
        polygonShape.setAsBox(b.width * 0.5f, b.height * 0.5f);
        body.createFixture(polygonShape, 1.0f);
        return body;
    }

    public Body getBody(int entityId) {
        return bodies.get(entityId);
    }

    @Override
    protected void processSystem() {
        if (shouldRender) {
            batch.begin();
            renderer.render(physWorld, cs.getGameCamera().combined);
            batch.end();
        }

        physWorld.step(PixelonTransmission.PHYSICS_TIMESTEP, 6, 2);
    }

    @Override
    protected void removed(int entityId) {
        try {
            removeBody(entityId);
        } catch (NullPointerException ex){
            logger.error("ERR: NullPtr; cannot remove physics body");
        }
    }

    public void removeBody(int entityId) {
        Body body = getBody(entityId);
        bodies.remove(entityId);
        physWorld.destroyBody(body);
    }

    @Override
    protected void dispose() {
        // TODO: delay doing this until all threads are done adding bodies? else JVM EXCEPTION_ACCESS_VIOLATION
//        physWorld.dispose();
    }

    public void setContactListener(ContactListener listener) {
        physWorld.setContactListener(listener);
    }

    public void queryAABB(QueryCallback queryCallback, float lowerX, float lowerY, float upperX, float upperY) {
        physWorld.QueryAABB(queryCallback, lowerX, lowerY, upperX, upperY);
    }

    public HashMap<Integer, Body> getBodies() {
        return bodies;
    }

    public void shouldRender(boolean render) {
        if (batch != null)
            this.shouldRender = render;
        else
            System.out.println("Cannot enable physics rendering without a specifying a batch in the constructor!");
    }
}
