package com.emergentorganization.cellrpg.components;

import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.glutils.ShapeRenderer;
import com.badlogic.gdx.math.Vector2;
import com.emergentorganization.cellrpg.components.listeners.BaseComponentListener;
import com.emergentorganization.cellrpg.components.messages.BaseComponentMessage;
import com.emergentorganization.cellrpg.physics.Tag;
import org.dyn4j.dynamics.Body;
import org.dyn4j.dynamics.BodyFixture;
import org.dyn4j.dynamics.World;
import org.dyn4j.geometry.AABB;
import org.dyn4j.geometry.Mass;
import org.dyn4j.geometry.Polygon;

/**
 * Created by BrianErikson on 6/6/2015.
 */
public class PhysicsComponent extends BaseComponent {
    private final World world;
    private final Body body;
    private MovementComponent moveComponent;
    private Tag tag;
    private boolean rendering = false;
    private ShapeRenderer debugRenderer = new ShapeRenderer();
    private org.dyn4j.geometry.Vector2 size;

    public PhysicsComponent(World world, Body body, Tag tag) {
        type = ComponentType.PHYSICS;
        this.world = world;
        this.body = body;
        this.tag = tag;
        body.setMassType(Mass.Type.FIXED_ANGULAR_VELOCITY);
        world.addBody(body);
    }

    @Override
    public boolean shouldRender() {
        return rendering;
    }

    @Override
    public void render(SpriteBatch batch, Vector2 pos, float rot, Vector2 scale) {
        super.render(batch, pos, rot, scale);
        batch.end();

        debugRenderer.setProjectionMatrix(batch.getProjectionMatrix());
        debugRenderer.begin(ShapeRenderer.ShapeType.Filled);
        drawBody();
        debugRenderer.end();

        batch.begin();
    }

    public void enableDebugRenderer(boolean on) {
        rendering = on;
    }

    private void drawBody() {
        org.dyn4j.geometry.Vector2 offset = body.getTransform().getTranslation();
        debugRenderer.setColor(Color.GREEN);

        for (BodyFixture fixture : body.getFixtures()) {
            if (fixture.getShape() instanceof Polygon) {
                Polygon gon = (Polygon) fixture.getShape();
                org.dyn4j.geometry.Vector2[] verts = gon.getVertices();
                for (int i = 0; i < gon.getVertices().length; i++) {
                    int index = i;
                    float x1 = (float) (verts[index].x + offset.x);
                    float y1 = (float) (verts[index].y + offset.y);
                    if (index == gon.getVertices().length - 1) index = 0; // connect last line
                    else index++;
                    float x2 = (float) (verts[index].x + offset.x);
                    float y2 = (float) (verts[index].y + offset.y);

                    debugRenderer.rectLine(x1, y1, x2, y2, 2f);
                }
            }
        }

    }

    @Override
    public void added() {
        super.added();

        moveComponent = (MovementComponent) getFirstSiblingByType(ComponentType.MOVEMENT);
        body.setUserData(new CellUserData(moveComponent, tag));
        AABB ab = body.createAABB();
        size = new org.dyn4j.geometry.Vector2(ab.getWidth(), ab.getHeight());
    }

    @Override
    public void update(float deltaTime) {
        super.update(deltaTime);

        Vector2 pos = moveComponent.getWorldPosition();
        body.getTransform().setTranslation(pos.x - (size.x / 2f), pos.y - (size.y / 2f));
    }

    @Override
    public ComponentType getType() {
        return super.getType();
    }

    @Override
    public void addListener(BaseComponentListener listener) {
        super.addListener(listener);
    }

    @Override
    public void removeListener(BaseComponentListener listener) {
        super.removeListener(listener);
    }

    @Override
    public void receiveMessage(BaseComponentMessage message) {
        super.receiveMessage(message);
    }

    @Override
    protected void broadcast(BaseComponentMessage message) {
        super.broadcast(message);
    }

    @Override
    protected void broadcast(ComponentType type, BaseComponentMessage message) {
        super.broadcast(type, message);
    }

    @Override
    public void dispose() {
        super.dispose();
        world.removeBody(body);
    }

    public class CellUserData {
        public final Tag tag;
        public final MovementComponent movementComponent;

        public CellUserData(MovementComponent movementComponent, Tag tag) {
            this.movementComponent = movementComponent;
            this.tag = tag;
        }
    }
}
