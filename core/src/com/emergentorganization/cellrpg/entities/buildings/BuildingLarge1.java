package com.emergentorganization.cellrpg.entities.buildings;

import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.math.Vector2;
import com.emergentorganization.cellrpg.components.PhysicsComponent;
import com.emergentorganization.cellrpg.components.SpriteComponent;
import com.emergentorganization.cellrpg.entities.Entity;
import com.emergentorganization.cellrpg.physics.Tag;
import com.emergentorganization.cellrpg.tools.physics.BodyLoader;
import org.dyn4j.dynamics.Body;
import org.dyn4j.dynamics.World;

/**
 * Created by BrianErikson on 6/5/2015.
 */
public class BuildingLarge1 extends Entity {
    public static final String ID = "building-large-1";
    Texture texture;

    public BuildingLarge1() {
        texture = new Texture(ID + ".png");
        addComponent(new SpriteComponent(texture));

    }

    public BuildingLarge1(Texture texture, Vector2 position) {
        this.texture = texture;
        this.getMovementComponent().setWorldPosition(position);

        addComponent(new SpriteComponent(texture));
    }

    @Override
    public void added() {
        super.added();

        World world = getScene().getWorld();
        Body body = BodyLoader.fetch().generateBody(ID, texture.getWidth());
        PhysicsComponent phys = new PhysicsComponent(world, body, Tag.BUILDING_LARGE_1);
        //phys.enableDebugRenderer(true);
        addComponent(phys);
    }
}
