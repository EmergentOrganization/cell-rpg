package com.emergentorganization.cellrpg.scenes;

import com.emergentorganization.cellrpg.PixelonTransmission;
import com.emergentorganization.cellrpg.scenes.game.ArtemisScene;
import com.emergentorganization.cellrpg.scenes.menu.MainMenu;

/**
 * Created by orelb on 10/30/2015.
 */
public enum Scene {

    MAIN_MENU {
        @Override
        public BaseScene getScene(PixelonTransmission pt) {
            return new MainMenu(pt);
        }
    },
    GAME {
        @Override
        public BaseScene getScene(PixelonTransmission pt) {
            return new ArtemisScene(pt);
        }
    };

    public abstract BaseScene getScene(PixelonTransmission pt);
}
