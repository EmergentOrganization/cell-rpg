package com.emergentorganization.cellrpg.tools.mapeditor.map;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.math.Vector2;
import com.emergentorganization.cellrpg.components.MovementComponent;
import com.emergentorganization.cellrpg.entities.Entity;
import com.emergentorganization.cellrpg.scenes.Scene;
import org.json.simple.JSONArray;
import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;
import org.json.simple.parser.ParseException;

import java.io.*;
import java.util.ArrayList;
import java.util.LinkedHashMap;

/**
 * Created by BrianErikson on 6/19/2015.
 */
public class MapTools {
    public static String FOLDER_ROOT = Gdx.files.getLocalStoragePath() + "maps/";
    public static String EXTENSION = ".json";

    public static Map importMap(String fileName) {
        JSONParser parser = new JSONParser();
        Map map = new Map();

        try {
            String path = FOLDER_ROOT + fileName + EXTENSION;
            JSONObject obj = (JSONObject) parser.parse(new FileReader(path));
            JSONArray jsonEntities = (JSONArray) obj.get(JSONKey.ENTITIES);

            if (jsonEntities == null) throw new RuntimeException("Could not get entities from " + path);


            for (Object json : jsonEntities) {
                JSONObject jsonEntity = (JSONObject) json;
                String type = (String) jsonEntity.get(JSONKey.TYPE);
                float x = getFloat(jsonEntity.get(JSONKey.POSITION_X));
                float y = getFloat(jsonEntity.get(JSONKey.POSITION_Y));
                float rot = getFloat(jsonEntity.get(JSONKey.ROTATION));
                float scaleX = getFloat(jsonEntity.get(JSONKey.SCALE_X));
                float scaleY = getFloat(jsonEntity.get(JSONKey.SCALE_Y));
                Class cls = Class.forName(type);

                Entity instance = (Entity) cls.newInstance();

                MovementComponent move = instance.getMovementComponent();
                move.setScale(new Vector2(scaleX, scaleY));
                move.setRotation(rot);
                move.setWorldPosition(new Vector2(x, y));

                map.addEntity(instance);
            }

        } catch (FileNotFoundException e) {
            e.printStackTrace();
        } catch (ParseException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        } catch (ClassNotFoundException e) {
            e.printStackTrace();
        } catch (InstantiationException e) {
            e.printStackTrace();
        } catch (IllegalAccessException e) {
            e.printStackTrace();
        }

        return map;
    }

    private static float getFloat(Object obj) {
        return ((Double) obj).floatValue();
    }

    @SuppressWarnings("MismatchedQueryAndUpdateOfCollection")
    public static void exportMap(Scene scene, String fileName) {
        LinkedHashMap map = new LinkedHashMap();

        ArrayList<LinkedHashMap> entityList = new ArrayList<LinkedHashMap>();
        for (Entity entity : scene.getEntities()) {
            entityList.add(exportEntity(entity));
        }
        map.put(JSONKey.ENTITIES, entityList);

        JSONObject obj = new JSONObject(map);
        try {
            String path = FOLDER_ROOT + fileName + EXTENSION;
            System.out.println("Writing map to path " + path);

            new File(FOLDER_ROOT).mkdirs(); // create folder if necessary

            FileWriter writer = new FileWriter(path);
            writer.write(obj.toJSONString());
            writer.flush();
            writer.close();

        } catch (FileNotFoundException e) {


        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private static LinkedHashMap exportEntity(Entity entity) {
        LinkedHashMap map = new LinkedHashMap();

        map.put(JSONKey.TYPE, entity.getClass().getName());
        map.put(JSONKey.POSITION_X, entity.getMovementComponent().getWorldPosition().x);
        map.put(JSONKey.POSITION_Y, entity.getMovementComponent().getWorldPosition().y);
        map.put(JSONKey.ROTATION, entity.getMovementComponent().getRotation());
        map.put(JSONKey.SCALE_X, entity.getMovementComponent().getScale().x);
        map.put(JSONKey.SCALE_Y, entity.getMovementComponent().getScale().y);

        return map;
    }
}

class JSONKey {
    public static String TYPE = "type";
    public static String ENTITIES = "entities";
    public static String POSITION_X = "positionX";
    public static String POSITION_Y = "positionY";
    public static String ROTATION = "rotation";
    public static String SCALE_X = "scaleX";
    public static String SCALE_Y = "scaleY";
}