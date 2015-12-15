package com.emergentorganization.cellrpg.systems.CASystems.GeneticCells.GeneticCellBuilders;

import com.emergentorganization.cellrpg.systems.CASystems.CACell.GeneticCell;
import com.emergentorganization.cellrpg.systems.CASystems.GeneticCells.DGRN4j.DGRN;
import it.uniroma1.dis.wsngroup.gexf4j.core.Node;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import javax.xml.crypto.KeySelectorException;

/**
 * Created by 7yl4r on 10/6/2015.
 */
public class CellAlpha implements GeneticNetworkBuilderInterface {
    private static final Logger logger = LogManager.getLogger(GeneticCell.class);

    public void buildNetwork(DGRN dgrn){
        try{
            Node lightener = dgrn.graph.createNode("always-lighten-gene");
            lightener
                    .setLabel("always-lighten-gene")
                    .getAttributeValues()
                    .addValue(GeneticCell.attr_ActivationValue, "0")
                    .addValue(DGRN.attr_AlleleCount, "1");
            dgrn.connect(GeneticCell.inflowNodes.ALWAYS_ON, lightener.getId(), 1);
            dgrn.connect(lightener.getId(), GeneticCell.outflowNodes.COLOR_DARKEN, 1);

    //            Node bluer = dgrn.graph.createNode("blue-lonely-gene");
    //            bluer.setLabel("blue-lonely-gene")
    //                    .getAttributeValues()
    //                    .addValue(attr_ActivationValue, "0")
    //                    .addValue(DGRN.attr_AlleleCount, "2");
    //            dgrn.connect(inflowNodes.LONELY, bluer.getId(), 1);
    //            dgrn.connect(bluer.getId(), outflowNodes.COLOR_ADD_B, 1);
    //            bluer.connectTo(lightener).setWeight(-2);
            dgrn.connect(GeneticCell.inflowNodes.LONELY, GeneticCell.outflowNodes.COLOR_ADD_B, 1);
    //
    //            Node reddener = dgrn.graph.createNode("redden-crowded-gene");
    //            reddener.setLabel("redden-crowded-gene")
    //                    .getAttributeValues()
    //                    .addValue(attr_ActivationValue, "0")
    //                    .addValue(DGRN.attr_AlleleCount, "2");
    //            dgrn.connect(inflowNodes.CROWDED, reddener.getId(), 1);
    //            dgrn.connect(reddener.getId(), outflowNodes.COLOR_ADD_R, 1);
            dgrn.connect(GeneticCell.inflowNodes.CROWDED, GeneticCell.outflowNodes.COLOR_ADD_R, 1);

            // green-ize
            dgrn.connect(GeneticCell.inflowNodes.ALWAYS_ON, GeneticCell.outflowNodes.COLOR_ADD_G, 1);

        } catch(KeySelectorException err){
            logger.error("nodes failed to insert in building mock network: " + err.getMessage());
        }

    }
}
