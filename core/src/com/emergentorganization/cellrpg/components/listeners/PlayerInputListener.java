package com.emergentorganization.cellrpg.components.listeners;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.math.Vector2;
import com.emergentorganization.cellrpg.components.BaseComponent;
import com.emergentorganization.cellrpg.components.MovementComponent;
import com.emergentorganization.cellrpg.components.messages.BaseComponentMessage;
import com.emergentorganization.cellrpg.components.messages.MoveToMessage;

/**
 * Created by OrelBitton on 05/06/2015.
 */
public class PlayerInputListener implements BaseComponentListener {

    // this will only get called if the message is type of MoveToMessage
    @Override
    public void run(BaseComponent comp, BaseComponentMessage msg) {
        MoveToMessage moveTo = (MoveToMessage) msg;
        MovementComponent mc = (MovementComponent) comp;

        Vector2 pos = mc.getLocalPosition();

        Vector2 dest = moveTo.destination;
        dest.sub(pos).nor().scl(mc.getVelocity());

        pos.add(dest.scl(Gdx.graphics.getDeltaTime()));

        mc.setWorldPosition(pos);
    }

    public boolean validate(BaseComponentMessage msg){
        return msg instanceof MoveToMessage;
    }

}
