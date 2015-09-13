package com.emergentorganization.cellrpg.entities.characters.npcs;

import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.math.Vector2;
import com.emergentorganization.cellrpg.components.entity.MovementComponent;
import com.emergentorganization.cellrpg.components.entity.PhysicsComponent;
import com.emergentorganization.cellrpg.entities.characters.Character;
import com.emergentorganization.cellrpg.physics.CellUserData;
import com.emergentorganization.cellrpg.physics.Tag;
import com.emergentorganization.cellrpg.scenes.Scene;
import com.emergentorganization.cellrpg.tools.physics.BodyLoader;

/**
 * Created by OrelBitton on 10/06/2015.
 */
public class CharCiv1Blinker extends Character {
    private static final String ID = "char-civ1-blinker";

    /*
    This constructor is needed for MapEditor. Do not remove.
     */
    public CharCiv1Blinker() {
        super(ID + ".png", 2, 1, 1.2f);

        /*
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

        addComponent(input);*/
    }

    @Override
    public void added() {
        super.added();

        final TextureRegion currentFrame = getGraphicsComponent().getCurrentFrame();
        float scale = currentFrame.getTexture().getWidth() * getFirstComponentByType(MovementComponent.class).getScale().x;
        PhysicsComponent phys = new PhysicsComponent(getScene().getWorld(),
                BodyLoader.fetch().generateBody(ID, scale), Tag.CHAR_CIV1_BLINKER);
        phys.setUserData(new CellUserData(this, Tag.CHAR_CIV1_BLINKER));
        addComponent(phys);
    }

    public CharCiv1Blinker(Texture texture, Vector2 position) {
        super(texture, 2, 1, 1.2f);

        getFirstComponentByType(MovementComponent.class).setWorldPosition(position);

        // TODO: Add scripted input component with position offset
    }
}
