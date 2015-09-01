package com.emergentorganization.cellrpg.entities.characters;

import com.emergentorganization.cellrpg.components.entity.BulletComponent;
import com.badlogic.gdx.math.Vector2;
import com.emergentorganization.cellrpg.components.entity.GraphicsComponent;
import com.emergentorganization.cellrpg.entities.Entity;
import com.emergentorganization.cellrpg.entities.ZIndex;

/**
 * Created by OrelBitton on 06/06/2015.
 */
public class Bullet extends Entity {

    private final GraphicsComponent graphicsComponent;
    private BulletComponent bc;

    public Bullet(Vector2 shootingPos, Vector2 velocity, float maxDist){
        super(ZIndex.PROJECTILE);
        graphicsComponent = new GraphicsComponent();
        graphicsComponent.register("idle", "bullet.png");
        graphicsComponent.play("idle");
        addComponent(this.graphicsComponent);

        bc = new BulletComponent(shootingPos, velocity, maxDist);
        addComponent(bc);
    }

}

