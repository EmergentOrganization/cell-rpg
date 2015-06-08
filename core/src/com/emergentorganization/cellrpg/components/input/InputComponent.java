package com.emergentorganization.cellrpg.components.input;

import com.emergentorganization.cellrpg.components.BaseComponent;
import com.emergentorganization.cellrpg.components.ComponentType;
import com.emergentorganization.cellrpg.components.MovementComponent;
import com.emergentorganization.cellrpg.components.WeaponComponent;

/**
 * A universal class to send input.
 *
 * Created by OrelBitton on 08/06/2015.
 */
public class InputComponent extends BaseComponent{

    protected MovementComponent mc;
    protected WeaponComponent wc;

    public void added(){
        mc = (MovementComponent) getFirstSiblingByType(ComponentType.MOVEMENT);

        if(hasWeapon()){
            wc = (WeaponComponent) getFirstSiblingByType(ComponentType.WEAPON);
        }
    }

    protected void moveTo(float x, float y){
        mc.setDest(x, y);
    }

    protected void shootTo(float x, float y){
        if(hasWeapon()){
            wc.shootTo(x, y);
        }
    }

    public boolean hasWeapon(){
        return false;
    }

}
