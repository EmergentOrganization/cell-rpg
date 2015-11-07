package com.emergentorganization.cellrpg.input.player;

import com.artemis.ComponentMapper;
import com.artemis.World;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.Camera;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.math.Vector3;
import com.emergentorganization.cellrpg.components.Input;
import com.emergentorganization.cellrpg.components.Position;
import com.emergentorganization.cellrpg.core.EntityFactory;
import com.emergentorganization.cellrpg.input.InputProcessor;
import com.emergentorganization.cellrpg.systems.CameraSystem;

/**
 * Created by brian on 11/7/15.
 */
public class PlayerWeapon extends InputProcessor {
    private final ComponentMapper<Position> pm;
    private final EntityFactory entityFactory;
    private final Camera camera;

    public PlayerWeapon(World world, EntityFactory entityFactory, ComponentMapper<Input> im, ComponentMapper<Position> pm) {
        super(world, im);

        this.entityFactory = entityFactory;
        this.pm = pm;
        this.camera = world.getSystem(CameraSystem.class).getGameCamera();
    }

    @Override
    public void process(int entityId) {
        if (Gdx.input.justTouched()) { // LMB or RMB?
            shootBullet(entityId);
        }
    }

    private void shootBullet(int entityId) {
        // TODO: Maintain lifecycle -- cull bullets once a distance away from player
        Vector2 playerPos = pm.get(entityId).position;

        int x = Gdx.input.getX();
        int y = Gdx.input.getY();
        Vector3 unproject = camera.unproject(new Vector3(x, y, 0));
        Vector2 dir = new Vector2(unproject.x, unproject.y).sub(playerPos).nor();

        entityFactory.createBullet(playerPos, dir);
    }
}
