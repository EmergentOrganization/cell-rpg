package com.emergentorganization.cellrpg.components.CAInteraction;

import com.artemis.Component;
import com.badlogic.gdx.utils.IntMap;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.util.HashMap;

/**
 * List of CA interactions with a CAGrid.
 *
 * Adapted from CACollisionComponent by 7yl4r on 2015-12-08.
 */
public class CAInteractionList extends Component {
    private final Logger logger = LogManager.getLogger(getClass());

    // map target CA Grid entity IDs to CAInteraction objects for an entity:
    public HashMap<Integer, CAInteraction> interactions = new HashMap<Integer, CAInteraction>();

    public int colliderRadius = 0;  // radius of collision object
    public int colliderGridSize = 0;  // when checking the collision area, checks at grid intersections


    // TODO: private registerLayer(int gridEntityId)
    //      creates CAInteraction object for given ca grid id

    public void addInteraction(int layerId, CAInteraction interaction) {
        //      adds interaction to the given ca grid's CAInteraction
        //      adds CAInteraction object for ca grid if it doesn't already exist.

        if (interactions.containsKey(layerId)){
            logger.error("can't add CA interaction. Object already interacts with this layer");
        } else {
            interactions.put(layerId, new CAInteraction());
        }
    }


    // CAInteraction data:
    //  target layer(s)
    //  colliding layer(s)
    //  resulting stamp  // TODO: (enhancement) could apply multiple stamps over multiple generations
    //  resulting event

    // EXAMPLE OF DATA REPRESENTED:
    // spawn pattern A to targetGrid tg1 and pattern B to tg2 when entity collides with colliderGrids cg1, cg2.
    // spawn pattern C to targetGrid tg3 when entity collides with colliderGrid cg3.

    // REGARDING EFFICIENCY OF COLLISION-CHECKING:
    // = Option 1: Component on Colliding Object Entity
    // for each object entity with collision component:
    //      for each layer this entity collides with:
    //          if collision with colliding layer(s)
    //              apply stamp to target layer

    // = Option 2: Component on Colliding CA Entity
    // for each ca layer entity with collision component:
    //      for each object entity that collides with this layer
    //          if collision with entity
    //              apply stamp to target

    // = Option 3?: use rects & contains
    //    when gridRects[i] contains entityRect {
    //        if (gridRects[i] == (cg1 or cg2)) {
    //            spawn pattern A to cg1
    //            spawn pattern B to cg2
    //        } else if (gridRects[i] == tg3) {
    //            spawn pattern c to tg3
    //        }
    //    }


    private void setCollisionSize(final int radius, final int gridSize){
        assert (gridSize > 0);
        colliderRadius = radius;
        colliderGridSize = gridSize;
    }
}
