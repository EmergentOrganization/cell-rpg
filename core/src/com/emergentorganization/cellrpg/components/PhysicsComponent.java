package com.emergentorganization.cellrpg.components;

import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.glutils.ShapeRenderer;
import com.badlogic.gdx.math.Vector2;
import com.emergentorganization.cellrpg.components.listeners.BaseComponentListener;
import com.emergentorganization.cellrpg.components.messages.BaseComponentMessage;
import com.emergentorganization.cellrpg.physics.CellUserData;
import com.emergentorganization.cellrpg.physics.Tag;
import com.emergentorganization.cellrpg.scenes.Scene;
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
    private org.dyn4j.geometry.Vector2 size;

    public PhysicsComponent(World world, Body body, Tag tag) {
        type = ComponentType.PHYSICS;
        this.world = world;
        this.body = body;
        this.tag = tag;
        body.setMassType(Mass.Type.FIXED_ANGULAR_VELOCITY);
        world.addBody(body);
        enableDebugRenderer(true);
    }

    @Override
    public void debugRender(ShapeRenderer renderer) {
        super.debugRender(renderer);
        renderer.set(ShapeRenderer.ShapeType.Filled);
        drawBody(renderer);
    }

    @Override
    public boolean shouldDebugRender() {
        return rendering;
    }

    public void enableDebugRenderer(boolean on) {
        rendering = on;
    }

    private void drawBody(ShapeRenderer renderer) {
        org.dyn4j.geometry.Vector2 offset = body.getTransform().getTranslation();
        renderer.setColor(Color.GREEN);

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

                    renderer.rectLine(x1, y1, x2, y2, 2f * Scene.scale);
                }
            }
        }
    }

    public void setUserData(CellUserData data) {
        body.setUserData(data);
    }

    @Override
    public void added() {
        super.added();

        moveComponent = (MovementComponent) getFirstSiblingByType(ComponentType.MOVEMENT);
        if (body.getUserData() == null) {
            body.setUserData(new CellUserData(getEntity(), tag));
        }
        AABB ab = body.createAABB();
        size = new org.dyn4j.geometry.Vector2(ab.getWidth(), ab.getHeight());
    }

    @Override
    public void update(float deltaTime) {
        super.update(deltaTime);

        Vector2 pos = moveComponent.getWorldPosition();
        body.getTransform().setTranslation(pos.x - (size.x / 2d), pos.y - (size.y / 2d));
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
}
