package com.emergentorganization.cellrpg.entities.characters;

import com.badlogic.gdx.graphics.Texture;
import com.emergentorganization.cellrpg.components.SpriteComponent;
import com.emergentorganization.cellrpg.entities.Entity;

/**
 * Created by tylar on 6/2/15.
 */

/**
 *used for NPCs & players
 */
public class Character extends Entity {
    SpriteComponent spriteComponent;

    public Character(String textureFileName){
        spriteComponent = new SpriteComponent(new Texture(textureFileName));
        addComponent(spriteComponent);
    }
}
