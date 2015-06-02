package com.emergentorganization.cellrpg.entities.characters;

import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.emergentorganization.cellrpg.entities.Entity;

/**
 * Created by tylar on 6/2/15.
 */

/**
 *used for NPCs & players
 */
public class Character extends Entity {
    // TODO: create RenderComponent class and move img out of here
    Texture img;

    public Character(String textureFileName){
        img = new Texture(textureFileName);
    }

    @Override
    public void render(SpriteBatch batch) {
        super.render(batch);
        batch.draw(img, 300, 300); // TODO: character should be centered
    }
}
