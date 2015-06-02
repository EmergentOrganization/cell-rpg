package com.emergentorganization.cellrpg;

import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.Texture;

/**
 * Created by tylar on 6/2/15.
 */
public class Character {
    /*
    used for NPCs & players
     */
    Texture img;

    public Character(String textureFileName){
        img = new Texture(textureFileName);
    }


    public void render (SpriteBatch batch) {
        batch.draw(img, 300, 300); // TODO: player should be centered
    }
}
