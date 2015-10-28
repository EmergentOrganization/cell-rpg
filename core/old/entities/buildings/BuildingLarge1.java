package com.emergentorganization.cellrpg.entities.buildings;

import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.math.Vector2;
import com.emergentorganization.cellrpg.components.entity.MovementComponent;
import com.emergentorganization.cellrpg.components.entity.PhysicsComponent;
import com.emergentorganization.cellrpg.components.entity.SpriteComponent;
import com.emergentorganization.cellrpg.entities.Entity;
import com.emergentorganization.cellrpg.physics.Tag;
import com.emergentorganization.cellrpg.scenes.Scene;
import com.emergentorganization.cellrpg.tools.physics.BodyLoader;
import org.dyn4j.dynamics.Body;
import org.dyn4j.dynamics.World;

/**
 * Created by BrianErikson on 6/5/2015.
 */
public class BuildingLarge1 extends Entity {
    public static final String ID = "building-large-1";

    /*
    This constructor is needed for MapEditor. Do not remove.
     */
    public BuildingLarge1() {
        addComponent(new SpriteComponent("game/buildings/" + ID));
    }

    public BuildingLarge1(Vector2 position) {
        getFirstComponentByType(MovementComponent.class).setWorldPosition(position);
        addComponent(new SpriteComponent("game/buildings/" + ID));
    }

    @Override
    public void added() {
        super.added();
        float width = getFirstComponentByType(SpriteComponent.class).getSprite().getWidth();
        float scale = width * getFirstComponentByType(MovementComponent.class).getScale().x;
        World world = getScene().getWorld();
        Body body = BodyLoader.fetch().generateBody(ID, scale);
        PhysicsComponent phys = new PhysicsComponent(world, body, Tag.STATIC);
        //phys.enableDebugRenderer(true);
        addComponent(phys);
    }
}
