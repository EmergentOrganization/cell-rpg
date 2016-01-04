package com.emergentorganization.cellrpg.input.player.WeaponControls;

import com.artemis.ComponentMapper;
import com.artemis.Entity;
import com.artemis.World;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Input;
import com.badlogic.gdx.graphics.Camera;
import com.badlogic.gdx.math.Vector2;
import com.emergentorganization.cellrpg.components.Bounds;
import com.emergentorganization.cellrpg.components.InputComponent;
import com.emergentorganization.cellrpg.components.Position;
import com.emergentorganization.cellrpg.core.entityfactory.EntityFactory;
import com.emergentorganization.cellrpg.managers.EventManager;
import com.emergentorganization.cellrpg.systems.CameraSystem;

/**
 * Created by 7yl4r 2015-09-13
 * ported to artemis branch by 7yl4r 2016-01-04
 */
public class ArrowsShoot extends iWeaponCtrl {
    private final EntityFactory entityFactory;
    private final Camera camera;
    private final EventManager eventManager;

    public ArrowsShoot(World world, EntityFactory entityFactory, ComponentMapper<InputComponent> im) {
        super(world, im);

        this.entityFactory = entityFactory;
        this.camera = world.getSystem(CameraSystem.class).getGameCamera();
        this.eventManager = world.getSystem(EventManager.class);
    }

    @Override
    public void process(int entityId) {
        Entity player = world.getEntity(entityId);
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
            WeaponUtil.shootTo(target.x, target.y, camera, player, eventManager, entityFactory);
        }
    }
}
