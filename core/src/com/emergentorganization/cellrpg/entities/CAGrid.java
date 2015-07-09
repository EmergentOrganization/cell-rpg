package com.emergentorganization.cellrpg.entities;

import com.badlogic.gdx.graphics.Camera;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.Pixmap;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;


/**
 * Created by tylar on 2015-07-06.
 */
public class CAGrid extends Entity {
    private static final int OFF_SCREEN_PIXELS = 200;  // number of pixels off screen edge to run CA grid
    private static final int TIME_PER_GENERATION = 800;  // ms allocated per generation (approximate, actual is slightly longer)

    public long timeToGenerate = TIME_PER_GENERATION;

    private final Logger logger = LogManager.getLogger(getClass());

    // size of grid in pixels
    private int sx;
    private int sy;

    // size of grid in cells
    private int w;
    private int h;

    // size of each cell
    private int cellSize;

    private long lastGenerationTime = 0;
    private int[][] states;
    private Texture gridTexture;  // texture which contains all cells

    public CAGrid(int sizeOfCells, ZIndex z_index) {
        /*
        :param cellSize: size level of grid
         */
        super(z_index);
        checkCellSize(sizeOfCells);

        cellSize = sizeOfCells;

    }

    @Override
    public void render(SpriteBatch batch){
        super.render(batch);
        long before = System.currentTimeMillis();
        // maintains grid around player while not computing on grid farther from player
        Camera camera = getScene().getGameCamera();
        float scale = getScene().scale;

        //cellSprite.setSize(scale*cellSize, scale*cellSize);
        //cellSprite.setPosition(x, y);
        //sprite.setRotation(moveComponent.getRotation());
        batch.draw(gridTexture,
            -OFF_SCREEN_PIXELS,
            -OFF_SCREEN_PIXELS//);
            sx,
            sy
        );

        logger.info("renderTime=" + (System.currentTimeMillis() - before));
    }

    @Override
    public void update(float deltaTime) {
        super.update(deltaTime);

        if (!getScene().isEditor()) {
            long now = System.currentTimeMillis();
            if (now - lastGenerationTime > TIME_PER_GENERATION) {
                lastGenerationTime = now;
                generate();
                timeToGenerate = System.currentTimeMillis() - now;
                logger.info("new cell generation computed. t=" + timeToGenerate + "ms");
            }
        }
    }

    @Override
    public void added(){
        super.added();
        Camera camera = getScene().getGameCamera();
        float scale = getScene().scale;
        sx = (int)(camera.viewportWidth/scale) + OFF_SCREEN_PIXELS;  // TODO: OFF_SCREEN_PIXELS * 2??? (try it after load test)
        sy = (int)(camera.viewportHeight/scale) + OFF_SCREEN_PIXELS;

        w = sx / (cellSize + 1);  // +1 for border pixel between cells
        h = sy / (cellSize + 1);

        logger.info("created CAGrid "+ w + "(" + sx +"px)x" + h + "(" + sy + "px)" );

        states = new int[w][h];

        // init states for testing (TODO: remove this after testing done, all init to 0)
        randomizeState();
    }

    private void onStateChange(){
        gridTexture = generateSpriteTexture();
    }

    private Texture generateSpriteTexture(){
        // returns texture of entire grid with cells in it
        float x;
        float y;
        Camera camera = getScene().getGameCamera();
        float scale = getScene().scale;
        Pixmap pix = new Pixmap(sx, sy, Pixmap.Format.RGBA8888);
        pix.setColor(0f, 1f, .8f, .2f);

        // TODO: pad leading edge of states with 0s, push existing states over, drop falling edge
        for (int i=0; i < states.length; i++){
            for (int j = 0; j < states[0].length; j++) {
                if (states[i][j] != 0) {
                    // draw square

                    // TODO: adjust position based on camera, move (lower left) corner into negative using OFF_SCREEN_PIXELS
                    x = ((float)(i*(cellSize+1)) - camera.position.x)*scale;  // +1 for cell border
                    y = ((float)(j*(cellSize+1)) - camera.position.y)*scale;

                    pix.drawRectangle((int) x, (int) y, cellSize, cellSize);
                }
            }
        }
        Texture tex = new Texture(pix);
        pix.dispose();
        return tex;
    }

    private void generate(){
        // generates the next frame of the CA
        randomizeState();  // TODO: replace this with actual CA
        onStateChange();
    }

    private void checkCellSize(int size){
        // checks that given size is acceptable, else throws error
        if ( size == 1 ){
            return;
        } else {
            int acceptableSize = 3;
            while (acceptableSize <= size){
                if (size == acceptableSize){
                    return;
                } else {
                    acceptableSize = getNextSizeUp(acceptableSize);
                }
            } // else size not acceptable
            throw new UnsupportedOperationException("size must be in 1, 3, 11, 35...");
        }
    }

    private int getNextSizeUp(int lastSize){
        /*
         * available sizes assuming 1px border between cells given by
         * s(n) = 3(s(n-1))+2 for n > 1 (i.e. starting at s(2)=11)
         */
        if (lastSize < 3){
            throw new UnsupportedOperationException("previous cell size must be >= 3");
        } else {
            return 3*lastSize + 2;
        }
    }

    private void randomizeState(){
        for (int i=0; i < states.length; i++){
            for (int j = 0; j < states[0].length; j++){
                states[i][j] = Math.round(Math.round(Math.random()));  // round twice? one is just a cast (I think)
            }
        }
        onStateChange();
    }
}
