package com.emergentorganization.cellrpg.components;

import com.artemis.Component;
import com.badlogic.gdx.math.Vector2;

/**
 * defines position of an entity.
 * The Position.position Vector2 defines the bottom-left by default,
 * if you want the position of the center of the object's bounds, use getCenter()
 */
public class Position extends Component {
    public final Vector2 position = new Vector2();

    public Vector2 getCenter(Bounds bounds) {
        // NOTE: this doesn't take into account rotation!  TODO: fix!
        Vector2 center = position.cpy();
        center.x += bounds.width / 2f;
        center.y += bounds.height / 2f;
        return center;
    }
}
