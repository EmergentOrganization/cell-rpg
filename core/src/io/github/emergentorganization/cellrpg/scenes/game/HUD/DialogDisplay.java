package io.github.emergentorganization.cellrpg.scenes.game.HUD;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.scenes.scene2d.InputEvent;
import com.badlogic.gdx.scenes.scene2d.Stage;
import com.badlogic.gdx.scenes.scene2d.utils.ClickListener;
import com.badlogic.gdx.utils.Align;
import com.badlogic.gdx.utils.TimeUtils;
import com.kotcrab.vis.ui.widget.VisLabel;
import com.kotcrab.vis.ui.widget.VisWindow;
import io.github.emergentorganization.cellrpg.scenes.game.dialogue.DialogueSequenceInterface;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

public class DialogDisplay {
    private final Logger logger = LogManager.getLogger(getClass());

    private final VisWindow dialog;
    private final VisLabel label;
    private DialogueSequenceInterface dialogueSequence;
    private String typewriterText = "";
    private boolean noMoreText = true;

    private long timer;

    public DialogDisplay(Stage _stage) {
        label = new VisLabel("");
        label.setAlignment(Align.topLeft);

        dialog = new VisWindow("", false);
        dialog.setMovable(false);
        dialog.setVisible(false);
        float dialogHeight = Gdx.graphics.getHeight() * 0.25f;
        float dialogWidth = Gdx.graphics.getWidth();
        dialog.setBounds(0, 0, dialogWidth, dialogHeight);
        dialog.add(label).expand().fill().align(Align.topLeft);
        dialog.addListener(new ClickListener() {
            @Override
            public void clicked(InputEvent event, float x, float y) {
                logger.trace("dialog click@" + x + ',' + y);
                super.clicked(event, x, y);
                dialogClicked();
            }
        });

        _stage.addActor(dialog);
    }

    /**
     * Loads a dialog sequence to be handled and displayed
     *
     * @param sequence      A dialog sequence to load
     * @param textAlignment Aligns the text to an anchor. Use {@link Align}
     * @return Itself for chaining
     */
    public DialogDisplay loadDialogueSequence(DialogueSequenceInterface sequence, int textAlignment) {
        // load given dialogueSequence implementing class for use.
        dialogueSequence = sequence;
        // initializes dialogue sequence
        dialog.setVisible(true);
        label.setAlignment(textAlignment);
        dialogueSequence.init();
        addText(dialogueSequence.enter());
        noMoreText = false;
        return this;
    }

    private void selectOption(int option) {
        // select given option, or simply advance the story
        addText(dialogueSequence.enter(option));
    }

    private void dialogClicked() {
        logger.trace("dialog has been clicked");
        boolean optionsPresented = false;
        if (optionsPresented) {
            return;
        } else {
            selectOption(0);
        }
    }

    public void update(float deltaTime) {
        if (!dialog.isVisible())
            return;
        handleTypewriter();
    }

    private void handleTypewriter() {

        if (typewriterText.isEmpty()) {
            return;
        }

        if (timer == 0L)
            timer = TimeUtils.millis();

        long typewriterDelay = 100;
        if (TimeUtils.timeSinceMillis(timer) >= typewriterDelay) {
            timer += typewriterDelay;

            char c = typewriterText.charAt(0);
            typewriterText = typewriterText.substring(1);
            label.setText(getText() + c);
        }
    }

    private void finishDialogSection() {
        label.setText("");
        dialog.setVisible(false);
    }

    private void finishText() {
        label.setText(getText() + typewriterText);
        typewriterText = "";
    }

    private void addText(String text) {
        // adds text to dialogue, if text==null, dialog component disables
        logger.debug("adding dialog text: " + text);
        if (text == null) {
            if (noMoreText) {
                finishDialogSection();
            } else {
                noMoreText = true;
                finishText();
            }
        } else {
            finishText();
            typewriterText = '\n' + text;
        }
    }

    private String getText() {
        return label.getText().toString();
    }
}
