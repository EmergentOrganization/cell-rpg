package com.emergentorganization.cellrpg;

import com.emergentorganization.cellrpg.Character;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;

/**
 * Created by tylar on 6/2/15.
 */
public class Player {
    private final String TEXTURE_FILE_NAME = "light.png";
    private Character character;

    public Player(){
        character = new Character(TEXTURE_FILE_NAME);
    }

    public void render(SpriteBatch batch){
        character.render(batch);
    }
}
