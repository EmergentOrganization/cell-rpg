package com.emergentorganization.cellrpg.scenes.game.dialogue;


public interface DialogueSequenceInterface {

    void init();
    // initialize dialogue

    String enter(int option);
    // select dialogue option, move to next dialogue section
    //   returns next dialog text if more, null dialog sequence is over

    String enter();
    // select default option, move to next dialogue section
    //   returns next dialog text if more, null dialog sequence is over

}
