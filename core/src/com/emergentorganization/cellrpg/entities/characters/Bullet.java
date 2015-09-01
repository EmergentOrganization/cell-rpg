package com.emergentorganization.cellrpg.entities.characters;

import com.emergentorganization.cellrpg.components.entity.BulletComponent;
import com.badlogic.gdx.math.Vector2;
import com.emergentorganization.cellrpg.components.entity.CACollisionComponent;
import com.emergentorganization.cellrpg.components.entity.GraphicsComponent;
import com.emergentorganization.cellrpg.entities.Entity;
import com.emergentorganization.cellrpg.entities.EntityEvents;
import com.emergentorganization.cellrpg.entities.ZIndex;
import com.emergentorganization.cellrpg.scenes.CALayer;

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

        CACollisionComponent cacc = new CACollisionComponent(CALayer.VYROIDS);
        // bullet trail energy layer effect
        cacc.addCollision(
            0,
            new int[][] {
                    {0,1,0},
                    {1,0,1},
                    {0,1,0},
            },
            CALayer.ENERGY
        );
        // vyroid destruction effect
        cacc.addCollision(
            1,
            new int[][] {
                    {0,0,0,0,0},
                    {0,0,0,0,0},
                    {0,0,0,0,0},
                    {0,0,0,0,0},
                    {0,0,0,0,0}
            },
            CALayer.VYROIDS
        );
        cacc.addCollision(1, EntityEvents.DESTROYED);
        addComponent(cacc);
    }

}

