package com.emergentorganization.cellrpg.entities.characters.npcs;


import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.math.Vector2;
import com.emergentorganization.cellrpg.components.PhysicsComponent;
import com.emergentorganization.cellrpg.components.input.scripted.Script;
import com.emergentorganization.cellrpg.components.input.scripted.ScriptAction;
import com.emergentorganization.cellrpg.components.input.scripted.ScriptedInputComponent;
import com.emergentorganization.cellrpg.components.input.scripted.actions.MoveToAction;
import com.emergentorganization.cellrpg.components.input.scripted.actions.WaitAction;
import com.emergentorganization.cellrpg.entities.characters.Character;
import com.emergentorganization.cellrpg.physics.CellUserData;
import com.emergentorganization.cellrpg.physics.Tag;
import com.emergentorganization.cellrpg.tools.physics.BodyLoader;

/**
 * Created by OrelBitton on 10/06/2015.
 */
public class CharCiv1Blinker extends Character {
    private static final String ID = "char-civ1-blinker";

    public CharCiv1Blinker() {
        super(ID + ".png", 2, 1, 1.2f);

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

    @Override
    public void added() {
        super.added();

        final TextureRegion currentFrame = getGraphicsComponent().getCurrentFrame();
        int scale = Math.max(currentFrame.getTexture().getWidth(), currentFrame.getTexture().getHeight());
        PhysicsComponent phys = new PhysicsComponent(getScene().getWorld(),
                BodyLoader.fetch().generateBody(ID, scale), Tag.CHAR_CIV1_BLINKER);
        phys.setUserData(new CellUserData(getMovementComponent(), Tag.CHAR_CIV1_BLINKER));
        addComponent(phys);
    }

    public CharCiv1Blinker(Texture texture, Vector2 position) {
        super(texture, 2, 1, 1.2f);

        getMovementComponent().setWorldPosition(position);

        // TODO: Add scripted input component with position offset
    }
}
