package com.emergentorganization.cellrpg.input.player;

import com.artemis.ComponentMapper;
import com.artemis.Entity;
import com.artemis.World;
import com.artemis.managers.TagManager;
import com.badlogic.gdx.Preferences;
import com.badlogic.gdx.graphics.glutils.ShapeRenderer;
import com.emergentorganization.cellrpg.components.Bounds;
import com.emergentorganization.cellrpg.components.InputComponent;
import com.emergentorganization.cellrpg.components.Position;
import com.emergentorganization.cellrpg.core.entityfactory.EntityFactory;
import com.emergentorganization.cellrpg.input.InputProcessor;
import com.emergentorganization.cellrpg.input.player.MovementControls.PathDraw;
import com.emergentorganization.cellrpg.input.player.MovementControls.WASD;
import com.emergentorganization.cellrpg.input.player.WeaponControls.ArrowsShoot;
import com.emergentorganization.cellrpg.input.player.WeaponControls.ClickShoot;
import com.emergentorganization.cellrpg.tools.GameSettings;

import java.util.ArrayList;

/**
 * processes user input and produces movement of player avatar.
 */
public class PlayerInputProcessor extends InputProcessor {

    Preferences prefs;
    private TagManager tagManager;
    private ArrayList<iPlayerCtrl> movementControls = new ArrayList<iPlayerCtrl>();  // TODO: should be list of iMovementCtrl
    private ArrayList<iPlayerCtrl> weaponControls = new ArrayList<iPlayerCtrl>();
    // PlayerAbilities abilites; ...

    public PlayerInputProcessor(World world, EntityFactory ef, ComponentMapper<InputComponent> im,
                                ComponentMapper<Position> pm, ComponentMapper<Bounds> bm,
                                ShapeRenderer renderer) {
        super(world, im);

        tagManager = world.getSystem(TagManager.class);

        prefs = GameSettings.getPreferences();

        movementControls.add(new WASD(world, im));
        movementControls.add(new PathDraw(world, im, renderer));
        // TODO: add more movement control options

        weaponControls.add(new ClickShoot(world, ef, im));
        weaponControls.add(new ArrowsShoot(world, ef, im));
        // TODO: add more weapon control options
    }

    public String[] getMovementCtrlChoices() {
        // returns list of movement control names.
        String nameList[] = new String[movementControls.size()];
        for (int i = 0; i < movementControls.size(); i++) {
            nameList[i] = movementControls.get(i).getName();
        }
        return nameList;
    }

    public String[] getWeaponCtrlChoices() {
        // returns list of weapon control names.
        String nameList[] = new String[weaponControls.size()];
        for (int i = 0; i < weaponControls.size(); i++) {
            nameList[i] = weaponControls.get(i).getName();
        }
        return nameList;
    }

    public iPlayerCtrl getPlayerMovement() {
        return movementControls.get(prefs.getInteger(GameSettings.KEY_MOVEMENT_CONTROL_METHOD));
    }

    public iPlayerCtrl getPlayerWeapon() {
        return weaponControls.get(prefs.getInteger(GameSettings.KEY_WEAPON_CONTROL_METHOD));
    }

    private boolean isPlayer(int entityId) {
        Entity player = tagManager.getEntity("player");

        if (player.getId() == entityId)
            return true;

        return false;
    }

    public void process(int entityId) {
        if (!isPlayer(entityId)) {
            return;
        } else {
            Entity player = tagManager.getEntity("player");
            getPlayerMovement().process(player);
            getPlayerWeapon().process(player);
            // abilites.process(entityId);
        }
    }
}
