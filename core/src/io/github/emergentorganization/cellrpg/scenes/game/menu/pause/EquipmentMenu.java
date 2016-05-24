package io.github.emergentorganization.cellrpg.scenes.game.menu.pause;

import com.badlogic.gdx.Preferences;
import com.badlogic.gdx.scenes.scene2d.Stage;
import com.badlogic.gdx.scenes.scene2d.ui.HorizontalGroup;
import com.badlogic.gdx.scenes.scene2d.ui.Image;
import com.badlogic.gdx.scenes.scene2d.ui.ImageButton;
import com.badlogic.gdx.scenes.scene2d.ui.VerticalGroup;
import com.badlogic.gdx.utils.Align;
import com.kotcrab.vis.ui.widget.VisImageButton;
import com.kotcrab.vis.ui.widget.VisLabel;
import com.kotcrab.vis.ui.widget.VisTable;
import com.kotcrab.vis.ui.widget.VisTextButton;
import io.github.emergentorganization.cellrpg.PixelonTransmission;
import io.github.emergentorganization.cellrpg.tools.GameSettings;

public class EquipmentMenu extends Submenu {
    PixelonTransmission pt;
    public EquipmentMenu(PixelonTransmission pt, VisTable table, Stage stage, String buttonText) {
        super(table, stage, buttonText);
        this.pt = pt;
    }

    @Override
    public void launchSubmenu() {
        super.launchSubmenu();

//        final Preferences prefs = GameSettings.getPreferences();
//        final VisTextButton devMode = new VisTextButton( );
//        menuTable.add(devMode).pad(0f, 0f, 5f, 0f).fill(true, false).row();
//        devMode.addListener(new ClickListener(){
//            @Override
//        public void clicked(InputEvent event, float x, float y){
//                super.clicked(event, x, y);
//                prefs.putBoolean(GameSettings.KEY_DEV_DEVMODE, !GameSettings.devMode());
//                devMode.setText( );
//            }
//        });

        final HorizontalGroup mainRow = new HorizontalGroup();

        // === LEFT COLUMN ===
        final VerticalGroup leftCol = new VerticalGroup();

        //final Image powerImg = new Image();
        final VisLabel powerImg = new VisLabel("pwr");
        leftCol.addActor(powerImg);

        //final Image damageImg = new Image();
        final VisLabel damageImg = new VisLabel("dmg");
        leftCol.addActor(damageImg);

//        final VisTextButton minusBtn = new VisTextButton("-");
        final ImageButton minusBtn = new ImageButton(pt.getUISkin(), "minus");
        leftCol.addActor(minusBtn);

        mainRow.addActor(leftCol);

        // === MIDDLE COLUMN ===
        final VerticalGroup midCol = new VerticalGroup();
        final HorizontalGroup spriteAndDetailGrp = new HorizontalGroup();
        final Image sprite = new Image();
        spriteAndDetailGrp.addActor(sprite);
        final VerticalGroup detailsCol = new VerticalGroup();

        final VisLabel name = new VisLabel("module name");
        detailsCol.addActor(name);

        final HorizontalGroup statsGrp = new HorizontalGroup();
        final VisLabel shieldStat = new VisLabel("#/e");
        statsGrp.addActor(shieldStat);
        final VisLabel attackStat = new VisLabel("#/e");
        statsGrp.addActor(attackStat);
        final VisLabel moveStat = new VisLabel("#/e");
        statsGrp.addActor(moveStat);
        final VisLabel subStat = new VisLabel("#");
        statsGrp.addActor(subStat);
        detailsCol.addActor(statsGrp);

        final VisTextButton actionBtn = new VisTextButton("click to edit");
        detailsCol.addActor(actionBtn);

        spriteAndDetailGrp.addActor(detailsCol);

        midCol.addActor(spriteAndDetailGrp);

        // energy bar
        midCol.addActor(new VisLabel("---------"));

        mainRow.addActor(midCol);

        // === right col ===
        final VerticalGroup rightCol = new VerticalGroup().align(Align.bottom);  // bottom?
//        final VisTextButton plusBtn = new VisTextButton("+");
        final ImageButton plusBtn = new ImageButton(pt.getUISkin(), "plus");
        plusBtn.bottom();  // bottom!!!
        rightCol.addActor(plusBtn);
        mainRow.addActor(rightCol);

        // still not bottom? why? :C

        menuTable.add(mainRow);

        menuTable.row();
        //menuTable.pack();
        //menuTable.layout();
    }
}
