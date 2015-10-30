package com.emergentorganization.cellrpg.scenes;

import com.emergentorganization.cellrpg.scenes.game.ArtemisScene;
import com.emergentorganization.cellrpg.scenes.menu.MainMenu;

/**
 * Created by orelb on 10/30/2015.
 */
public enum Scene {

    Main_Menu {
        @Override
        public BaseScene getScene() {
            return new MainMenu();
        }
    },
    Game {
        @Override
        public BaseScene getScene() {
            return new ArtemisScene();
        }
    };

    public abstract BaseScene getScene();
}
