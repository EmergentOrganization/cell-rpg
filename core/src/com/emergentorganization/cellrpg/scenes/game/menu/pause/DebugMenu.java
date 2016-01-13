package com.emergentorganization.cellrpg.scenes.game.menu.pause;

import com.badlogic.gdx.scenes.scene2d.InputEvent;
import com.badlogic.gdx.scenes.scene2d.Stage;
import com.badlogic.gdx.scenes.scene2d.utils.ClickListener;
import com.kotcrab.vis.ui.widget.VisTable;
import com.kotcrab.vis.ui.widget.VisTextButton;


public class DebugMenu extends Submenu {
    public DebugMenu(VisTable table, Stage stage, String buttonText) {
        super(table, stage, buttonText);
    }

    @Override
    public void launchSubmenu() {
        super.launchSubmenu();

        // set up menu buttons:
        VisTextButton damageMe = new VisTextButton("damageMe");
        menuTable.add(damageMe).pad(0f, 0f, 5f, 0f).fill(true, false).row();
        damageMe.addListener(new ClickListener() {
            @Override
            public void clicked(InputEvent event, float x, float y) {
                super.clicked(event, x, y);
                //parentScene.getPlayer().getFirstComponentByType(ShieldComponent.class).damage();
            }
        });

        VisTextButton regenShield = new VisTextButton("regenShield");
        menuTable.add(regenShield).pad(0f, 0f, 5f, 0f).fill(true, false).row();
        regenShield.addListener(new ClickListener() {
            @Override
            public void clicked(InputEvent event, float x, float y) {
                super.clicked(event, x, y);
                //parentScene.getPlayer().getFirstComponentByType(ShieldComponent.class).addEnergy(26);
            }
        });
    }
}
