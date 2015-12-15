//package com.emergentorganization.cellrpg.systems.CARenderSystem.CAGrid;
//
//import com.badlogic.gdx.graphics.Color;
//import com.badlogic.gdx.graphics.glutils.ShapeRenderer;
//
///**
// * Created by tylar on 2015-07-06.
// */
//public class decayCAGrid extends CAGridBase {
//    private final int MIN_RENDER = -3;
//    private final float DELT = 1f/(float) MIN_RENDER;
//
//    public decayCAGrid(int sizeOfCells,Color[] state_color_map) {
//        /*
//        @sizeOfCells     : display size of an individual cell
//        @z_index         : ZIndex level to render the grid
//        @state_color_map : list of colors which correspond to ca states  TODO: use a CAColorDefinition class instead
//         */
//        super(sizeOfCells, state_color_map);
//    }
//
//    @Override
//    protected void generate() {
//        super.generate();
//        // generates the next frame of the CA
//        for (int i = 0; i < states.length; i++) {
//            for (int j = 0; j < states[0].length; j++) {
//                states[i][j].setState(states[i][j].getState() - 1);
//            }
//        }
//    }
//
//    public int getLastState(final int row, final int col){
//        // with no buffer there is no last state, just use current
//        return getState(row, col);
//    }
//
//    private void drawSquare(final int i, final int j, ShapeRenderer shapeRenderer, final float x_origin, final float y_origin){
//        float x = i * (cellSize + 1) + x_origin;  // +1 for cell border
//        float y = j * (cellSize + 1) + y_origin;
//        shapeRenderer.rect(x, y, cellSize, cellSize);
//    }
//    @Override
//    protected void renderCell(final int i, final int j, ShapeRenderer shapeRenderer,
//                            final float x_origin, final float y_origin){
//        if (states[i][j].getState() > 0) {  // state must be > 0 else stateColorMap indexError
//            // draw square
//            shapeRenderer.setColor(stateColorMap[states[i][j].getState()-1]);
//            drawSquare(i, j, shapeRenderer, x_origin, y_origin);
//        } else if (states[i][j].getState() > MIN_RENDER){
//            shapeRenderer.setColor(new Color(
//                    stateColorMap[0].r,
//                    stateColorMap[0].g,
//                    stateColorMap[0].b,
//                    stateColorMap[0].a - DELT * (float)states[i][j].getState()
//            ));
//            drawSquare(i, j, shapeRenderer, x_origin, y_origin);
//        }  // else don't bother
//    }
//}