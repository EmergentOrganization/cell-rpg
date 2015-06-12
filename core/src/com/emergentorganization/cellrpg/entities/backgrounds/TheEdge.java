package com.emergentorganization.cellrpg.entities.backgrounds;

import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.math.Vector2;
import com.emergentorganization.cellrpg.components.SpriteComponent;
import com.emergentorganization.cellrpg.entities.Entity;
import com.emergentorganization.cellrpg.entities.ZIndex;

/**
 * Created by BrianErikson on 6/12/2015.
 */
public class TheEdge extends Entity {
    public static final String ID = "the_edge";
    private final Texture texture;

    public TheEdge(Texture texture, Vector2 position) {
        super(ZIndex.BACKGROUND);
        this.texture = texture;
        SpriteComponent spriteComponent = new SpriteComponent(texture);
        addComponent(spriteComponent);

        this.getMovementComponent().setWorldPosition(position);
    }
}
