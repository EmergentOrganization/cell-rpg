package com.emergentorganization.cellrpg.scenes.submenus;

import com.badlogic.gdx.scenes.scene2d.InputEvent;
import com.badlogic.gdx.scenes.scene2d.utils.ClickListener;
import com.badlogic.gdx.utils.Align;
import com.emergentorganization.cellrpg.components.entity.ShieldComponent;
import com.emergentorganization.cellrpg.scenes.Scene;
import com.kotcrab.vis.ui.widget.VisTable;
import com.kotcrab.vis.ui.widget.VisTextButton;

/**
 * Created by 7yl4r on 9/1/2015.
 */
public class DebugMenu extends Submenu{
    public DebugMenu(VisTable table, Scene parent_scene){
        super(table, parent_scene);
    }

    @Override
    public void launchSubmenu() {
        super.launchSubmenu();

        VisTextButton damageMe = new VisTextButton("damageMe");
        menuTable.add(damageMe).pad(0f, 0f, 5f, 0f).fill(true, false).row();
        damageMe.addListener(new ClickListener() {
            @Override
            public void clicked(InputEvent event, float x, float y) {
                super.clicked(event, x, y);
                parentScene.getPlayer().getFirstComponentByType(ShieldComponent.class).damage(26);
            }
        });
        menuTable.align(Align.center);

        VisTextButton regenShield = new VisTextButton("regenShield");
        menuTable.add(regenShield).pad(0f, 0f, 5f, 0f).fill(true, false).row();
        regenShield.addListener(new ClickListener() {
            @Override
            public void clicked(InputEvent event, float x, float y) {
                super.clicked(event, x, y);
                // TODO: recharge player shield
                parentScene.getPlayer().getFirstComponentByType(ShieldComponent.class).recharge(26);
            }
        });
        menuTable.align(Align.center);

        VisTextButton back = new VisTextButton("<-back");
        menuTable.add(back).pad(0f, 0f, 5f, 0f).fill(true, false).row();
        back.addListener(new ClickListener() {
            @Override
            public void clicked(InputEvent event, float x, float y) {
                super.clicked(event, x, y);
                closeSubmenu();
                System.out.println("back out of debug menu");
            }
        });
        menuTable.align(Align.center);

        parentScene.getUiStage().addActor(menuWindow);
    }
}
