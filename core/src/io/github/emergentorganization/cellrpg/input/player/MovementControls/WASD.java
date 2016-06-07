package io.github.emergentorganization.cellrpg.input.player.MovementControls;


import com.artemis.ComponentMapper;
import com.artemis.Entity;
import com.artemis.World;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Input;
import com.badlogic.gdx.math.Vector2;
import com.kotcrab.vis.ui.widget.VisTable;
import com.kotcrab.vis.ui.widget.VisWindow;
import io.github.emergentorganization.cellrpg.core.components.InputComponent;
import io.github.emergentorganization.cellrpg.input.player.iPlayerCtrl;


public class WASD extends iPlayerCtrl {
    private final String NAME = "WASD Movement";

    public WASD(World world, ComponentMapper<InputComponent> im) {
        super(world, im);
    }

    @Override
    public String getName() {
        return NAME;
    }

    public void addInputConfigButtons(VisTable table, VisWindow menuWindow) {
        // config items for wasd movement controls? can't think of any...
    }

    @Override
    public void process(Entity player) {
        InputComponent input = player.getComponent(InputComponent.class);
        if (input != null) {
            Vector2 dir = input.direction.setZero();
            if (Gdx.input.isKeyPressed(Input.Keys.W)) {
                dir.add(0, 1f);
            } else if (Gdx.input.isKeyPressed(Input.Keys.S)) {
                dir.add(0, -1f);
            }
            if (Gdx.input.isKeyPressed(Input.Keys.A)) {
                dir.add(-1f, 0);
            } else if (Gdx.input.isKeyPressed(Input.Keys.D)) {
                dir.add(1f, 0);
            }
            input.direction.set(dir);
        }
    }
}
