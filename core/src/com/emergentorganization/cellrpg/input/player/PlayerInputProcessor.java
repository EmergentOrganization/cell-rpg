package com.emergentorganization.cellrpg.input.player;

import com.artemis.ComponentMapper;
import com.artemis.Entity;
import com.artemis.World;
import com.artemis.managers.TagManager;
import com.badlogic.gdx.Preferences;
import com.emergentorganization.cellrpg.components.Bounds;
import com.emergentorganization.cellrpg.components.InputComponent;
import com.emergentorganization.cellrpg.components.Position;
import com.emergentorganization.cellrpg.core.entityfactory.EntityFactory;
import com.emergentorganization.cellrpg.input.InputProcessor;
import com.emergentorganization.cellrpg.input.player.MovementControls.WASD;
import com.emergentorganization.cellrpg.input.player.WeaponControls.ArrowsShoot;
import com.emergentorganization.cellrpg.input.player.WeaponControls.ClickShoot;
import com.emergentorganization.cellrpg.input.player.WeaponControls.iWeaponCtrl;
import com.emergentorganization.cellrpg.tools.GameSettings;

import java.util.ArrayList;

/**
 * Created by orelb on 10/29/2015.
 */
public class PlayerInputProcessor extends InputProcessor {

    private TagManager tagManager;
    Preferences prefs;

    private ArrayList<WASD> movementControls = new ArrayList<WASD>();  // TODO: should be list of iMovementCtrl
    private ArrayList<iWeaponCtrl> weaponControls = new ArrayList<iWeaponCtrl>();
    // PlayerAbilities abilites; ...

    public PlayerInputProcessor(World world, EntityFactory ef, ComponentMapper<InputComponent> im,
                                ComponentMapper<Position> pm, ComponentMapper<Bounds> bm) {
        super(world, im);

        tagManager = world.getSystem(TagManager.class);

        prefs = GameSettings.getPreferences();

        prefs.putInteger(GameSettings.KEY_MOVEMENT_CONTROL_METHOD, 0);  // default to first controller
        movementControls.add(new WASD(world, im));
        // TODO: add more movement control options

        prefs.putInteger(GameSettings.KEY_WEAPON_CONTROL_METHOD, 0);  // default to first controller
        weaponControls.add(new ClickShoot(world, ef, im));
        weaponControls.add(new ArrowsShoot(world, ef, im));
        // TODO: add more weapon control options
    }

    private InputProcessor getPlayerMovement(){
        return movementControls.get(prefs.getInteger(GameSettings.KEY_MOVEMENT_CONTROL_METHOD));
    }

    private InputProcessor getPlayerWeapon(){
        return weaponControls.get(prefs.getInteger(GameSettings.KEY_WEAPON_CONTROL_METHOD));
    }

    private boolean isPlayer(int entityId) {
        Entity player = tagManager.getEntity("player");

        if (player.getId() == entityId)
            return true;

        return false;
    }

    @Override
    public void process(int entityId) {
        if (!isPlayer(entityId))
            return;

        getPlayerMovement().process(entityId);
        getPlayerWeapon().process(entityId);
        // abilites.process(entityId);
    }
}
