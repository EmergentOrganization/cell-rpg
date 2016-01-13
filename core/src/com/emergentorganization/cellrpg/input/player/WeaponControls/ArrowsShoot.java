package com.emergentorganization.cellrpg.input.player.WeaponControls;

import com.artemis.ComponentMapper;
import com.artemis.Entity;
import com.artemis.World;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Input;
import com.badlogic.gdx.math.Vector2;
import com.emergentorganization.cellrpg.components.Bounds;
import com.emergentorganization.cellrpg.components.InputComponent;
import com.emergentorganization.cellrpg.components.Position;
import com.emergentorganization.cellrpg.core.entityfactory.EntityFactory;
import com.emergentorganization.cellrpg.input.player.iPlayerCtrl;
import com.emergentorganization.cellrpg.managers.EventManager;
import com.kotcrab.vis.ui.widget.VisTable;
import com.kotcrab.vis.ui.widget.VisWindow;

public class ArrowsShoot extends iPlayerCtrl {
    private final String NAME = "Directional Arrows To Shoot";

    private final EntityFactory entityFactory;
    private final EventManager eventManager;

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
        Vector2 target = player.getComponent(Position.class).getCenter(player.getComponent(Bounds.class));
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
        if (shooting) {
            WeaponUtil.shootTo(
                    target,
                    player,
                    eventManager,
                    entityFactory
            );
        }
    }
}
