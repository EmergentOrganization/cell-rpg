package com.emergentorganization.cellrpg.components.entity;

import com.emergentorganization.cellrpg.entities.Entity;
import com.emergentorganization.cellrpg.entities.EntityEvents;
import com.emergentorganization.cellrpg.entities.ca.CAGridBase;
import com.emergentorganization.cellrpg.scenes.CALayer;
import com.emergentorganization.cellrpg.scenes.CAScene;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

/**
 * Static helper class to make adding ca collider components to entities easier.
 *
 * Created by 7yl4r on 2015-10-13.
 */
public class CACollisionBuilder {
    private static final Logger logger = LogManager.getLogger(CACollisionBuilder.class);

    private static int getCollideRadius(){
        return 0;  //TODO: (int) (getGraphicsComponent().getSize().x*scale);
    }

    private static int getCollideGrid(CAScene scene){
        // NOTE: this uses only x-dimension; assumes width ~= height
        try {
            return scene.getLayer(CALayer.VYROIDS).getCellSize();
        } catch(NullPointerException err){
            return 1;
        }
    }

    private static boolean tryAddCollide(CAScene scene, Entity parentEntity, CALayer layer,
                                         int collideState, EntityEvents event, int[][] gridStamp){
        // attempts to add collision, returns true if success, false if exception (presumably from missing layer)
        try {
            logger.trace("adding collide w/ ca layer '"+layer+"'...");
            CACollisionComponent cacc = new CACollisionComponent(scene, layer);
            cacc.addCollision(
                    collideState,
                    event,
                    getCollideRadius(),
                    getCollideGrid(scene)
            );
            cacc.addCollision(
                    collideState,
                    gridStamp,
                    layer,
                    getCollideRadius(),
                    getCollideGrid(scene)
            );
            parentEntity.addComponent(cacc);
            return true;
        } catch (Exception ex){
            logger.debug("collision for ca layer '"+layer+"' not added: " + ex.getMessage());
            return false;
        }
    }

    public static void collideWithAllVyroids(Entity parentEntity, int collideState, EntityEvents event, int[][] gridStamp){
        // uses same size gridStamp for all layers
        collideWithAllVyroids(parentEntity, collideState, event, gridStamp, gridStamp, gridStamp);
    }
    public static void collideWithAllVyroids(Entity parentEntity, int collideState, EntityEvents event,
                                             int[][] miniStamp, int[][] medStamp, int[][] megaStamp ){
        // sets up given entity to collide with all vyroid layers in the same way.
        // uses different size stamps for mini/mega sized layers
        CAScene scene =  (CAScene) parentEntity.getScene();
        if (scene instanceof CAScene) {
            // normal size vyroids
            tryAddCollide(scene, parentEntity, CALayer.VYROIDS, collideState, event, medStamp);
            tryAddCollide(scene, parentEntity, CALayer.VYROIDS_GENETIC, collideState, event, medStamp);
            // mini vyroids
            tryAddCollide(scene, parentEntity, CALayer.VYROIDS_MINI, collideState, event, miniStamp);
            // mega vyroids
            tryAddCollide(scene, parentEntity, CALayer.VYROIDS_MEGA, collideState, event, megaStamp);
        } else {
            logger.debug("cannot collide w/ vyroids in non-CA scene");
        }
    }

    public static CALayer getAvailableVyroidLayer(CAScene scene){
        // returns a CA_layer with vyroids (whichever is found first)
        for (CALayer layer : CALayer.vyroid_values()){
            CAGridBase lay = scene.getLayer(layer);
            if (lay != null){
                return layer;
            }
        }  // else
        throw new IllegalStateException("no vyroid layers available in current scene");
    }
}
