package com.emergentorganization.cellrpg.tools.map;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.math.Vector2;
import org.w3c.dom.Document;
import org.w3c.dom.NamedNodeMap;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;
import org.xml.sax.SAXException;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;
import java.io.IOException;
import java.util.regex.Pattern;

/**
 * Created by BrianErikson on 6/12/2015.
 */
public class MapLoader {

    public static Document loadSVGFile(String internalPath) {
        DocumentBuilderFactory dbf = DocumentBuilderFactory.newInstance();

        try {
            DocumentBuilder db = dbf.newDocumentBuilder();
            print("Parsing map " + internalPath);
            Document dom = db.parse(Gdx.files.internal(internalPath).file());
            return dom;

        } catch (ParserConfigurationException e) {
            e.printStackTrace();
        } catch (SAXException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }
        return null;
    }

    public static Map getMap(String internalPath) {
        Map map = new Map();

        Document doc = MapLoader.loadSVGFile(internalPath);
        if (doc != null) {
            NodeList groups = doc.getDocumentElement().getElementsByTagName("g");
            for (int g = 0; g < groups.getLength(); g++) {
                MapLayer layer = parseLayer(groups.item(g));
                if (layer != null) {
                    map.addLayer(layer);
                }
            }
        }

        return map;
    }

    private static MapLayer parseLayer(Node group) {
        String label = group.getAttributes().getNamedItem("inkscape:label").getTextContent();
        LayerType type = getLayerType(label);

        if (isLayerSupported(type)) {
            print("Importing layer: " + label);
            MapLayer layer = new MapLayer(getLayerType(label));

            NodeList childNodes = group.getChildNodes();
            for (int c = 0; c < childNodes.getLength(); c++) {
                Node item = childNodes.item(c);
                MapObject obj = parseItem(item);
                if (obj != null) {
                    layer.addMapObject(obj);
                }
            }

            return layer;
        }

        return null;
    }

    private static Vector2 parseTransform(String transform) {
        if (transform.contains("translate")) {
            // RAW FORMAT: translate(0,0)

            String[] split = transform.split(Pattern.quote(",")); // [translate(0] [0)]
            String[] partX = split[0].split(Pattern.quote("(")); // [translate] [0]

            Vector2 offset = new Vector2();
            offset.x = Float.parseFloat(partX[partX.length - 1]);
            offset.y = Float.parseFloat(split[1].substring(0, split[1].length() - 1));

            return offset;
        }
        else {
            //throw new RuntimeException("ERROR: Unsupported transform description: " + transform);
            return new Vector2(0,0);
        }
    }

    private static MapObject parseItem(Node item) {
        if (item.getNodeName().contentEquals("image")) {


            Vector2 offset = new Vector2();
            return parseImage(item, offset);
        }
        else if (item.getNodeName().contentEquals("#text")) { /*stifle empty objects*/ }
        else {
            print("parsing for item " + item.getNodeName() + " is unsupported");
        }

        return null;
    }

    private static MapImage parseImage(Node item, Vector2 offset) {
        NamedNodeMap attributes = item.getAttributes();

        String rawPath = attributes.getNamedItem("xlink:href").getTextContent();
        String[] split = rawPath.split("/");
        String internalPath = split[split.length-1]; // NOTE: Will need to change this if we move assets into subfolders

        float x = Float.parseFloat(attributes.getNamedItem("x").getTextContent());
        float y = Float.parseFloat(attributes.getNamedItem("y").getTextContent());
        Vector2 pos = new Vector2(x, y).add(offset);

        return new MapImage(pos,internalPath);
    }

    private static LayerType getLayerType(String layer) {
        if (layer.contentEquals("buildingsAndScenery"))
            return LayerType.BUILDINGS_AND_SCENERY;
        else if (layer.contentEquals("chars"))
            return LayerType.CHARS;
        else if (layer.contentEquals("backgrounds"))
            return LayerType.BACKGROUNDS;
        else if (layer.contentEquals("editor_tools"))
            return LayerType.EDITOR_TOOLS;
        else if (layer.contentEquals("bg_paths"))
            return LayerType.BG_PATHS;
        else if (layer.contentEquals("notes"))
            return LayerType.NOTES;
        else
            throw new RuntimeException("ERROR: Could not get layer type. Type is " + layer);
    }

    private static boolean isLayerSupported(LayerType type) {
        switch (type) {
            case NOTES:
                return false;
            case CHARS:
                return true;
            case BUILDINGS_AND_SCENERY:
                return true;
            case BACKGROUNDS:
                return true;
            case EDITOR_TOOLS:
                return false;
            default:
                return false;
        }
    }

    private static void print(String str) {
        System.out.println("[MapLoader] " + str);
    }
}
