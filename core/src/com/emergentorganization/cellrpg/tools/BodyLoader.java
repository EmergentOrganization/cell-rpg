package com.emergentorganization.cellrpg.tools;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.files.FileHandle;
import com.badlogic.gdx.math.Vector2;
import org.dyn4j.dynamics.Body;
import org.dyn4j.dynamics.BodyFixture;
import org.dyn4j.geometry.Circle;
import org.dyn4j.geometry.Polygon;

import java.util.List;

/**
 * Created by BrianErikson on 6/5/2015.
 */
public class BodyLoader extends BodyEditorLoader {
    private static BodyLoader singleton;

    public BodyLoader(String str) {
        super(str);
    }

    public BodyLoader(FileHandle file) {
        super(file);
    }

    public static BodyLoader fetch() {
        if (singleton == null) {
            singleton = new BodyLoader(Gdx.files.internal("data/colliderProject"));
        }
        return singleton;
    }

    /**
     * Generates a dyn4j physics body to add to the world
     * @param bodyName the body name that was configured in the collider generator
     * @return the body to add to the world
     */
    public Body generateBody(String bodyName, float scale) {
        final Model model = getInternalModel();

        // new body
        Body result = new Body();

        // create fixtures
        final RigidBodyModel rawBody = model.rigidBodies.get(bodyName);

        // polygons
        for (PolygonModel polygon : rawBody.polygons) {
            Polygon gon = new Polygon(convertVectors(polygon.vertices, scale));
            result.addFixture(new BodyFixture(gon));
        }

        // circles
        for (CircleModel circleModel : rawBody.circles) {
            Circle circle = new Circle(circleModel.radius * scale);
            result.addFixture(new BodyFixture(circle));
            throw new RuntimeException("Circle colliders are currently unimplemented");
        }

        return result;
    }

    private org.dyn4j.geometry.Vector2[] convertVectors(List<Vector2> vecs, float scale) {
        org.dyn4j.geometry.Vector2[] newVecs = new org.dyn4j.geometry.Vector2[vecs.size()];

        for (int i = 0; i < vecs.size(); i++) {
            Vector2 vec = vecs.get(i);
            newVecs[i] = new org.dyn4j.geometry.Vector2(vec.x * scale, vec.y * scale);
        }
        return newVecs;
    }
}
