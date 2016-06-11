package io.github.emergentorganization.cellrpg.scenes.menu;

import com.artemis.World;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Preferences;
import com.badlogic.gdx.scenes.scene2d.InputEvent;
import com.badlogic.gdx.scenes.scene2d.Stage;
import com.badlogic.gdx.scenes.scene2d.ui.Label;
import com.badlogic.gdx.scenes.scene2d.ui.Skin;
import com.badlogic.gdx.scenes.scene2d.ui.TextField;
import com.badlogic.gdx.scenes.scene2d.utils.ClickListener;
import com.badlogic.gdx.utils.Align;
import com.kotcrab.vis.ui.widget.VisTable;
import com.kotcrab.vis.ui.widget.VisTextButton;
import com.kotcrab.vis.ui.widget.VisWindow;
import io.github.emergentorganization.cellrpg.PixelonTransmission;
import io.github.emergentorganization.cellrpg.scenes.Scene;
import io.github.emergentorganization.cellrpg.scenes.game.menu.pause.SettingsMenu;
import io.github.emergentorganization.cellrpg.tools.GameSettings;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;


class FirstStartWindow extends VisWindow {
    private final VisTable table;
    private final Logger logger = LogManager.getLogger(getClass());
    private final SettingsMenu settingsMenu;
    private final PixelonTransmission pt;
    private final TextField nameField;
    private final Skin skin;


    public FirstStartWindow(final Stage stage, World world, PixelonTransmission pt) {
        super("", false);
        this.pt = pt;

        logger.debug("enter initial firstStart window");

        table = new VisTable();
        this.setFillParent(true);
        this.centerWindow();
        this.add(table);
        this.clearListeners();

        skin = pt.getUISkin();

        Label title = new Label("Incoming Transmission:", skin, "header");
        table.add(title).row();

        Label initText = new Label(
                "We need your help.\n" +
                        "\n" +
                        "We are broadcasting from a world very different from yours. Your 3-dimensional universe is" +
                        " beyond our comprehension, for we live in a 2-dimensional planiverse." +
                        " Our planiverse is being consumed by Vyroids, and we are powerless to stop them." +
                        " In our desperation we have searched the multiverse for someone who could help, and we" +
                        " believe that someone is you." +
                        " Your power to look upon our world from a 3rd dimension will allow you to understand how" +
                        " the vyroids grow and stop them before it is too late." +
                        " \n\n" +
                        "We have built a bridge between our planiverses. Through this program you can connect to the" +
                        " Planiverse Bridge Orb and control it. If you accept our mission, please send the following" +
                        " information so that we may configure your Planiverse Bridge Orb.",
                skin
        );
        initText.setWrap(true);
        initText.setWidth(100);
        table.add(initText).width(Gdx.graphics.getWidth() * 2f / 3f).row();

        Label nameText = new Label("What should I call you?", skin);
        table.add(nameText).row();

        nameField = new TextField("interdimensional traveller", skin);
        table.add(nameField).width(Gdx.graphics.getWidth() / 3f).row();

        settingsMenu = new SettingsMenu(table, stage, "additional settings", world);

        VisTextButton exit = new VisTextButton("Send Configuration Details To Initiate Bridge");
        exit.addListener(new ClickListener() {
            @Override
            public void clicked(InputEvent event, float x, float y) {
                if (formIsValid()) {
                    submitForm();
                } else {
                    showFormErrors();
                }
            }
        });
        table.add(exit).pad(0f, 0f, 5f, 0f).fill(true, false).row();

        table.align(Align.center);
        table.pack();
        this.pack();

        this.setPosition((stage.getWidth() / 2f) - (this.getWidth() / 2f),
                (stage.getHeight() / 2f) - (this.getHeight() / 2f));
        stage.addActor(this.fadeIn());
    }

    private void submitForm() {
        Preferences prefs = GameSettings.getPreferences();
        prefs.putString(GameSettings.KEY_USER_NAME, nameField.getText());
        prefs.putBoolean(GameSettings.KEY_FIRST_START, false);
        prefs.flush();

        fadeOut();
        pt.setScene(Scene.MAIN_MENU);
        pt.mixpanel.startupEvent();
    }

    private void showFormErrors() {
        // TODO
        Label errorText = new Label("Data Validation Fail", skin);
        table.add(errorText);
    }

    private boolean formIsValid() {
        return isValidtext(nameField.getText())
                // && otherTestExpression
                ;
    }

    private boolean isValidtext(String text) {
        // returns true if give text is acceptable
        return text != null
                && text.length() > 2
                && text.length() < 20;
    }

    @Override
    public void fadeOut() {
        logger.info("closing pause window");
        super.fadeOut();
        if (settingsMenu != null)
            settingsMenu.closeSubmenu();
    }
}
