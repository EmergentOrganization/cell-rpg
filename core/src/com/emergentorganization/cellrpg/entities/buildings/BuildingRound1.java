package com.emergentorganization.cellrpg.entities.buildings;

import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.math.Vector2;
import com.emergentorganization.cellrpg.components.SpriteComponent;
import com.emergentorganization.cellrpg.entities.Entity;
import com.emergentorganization.cellrpg.entities.ZIndex;

/**
 * Created by BrianErikson on 6/12/2015.
 */
public class BuildingRound1 extends Entity {
    public static final String ID = "building-round-1";
    Texture texture;

    public BuildingRound1() {
        super(ZIndex.BUILDING);
        texture = new Texture(ID + ".png");
        addComponent(new SpriteComponent(texture));
    }

    public BuildingRound1(Texture texture, Vector2 position) {
        super(ZIndex.BUILDING);
        this.texture = texture;
        this.getMovementComponent().setWorldPosition(position);

        addComponent(new SpriteComponent(texture));
    }
}
