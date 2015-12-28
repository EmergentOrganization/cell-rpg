package com.emergentorganization.cellrpg.systems.CASystems;

/**
 * key which identifies a single CA cell (by it's x,y and cellLayer attributes) to be used in a HashMap
 *
 * Created by 7yl4r on 12/27/2015.
 */
public class CACellKey{

    public final int x;
    public final int y;
    public final int layer;

    public CACellKey(int x, int y, int layer) {
        this.x = x;
        this.y = y;
        this.layer = layer;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof CACellKey)) return false;
        CACellKey key = (CACellKey) o;
        return x == key.x && y == key.y && layer == key.layer;
    }

    @Override
    public int hashCode() {
        int result = 1031*layer + 31*x + y;
        return result;
    }

}
