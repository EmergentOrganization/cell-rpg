package com.emergentorganization.cellrpg.systems.CARenderSystem;

import com.artemis.BaseEntitySystem;
import com.badlogic.gdx.graphics.Camera;
import com.emergentorganization.cellrpg.systems.CARenderSystem.CACell.BaseCell;

/**
 * Created by 7yl4r on 12/9/2015.
 */
public class CAPositionSystem extends BaseEntitySystem {


    private void addColLeft(){
        // pushes a column onto left side
        for (int i = states.length-1; i >= 0; i--){
            for (int j = 0; j < states[0].length; j++){
                if ( i == 0 ){
                    states[i][j] = getEdgeState(j);
                } else {
                    states[i][j] = states[i-1][j];
                }
            }
        }
        gridOriginX -= cellSize+1;
    }

    private void addColRight(){
        // pushes a column onto right side
        for (int i = 0; i < states.length; i++){
            for (int j = 0; j < states[0].length; j++){
                if ( i == states.length-1 ){
                    states[i][j] = getEdgeState(j);
                } else {
                    states[i][j] = states[i+1][j];
                }
            }
        }
        gridOriginX += cellSize+1;
    }

    private void addRowBottom(){
        // pushes a column of zeros
        for (int i = 0; i < states.length; i++){
            for (int j = states[0].length-1; j >= 0; j--){
                if ( j == 0 ){
                    states[i][j] = getEdgeState(i);
                } else {
                    states[i][j] = states[i][j-1];
                }
            }
        }
        gridOriginY -= cellSize+1;
    }

    private void addRowTop(){
        // pushes a column of zeros
        for (int i = 0; i < states.length; i++){
            for (int j = 0; j < states[0].length; j++){
                if ( j == states[0].length-1 ){
                    states[i][j] = getEdgeState(i);
                } else {
                    states[i][j] = states[i][j+1];
                }
            }
        }
        gridOriginY += cellSize+1;
    }

    private BaseCell getEdgeState(int x){
        // returns cell state in position x on a newly added edge
        int state;
        if (edgeSpawner == CAEdgeSpawnType.RANDOM_SPARSE) {
            state = getRandomState(0.05f);
        } else if ( edgeSpawner == CAEdgeSpawnType.RANDOM_50_50) {
            state = getRandomState(.5f);
        } else if ( edgeSpawner == CAEdgeSpawnType.RANDOM_DENSE){
            state = getRandomState(.8f);
        } else if (edgeSpawner == CAEdgeSpawnType.EMPTY){
            state = 0;
        } else {
            throw new IllegalStateException("edgeSpawn type not recognized");
        }
        return newCell(state);  // TODO: this should be constructor based on desired cell type
    }

    private void gridFollow(Camera camera){
        // enables grid to follow the camera

        float dY = gridOriginY - camera.position.y/SCALE;
        //System.out.println(dY + "=" + gridOriginY + "-" + camera.position.y + "/" + scale);

        while ( dY > cellSize+1){
            //System.out.println("BotAddRow");
            addRowBottom();
            dY = gridOriginY - camera.position.y/SCALE;
            //System.out.println(dY + "=" + gridOriginY + "-" + camera.position.y + "/" + scale);
        }
        while ( dY < -cellSize+1){
            //System.out.println("TopAddRow");
            addRowTop();
            dY = gridOriginY - camera.position.y/SCALE;
            //System.out.println(dY + "=" + gridOriginY + "-" + camera.position.y + "/" + scale);
        }

        float dX = gridOriginX - camera.position.x/SCALE;

        while ( dX > cellSize+1){
            addColLeft();
            dX = gridOriginX - camera.position.x/SCALE;
        }
        while ( dX < -cellSize+1){
            addColRight();
            dX = gridOriginX - camera.position.x/SCALE;
        }

        //logger.info("camera is (" + dX + "," + dY + ") from grid origin.");
    }
}
