package com.emergentorganization.cellrpg.components.entity;

import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.g2d.Sprite;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.glutils.ShapeRenderer;
import com.badlogic.gdx.math.Vector2;
import com.emergentorganization.cellrpg.components.EntityComponent;
import com.emergentorganization.cellrpg.physics.CellUserData;
import com.emergentorganization.cellrpg.physics.Tag;
import com.emergentorganization.cellrpg.scenes.Scene;
import com.sun.corba.se.impl.orbutil.graph.Graph;
import org.dyn4j.dynamics.Body;
import org.dyn4j.dynamics.BodyFixture;
import org.dyn4j.dynamics.World;
import org.dyn4j.geometry.AABB;
import org.dyn4j.geometry.Mass;
import org.dyn4j.geometry.Polygon;
import org.dyn4j.geometry.Transform;

/**
 * Created by BrianErikson on 6/6/2015.
 */
public class PhysicsComponent extends EntityComponent {
    private final World world;
    private final Body body;
    private MovementComponent moveComponent;
    private Tag tag;
    private boolean rendering = true;
    private org.dyn4j.geometry.Vector2 size;
    public static float BB_THICKNESS = 1f; // Bounding box thickness of lines

    public PhysicsComponent(World world, Body body, Tag tag) {
        this.world = world;
        this.body = body;
        this.tag = tag;
        body.setMassType(Mass.Type.FIXED_ANGULAR_VELOCITY);
        world.addBody(body);
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
        Transform transform = body.getTransform();
        renderer.setColor(Color.GREEN);

        for (BodyFixture fixture : body.getFixtures()) {
            if (fixture.getShape() instanceof Polygon) {
                Polygon gon = (Polygon) fixture.getShape();
                org.dyn4j.geometry.Vector2[] verts = gon.getVertices();
                for (int i = 0; i < gon.getVertices().length; i++) {
                    int index = i;
                    org.dyn4j.geometry.Vector2 p1 = new org.dyn4j.geometry.Vector2(verts[index].x, verts[index].y);
                    p1 = transform.getTransformed(p1);

                    if (index == gon.getVertices().length - 1) index = 0; // connect last line
                    else index++;

                    org.dyn4j.geometry.Vector2 p2 = new org.dyn4j.geometry.Vector2(verts[index].x, verts[index].y);
                    p2 = transform.getTransformed(p2);

                    renderer.rectLine((float)p1.x, (float)p1.y, (float)p2.x, (float)p2.y, 2f * Scene.scale);
                }
            }
        }

        AABB ab = body.createAABB();
        drawBoundingBox(renderer, new Vector2((float)ab.getWidth(), (float)ab.getHeight()), getEntity().getFirstComponentByType(MovementComponent.class).getWorldPosition());
    }

    /**
     * Must call between ShapeRenderer.begin() and ShapeRenderer.end()
     * @param size Scaled size of the object
     * @param pos center origin of object
     */
    private void drawBoundingBox(ShapeRenderer shapeRenderer, Vector2 size, Vector2 pos) {
        Vector2 hs = size.cpy().scl(0.5f);
        shapeRenderer.rectLine(pos.x - hs.x, pos.y - hs.y, pos.x + hs.x, pos.y - hs.y, BB_THICKNESS); // bl to br
        shapeRenderer.rectLine(pos.x + hs.x, pos.y - hs.y, pos.x + hs.x, pos.y + hs.y, BB_THICKNESS); // br to tr
        shapeRenderer.rectLine(pos.x + hs.x, pos.y + hs.y, pos.x - hs.x, pos.y + hs.y, BB_THICKNESS); // tr to tl
        shapeRenderer.rectLine(pos.x - hs.x, pos.y + hs.y, pos.x - hs.x, pos.y - hs.y, BB_THICKNESS); // tl to bl
    }

    public void setUserData(CellUserData data) {
        body.setUserData(data);
    }

    public Body getBody() {
        return body;
    }

    @Override
    public void added() {
        super.added();

        moveComponent = getFirstSiblingByType(MovementComponent.class);
        if (body.getUserData() == null) {
            body.setUserData(new CellUserData(getEntity(), tag));
        }

        // size using bb in case of no graphics
        AABB ab = body.createAABB();
        size = new org.dyn4j.geometry.Vector2(ab.getWidth(), ab.getHeight());

        // set size using graphics/sprite
        float scaler = getEntity().getScene().scale;
        GraphicsComponent gc = getFirstSiblingByType(GraphicsComponent.class);
        if (gc != null){
            size = gc.getSize();
        }
        SpriteComponent sc = getFirstSiblingByType(SpriteComponent.class);
        if (sc != null){
            size = new org.dyn4j.geometry.Vector2(sc.getSprite().getRegionWidth()*scaler, sc.getSprite().getRegionHeight()*scaler);
        }
    }

    @Override
    public void update(float deltaTime) {
        super.update(deltaTime);

        Vector2 pos = moveComponent.getWorldPosition();
        org.dyn4j.geometry.Vector2 worldCenter = new org.dyn4j.geometry.Vector2(pos.x - (size.x / 2d),
                                                                                pos.y - (size.y / 2d));

        Transform transform = body.getTransform();
        transform.setTranslation(worldCenter);
        transform.setRotation(0d);
        body.rotate(moveComponent.getRotationRad(), pos.x, pos.y);
    }

    @Override
    public void dispose() {
        super.dispose();
        world.removeBody(body);
    }
}
