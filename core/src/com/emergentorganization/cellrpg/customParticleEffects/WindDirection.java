package com.emergentorganization.cellrpg.customParticleEffects;

/**
 * Created by 7yl4r on 9/12/2015.
 */
public enum WindDirection {
    UP,
    DOWN,
    RIGHT,
    LEFT,
    UP_LEFT,
    UP_RIGHT,
    DOWN_LEFT,
    DOWN_RIGHT;

    public int getX(){
        switch (this){
            case RIGHT:
            case UP_RIGHT:
            case DOWN_RIGHT:
                return 1;
            case LEFT:
            case UP_LEFT:
            case DOWN_LEFT:
                return -1;
            default:
                return 0;
        }
    }

    public int getY(){
        switch (this){
            case UP:
            case UP_LEFT:
            case UP_RIGHT:
                return -1;
            case DOWN:
            case DOWN_LEFT:
            case DOWN_RIGHT:
                return 1;
            default:
                return 0;
        }
    }
}
