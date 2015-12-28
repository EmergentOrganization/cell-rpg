package com.emergentorganization.cellrpg.components;

import com.artemis.Component;
import com.badlogic.gdx.math.Vector2;

/**
 * defines position of an entity.
 * The Position.position Vector2 defines the bottom-left by default,
 * if you want the position of the center of the object's bounds, use getCenter()
 *
 * Created by brian on 10/28/15.
 */
public class Position extends Component {
    public final Vector2 position = new Vector2();

    public Vector2 getCenter(Bounds bounds){
        Vector2 center = position.cpy();
        center.x += bounds.width/2f;
        center.y += bounds.height/2f;
        return center;
    }
}
