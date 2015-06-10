package com.emergentorganization.cellrpg.entities.characters;

import com.badlogic.gdx.Gdx;
import com.emergentorganization.cellrpg.components.MovementComponent;
import com.emergentorganization.cellrpg.components.input.scripted.Script;
import com.emergentorganization.cellrpg.components.input.scripted.ScriptAction;
import com.emergentorganization.cellrpg.components.input.scripted.ScriptedInputComponent;
import com.emergentorganization.cellrpg.components.input.scripted.actions.MoveToAction;
import com.emergentorganization.cellrpg.components.input.scripted.actions.WaitAction;

/**
 * Created by OrelBitton on 10/06/2015.
 */
public class NPC extends Character{

    public NPC() {
        super("char-civ1-blinker.png", 2, 1, 1.2f);

        getMovementComponent().setSpeed(50);

        ScriptedInputComponent input = new ScriptedInputComponent();

        ScriptAction[] walkAndReturn = new ScriptAction[]{
                new MoveToAction(50, 100),
                new WaitAction(1),
                new MoveToAction(-50, -100),
                new WaitAction(1)
        };

        input.registerScript("walkAndReturn", new Script(walkAndReturn));
        input.playScript("walkAndReturn");

        addComponent(input);
    }
}
