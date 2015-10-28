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
public class BuildingRound1 extends Entity {
    public static final String ID = "building-round-1";

    /*
    This constructor is needed for MapEditor. Do not remove.
     */
    public BuildingRound1() {
        super(ZIndex.BUILDING);
        addComponent(new SpriteComponent("game/buildings/" + ID));
        initCAGrid();
    }

    public BuildingRound1(Vector2 position) {
        super(ZIndex.BUILDING);
        getFirstComponentByType(MovementComponent.class).setWorldPosition(position);

        addComponent(new SpriteComponent("game/buildings/" + ID));
        initCAGrid();
    }

    private void initCAGrid(){
        int[][] pattern = {
                {1,1,1}
        };
        addComponent(new GridSeedComponent(
                pattern,
                5,
                new Vector2(0,0),  // TODO: place this in center of img
                CALayer.VYROIDS
        ));
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
