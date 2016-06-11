package io.github.emergentorganization.cellrpg.components.CAInteraction;

import com.artemis.Component;
import com.badlogic.gdx.math.Vector2;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.util.HashMap;

/**
 * List of CA interactions with a CAGrid.
 * <p/>
 * CAInteraction data:
 * target layer(s)
 * colliding layer(s)
 * resulting stamp  // TODO: (enhancement) could apply multiple stamps over multiple generations
 * resulting event
 * <p/>
 * EXAMPLE OF DATA REPRESENTED:
 * spawn pattern A to targetGrid tg1 and pattern B to tg2 when entity collides with colliderGrids cg1, cg2.
 * spawn pattern C to targetGrid tg3 when entity collides with colliderGrid cg3.
 * <p/>
 * Adapted from CACollisionComponent by 7yl4r on 2015-12-08.
 */
public class CAInteractionList extends Component {
    private final Logger logger = LogManager.getLogger(getClass());

    // map target CA Grid entity IDs to CAInteraction objects for an entity:
    public final HashMap<Integer, CAInteraction> interactions = new HashMap<Integer, CAInteraction>();
    public Vector2 lastCollisionPosition;  // position @ last location position was checked
    private int colliderRadius_1 = 0;  // radius of collision object for 1px CA
    private int colliderRadius_3 = 0;  // for 3px CA
    private int colliderRadius_11 = 0;  // for 11px CA

    // TODO: public void setColliderRadiusFromBounds(Bounds bounds){

    public CAInteractionList addInteraction(int layerId, CAInteraction interaction) {
        //      adds interaction to the given ca grid's CAInteraction
        //      adds CAInteraction object for ca grid if it doesn't already exist.

        if (interactions.containsKey(layerId)) {
            logger.error("can't add interaction w/ layer#" + layerId + ". Object already interacts with this layer");
        } else {
            interactions.put(layerId, interaction);
        }
        return this;
    }

    public CAInteractionList setColliderRadius(int newVal) {
        colliderRadius_1 = newVal * 3;
        colliderRadius_3 = newVal;
        colliderRadius_11 = newVal / 3;
        if (colliderRadius_11 < 1) colliderRadius_11 = 1;
        return this;
    }

    public int getColliderRadius(int collidingGridSize) {
        switch (collidingGridSize) {
            case 1:
                return colliderRadius_1;
            case 3:
                return colliderRadius_3;
            case 11:
                return colliderRadius_11;
            default:
                return 1;
        }
    }
}
