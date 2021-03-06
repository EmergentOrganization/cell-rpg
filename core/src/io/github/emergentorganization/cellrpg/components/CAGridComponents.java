package io.github.emergentorganization.cellrpg.components;

import com.artemis.Component;
import com.badlogic.gdx.graphics.Camera;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.math.Vector2;
import io.github.emergentorganization.cellrpg.systems.CASystems.CAEdgeSpawnType;
import io.github.emergentorganization.cellrpg.systems.CASystems.CARenderSystem.CellRenderers.CellRenderer;
import io.github.emergentorganization.cellrpg.systems.CASystems.CAs.CA;
import io.github.emergentorganization.cellrpg.systems.CASystems.CAs.CACell.BaseCell;
import io.github.emergentorganization.cellrpg.systems.CASystems.CAs.CACell.CellWithHistory;
import io.github.emergentorganization.cellrpg.systems.CASystems.CAs.CACell.GeneticCell;
import io.github.emergentorganization.cellrpg.systems.CASystems.GeneticCells.GeneticCellBuilders.*;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

/**
 * This component contains a lot of information needed by a ca layer.
 * Much of these sub-components don't make much sense standing alone,
 * and it's not likely this will get used by anything other than the
 * CA grid layers. However, it may be better to separate these out
 * at a later date.
 */

public class CAGridComponents extends Component {
    private static final int OFF_SCREEN_PIXELS = 200;  // number of pixels off screen edge to run CA grid
    private final Logger logger = LogManager.getLogger(getClass());
    public long TIME_BTWN_GENERATIONS = 100;  // ms time in between generation() calls
    public final float SCALE = .025f;  // empirically derived constant... why is it this? idk...

    // location of grid center
    public float gridOriginX = 0;
    public float gridOriginY = 0;

    public long minGenTime = 999999;
    public long maxGenTime = 0;

    public CellType cellType = CellType.BASE;
    public CellRenderer renderType = CellRenderer.COLOR_MAP;
    public CA ca = CA.NO_BUFFER;

    public int intensityPerCell = 0;  // amount of intensity added to MoodSystem per live cell

    public BaseCell[][] states;
    public int cellSize = 3;  // size of each cell [px]
    public int cellCount = 0;  // number of live cells
    public CAEdgeSpawnType edgeSpawner = CAEdgeSpawnType.EMPTY;
    public Color[] stateColorMap;
    public long generation = 0;
    private int stampCount = 0;
    // === NOTE: these used by Genetic Cell NewCell only: ===
    public int lastBuilderStamp = 0;
    private int selectedBuilder = 0;
    // list of cells used to seed when stamps are placed:
    private final GeneticNetworkBuilderInterface[] builders = new GeneticNetworkBuilderInterface[]{
            //new CellAlpha(),
            new AgeDarkener(),
            new MrBlue(),
            new MrGreen(),
            new MrRed()
    };

    public int getState(Vector2 pos) {
        // returns state of cell nearest to given world-coordinates
//        logger.trace("getState(position)");
        int row = getIndexOfX(pos.x);
        int col = getIndexOfY(pos.y);
        return getState(row, col);
    }

    public int getState(final int row, final int col) {
        // returns state of given location, returns 0 for out-of-bounds
//        logger.trace("getState(int,int)");
        try {
            return _getState(row, col);
        } catch (IndexOutOfBoundsException err) {
            return 0;
        }
    }

    public int getLastState(final int row, final int col) {
        switch (cellType) {
            case WITH_HISTORY:
                return getLastState_buffered(row, col);
            case BASE:
            case DECAY:
            case GENETIC:
            default:
                return getLastState_noBuffer(row, col);
        }
    }

    public void init(Camera camera) {
        int sx = (int) (camera.viewportWidth) + 2 * OFF_SCREEN_PIXELS;
        int sy = (int) (camera.viewportHeight) + 2 * OFF_SCREEN_PIXELS;

        int w = sx / (cellSize + 1);  // +1 for border pixel between cells
        int h = sy / (cellSize + 1);

        logger.trace("initializing CAGrid " + w + "(" + sx + "px)x" + h + "(" + sy + "px). cellSize=" + cellSize);

        initStates(w, h);
    }

    private void initStates(int w, int h) {
        states = new BaseCell[w][h];
        // init states. ?required?
        for (int i = 0; i < states.length; i++) {
            for (int j = 0; j < states[0].length; j++) {
                states[i][j] = newCell(0);
            }
        }
        // init states for testing
        //randomizeState(gridComponents);
    }

    private int getLastState_noBuffer(final int row, final int col) {
        // with no buffer there is no last state, just use current
        return getState(row, col);
    }

    private int getLastState_buffered(final int row, final int col) {
        try {
            CellWithHistory cell = (CellWithHistory) states[row][col];
            return cell.getLastState();
        } catch (IndexOutOfBoundsException err) {
            return 0;  // if out-of-bounds, assume state=0
        }
    }

    private int getSizeX() {
        // returns grid size (number of cells) in x-dimension
        return states.length;
    }

    private int getSizeY() {
        // returns grid size (number of cells) in y-dimension
        return states[0].length;
    }

    public float getXOrigin(Camera camera) {
        return -OFF_SCREEN_PIXELS + gridOriginX - camera.position.x / SCALE;
    }

    public float getYOrigin(Camera camera) {
        return -OFF_SCREEN_PIXELS + gridOriginY - camera.position.y / SCALE;
    }

    private GeneticNetworkBuilderInterface getBuilder() {
        // returns most appropriate cell builder
        if (stampCount != lastBuilderStamp) { // to keep builder from changing in middle of a stamp
            lastBuilderStamp = stampCount;
            selectedBuilder++;
            if (selectedBuilder >= builders.length) {
                selectedBuilder = 0;
            }
        }  // else return previously selected builder
        return builders[selectedBuilder];
    }
    // === END Genetic-cell newcell only stuff ===

    public BaseCell newCell(int init_state) {
        // TODO: use of inteface, enum, map like w/ CellRenderer is preferred.
        switch (cellType) {
            case WITH_HISTORY:
                return new CellWithHistory(init_state);
            case GENETIC:
                return new GeneticCell(init_state, getBuilder()).incubate();
            case BASE:
            case DECAY:
            default:
                return new BaseCell(init_state);
        }
    }

    public int getIndexOfX(float x) {
        // returns x index of cell given float position relative to global origin
        float relative_x = x / SCALE - gridOriginX;
        return (int) relative_x / (cellSize + 1) + getSizeX() / 2;
    }

    public int getIndexOfY(float y) {
        float relative_y = y / SCALE - gridOriginY;
        int cell = (int) relative_y / (cellSize + 1) + getSizeY() / 2;
        logger.trace("global y " + y + " (rel:" + relative_y + ") " + " from " + gridOriginY + " nearest to " + cell);
        return cell;
    }

    public Vector2 getPositionOf(final int x, final int y) {
        // returns position vector for given cell location
        Vector2 pos = new Vector2();
        // x = (pos.x /SCALE-gridOriginX)/(cellSize+1) + getSizeX()/2;
        // x = ( xp   /S    -  xo       )/( si     +1) + SX / 2;
        // x - SX/2 = ( xp   /S    -  xo       )/( si     +1)
        // (x - SX/2) * (si + 1) = xp /S - xo
        // (x - SX/2) * (si + 1) + xo = xp/S
        // ( (x - SX/2) * (si + 1) + xo ) * S = xp
        pos.x = ((x - getSizeX() / 2) * (cellSize + 1) + gridOriginX) * SCALE;
        pos.y = ((y - getSizeY() / 2) * (cellSize + 1) + gridOriginY) * SCALE;
        return pos;
    }

    private long stampCenteredAt(final int[][] pattern, int row, int col) {
        //center the pattern
        row -= pattern.length / 2;
        col -= pattern[0].length / 2;
        return stampState(pattern, row, col);
    }

    public long stampCenteredAt(final int[][] pattern, Vector2 position) {
        // stamps a pattern onto the grid centered at the nearest grid cells to the given world position
        int row = getIndexOfX(position.x);
        int col = getIndexOfY(position.y);
        logger.trace("(" + position.x + "," + position.y + ")==>(" + row + "," + col + ")");
        return stampCenteredAt(pattern, row, col);
    }

    public long stampState(final int[][] pattern, final int row, final int col) {
        // stamps a pattern onto the state with top-left corner @ (row, col)
        // returns estimated UNIX time when the pattern will be applied (@ next generation)
        if (row > -1
                && col > -1
                && row < states.length - pattern.length
                && col < states[0].length - pattern[0].length) {

            _stampState(pattern, row, col);
            stampCount++;
            return TIME_BTWN_GENERATIONS;
        } else {
            //logger.warn("attempt to stamp pattern out of bounds (" + row + "," + col +"); error suppressed.");
            return -1;  // -1 indicates pattern not drawn, out-of-CAGrid bounds
        }
    }

    public void setState(final int row, final int col, final int newVal) {
        states[row][col].setState(newVal);
    }

    public String statesToString(int x, int y, int w, int h) {
        // returns string showing state of cells in given rect
        String res = "";
        for (int i = 0; i < w; i++) {
            res += "{";
            for (int j = 0; j < h; j++) {
                res += getState(i + x, j + y) + ",";
            }
            res += "}\n";
        }
        return res;
    }

    public String statesToString() {
        // returns string showing state of all cells
        return statesToString(0, 0, getSizeX(), getSizeY());
    }

    public void fill(int state) {
        // fills the current ca grid with a single state
        // useful mostly for testing
        for (int i = 0; i < states.length; i++) {
            for (int j = 0; j < states[0].length; j++) {
                setState(i, j, state);
            }
        }
    }

    private void _stampState(final int[][] pattern, final int row, final int col) {
        // stamps pattern immediately into given cellStates
        logger.trace("insert " + pattern.length + "x" + pattern[0].length + " pattern @ (" + row + "," + col + ")");
        for (int i = 0; i < pattern.length; i++) {
            for (int j = 0; j < pattern[0].length; j++) {
                states[row + i][col + j].setState(pattern[i][j]);
            }
        }
        logger.trace(pattern.length + "x" + pattern[0].length + " CA stamp applied!");
    }

    private int _getState(final int row, final int col) {
        // returns state, throws exception if out of bounds
        return states[row][col].getState();
    }

    private void checkCellSize(int size) {
        // checks that given size is acceptable, else throws error
        // TODO: appropriate sizes are incorrect here... should be 1, 5, 17, 53...
        if (size == 1) {
            return;
        } else {
            int acceptableSize = 3;
            while (acceptableSize <= size) {
                if (size == acceptableSize) {
                    return;
                } else {
                    acceptableSize = getNextSizeUp(acceptableSize);
                }
            } // else size not acceptable
            throw new UnsupportedOperationException("size must be in 1, 3, 11, 35...");
        }
    }

    private int getNextSizeUp(int lastSize) {
        /*
         * available sizes assuming 1px border between cells given by
         * s(n) = 3(s(n-1))+2 for n > 1 (i.e. starting at s(2)=11)
         */
        if (lastSize < 3) {
            throw new UnsupportedOperationException("previous cell size must be >= 3");
        } else {
            return 3 * lastSize + 2;
        }
    }


}
