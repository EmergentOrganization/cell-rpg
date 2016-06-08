package io.github.emergentorganization.cellrpg.systems.CASystems;

/**
 * key which identifies a single CA cell (by it's x,y and cellLayer attributes) to be used in a HashMap
 */
class CACellKey {

    private final int x;
    private final int y;
    private final int layer;

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
        return 1031 * layer + 31 * x + y;
    }

}
