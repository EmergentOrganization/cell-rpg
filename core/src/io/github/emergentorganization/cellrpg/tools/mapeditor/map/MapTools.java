package com.emergentorganization.cellrpg.tools.mapeditor.map;

import com.artemis.Entity;
import com.artemis.World;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.math.Vector2;
import io.github.emergentorganization.engine.components.Name;
import io.github.emergentorganization.engine.components.Position;
import io.github.emergentorganization.engine.components.Rotation;
import io.github.emergentorganization.engine.components.Scale;
import com.emergentorganization.cellrpg.core.EntityID;
import com.emergentorganization.cellrpg.core.entityfactory.EntityFactory;
import io.github.emergentorganization.engine.systems.RenderSystem;
import com.emergentorganization.cellrpg.tools.FileStructure;
import org.json.simple.JSONArray;
import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;
import org.json.simple.parser.ParseException;

import java.io.*;
import java.util.ArrayList;
import java.util.EnumMap;
import java.util.LinkedHashMap;


public class MapTools {

    public static String FOLDER_ROOT = Gdx.files.getLocalStoragePath() + FileStructure.RESOURCE_DIR + "maps/";
    public static String EXTENSION = ".json";
    public static EnumMap<EntityID, Void> ENTITY_BLACKLIST = new EnumMap<EntityID, Void>(EntityID.class); // Using Map for contains API

    static {
        ENTITY_BLACKLIST.put(EntityID.PLAYER_SHIELD, null);
        ENTITY_BLACKLIST.put(EntityID.CA_LAYER_VYROIDS, null);
        ENTITY_BLACKLIST.put(EntityID.CA_LAYER_ENERGY, null);
        ENTITY_BLACKLIST.put(EntityID.CA_LAYER_GENETIC, null);
    }

    /**
     * Imports external JSON maps into the world
     *
     * @param fileName      Name of the map file, without the extension
     * @param entityFactory Fully-initialized entityFactory for a defined world
     */
    public static void importMap(String fileName, EntityFactory entityFactory) {
        JSONParser parser = new JSONParser();

        try {
            String path = FOLDER_ROOT + fileName + EXTENSION;
            JSONObject obj = (JSONObject) parser.parse(new FileReader(path));
            JSONArray jsonEntities = (JSONArray) obj.get(JSONKey.ENTITIES);

            if (jsonEntities == null) throw new RuntimeException("Could not get entities from " + path);


            for (Object json : jsonEntities) {
                JSONObject jsonEntity = (JSONObject) json;
                String type = (String) jsonEntity.get(JSONKey.TYPE);
                EntityID id = EntityID.fromString(type);
                if (!ENTITY_BLACKLIST.containsKey(id)) {
                    float x = getFloat(jsonEntity.get(JSONKey.POSITION_X));
                    float y = getFloat(jsonEntity.get(JSONKey.POSITION_Y));
                    float rot = getFloat(jsonEntity.get(JSONKey.ROTATION));
                    float scaleX = getFloat(jsonEntity.get(JSONKey.SCALE_X));
                    float scaleY = getFloat(jsonEntity.get(JSONKey.SCALE_Y));

                    entityFactory.createEntityByID(id, new Vector2(x, y), rot);
                }
            }

        } catch (ParseException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private static float getFloat(Object obj) {
        return ((Double) obj).floatValue();
    }

    @SuppressWarnings("MismatchedQueryAndUpdateOfCollection")
    public static void exportMap(World world, String fileName) {
        LinkedHashMap map = new LinkedHashMap();

        ArrayList<LinkedHashMap> entityList = new ArrayList<LinkedHashMap>();

        for (Integer id : world.getSystem(RenderSystem.class).getSortedEntityIds()) {
            Entity entity = world.getEntity(id);
            if (!ENTITY_BLACKLIST.containsKey(entity.getComponent(Name.class).internalID)) {
                entityList.add(exportEntity(entity));
            }
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

        Vector2 pos = entity.getComponent(Position.class).position;
        float rot = entity.getComponent(Rotation.class).angle;
        float scale = entity.getComponent(Scale.class).scale;

        map.put(JSONKey.TYPE, entity.getComponent(Name.class).internalID);
        map.put(JSONKey.POSITION_X, pos.x);
        map.put(JSONKey.POSITION_Y, pos.y);
        map.put(JSONKey.ROTATION, rot);
        map.put(JSONKey.SCALE_X, scale);
        map.put(JSONKey.SCALE_Y, scale);

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