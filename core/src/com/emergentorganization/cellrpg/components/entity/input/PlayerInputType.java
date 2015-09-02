package com.emergentorganization.cellrpg.components.entity.input;

/**
 * Created by 7yl4r on 9/2/2015.
 */
public enum PlayerInputType {
    MOUSE,
    TELEPATHIC;

    public String toString() {
        switch(this) {
            case MOUSE: return "mouse control";
            case TELEPATHIC: return "telepathic control";
            default: throw new IllegalArgumentException();
        }
    }
}
