package com.emergentorganization.cellrpg.input.player.WeaponControls;

import com.artemis.ComponentMapper;
import com.artemis.World;
import com.emergentorganization.cellrpg.components.InputComponent;
import com.emergentorganization.cellrpg.input.InputProcessor;

/**
 * Created by 7yl4r on 1/4/2016.
 */
public abstract class iWeaponCtrl extends InputProcessor {

    public iWeaponCtrl(World world, ComponentMapper<InputComponent> inp_m){
        super(world, inp_m);
    }

    @Override
    public abstract void process(int entityId);

}
