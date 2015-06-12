package com.emergentorganization.cellrpg.components.input.scripted.actions;

import com.emergentorganization.cellrpg.components.MovementComponent;
import com.emergentorganization.cellrpg.components.input.scripted.ScriptAction;
import com.emergentorganization.cellrpg.components.input.scripted.ScriptedInputComponent;
import com.badlogic.gdx.math.Vector2;

/**
 * Created by OrelBitton on 10/06/2015.
 */
public class MoveToAction extends ScriptAction {

    private MovementComponent mc;
    private float x, y;
    private boolean completed;

    public MoveToAction(float x, float y){
        this.x = x;
        this.y = y;
    }

    @Override
    public void init(ScriptedInputComponent input) {
        mc = input.getMovementComponent();
    }

    @Override
    public void start() {
        Vector2 pos = mc.getWorldPosition().add(x, y);
        mc.setDest(pos);

        completed = false;
    }

    @Override
    public void run() {
        completed = mc.getDest() == null;
    }

    @Override
    public boolean completed() {
        if (completed) {
            // this needs to be done inside of the movement component when it reaches destination
            // we are not currently doing it cause it makes the player movement not smooth
            mc.setVelocity(Vector2.Zero);
        }

        return completed;
    }

}