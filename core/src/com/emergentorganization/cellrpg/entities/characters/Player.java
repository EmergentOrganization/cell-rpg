package com.emergentorganization.cellrpg.entities.characters;

/**
 * Created by tylar on 6/2/15.
 */
public class Player extends com.emergentorganization.cellrpg.entities.characters.Character {
    // TODO: Handle this in the RenderComponent when it's created
    private static final String TEXTURE_FILE_NAME = "light.png";

    public Player(){
        super(TEXTURE_FILE_NAME);
    }
}
