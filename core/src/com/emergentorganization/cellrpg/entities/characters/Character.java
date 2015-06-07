package com.emergentorganization.cellrpg.entities.characters;

import com.emergentorganization.cellrpg.entities.Entity;
import com.emergentorganization.cellrpg.components.GraphicsComponent;

/**
 * Created by tylar on 6/2/15.
 */

/**
 *used for NPCs & players
 */
public class Character extends Entity {
    private GraphicsComponent graphicsComponent;

    

    public Character(String textureFileName, int n_columns, int n_rows, float time_per_frame) {
        graphicsComponent = new GraphicsComponent();
        graphicsComponent.register("idle", textureFileName, n_columns, n_rows, time_per_frame);
        graphicsComponent.play("idle");
        addComponent(this.graphicsComponent);
    }

}
