package com.emergentorganization.cellrpg.entities.buildings;

import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.math.Vector2;
import com.emergentorganization.cellrpg.components.entity.GridSeedComponent;
import com.emergentorganization.cellrpg.components.entity.MovementComponent;
import com.emergentorganization.cellrpg.components.entity.PhysicsComponent;
import com.emergentorganization.cellrpg.components.entity.SpriteComponent;
import com.emergentorganization.cellrpg.entities.Entity;
import com.emergentorganization.cellrpg.entities.ZIndex;
import com.emergentorganization.cellrpg.physics.Tag;
import com.emergentorganization.cellrpg.scenes.CALayer;
import com.emergentorganization.cellrpg.scenes.Scene;
import com.emergentorganization.cellrpg.tools.physics.BodyLoader;

/**
 * Created by BrianErikson on 6/12/2015.
 */
public class Rift1 extends Entity {
    public static final String ID = "rift1";
    Texture texture;

    /*
    This constructor is needed for MapEditor. Do not remove.
     */
    public Rift1() {
        super(ZIndex.BUILDING);
        texture = new Texture(ID + ".png");
        addComponent(new SpriteComponent(texture));
    }

    public Rift1(Texture texture, Vector2 position) {
        super(ZIndex.BUILDING);
        this.texture = texture;
        getFirstComponentByType(MovementComponent.class).setWorldPosition(position);

        addComponent(new SpriteComponent(texture));
    }

    @Override
    public void added() {
        super.added();

        float scale = Math.max(texture.getWidth(), texture.getHeight()) * Scene.scale;
        PhysicsComponent phys = new PhysicsComponent(getScene().getWorld(),
                BodyLoader.fetch().generateBody(ID, scale), Tag.RIFT_1);
        addComponent(phys);
    }
}
