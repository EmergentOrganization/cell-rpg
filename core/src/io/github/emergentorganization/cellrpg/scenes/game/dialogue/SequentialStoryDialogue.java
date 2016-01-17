package io.github.emergentorganization.cellrpg.scenes.game.dialogue;


public class SequentialStoryDialogue implements DialogueSequenceInterface {
    int step = -1;
    String[] steps;

    public SequentialStoryDialogue(String[] _steps) {
        steps = _steps;
    }

    public void init() {
        step = -1;
    }

    public String enter() {
        step += 1;
        if (step < steps.length) {
            return steps[step];
        } else {
            return null;
        }
    }

    public String enter(int choice) {
        return enter();
    }
}
