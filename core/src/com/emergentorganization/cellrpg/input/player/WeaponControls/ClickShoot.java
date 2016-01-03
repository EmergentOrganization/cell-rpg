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
    private final ComponentMapper<Position> pm;
    private final ComponentMapper<Bounds> bm;
    private final EntityFactory entityFactory;
    private final Camera camera;
    private final EventManager eventManager;

    public ClickShoot(World world, EntityFactory entityFactory, ComponentMapper<InputComponent> im, ComponentMapper<Position> pm, ComponentMapper<Bounds> bm) {
        super(world, im);

        this.entityFactory = entityFactory;
        this.pm = pm;
        this.bm = bm;
        this.camera = world.getSystem(CameraSystem.class).getGameCamera();
        this.eventManager = world.getSystem(EventManager.class);
    }

    @Override
    public void process(int entityId) {
        if (Gdx.input.justTouched()) { // LMB or RMB?
            shootBullet(entityId);
            eventManager.pushEvent(GameEvent.PLAYER_SHOOT);
        }
    }

    private void shootBullet(int entityId) {
        Bounds bounds = bm.get(entityId);
        Vector2 offset = new Vector2(bounds.width, bounds.height).scl(0.5f);
        Vector2 playerPos = pm.get(entityId).position;
        Vector2 center = playerPos.cpy().add(offset); // TODO: A bit off for some reason
        Vector2 arm = new Vector2(0, Math.max(bounds.width, bounds.height));

        int x = Gdx.input.getX();
        int y = Gdx.input.getY();
        Vector3 unproject = camera.unproject(new Vector3(x, y, 0));
        Vector2 dir = new Vector2(unproject.x, unproject.y).sub(playerPos).nor();
        arm.setAngle(dir.angle());

        entityFactory.createBullet(center.add(arm), dir);
    }
}
