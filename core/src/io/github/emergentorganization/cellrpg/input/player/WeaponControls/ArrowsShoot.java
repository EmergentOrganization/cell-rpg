package io.github.emergentorganization.cellrpg.input.player.WeaponControls;

import com.artemis.ComponentMapper;
import com.artemis.Entity;
import com.artemis.World;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Input;
import com.badlogic.gdx.math.Vector2;
import com.kotcrab.vis.ui.widget.VisTable;
import com.kotcrab.vis.ui.widget.VisWindow;
import io.github.emergentorganization.cellrpg.core.components.Bounds;
import io.github.emergentorganization.cellrpg.core.components.InputComponent;
import io.github.emergentorganization.cellrpg.core.components.Position;
import io.github.emergentorganization.cellrpg.core.entityfactory.EntityFactory;
import io.github.emergentorganization.cellrpg.input.player.iPlayerCtrl;
import io.github.emergentorganization.cellrpg.managers.EventManager;

public class ArrowsShoot extends iPlayerCtrl {
    private final String NAME = "Directional Arrows To Shoot";

    private final EntityFactory entityFactory;
    private final EventManager eventManager;

    private long lastClick = 0;
    private final long clickInterval = 100;

    public ArrowsShoot(World world, EntityFactory entityFactory, ComponentMapper<InputComponent> im) {
        super(world, im);

        this.entityFactory = entityFactory;
        this.eventManager = world.getSystem(EventManager.class);
    }

    public String getName() {
        return NAME;
    }

    public void addInputConfigButtons(VisTable table, VisWindow menuWindow) {
        // config items for arrow controls? can't think of any...
    }

    @Override
    public void process(Entity player) {
        Vector2 target = player.getComponent(Position.class).getCenter(player.getComponent(Bounds.class), 0);
        boolean shooting = false;
        if (Gdx.input.isKeyPressed(Input.Keys.UP)) {
            target.add(0, 10);
            shooting = true;
        } else if (Gdx.input.isKeyPressed(Input.Keys.DOWN)) {
            target.add(0, -10);
            shooting = true;
        }
        if (Gdx.input.isKeyPressed(Input.Keys.LEFT)) {
            target.add(-10, 0);
            shooting = true;
        } else if (Gdx.input.isKeyPressed(Input.Keys.RIGHT)) {
            target.add(10, 0);
            shooting = true;
        }
        if (shooting && readyForNextClick()) {
            WeaponUtil.shootTo(
                    target,
                    player,
                    eventManager,
                    entityFactory
            );
            lastClick = System.currentTimeMillis();
        }
    }

    private boolean readyForNextClick(){
        return System.currentTimeMillis() - lastClick > clickInterval;
    }
}
