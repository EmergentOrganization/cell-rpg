package com.emergentorganization.cellrpg.components.SpontaneousGeneration;

import com.badlogic.gdx.math.Vector2;
import com.emergentorganization.cellrpg.systems.CASystems.layers.CALayer;


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
