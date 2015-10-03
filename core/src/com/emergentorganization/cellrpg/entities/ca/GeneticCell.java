package com.emergentorganization.cellrpg.entities.ca;

import it.uniroma1.dis.wsngroup.gexf4j.core.Gexf;
import it.uniroma1.dis.wsngroup.gexf4j.core.impl.GexfImpl;

/**
 * Created by 7yl4r on 9/25/2015.
 */
public class GeneticCell extends BaseCell{
    // TODO: replace this with bufferSwapCell which should store two state values here,
    //       one used for current, one used for previous for max efficiency,
    public GeneticCell(int _state){
        super(_state);
        Gexf gexf = new GexfImpl();
    }
}
