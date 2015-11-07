package com.emergentorganization.cellrpg.input.player;

import com.artemis.ComponentMapper;
import com.artemis.Entity;
import com.artemis.World;
import com.artemis.managers.TagManager;
import com.emergentorganization.cellrpg.components.Input;
import com.emergentorganization.cellrpg.components.Position;
import com.emergentorganization.cellrpg.core.EntityFactory;
import com.emergentorganization.cellrpg.input.InputProcessor;

/**
 * Created by orelb on 10/29/2015.
 */
public class PlayerInputProcessor extends InputProcessor {

    private TagManager tagManager;

    private PlayerMovement movement;
    private PlayerWeapon weapon;
    // PlayerAbilities abilites; ...

    public PlayerInputProcessor(World world, EntityFactory ef, ComponentMapper<Input> im, ComponentMapper<Position> pm) {
        super(world, im);

        tagManager = world.getSystem(TagManager.class);

        movement = new PlayerMovement(world, im);
        weapon = new PlayerWeapon(world, ef, im, pm);
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

        movement.process(entityId);
        weapon.process(entityId);
        // abilites.process(entityId);
    }
}
