package com.emergentorganization.cellrpg.entities.buildings;

import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.math.Vector2;
import com.emergentorganization.cellrpg.components.entity.MovementComponent;
import com.emergentorganization.cellrpg.components.entity.PhysicsComponent;
import com.emergentorganization.cellrpg.components.entity.SpriteComponent;
import com.emergentorganization.cellrpg.entities.Entity;
import com.emergentorganization.cellrpg.entities.ZIndex;
import com.emergentorganization.cellrpg.physics.Tag;
import com.emergentorganization.cellrpg.tools.physics.BodyLoader;

/**
 * Created by BrianErikson on 6/12/2015.
 */
public class Rift2 extends Entity {
    public static final String ID = "rift2";

    /*
    This constructor is needed for MapEditor. Do not remove.
     */
    public Rift2() {
        super(ZIndex.BUILDING);
        addComponent(new SpriteComponent("game/environment/" + ID));
    }

    public Rift2(Vector2 position) {
        super(ZIndex.BUILDING);
        getFirstComponentByType(MovementComponent.class).setWorldPosition(position);

        addComponent(new SpriteComponent("game/environment/" + ID));
    }

    @Override
    public void added() {
        super.added();

        float width = getFirstComponentByType(SpriteComponent.class).getSprite().getWidth();
        float scale = width * getFirstComponentByType(MovementComponent.class).getScale().x;
        PhysicsComponent phys = new PhysicsComponent(getScene().getWorld(),
                BodyLoader.fetch().generateBody(ID, scale), Tag.STATIC);
        addComponent(phys);
    }
}
