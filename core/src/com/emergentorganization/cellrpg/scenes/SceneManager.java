package com.emergentorganization.cellrpg.scenes;

import com.badlogic.gdx.utils.IntMap;
import com.emergentorganization.cellrpg.PixelonTransmission;

/**
 * Created by orelb on 10/30/2015.
 */
public class SceneManager {

    private final PixelonTransmission pt;
    private IntMap<BaseScene> scenes;

    public SceneManager(PixelonTransmission pt) {
        this.pt = pt;

        scenes = new IntMap<BaseScene>();
    }

    public void setScene(Scene s) {
        BaseScene old = (BaseScene) pt.getScreen();

        if (scenes.containsKey(s.ordinal())) {
            pt.setScreen(scenes.get(s.ordinal()));
        } else {
            BaseScene newScene = s.getScene(pt);
            if (newScene.shouldStash())
                scenes.put(s.ordinal(), newScene);
            pt.setScreen(newScene);
        }

        if (old != null && !old.shouldStash())
            old.dispose();
    }

    public void dispose(Scene s) {
        BaseScene scene = scenes.get(s.ordinal());
        if (scene != null) {
            scene.dispose();
            scenes.remove(s.ordinal());
        }
    }

    public void dispose() {
        for (IntMap.Entry<BaseScene> s : scenes) {
            s.value.dispose();
            scenes.remove(s.key);
        }
    }
}
