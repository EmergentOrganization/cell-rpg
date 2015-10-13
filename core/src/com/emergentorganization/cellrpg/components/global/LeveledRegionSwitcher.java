package com.emergentorganization.cellrpg.components.global;

import com.badlogic.gdx.scenes.scene2d.InputEvent;
import com.badlogic.gdx.scenes.scene2d.Stage;
import com.badlogic.gdx.scenes.scene2d.ui.Dialog;
import com.badlogic.gdx.scenes.scene2d.ui.Label;
import com.badlogic.gdx.scenes.scene2d.utils.ClickListener;
import com.badlogic.gdx.utils.Align;
import com.emergentorganization.cellrpg.components.GlobalComponent;
import com.emergentorganization.cellrpg.scenes.regions.Region;
import com.kotcrab.vis.ui.VisUI;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.util.List;

/**
 * Leveled region switcher can be used at the Scene level to control switching of regions.
 * Scene initializes the switcher with region sequence to be used,
 * and triggers region switches by calling nextRegion().
 * Switcher will handle loading & prep of new region,
 * move player into the region and dispose of previous region.
 * Intended to enable easy creation of level-based gameflow.
 * Switcher also (optionally) displays level #.
 *
 * Created by 7yl4r on 2015-10-12.
 */
public class LeveledRegionSwitcher extends GlobalComponent{
    private final Logger logger = LogManager.getLogger(getClass());

    // UI elements for (briefly) displaying level # (and maybe other info) (TODO)
    private Stage stage;
    private Label label;
    private Dialog dialog;

    private List<Class> regions;
    private int currentRegionIndex = 0;
    private Region currentRegion;
    private Region nextRegion;

    public LeveledRegionSwitcher(List<Class> _regions){
        regions = _regions;
        loadRegion(0);
        nextRegion();
    }

    public boolean nextRegion(){
        // transition to next region in list.
        // returns true if next region is the final region
        currentRegionIndex += 1;
        enterNextRegion();
        loadRegion(currentRegionIndex);
        if (noMoreRegions()){
            return true;
        } else {
            return false;
        }
    }

    public int getRemainingRegions(){
        // returns the number of regions remaining, excluding current region
        return regions.size() - (currentRegionIndex + 1);
    }

    public boolean noMoreRegions(){
        // returns true if this is the last region
        return getRemainingRegions() == 0;
    }

    private void loadRegion(int index){
        // loads given region into nextRegion
        if (noMoreRegions()) {
            try {
                nextRegion = (Region) regions.get(index).newInstance();
            } catch (IllegalAccessException | InstantiationException ex) {
                logger.error("region-list badness in regionList:" + regions + "\n\nERR: " + ex.getMessage());
                // TODO: return to main menu or something?
            }
        } else {
            logger.warn("cannot load next region; current region is last");
        }
    }

    private void enterNextRegion(){
        // makes nextRegion currentRegion and disposes of old region
        if (nextRegion == null){
            throw new IllegalStateException("cannot enter next region, nextRegion==null");
        }
        // currentRegion.dispose() ???
        currentRegion = nextRegion;
        // nextRegion.init() ???
    }

    @Override
    public void added() {
        stage = getScene().getUiStage();

        // TODO: use different skin to make UI background transparent & stylize text:
        label = new Label("", VisUI.getSkin());
        dialog = new Dialog("", VisUI.getSkin());
        dialog.text(label);
        dialog.setMovable(false);
        dialog.setVisible(false);
        // TODO: set these bounds to something reasonable
        //dialog.setBounds(0, 0, dialogWidth, dialogHeight);
        dialog.addListener(new ClickListener() {
            @Override
            public void clicked(InputEvent event, float x, float y) {
                logger.trace("level display click@" + x + ',' + y);
                super.clicked(event, x, y);
                // close dialog immediately
                setEnabled(false);
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
        // TODO: fade out dialog after certain time
    }

    @Override
    public void onToggle(boolean enabled) {
        dialog.setVisible(enabled);
    }
}
