package io.github.emergentorganization.cellrpg.scenes.game.menu.pause.Equipment;

import com.artemis.Entity;
import com.artemis.World;
import com.artemis.managers.TagManager;
import com.badlogic.gdx.scenes.scene2d.InputEvent;
import com.badlogic.gdx.scenes.scene2d.Stage;
import com.badlogic.gdx.scenes.scene2d.Touchable;
import com.badlogic.gdx.scenes.scene2d.ui.Image;
import com.badlogic.gdx.scenes.scene2d.ui.ImageButton;
import com.badlogic.gdx.scenes.scene2d.utils.ClickListener;
import com.kotcrab.vis.ui.widget.VisLabel;
import com.kotcrab.vis.ui.widget.VisTable;
import com.kotcrab.vis.ui.widget.VisTextButton;
import io.github.emergentorganization.cellrpg.PixelonTransmission;
import io.github.emergentorganization.cellrpg.components.EnergyLevel;
import io.github.emergentorganization.cellrpg.components.EquipmentList;
import io.github.emergentorganization.cellrpg.core.Tags;
import io.github.emergentorganization.cellrpg.core.entityfactory.Entities.Equipment.Equipment;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import io.github.emergentorganization.cellrpg.scenes.game.menu.pause.Submenu;

public class EquipmentMenu extends Submenu {
    private final Logger logger = LogManager.getLogger(getClass());
    private final PixelonTransmission pt;
    private final World world;
    private VisLabel energyLabel;
    private Stage stage;

    public EquipmentMenu(PixelonTransmission pt, World world, VisTable table, Stage stage, String buttonText) {
        super(table, stage, buttonText);
        this.pt = pt;
        this.world = world;
        this.stage = stage;
    }

    @Override
    public void launchSubmenu() {
        super.launchSubmenu();
        menuTable.addSeparator();
        //menuTable.setDebug(true);
        //menuTable.debugAll();

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
            EnergyLevel energyLevel = player.getComponent(EnergyLevel.class);

            for (Equipment equip : player.getComponent(EquipmentList.class).equipment) {
                appendEquipmentCard(equip, energyLevel);
            }
            appendFreeEnergyIndicator(energyLevel);
        } catch (NullPointerException ex) {
            logger.error("cannot show equipment (player not instantiated?)", ex);
        }

        //menuTable.pack();
        //menuTable.layout();
    }

    private void appendFreeEnergyIndicator(EnergyLevel energyLevel) {
        energyLabel = new VisLabel();
        update_energyLabel(energyLevel);
        menuTable.add(energyLabel);
        menuTable.row();
    }

    private void appendEquipmentCard(final Equipment equipm, final EnergyLevel energyLevel) {
        // TODO: set color of card based on equipm.type
        final VisTable cardContainer = new VisTable();
        cardContainer.addSeparator(true);

        // === LEFT COLUMN ===
        final VisTable leftCol = new VisTable();

        // Power
        final VisLabel powerImg = new VisLabel();
        update_pwrIndicator(powerImg, equipm);
        leftCol.add(powerImg);
        leftCol.row().expandX();

        // Damage
        final VisLabel damageImg = new VisLabel("dmg:" + equipm.damaged);
        leftCol.add(damageImg);
        leftCol.row().expandX();

        // Minus
        final ImageButton minusBtn = new ImageButton(pt.getUISkin(), "minus");
        leftCol.add(minusBtn).expand().right().bottom();

        cardContainer.add(leftCol).pad(2f).expandY().fill();

        // === MIDDLE COLUMN ===
        final VisTable midCol = new VisTable();

        final VisTable spriteAndDetailGrp = new VisTable();
        final Image sprite = new Image();
        spriteAndDetailGrp.add(sprite);
        final VisTable detailsCol = new VisTable();

        final VisLabel name = new VisLabel(equipm.name);
        detailsCol.add(name).row();

        final VisTable statsGrp = new VisTable();
        final VisLabel shieldStat = new VisLabel();
        statsGrp.add(shieldStat);
        final VisLabel attackStat = new VisLabel();
        statsGrp.add(attackStat);
        final VisLabel moveStat = new VisLabel();
        statsGrp.add(moveStat);
        final VisLabel subStat = new VisLabel();
        statsGrp.add(subStat);
        update_stats(equipm, shieldStat, attackStat, moveStat, subStat);
        detailsCol.add(statsGrp).row();

        final VisTextButton actionBtn = new VisTextButton("click to edit");
        detailsCol.add(actionBtn);

        Slot slot = new Slot(Item.BATTERY, 1);  // TODO: this needs to be replaced...
        SlotTooltip tooltip = new SlotTooltip(slot, pt.getUISkin());
        tooltip.setTouchable(Touchable.disabled); // allows for mouse to hit tooltips in the top-right corner of the screen without flashing
        stage.addActor(tooltip);

        actionBtn.addListener(new TooltipListener(tooltip, true));

        spriteAndDetailGrp.add(detailsCol);

        midCol.add(spriteAndDetailGrp).row();

        // energy bar
        final VisLabel energyBarLabel = new VisLabel("");
        update_energyBarLabel(energyBarLabel, equipm);
        midCol.add(energyBarLabel).bottom().center();

        cardContainer.add(midCol).pad(2f).expand().fill();

        // === right col ===
        final VisTable rightCol = new VisTable();
        final ImageButton plusBtn = new ImageButton(pt.getUISkin(), "plus");
        rightCol.add(plusBtn).expand().bottom().left();
        cardContainer.add(rightCol).pad(2f).expandY().fill();


        cardContainer.addSeparator(true);
        menuTable.add(cardContainer).expandX().fill();
        menuTable.row();
        menuTable.addSeparator();

        plusBtn.addListener(new ClickListener() {
            @Override
            public void clicked(InputEvent event, float x, float y) {
                super.clicked(event, x, y);
                logger.trace(!equipm.powerIsFull());
                if (!equipm.powerIsFull() && energyLevel.allocateEnergy(1)) {
                    equipm.powerFilled += 1;
                    update_energyLabel(energyLevel);
                    update_energyBarLabel(energyBarLabel, equipm);
                    update_pwrIndicator(powerImg, equipm);
                    update_stats(equipm, shieldStat, attackStat, moveStat, subStat);
                } // else not enough power available or already full
            }
        });

        minusBtn.addListener(new ClickListener() {
            @Override
            public void clicked(InputEvent event, float x, float y) {
                super.clicked(event, x, y);
                if (equipm.powerIsEmpty() && energyLevel.freeEnergy(1)) {  // if power not empty
                    equipm.powerFilled -= 1;
                    update_energyLabel(energyLevel);
                    update_energyBarLabel(energyBarLabel, equipm);
                    update_pwrIndicator(powerImg, equipm);
                    update_stats(equipm, shieldStat, attackStat, moveStat, subStat);
                } // else no power allocated to free
            }
        });
    }

    private void update_stats(Equipment equipm,
                              VisLabel shieldStat, VisLabel attackStat, VisLabel moveStat, VisLabel subStat) {
        shieldStat.setText("|" + equipm.shieldStat() + "/e|");
        attackStat.setText("|" + equipm.attackStat() + "/e|");
        moveStat.setText("|" + equipm.moveStat() + "/e|");
        subStat.setText("|" + Integer.toString(equipm.satStat()) + "|");
    }

    private void update_pwrIndicator(VisLabel powerIndicator, Equipment equipm) {
        powerIndicator.setText("pwr:" + equipm.isPowered());
    }

    private void update_energyLabel(EnergyLevel energyLevel) {
        energyLabel.setText("Energy Available:" + energyLevel.energyAvailable());
    }

    private void update_energyBarLabel(VisLabel energyBarLabel, Equipment equipm) {
        String energyBar = "|";
        // base energy boxes
        for (int i = 0; i < equipm.baseEnergy; i++) {
            if (equipm.powerFilled > i) {
                energyBar += "X|";
            } else {
                energyBar += "O|";
            }
        }
        // other energy boxes
        for (int i = 0; i < equipm.energySlots; i++) {
            if (equipm.powerFilled - equipm.baseEnergy > i) {
                energyBar += "x|";
            } else {
                energyBar += "o|";
            }
        }
        energyBarLabel.setText(energyBar);
    }
}
