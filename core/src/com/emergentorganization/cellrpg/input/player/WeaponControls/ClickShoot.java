package com.emergentorganization.cellrpg.input.player.WeaponControls;

import com.artemis.ComponentMapper;
import com.artemis.Entity;
import com.artemis.World;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.Camera;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.math.Vector3;
import com.emergentorganization.cellrpg.components.Bounds;
import com.emergentorganization.cellrpg.components.InputComponent;
import com.emergentorganization.cellrpg.components.Position;
import com.emergentorganization.cellrpg.core.entityfactory.EntityFactory;
import com.emergentorganization.cellrpg.input.player.iPlayerCtrl;
import com.emergentorganization.cellrpg.input.player.inputUtil;
import com.emergentorganization.cellrpg.managers.EventManager;
import com.emergentorganization.cellrpg.systems.CameraSystem;
import com.kotcrab.vis.ui.widget.VisTable;
import com.kotcrab.vis.ui.widget.VisWindow;

/**
 * Created by brian on 11/7/15.
 */
public class ClickShoot extends iPlayerCtrl {
    private final String NAME = "Click to Shoot";
    private final EntityFactory entityFactory;
    private final float exclusionRadius = .5f;  // radius inside which clicks don't count
    private final Camera camera;
    private final EventManager eventManager;

    public ClickShoot(World world, EntityFactory entityFactory, ComponentMapper<InputComponent> im) {
        super(world, im);
        this.entityFactory = entityFactory;
        this.camera = world.getSystem(CameraSystem.class).getGameCamera();
        this.eventManager = world.getSystem(EventManager.class);
    }

    @Override
    public String getName(){
        return NAME;
    }

    public void addInputConfigButtons(VisTable table, VisWindow menuWindow){
        // TODO: exclusionRadius
    }

    @Override
    public void process(Entity player) {
        if (Gdx.input.justTouched()) { // LMB or RMB?
            Vector2 mouse = inputUtil.getMousePos(camera);
            Vector2 playerPos = player.getComponent(Position.class).getCenter(player.getComponent(Bounds.class));
            if (isOutsideExclusionRadius(mouse, playerPos)) {
                WeaponUtil.shootTo(mouse, player, eventManager, entityFactory);
            }
        }
    }

    private boolean isOutsideExclusionRadius(Vector2 pos, Vector2 center){
        return pos.dst(center) > exclusionRadius;
    }
}
