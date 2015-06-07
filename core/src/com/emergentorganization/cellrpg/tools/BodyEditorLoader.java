package com.emergentorganization.cellrpg.tools;

import com.badlogic.gdx.files.FileHandle;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.utils.JsonReader;
import com.badlogic.gdx.utils.JsonValue;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Loads the collision fixtures defined with the Physics Body Editor
 * application. You only need to give it a body and the corresponding fixture
 * name, and it will attach these fixtures to your body.
 *
 * @author Aurelien Ribon | http://www.aurelienribon.com
 * @author Converted to LibGDX v1.6.1 without Box2D by BrianErikson on 6/5/2015
 */
public class BodyEditorLoader {

    // Model
    private final Model model;

    // Reusable stuff
    private final List<Vector2> vectorPool = new ArrayList<Vector2>();
    private final Vector2 vec = new Vector2();

    // -------------------------------------------------------------------------
    // Ctors
    // -------------------------------------------------------------------------

    public BodyEditorLoader(FileHandle file) {
        if (file == null) throw new NullPointerException("file is null");
        model = readJson(file.readString());
    }

    public BodyEditorLoader(String str) {
        if (str == null) throw new NullPointerException("str is null");
        model = readJson(str);
    }

    // -------------------------------------------------------------------------
    // Public API
    // -------------------------------------------------------------------------

    /**
     * Gets the image path attached to the given name.
     */
    public String getImagePath(String name) {
        RigidBodyModel rbModel = model.rigidBodies.get(name);
        if (rbModel == null) throw new RuntimeException("Name '" + name + "' was not found.");

        return rbModel.imagePath;
    }

    /**
     * Gets the origin point attached to the given name. Since the point is
     * normalized in [0,1] coordinates, it needs to be scaled to your body
     * size. Warning: this method returns the same Vector2 object each time, so
     * copy it if you need it for later use.
     */
    public Vector2 getOrigin(String name, float scale) {
        RigidBodyModel rbModel = model.rigidBodies.get(name);
        if (rbModel == null) throw new RuntimeException("Name '" + name + "' was not found.");

        return vec.set(rbModel.origin).scl(scale);
    }

    /**
     * <b>For advanced users only.</b> Lets you access the internal model of
     * this loader and modify it. Be aware that any modification is permanent
     * and that you should really know what you are doing.
     */
    public Model getInternalModel() {
        return model;
    }

    // -------------------------------------------------------------------------
    // Json Models
    // -------------------------------------------------------------------------

    public static class Model {
        public final Map<String, RigidBodyModel> rigidBodies = new HashMap<String, RigidBodyModel>();
    }

    public static class RigidBodyModel {
        public String name;
        public String imagePath;
        public final Vector2 origin = new Vector2();
        public final List<PolygonModel> polygons = new ArrayList<PolygonModel>();
        public final List<CircleModel> circles = new ArrayList<CircleModel>();
    }

    public static class PolygonModel {
        public final List<Vector2> vertices = new ArrayList<Vector2>();
        private Vector2[] buffer; // used to avoid allocation in attachFixture()
    }

    public static class CircleModel {
        public final Vector2 center = new Vector2();
        public float radius;
    }

    // -------------------------------------------------------------------------
    // Json reading process
    // -------------------------------------------------------------------------

    protected Model readJson(String str) {
        Model m = new Model();
        JsonValue rootElem = new JsonReader().parse(str);

        JsonValue bodiesElems = rootElem.get("rigidBodies");

        for (int i=0; i<bodiesElems.size; i++) {
            JsonValue bodyElem = bodiesElems.get(i);
            RigidBodyModel rbModel = readRigidBody(bodyElem);
            m.rigidBodies.put(rbModel.name, rbModel);
        }

        return m;
    }

    private RigidBodyModel readRigidBody(JsonValue bodyElem) {
        RigidBodyModel rbModel = new RigidBodyModel();
        rbModel.name = bodyElem.getString("name");
        rbModel.imagePath = bodyElem.getString("imagePath");

        JsonValue originElem = bodyElem.get("origin");
        rbModel.origin.x = originElem.getFloat("x");
        rbModel.origin.y = originElem.getFloat("y");

        // polygons

        JsonValue polygonsElem = bodyElem.get("polygons");

        for (int i=0; i<polygonsElem.size; i++) {
            PolygonModel polygon = new PolygonModel();
            rbModel.polygons.add(polygon);

            JsonValue verticesElem = polygonsElem.get(i);
            for (int ii=0; ii<verticesElem.size; ii++) {
                JsonValue vertexElem = verticesElem.get(ii);
                float x = vertexElem.getFloat("x");
                float y = vertexElem.getFloat("y");
                polygon.vertices.add(new Vector2(x, y));
            }

            polygon.buffer = new Vector2[polygon.vertices.size()];
        }

        // circles

        JsonValue circlesElem = bodyElem.get("circles");

        for (int i=0; i<circlesElem.size; i++) {
            CircleModel circle = new CircleModel();
            rbModel.circles.add(circle);

            JsonValue circleElem = circlesElem.get(i);
            circle.center.x = circleElem.getFloat("cx");
            circle.center.y = circleElem.getFloat("cy");
            circle.radius = circleElem.getFloat("r");
        }

        return rbModel;
    }

    // -------------------------------------------------------------------------
    // Helpers
    // -------------------------------------------------------------------------

    private Vector2 newVec() {
        return vectorPool.isEmpty() ? new Vector2() : vectorPool.remove(0);
    }

    private void free(Vector2 v) {
        vectorPool.add(v);
    }
}
