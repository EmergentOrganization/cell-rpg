package com.emergentorganization.cellrpg.entities.characters;

import com.badlogic.gdx.audio.Sound;
import com.emergentorganization.cellrpg.CellRpg;
import com.emergentorganization.cellrpg.components.entity.BulletComponent;
import com.badlogic.gdx.math.Vector2;
import com.emergentorganization.cellrpg.components.entity.CACollisionBuilder;
import com.emergentorganization.cellrpg.components.entity.CACollisionComponent;
import com.emergentorganization.cellrpg.components.entity.GraphicsComponent;
import com.emergentorganization.cellrpg.entities.Entity;
import com.emergentorganization.cellrpg.entities.EntityEvents;
import com.emergentorganization.cellrpg.entities.ZIndex;
import com.emergentorganization.cellrpg.scenes.CALayer;
import com.emergentorganization.cellrpg.scenes.CAScene;
import com.emergentorganization.cellrpg.scenes.arcadeScore;
import com.emergentorganization.cellrpg.tools.FileStructure;

import java.io.File;

/**
 * Created by OrelBitton on 06/06/2015.
 */
public class Bullet extends Entity {

    static final int POINTS_FOR_VYROID_HIT = 100;
    private final GraphicsComponent graphicsComponent;
    private final Sound hit;
    private BulletComponent bc;

    public Bullet(Vector2 shootingPos, Vector2 velocity, float maxDist){
        super(ZIndex.PROJECTILE);
        graphicsComponent = new GraphicsComponent();
        graphicsComponent.register("idle", "game/bullet");
        graphicsComponent.play("idle");
        addComponent(this.graphicsComponent);

        bc = new BulletComponent(shootingPos, velocity, maxDist);
        addComponent(bc);

        hit = CellRpg.fetch().getAssetManager().get(FileStructure.RESOURCE_DIR + "sounds" + File.separator + "Hit.wav", Sound.class);
    }

    @Override
    public void fireEvent(EntityEvents event){
        super.fireEvent(event);
        switch (event){
            case DESTROYED:
                hit.play();
                if(getScene() instanceof arcadeScore){
                    ((arcadeScore) getScene()).addPoints(POINTS_FOR_VYROID_HIT);
                }
                break;
        }
    }

    @Override
    public void added(){
        super.added();
        CAScene scene =  (CAScene) getScene();
        if (scene instanceof CAScene){
            CACollisionBuilder.collideWithAllVyroids(
                    this,
                    1,
                    EntityEvents.DESTROYED,
                    new int[][]{
                            {2, 2, 2, 2, 2, 2, 2},
                            {2, 2, 2, 2, 2, 2, 2},
                            {2, 2, 2, 2, 2, 2, 2},
                            {2, 2, 2, 2, 2, 2, 2},
                            {2, 2, 2, 2, 2, 2, 2},
                            {2, 2, 2, 2, 2, 2, 2},
                            {2, 2, 2, 2, 2, 2, 2}
                    },
                    new int[][]{
                            {2, 2, 2},
                            {2, 2, 2},
                            {2, 2, 2}
                    },
                    new int[][] {
                            {2,2,2,2,2},
                            {2,2,2,2,2},
                            {2,2,2,2,2},
                            {2, 2, 2, 2, 2},
                            {2, 2, 2, 2, 2}
                    }
            );

            CACollisionComponent cacc=new CACollisionComponent(scene, CACollisionBuilder.getAvailableVyroidLayer(scene));
            // bullet trail energy layer effect
            cacc.addCollision(
                    0,
                    new int[][] {
                            {1,1,1},
                            {1,1,1},
                            {1,1,1}
                    },
                    CALayer.ENERGY
            );
            addComponent(cacc);
        }
    }

}

