package com.emergentorganization.cellrpg.components.entity.input;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.Camera;
import com.badlogic.gdx.graphics.glutils.ShapeRenderer;
import com.badlogic.gdx.math.Vector3;
import com.emergentorganization.cellrpg.components.entity.input.PlayerInputMethods.BaseInputMethod;
import com.emergentorganization.cellrpg.components.entity.input.PlayerInputMethods.DirectFollowAndPathInputMethod;
import com.emergentorganization.cellrpg.components.entity.input.PlayerInputMethods.PathInputMethod;
import com.kotcrab.vis.ui.widget.VisTable;
import com.kotcrab.vis.ui.widget.VisWindow;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

/**
 * Created by OrelBitton on 04/06/2015.
 */
public class PlayerInputComponent extends InputComponent {

    private final Logger logger = LogManager.getLogger(getClass());

    protected Camera camera; // Used to unproject screen coordinates for the mouse

    public int currentInputMethodIndex = 0;     // base control type
    private BaseInputMethod[] inputChoices;

    public PlayerInputComponent(Camera camera) {
        this.camera = camera;
        inputChoices = new BaseInputMethod[] {
                new PathInputMethod(this),
                new DirectFollowAndPathInputMethod(this)  // TODO: add more control methods here
        };
    }

    public void addInputConfigButtons(VisTable menuTable, VisWindow menuWindow){
        getCurrentInputMethod().addInputConfigButtons(menuTable, menuWindow);
    }

    public void setInputMethod(int newMethodIndex){
        // sets base input using index (indicies should be same as listed by getInputTypeChoices
        currentInputMethodIndex = newMethodIndex;
        logger.info("input method set to " + inputChoices[newMethodIndex].getName());
    }

    public BaseInputMethod getCurrentInputMethod(){
        return inputChoices[currentInputMethodIndex];
    }

    public String[] getInputTypeChoices(){
        String nameList[] = new String[inputChoices.length];
        for (int i = 0; i < inputChoices.length; i++){
            nameList[i] = inputChoices[i].getName();
        }
        return nameList;
    }

    @Override
    public void update(float deltaTime) {
        Vector3 mousePos = getEntity().getScene().getGameCamera().unproject(new Vector3(Gdx.input.getX(), Gdx.input.getY(), 0f));
        getCurrentInputMethod().inputComponentUpdate(deltaTime, camera, mousePos, mc);
    }

    @Override
    public void debugRender(ShapeRenderer renderer) {
        getCurrentInputMethod().inputComponentDebugRender(renderer, mc);
    }

    @Override
    public boolean shouldDebugRender() {
        return true;
    }

    @Override
    public boolean hasWeapon() {
        return true;
    }

    public void skipDest() {
        getCurrentInputMethod().skipDest(mc);
    }
}
