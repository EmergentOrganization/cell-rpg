package io.github.emergentorganization.cellrpg.core.systems.MusicSystem;

import java.util.TimerTask;


/**
 * Used to manually loop the constantLoop (so we can drop in triggered tracks at appropriate time)
 * NOTE: this will break if deltaTime >= loop length (highly unlikely with long loops)
 */
class LoopTask extends TimerTask {
    private final MusicSystem system;

    public LoopTask(MusicSystem system) {
        this.system = system;
    }

    @Override
    public void run() {
        system.runLater(new Runnable() {
            @Override
            public void run() {
                if (system.isPrepped()) {
                    system.playConstantLoops();
                    system.updateCurrentLoops();
                    system.playCurrentLoops();
                    system.onLoopCompleted();
                }
            }
        });
    }
}
