package com.emergentorganization.cellrpg.components;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.scenes.scene2d.Stage;
import com.badlogic.gdx.scenes.scene2d.ui.Dialog;
import com.badlogic.gdx.scenes.scene2d.ui.Label;
import com.badlogic.gdx.utils.*;
import com.badlogic.gdx.utils.StringBuilder;
import com.kotcrab.vis.ui.VisUI;


/**
 * Created by OrelBitton on 14/06/2015.
 */
public class DialogComponent extends BaseComponent {

    private final float dialogWidth = Gdx.graphics.getWidth();
    private final float dialogHeight = Gdx.graphics.getHeight() * 0.35f;

    private Stage stage;
    private Dialog dialog;
    private Label label;

    private float typewriterDelay = 0;
    private String typewriterText = "";

    private long timer;

    @Override
    public void added() {
        stage = getEntity().getScene().getUiStage();

        label = new Label("", VisUI.getSkin());

        dialog = new Dialog("", VisUI.getSkin());
        dialog.text(label);
        dialog.setBounds(0, 0, dialogWidth, dialogHeight);

        stage.addActor(dialog);

        setTypewriterText("Hello", 2000);
    }

    @Override
    public void update(float deltaTime){
        if(timer == 0L) {
            System.out.println("initialized first");
            timer = TimeUtils.millis();
        }

        long dif = TimeUtils.timeSinceMillis(timer);

        if (dif >= typewriterDelay){
            timer += typewriterDelay;

        }
    }

    public void setName(String name){
        dialog.setName(name);
    }

    public void setText(String text) {
        label.setText(text);
    }

    public String getText(){
        return label.getText().toString();
    }

    public void setTypewriterText(String text, float delay){
        typewriterText = text;
        typewriterDelay = delay;

        setText("");
    }
}
