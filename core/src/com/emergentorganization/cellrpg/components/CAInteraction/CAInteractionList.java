package com.emergentorganization.cellrpg.components.CAInteraction;

import com.artemis.Component;
import com.badlogic.gdx.math.Vector2;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.util.HashMap;

/**
 * List of CA interactions with a CAGrid.
 *
 *  CAInteraction data:
 *   target layer(s)
 *   colliding layer(s)
 *   resulting stamp  // TODO: (enhancement) could apply multiple stamps over multiple generations
 *   resulting event
 *
 *  EXAMPLE OF DATA REPRESENTED:
 *  spawn pattern A to targetGrid tg1 and pattern B to tg2 when entity collides with colliderGrids cg1, cg2.
 *  spawn pattern C to targetGrid tg3 when entity collides with colliderGrid cg3.
 *
 * Adapted from CACollisionComponent by 7yl4r on 2015-12-08.
 */
public class CAInteractionList extends Component {
    private final Logger logger = LogManager.getLogger(getClass());

    // map target CA Grid entity IDs to CAInteraction objects for an entity:
    public HashMap<Integer, CAInteraction> interactions = new HashMap<Integer, CAInteraction>();

    public int colliderRadius = 0;  // radius of collision object

    public Vector2 lastCollisionPosition;  // position @ last location position was checked

    // TODO: public void setColliderRadiusFromBounds(Bounds bounds){

    public CAInteractionList addInteraction(int layerId, CAInteraction interaction) {
        //      adds interaction to the given ca grid's CAInteraction
        //      adds CAInteraction object for ca grid if it doesn't already exist.

        if (interactions.containsKey(layerId)){
            logger.error("can't add CA interaction. Object already interacts with this layer");
        } else {
            interactions.put(layerId, interaction);
        }
        return this;
    }

    public CAInteractionList setColliderRadius(int newVal){
        colliderRadius = newVal;
        return this;
    }
}
