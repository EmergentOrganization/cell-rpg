package com.emergentorganization.cellrpg.entities.ca;

import com.badlogic.gdx.graphics.Color;
import com.emergentorganization.cellrpg.CellRpg;
import com.emergentorganization.cellrpg.entities.ca.DGRN4j.DGRN;
import com.emergentorganization.cellrpg.entities.ca.DGRN4j.InflowNodeHandler;
import com.emergentorganization.cellrpg.entities.ca.DGRN4j.OutflowNodeHandler;
import it.uniroma1.dis.wsngroup.gexf4j.core.data.*;
import it.uniroma1.dis.wsngroup.gexf4j.core.impl.data.AttributeListImpl;

import javax.xml.crypto.KeySelectorException;

/**
 * CA grid cell which has a digital gene regulatory network (DGRN) to represent it's genome,
 * and traits which are extracted from this genome.
 *
 * Created by 7yl4r on 9/25/2015.
 */
public class GeneticCell extends CellWithHistory implements OutflowNodeHandler, InflowNodeHandler{
    protected DGRN dgrn;
    public static class inflowNodes{
        public static final String ALWAYS_ON = "alwaysOn";

        public static String[] values(){
            return new String[]{ALWAYS_ON};
        }
    }

    public static class outflowNodes{
        public static final String COLOR_LIGHTEN = "COLOR_LIGHTEN";
        public static final String COLOR_DARKEN = "COLOR_DARKEN";
        public static final String COLOR_ADD_R = "COLOR_ADD_R";
        public static final String COLOR_ADD_G = "COLOR_ADD_G";
        public static final String COLOR_ADD_B = "COLOR_ADD_B";
        public static final String COLOR_SUB_R = "COLOR_SUB_R";
        public static final String COLOR_SUB_G = "COLOR_SUB_G";
        public static final String COLOR_SUB_B = "COLOR_SUB_B";
        public static String[] values(){
            return new String[]{
                    COLOR_LIGHTEN, COLOR_DARKEN,
                    COLOR_ADD_R, COLOR_ADD_G, COLOR_ADD_B,
                    COLOR_SUB_R, COLOR_SUB_G, COLOR_SUB_B
            };
        }
    }
    public static class nodeAttribute{
        public static final String ACTIVATION_VALUE = "activation level";
    }
    static final Color DEFAULT_COLOR = new Color(.7f,.7f,.7f,1f);
    private Color color = new Color(DEFAULT_COLOR);
    private static AttributeList attrList = new AttributeListImpl(AttributeClass.NODE);

    public static Attribute attr_ActivationValue = attrList.createAttribute(
            nodeAttribute.ACTIVATION_VALUE,
            AttributeType.INTEGER,
            "activation value"
    ).setDefaultValue("0");  // NOTE: default value doesn't seem to have intended effect?
    // active state of a node defines whether the node is

    public GeneticCell(int _state, GeneticCell[] parents, int mutateLevel){
        // builds cell network inherting from parent(s), mutated based on given level
        this(_state);
        initDGRN();
        // TODO: inherit + mutation
    }

    public GeneticCell(int _state, GeneticCell[] parents){
        // builds cell network inherting from parent(s) (no mutations)
        this(_state);
        initDGRN();
        // TODO: inherit network from parents
    }

    public GeneticCell(int _state){
        // sets up cell with DGRN that has only inflow & outflow nodes, no connections
        super(_state);
        initDGRN();
    }

    public void initDGRN(){
        dgrn = new DGRN(
                "Planiverse Bridge v" + CellRpg.VERSION,
                "Digital Gene Regulatory Network",
                attrList,
                attr_ActivationValue,
                this,
                this
        );
    }

    public Color getColor(){
        return color;
    }

    public String[] getListOfInflowNodes(){
        return inflowNodes.values();
    }

    public int getInflowNodeValue(String key) throws KeySelectorException{
        if (key == inflowNodes.ALWAYS_ON){
            return 1;
        } else {
            throw new KeySelectorException("inflow node '" + key + "' not recognized");
        }
    }

    public String[] getListOfOutflowNodes(){
        return outflowNodes.values();
    }

    public void handleOutputNode(String key, int value){
        // handles special actions caused by outflow nodes
        final float COLOR_DELTA = .1f;
        if( key == outflowNodes.COLOR_LIGHTEN) {
            color.add(COLOR_DELTA, COLOR_DELTA, COLOR_DELTA, 0);
        } else if (key == outflowNodes.COLOR_DARKEN) {
            color.sub(COLOR_DELTA, COLOR_DELTA, COLOR_DELTA, 0);
        } else if (key == outflowNodes.COLOR_ADD_R) {
            color.add(COLOR_DELTA, 0, 0, 0);
        } else if (key == outflowNodes.COLOR_ADD_G) {
            color.add(0, COLOR_DELTA, 0, 0);
        } else if (key == outflowNodes.COLOR_ADD_B) {
            color.add(0, 0, COLOR_DELTA, 0);
        } else if (key == outflowNodes.COLOR_SUB_R) {
            color.sub(COLOR_DELTA, 0, 0, 0);
        } else if (key == outflowNodes.COLOR_SUB_G) {
            color.sub(0, COLOR_DELTA, 0, 0);
        } else if (key == outflowNodes.COLOR_SUB_B) {
            color.sub(0, 0, COLOR_DELTA, 0);
        } else { // not an output key
            return;  // do nothing
            //throw new KeySelectorException("inflow node '" + key + "' not recognized");
        }
    }

}
