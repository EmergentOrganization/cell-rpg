package io.github.emergentorganization.cellrpg.scenes;

import io.github.emergentorganization.cellrpg.PixelonTransmission;


public class SceneManager {

    private final PixelonTransmission pt;

    public SceneManager(PixelonTransmission pt) {
        this.pt = pt;
    }

    public void setScene(Scene newScene) {
        BaseScene old = (BaseScene) pt.getScreen();
        pt.setScreen(newScene.getScene(pt));

        if (old != null) {
            old.dispose();
        }
    }
}
