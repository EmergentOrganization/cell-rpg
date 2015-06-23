package com.emergentorganization.cellrpg.entities.characters;

import com.emergentorganization.cellrpg.components.entity.BulletComponent;
import com.badlogic.gdx.math.Vector2;
/**
 * Created by OrelBitton on 06/06/2015.
 */
public class Bullet extends Character {

    private BulletComponent bc;

    public Bullet(Vector2 shootingPos, Vector2 velocity, float maxDist){
        super("bullet.png");

        bc = new BulletComponent(shootingPos, velocity, maxDist);
        addComponent(bc);
    }

}

