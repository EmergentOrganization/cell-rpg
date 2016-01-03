package com.emergentorganization.cellrpg.scenes.game.dialogue;

/**
 * Created by 7yl4r on 2016-01-03.
 */
public class ArcadeStory2 implements DialogueSequenceInterface{
    int step;

    public void init(){
        step = -1;
    }

    public String enter(){
        step+=1;
        switch(step){
            case 0:
                return "the vyroids are alerted to your presence!.";
            case 1:
                return "More vyroids incoming!.";
            default:
                return null;
        }
    }

    public String enter(int choice){
        return enter();
    }
}
