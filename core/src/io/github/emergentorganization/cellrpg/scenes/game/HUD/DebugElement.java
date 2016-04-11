package io.github.emergentorganization.cellrpg.scenes.game.HUD;

import com.artemis.World;
import com.kotcrab.vis.ui.widget.VisLabel;
import com.kotcrab.vis.ui.widget.VisTable;
import io.github.emergentorganization.cellrpg.systems.TimingSystem;

/**
 * display element in the debugDisplay.
 */
public abstract class DebugElement {
    VisLabel elementLabel;
    String suffix;
    String prefix;

    public DebugElement(VisTable visTable){
        this(visTable, "", "");
    }

    public DebugElement(VisTable visTable, String prefix, String suffix){
        this.prefix = prefix;
        this.suffix = suffix;
        elementLabel = new VisLabel(prefix + "---" + suffix);
        visTable.add(elementLabel).row();
    }

    public abstract String getText(World world);
        // returns text to display (not including prefix & suffix)


    public void update(World world){
        elementLabel.setText(prefix + getText(world) + suffix);
    }

}
