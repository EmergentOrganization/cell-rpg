package com.emergentorganization.cellrpg.entities.characters;

import com.emergentorganization.cellrpg.components.entity.BulletComponent;
import com.badlogic.gdx.math.Vector2;
import com.emergentorganization.cellrpg.components.entity.CACollisionComponent;
import com.emergentorganization.cellrpg.components.entity.GraphicsComponent;
import com.emergentorganization.cellrpg.entities.Entity;
import com.emergentorganization.cellrpg.entities.EntityEvents;
import com.emergentorganization.cellrpg.entities.ZIndex;
import com.emergentorganization.cellrpg.scenes.CALayer;
import com.emergentorganization.cellrpg.scenes.arcadeScore;

/**
 * Created by OrelBitton on 06/06/2015.
 */
public class Bullet extends Entity {

    static final int POINTS_FOR_VYROID_HIT = 100;
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
                    {2,2,2,2,2},
                    {2,2,2,2,2},
                    {2,2,2,2,2},
                    {2,2,2,2,2},
                    {2,2,2,2,2}
            },
            CALayer.VYROIDS
        );
        cacc.addCollision(1, EntityEvents.DESTROYED);
        addComponent(cacc);
    }

    @Override
    public void fireEvent(EntityEvents event){
        super.fireEvent(event);
        switch (event){
            case DESTROYED:
                if(getScene() instanceof arcadeScore){
                    ((arcadeScore) getScene()).addPoints(POINTS_FOR_VYROID_HIT);
                }
                break;
        }
    }

}

