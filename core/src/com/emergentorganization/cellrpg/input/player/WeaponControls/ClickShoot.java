package com.emergentorganization.cellrpg.input.player.WeaponControls;

import com.artemis.ComponentMapper;
import com.artemis.World;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.Camera;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.math.Vector3;
import com.emergentorganization.cellrpg.components.Bounds;
import com.emergentorganization.cellrpg.components.InputComponent;
import com.emergentorganization.cellrpg.components.Position;
import com.emergentorganization.cellrpg.core.entityfactory.EntityFactory;
import com.emergentorganization.cellrpg.events.GameEvent;
import com.emergentorganization.cellrpg.input.InputProcessor;
import com.emergentorganization.cellrpg.managers.EventManager;
import com.emergentorganization.cellrpg.systems.CameraSystem;

/**
 * Created by brian on 11/7/15.
 */
public class ClickShoot extends InputProcessor {
    private final EntityFactory entityFactory;
    private final Camera camera;
    private final EventManager eventManager;

    public ClickShoot(World world, EntityFactory entityFactory, ComponentMapper<InputComponent> im) {
        super(world, im);

        this.entityFactory = entityFactory;
        this.camera = world.getSystem(CameraSystem.class).getGameCamera();
        this.eventManager = world.getSystem(EventManager.class);
    }

    @Override
    public void process(int entityId) {
        if (Gdx.input.justTouched()) { // LMB or RMB?
            int x = Gdx.input.getX();
            int y = Gdx.input.getY();
            WeaponUtil.shootTo(x, y, camera, world.getEntity(entityId), eventManager, entityFactory);
        }
    }
}
