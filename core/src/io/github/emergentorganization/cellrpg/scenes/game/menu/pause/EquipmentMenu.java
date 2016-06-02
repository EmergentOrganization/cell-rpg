package io.github.emergentorganization.cellrpg.scenes.game.menu.pause;

import com.artemis.Entity;
import com.artemis.World;
import com.artemis.managers.TagManager;
import com.badlogic.gdx.scenes.scene2d.Stage;
import com.badlogic.gdx.scenes.scene2d.ui.HorizontalGroup;
import com.badlogic.gdx.scenes.scene2d.ui.Image;
import com.badlogic.gdx.scenes.scene2d.ui.ImageButton;
import com.badlogic.gdx.scenes.scene2d.ui.VerticalGroup;
import com.badlogic.gdx.utils.Align;
import com.kotcrab.vis.ui.widget.VisLabel;
import com.kotcrab.vis.ui.widget.VisTable;
import com.kotcrab.vis.ui.widget.VisTextButton;
import io.github.emergentorganization.cellrpg.PixelonTransmission;
import io.github.emergentorganization.cellrpg.components.EnergyLevel;
import io.github.emergentorganization.cellrpg.components.EquipmentList;
import io.github.emergentorganization.cellrpg.core.Tags;
import io.github.emergentorganization.cellrpg.core.entityfactory.Entities.Equipment.Equipment;
import io.github.emergentorganization.cellrpg.core.entityfactory.Entities.Player;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

public class EquipmentMenu extends Submenu {
    PixelonTransmission pt;
    World world;

    private final Logger logger = LogManager.getLogger(getClass());
    public EquipmentMenu(PixelonTransmission pt, World world, VisTable table, Stage stage, String buttonText) {
        super(table, stage, buttonText);
        this.pt = pt;
        this.world = world;
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

        // TODO: show scroll bar if some cards outside of window
        try {
            Entity player = world.getSystem(TagManager.class).getEntity(Tags.PLAYER);
            for (Equipment equip : player.getComponent(EquipmentList.class).equipment){
                appendEquipmentCard(equip);
            }
            appendFreeEnergyIndicator(player);
        } catch(NullPointerException ex) {
            logger.error("cannot show equipment (player not instantiated?)", ex);
        }

        //menuTable.pack();
        //menuTable.layout();
    }

    private void appendFreeEnergyIndicator(Entity target){
        EnergyLevel energyLevel = target.getComponent(EnergyLevel.class);
        VisLabel energyLabel = new VisLabel("Energy Available:"+energyLevel.energyAvailable());
        menuTable.add(energyLabel);
        menuTable.row();
    }

    private void appendEquipmentCard(Equipment equipm){
        final boolean DEBUG = true;
        // TODO: set color of card based on equipm.type
        final HorizontalGroup mainRow = new HorizontalGroup();
        if (DEBUG) mainRow.debug();

        // === LEFT COLUMN ===
        final VerticalGroup leftCol = new VerticalGroup();
//        leftCol.setFillParent(true);
        if (DEBUG) leftCol.debug();

        //final Image powerImg = new Image();
        final VisLabel powerImg = new VisLabel("pwr:"+equipm.powered);
        leftCol.addActor(powerImg);

        //final Image damageImg = new Image();
        final VisLabel damageImg = new VisLabel("dmg:"+equipm.damaged);
        leftCol.addActor(damageImg);

//        final VisTextButton minusBtn = new VisTextButton("-");
        final ImageButton minusBtn = new ImageButton(pt.getUISkin(), "minus");
        leftCol.addActor(minusBtn);

        mainRow.addActor(leftCol);

        // === MIDDLE COLUMN ===
        final VerticalGroup midCol = new VerticalGroup();
        if (DEBUG) midCol.debug();

        final HorizontalGroup spriteAndDetailGrp = new HorizontalGroup();
        final Image sprite = new Image();
        spriteAndDetailGrp.addActor(sprite);
        final VerticalGroup detailsCol = new VerticalGroup();

        final VisLabel name = new VisLabel(equipm.name);
        detailsCol.addActor(name);

        final HorizontalGroup statsGrp = new HorizontalGroup();
        final VisLabel shieldStat = new VisLabel("|"+equipm.shieldStat +"/e|");
        statsGrp.addActor(shieldStat);
        final VisLabel attackStat = new VisLabel("|"+equipm.attackStat+"/e|");
        statsGrp.addActor(attackStat);
        final VisLabel moveStat = new VisLabel("|"+equipm.moveStat+"/e|");
        statsGrp.addActor(moveStat);
        final VisLabel subStat = new VisLabel("|"+Integer.toString(equipm.satStat)+"|");
        statsGrp.addActor(subStat);
        detailsCol.addActor(statsGrp);

        final VisTextButton actionBtn = new VisTextButton("--------");//"click to edit");
        detailsCol.addActor(actionBtn);

        spriteAndDetailGrp.addActor(detailsCol);

        midCol.addActor(spriteAndDetailGrp);

        // energy bar
        String energyBar = "|";
        // base energy boxes
        for (int i = 0; i < equipm.baseEnergy; i++){
            if (equipm.powerFilled > i) {
                energyBar += "X|";
            } else {
                energyBar += "O|";
            }
        }
        // other energy boxes
        for (int i = 0; i < equipm.energySlots; i++){
            if (equipm.powerFilled - equipm.baseEnergy > i){
                energyBar += "x|";
            } else {
                energyBar += "o|";
            }
        }
        midCol.addActor(new VisLabel(energyBar));

        mainRow.addActor(midCol);

        // === right col ===
        final VerticalGroup rightCol = new VerticalGroup().fill().align(Align.bottom);  // bottom?
//        rightCol.setFillParent(true);
        if (DEBUG) rightCol.debug();

//        final VisTextButton plusBtn = new VisTextButton("+");
        final ImageButton plusBtn = new ImageButton(pt.getUISkin(), "plus");
        plusBtn.bottom().padBottom(1);  // bottom!!!
        rightCol.addActor(plusBtn);
        mainRow.addActor(rightCol);
        // still not bottom? why? :C

        menuTable.add(mainRow);
        menuTable.row();
    }
}
