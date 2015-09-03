package com.emergentorganization.cellrpg.scenes;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.math.Vector2;
import com.emergentorganization.cellrpg.components.entity.MovementComponent;
import com.emergentorganization.cellrpg.components.entity.SpriteComponent;
import com.emergentorganization.cellrpg.entities.Entity;
import com.emergentorganization.cellrpg.tools.postprocessing.ShaderException;
import com.emergentorganization.cellrpg.tools.postprocessing.TronShader;

/**
 * Created by brian on 9/3/15.
 */
public class RenderTest extends Scene {
    @Override
    public void create() {
        super.create();

        try {
            addPostProcessor(new TronShader(5));
        } catch (ShaderException e) {
            e.printStackTrace();
        }

        Entity entity = new Entity() {};
        Texture texture = new Texture("testAlpha.png");
        entity.addComponent(new SpriteComponent(texture));
        MovementComponent movementComponent = entity.getFirstComponentByType(MovementComponent.class);
        movementComponent.translate(new Vector2(50f, 35f)); // dunno why
        addEntity(entity);
    }
}