package io.github.emergentorganization.cellrpg.components.SpontaneousGeneration;

import com.badlogic.gdx.math.Vector2;
import io.github.emergentorganization.cellrpg.systems.CASystems.layers.CALayer;

/**
 * defines a stamp effect onto a CA grid.
 * <p>
 * TODO: rename to more generic name
 */

public class SpontaneousGeneration {
    public CALayer targetLayer;
    public int[][] stamp;
    public Vector2 position;

    public SpontaneousGeneration(CALayer target, int[][] _stamp, Vector2 pos) {
        targetLayer = target;
        stamp = _stamp;
        position = pos;
    }
}
