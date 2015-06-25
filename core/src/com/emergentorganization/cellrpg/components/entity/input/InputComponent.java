package com.emergentorganization.cellrpg.components.entity.input;

import com.emergentorganization.cellrpg.components.EntityComponent;
import com.emergentorganization.cellrpg.components.entity.ComponentType;
import com.emergentorganization.cellrpg.components.entity.MovementComponent;
import com.emergentorganization.cellrpg.components.entity.WeaponComponent;

/**
 * A universal class to send input.
 *
 * Created by OrelBitton on 08/06/2015.
 */
public class InputComponent extends EntityComponent {

    protected MovementComponent mc;
    protected WeaponComponent wc;

    public void added(){
        mc = (MovementComponent) getFirstSiblingByType(ComponentType.MOVEMENT);

        if(hasWeapon()){
            wc = (WeaponComponent) getFirstSiblingByType(ComponentType.WEAPON);
        }
    }

    /*protected void moveTo(float x, float y){
        //mc.setDest(x, y); TODO: Removed destination triggers in movement component to preserve composition. re-implement
    }*/

    protected void shootTo(float x, float y){
        if(hasWeapon()){
            wc.shootTo(x, y);
        }
    }

    public boolean hasWeapon(){
        return false;
    }

    public MovementComponent getMovementComponent(){
        return mc;
    }

    public WeaponComponent getWeaponComponent(){
        return wc;
    }

}
