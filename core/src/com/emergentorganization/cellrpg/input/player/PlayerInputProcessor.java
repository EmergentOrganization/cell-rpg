package com.emergentorganization.cellrpg.input.player;

import com.artemis.ComponentMapper;
import com.artemis.Entity;
import com.artemis.World;
import com.artemis.managers.TagManager;
import com.emergentorganization.cellrpg.components.Input;
import com.emergentorganization.cellrpg.input.InputProcessor;

/**
 * Created by orelb on 10/29/2015.
 */
public class PlayerInputProcessor extends InputProcessor{

    private TagManager tagManager;

    private PlayerMovement movement;
    // PlayerShoot shoot; ...
    // PlayerAbilities abilites; ...

    public PlayerInputProcessor(World world, ComponentMapper<Input> im) {
        super(world, im);

        tagManager = world.getSystem(TagManager.class);

        movement = new PlayerMovement(world, im);
    }

    private boolean isPlayer(int entityId){
        Entity player = tagManager.getEntity("player");

        if(player.getId() == entityId)
            return true;

        return false;
    }

    @Override
    public void process(int entityId) {
        if(!isPlayer(entityId))
            return;

        movement.process(entityId);

        // shoot.process(entityId);
        // abilites.process(entityId);
    }
}
