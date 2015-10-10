package com.emergentorganization.cellrpg.story.dialogue;

/**
 * Created by 7yl4r on 10/9/2015.
 */
public class ArcadeStory implements DialogueSequenceInterface{
    int step;

    public void init(){
        step = -1;
    }

    public String enter(){
        step+=1;
        switch(step){
            case 0:
                return "you have connected a hostile planiverse.";
            case 1:
                return "Vyroids have consumed all matter here.";
            case 2:
                return "Stay as long as you like to study the vyroids...";
            case 3:
                return "...but your planiverse bridge orb won't survive the onslaught very long.";
            default:
                return null;
        }
    }

    public String enter(int choice){
        return enter();
    }
}
