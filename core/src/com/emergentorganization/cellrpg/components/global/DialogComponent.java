package com.emergentorganization.cellrpg.components.global;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.scenes.scene2d.InputEvent;
import com.badlogic.gdx.scenes.scene2d.Stage;
import com.badlogic.gdx.scenes.scene2d.ui.Dialog;
import com.badlogic.gdx.scenes.scene2d.ui.Label;
import com.badlogic.gdx.scenes.scene2d.utils.ClickListener;
import com.badlogic.gdx.utils.Align;
import com.badlogic.gdx.utils.TimeUtils;
import com.emergentorganization.cellrpg.components.GlobalComponent;
import com.emergentorganization.cellrpg.story.dialogue.DialogueSequenceInterface;
import com.kotcrab.vis.ui.VisUI;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

/**
 * Created by OrelBitton on 2015-07-11.
 */
public class DialogComponent extends GlobalComponent{
    private final Logger logger = LogManager.getLogger(getClass());

    private final float dialogWidth = Gdx.graphics.getWidth();
    private final float dialogHeight = Gdx.graphics.getHeight() * 0.25f;

    private Stage stage;
    private Dialog dialog;
    private Label label;
    private DialogueSequenceInterface dialogueSequence;
    private boolean optionsPresented = false;  // true when an option must be selected, and not just dialog shown.

    private long typewriterDelay = 100;
    private String typewriterText = "";

    private long timer;

    public DialogComponent loadDialogueSequence(DialogueSequenceInterface sequence){
        // load given dialogueSequence implementing class for use.
        dialogueSequence = sequence;
        return this;
    }

    public void init(){
        // initializes dialogue sequence
        setEnabled(true);
        dialogueSequence.init();
        addText(dialogueSequence.enter());
    }

    private void selectOption(int option){
        // select given option, or simply advance the story
        addText(dialogueSequence.enter(option));
    }

    private void dialogClicked(){
        logger.trace("dialog has been clicked");
        if (optionsPresented){
            return;
        } else {
            selectOption(0);
        }
    }

    @Override
    public void added() {
        stage = getScene().getUiStage();

        label = new Label("", VisUI.getSkin());

        dialog = new Dialog("", VisUI.getSkin());
        dialog.text(label);
        dialog.setMovable(false);
        dialog.setVisible(false);
        dialog.setBounds(0, 0, dialogWidth, dialogHeight);
        dialog.addListener(new ClickListener() {
            @Override
            public void clicked(InputEvent event, float x, float y) {
                logger.trace("dialog click@" + x + ',' + y);
                super.clicked(event, x, y);
                dialogClicked();
            }
        });

        // align topLeft TODO: this isn't working...
        dialog.align(Align.topLeft);
        label.setAlignment(Align.topLeft);

        stage.addActor(dialog);
    }

    @Override
    public void update(float deltaTime){
        if(!isEnabled())
            return;
        handleTypewriter();
    }

    private void handleTypewriter(){

        if(typewriterText.isEmpty()) {
            return;
        }

        if (timer == 0L)
            timer = TimeUtils.millis();

        if (TimeUtils.timeSinceMillis(timer) >= typewriterDelay) {
            timer += typewriterDelay;

            char c = typewriterText.charAt(0);
            typewriterText = typewriterText.substring(1);

            label.setText(getText() + c);
        }
    }

    private void setName(String name){
        dialog.setName(name);
    }

    private void finishDialogSection(){
        label.setText("...");
        setEnabled(false);
    }

    private void finishText(){
        label.setText(getText() + typewriterText);
        typewriterText = "";
    }

    private void addText(String text) {
        // adds text to dialogue, if text==null, dialog component disables
        if (text == null){
            finishDialogSection();
        } else {
            finishText();
            typewriterText = '\n' + text;
        }
    }

    private String getText(){
        return label.getText().toString();
    }

    private void setTypewriterText(String text){
        typewriterText = text;
    }

    @Override
    public void onToggle(boolean enabled) {
        dialog.setVisible(enabled);
    }
}
