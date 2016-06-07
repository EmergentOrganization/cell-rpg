package io.github.emergentorganization.cellrpg.scenes.game.HUD;

import com.artemis.World;
import com.kotcrab.vis.ui.widget.VisLabel;
import com.kotcrab.vis.ui.widget.VisTable;

/**
 * display element in the debugDisplay.
 */
abstract class DebugElement {
    private final VisLabel elementLabel;
    private final String suffix;
    private final String prefix;

    public DebugElement(VisTable visTable) {
        this(visTable, "", "");
    }

    public DebugElement(VisTable visTable, String prefix, String suffix) {
        this.prefix = prefix;
        this.suffix = suffix;
        elementLabel = new VisLabel(prefix + "---" + suffix);
        visTable.add(elementLabel).row();
    }

    public abstract String getText(World world);
    // returns text to display (not including prefix & suffix)


    public void update(World world) {
        elementLabel.setText(prefix + getText(world) + suffix);
    }

}
