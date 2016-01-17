package com.emergentorganization.cellrpg.scenes;

import io.github.emergentorganization.engine.PixelonTransmission;
import com.emergentorganization.cellrpg.scenes.game.Arcade;
import com.emergentorganization.cellrpg.scenes.game.LifeGeneLab;
import com.emergentorganization.cellrpg.scenes.game.Story;
import com.emergentorganization.cellrpg.scenes.menu.MainMenu;
import com.emergentorganization.cellrpg.tools.mapeditor.MapEditor;


public enum Scene {

    MAIN_MENU {
        @Override
        public BaseScene getScene(PixelonTransmission pt) {
            return new MainMenu(pt);
        }
    },
    ARCADE {
        @Override
        public BaseScene getScene(PixelonTransmission pt) {
            return new Arcade(pt);
        }
    },
    LAB {
        @Override
        public BaseScene getScene(PixelonTransmission pt) {
            return new LifeGeneLab(pt);
        }
    },
    EDITOR {
        @Override
        public BaseScene getScene(PixelonTransmission pt) {
            return new MapEditor(pt);
        }
    },
    STORY {
        @Override
        public BaseScene getScene(PixelonTransmission pt) {
            return new Story(pt);
        }
    };

    public abstract BaseScene getScene(PixelonTransmission pt);
}
