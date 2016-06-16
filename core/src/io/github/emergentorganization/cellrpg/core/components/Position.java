package io.github.emergentorganization.cellrpg.core.components;

import com.artemis.Component;
import com.badlogic.gdx.math.Vector2;

/**
 * defines position of an entity.
 * The Position.position Vector2 defines the bottom-left by default,
 * if you want the position of the center of the object's bounds, use getCenter()
 */
public class Position extends Component {
    public final Vector2 position = new Vector2();

    /**
     * Returns the center of the entity
     *
     * @param bounds Bounds of the same entity that this component is attached to
     * @param angle  Rotation, in degrees, of the same entity that this component is attached to
     * @return The exact center of the entity as a new Vector2
     */
    public Vector2 getCenter(Bounds bounds, float angle) {
        Vector2 center = position.cpy();
        center.x += bounds.width / 2f;
        center.y += bounds.height / 2f;
        if (angle != 0.0f) {
            Vector2 diff = center.cpy().sub(position); // length and default angle of vector between origin and center
            diff.set(Math.abs(diff.x), Math.abs(diff.y)); // ensure vector is pointing in the correct direction (NE)
            diff.rotate(angle); // Rotate vector so that it points to actual center of the rotated object
            center.set(position.x + diff.x, position.y + diff.y); // offset the final vec by the new offset
        }
        return center;
    }

    public void centerOnParent(Bounds childBounds, Position parentPos, Bounds parentBounds){
        // centers this position as child on given parent using given bounds of each.
        position.set(parentPos.position)
                .sub(
                        childBounds.width * 0.5f - parentBounds.width * 0.5f,
                        childBounds.height * 0.5f - parentBounds.height * 0.5f
                );
    }
}
