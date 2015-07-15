package com.emergentorganization.cellrpg.components.global;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.scenes.scene2d.Stage;
import com.badlogic.gdx.scenes.scene2d.ui.Dialog;
import com.badlogic.gdx.scenes.scene2d.ui.Label;
import com.badlogic.gdx.utils.TimeUtils;
import com.emergentorganization.cellrpg.components.GlobalComponent;
import com.kotcrab.vis.ui.VisUI;

/**
 * Created by OrelBitton on 11/07/2015.
 */
public class DialogComponent extends GlobalComponent{

    private final float dialogWidth = Gdx.graphics.getWidth();
    private final float dialogHeight = Gdx.graphics.getHeight() * 0.35f;

    private Stage stage;
    private Dialog dialog;
    private Label label;

    private long typewriterDelay = 0;
    private String typewriterText = "";

    private long timer;

    @Override
    public void added() {
        stage = getScene().getUiStage();

        label = new Label("", VisUI.getSkin());

        dialog = new Dialog("", VisUI.getSkin());
        dialog.text(label);
        dialog.setMovable(false);
        dialog.setVisible(false);
        dialog.setBounds(0, 0, dialogWidth, dialogHeight);

        stage.addActor(dialog);

        setTypewriterText("Hello, this is a test message...", 0.1f);
    }

    @Override
    public void update(float deltaTime){
        if(!isEnabled())
            return;
        handleTypewriter();
    }

    private void handleTypewriter(){
        if(typewriterDelay == 0)
            return;

        if(typewriterText.isEmpty()) {
            typewriterDelay = 0;
            return;
        }

        if (timer == 0L)
            timer = TimeUtils.millis();

        if (TimeUtils.timeSinceMillis(timer) >= typewriterDelay) {
            timer += typewriterDelay;

            char c = typewriterText.charAt(0);
            typewriterText = typewriterText.substring(1);

            setText(getText() + c);
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

    public void setTypewriterText(String text, float delaySeconds){
        typewriterText = text;
        typewriterDelay = (long) (delaySeconds * 1000);

        setText("");
    }

    @Override
    public void onToggle(boolean enabled) {
        dialog.setVisible(enabled);
    }
}
