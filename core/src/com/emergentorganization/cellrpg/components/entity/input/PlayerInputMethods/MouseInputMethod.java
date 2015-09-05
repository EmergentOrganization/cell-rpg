package com.emergentorganization.cellrpg.components.entity.input.PlayerInputMethods;

import com.emergentorganization.cellrpg.components.entity.input.PlayerInputComponent;

/**
 * Created by 7yl4r on 9/4/2015.
 */
public class MouseInputMethod extends BaseInputMethod {

    public MouseInputMethod(PlayerInputComponent parent) {
        super(parent);
    }

    @Override
    public String getName(){
        return "mouse only";
    }
}
