package com.emergentorganization.cellrpg.tools.mapeditor.map;

import com.badlogic.gdx.Gdx;
import com.emergentorganization.cellrpg.entities.Entity;
import com.emergentorganization.cellrpg.scenes.Scene;
import org.json.simple.JSONObject;

import java.io.FileWriter;
import java.util.ArrayList;
import java.util.LinkedHashMap;

/**
 * Created by BrianErikson on 6/19/2015.
 */
public class MapTools {

    public static Map importMap() {


        return new Map();
    }

    @SuppressWarnings("MismatchedQueryAndUpdateOfCollection")
    public static void exportMap(Scene scene, String fileName) {
        LinkedHashMap map = new LinkedHashMap();

        ArrayList<LinkedHashMap> entityList = new ArrayList<LinkedHashMap>();
        for (Entity entity : scene.getEntities()) {
            entityList.add(exportEntity(entity));
        }
        map.put("entites", entityList);

        JSONObject obj = new JSONObject(map);
        try {
            String path = Gdx.files.getLocalStoragePath() + fileName + ".json";
            System.out.println("Writing map to path " + path);

            FileWriter file = new FileWriter(path);
            file.write(obj.toJSONString());
            file.flush();
            file.close();

        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private static LinkedHashMap exportEntity(Entity entity) {
        LinkedHashMap map = new LinkedHashMap();

        map.put("type", entity.getClass().getName());
        map.put("positionX", entity.getMovementComponent().getWorldPosition().x);
        map.put("positionY", entity.getMovementComponent().getWorldPosition().y);
        map.put("rotation", entity.getMovementComponent().getRotation());
        map.put("scale", entity.getMovementComponent().getScale());

        return map;
    }
}