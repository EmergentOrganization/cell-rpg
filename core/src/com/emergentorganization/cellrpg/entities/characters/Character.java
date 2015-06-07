package com.emergentorganization.cellrpg.entities.characters;

import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.Animation;
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

    public Character(String textureFileName) {
        this(new Texture(textureFileName));
    }
    public Character(Texture bodyTexture){
        this.graphicsComponent = new GraphicsComponent();
        graphicsComponent.register("body", bodyTexture);
        addComponent(graphicsComponent);
        graphicsComponent.play("body");
    }
    public Character(Animation bodyAnimation){
        this.graphicsComponent = new GraphicsComponent();
        graphicsComponent.register("body", bodyAnimation);
        addComponent(graphicsComponent);
        graphicsComponent.play("body");
    }
}
