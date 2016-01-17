package com.emergentorganization.cellrpg.systems.CASystems.GeneticCells.GeneticCellBuilders;

import com.emergentorganization.cellrpg.systems.CASystems.CAs.CACell.GeneticCell;
import com.emergentorganization.cellrpg.systems.CASystems.GeneticCells.DGRN4j.DGRN;
import it.uniroma1.dis.wsngroup.gexf4j.core.Node;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import javax.xml.crypto.KeySelectorException;


public class MrBlue implements GeneticNetworkBuilderInterface {
    private static final Logger logger = LogManager.getLogger(GeneticCell.class);

    public void buildNetwork(DGRN dgrn) {
        try {
            Node bluer = dgrn.graph.createNode("blue-lonely-gene");
            bluer.setLabel("blue-lonely-gene")
                    .getAttributeValues()
                    .addValue(GeneticCell.attr_ActivationValue, "0")
                    .addValue(DGRN.attr_AlleleCount, "2");
            dgrn.connect(GeneticCell.inflowNodes.ALWAYS_ON, bluer.getId(), 1);
            dgrn.connect(bluer.getId(), GeneticCell.outflowNodes.COLOR_ADD_B, 1);

            // this is instantaneous, but inheritance doesn't deal with direct inflow to outflow connections (at this time)
            //dgrn.connect(GeneticCell.inflowNodes.LONELY, GeneticCell.outflowNodes.COLOR_ADD_B, 1);

        } catch (KeySelectorException err) {
            logger.error("nodes failed to insert in building mock network: " + err.getMessage());
        }

    }
}
