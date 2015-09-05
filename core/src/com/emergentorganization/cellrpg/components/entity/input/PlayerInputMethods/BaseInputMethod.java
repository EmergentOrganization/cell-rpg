package com.emergentorganization.cellrpg.components.entity.input.PlayerInputMethods;

import com.badlogic.gdx.graphics.Camera;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.glutils.ShapeRenderer;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.math.Vector3;
import com.emergentorganization.cellrpg.components.entity.MovementComponent;
import com.emergentorganization.cellrpg.components.entity.input.PlayerInputComponent;
import com.kotcrab.vis.ui.widget.VisTable;
import com.kotcrab.vis.ui.widget.VisWindow;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

/**
 * Base class for creating player input methods.
 * Created by 7yl4r on 9/4/2015.
 */
public class BaseInputMethod {

    private final Logger logger = LogManager.getLogger(getClass());

    protected PlayerInputComponent parentInputComp;
    protected Vector3 mouse; // The mouse coordinates for the current frame (if mouse is pressed)

    public BaseInputMethod(PlayerInputComponent parent) {
        parentInputComp = parent;
    }

    public String getName(){
        // returns human-readable name string
        return "base input component";
    }

    public void addInputConfigButtons(VisTable menuTable, final VisWindow menuWindow){
        // code to add config buttons specific to the inputMethod

        /* telepathic (heh)
        VisTextButton teleTest = new VisTextButton("test telepathic control");
                menuTable.add(teleTest).pad(0f, 0f, 5f, 0f).fill(true, false).row();
                final VisLabel testResult = new VisLabel("...");
                menuTable.add(testResult).pad(0f, 0f, 5f, 0f).fill(true, false).row();
                teleTest.addListener(new ClickListener() {
                    @Override
                    public void clicked(InputEvent event, float x, float y) {
                        super.clicked(event, x, y);
                        testResult.setText("RPGScene failed: no telepathic abilities detected.");
                        menuWindow.pack();
                    }
                });
                break;
         */
    }

    public void inputComponentUpdate(float deltaTime, Camera camera, Vector3 mousePos, MovementComponent mc) {

    }

    public void inputComponentDebugRender(ShapeRenderer renderer, MovementComponent mc) {

    }

    public void skipDest(MovementComponent mc) {

    }

}
