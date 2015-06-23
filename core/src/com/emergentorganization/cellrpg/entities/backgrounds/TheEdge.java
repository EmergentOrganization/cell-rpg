package com.emergentorganization.cellrpg.entities.backgrounds;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.math.Vector2;
import com.emergentorganization.cellrpg.components.entity.PhysicsComponent;
import com.emergentorganization.cellrpg.components.entity.SpriteComponent;
import com.emergentorganization.cellrpg.entities.Entity;
import com.emergentorganization.cellrpg.entities.ZIndex;
import com.emergentorganization.cellrpg.physics.Tag;
import org.dyn4j.dynamics.Body;
import org.dyn4j.dynamics.BodyFixture;
import org.dyn4j.geometry.Rectangle;

/**
 * Created by BrianErikson on 6/12/2015.
 */
public class TheEdge extends Entity {
    public static final String ID = "the_edge";
    private final Texture texture;
    private final SpriteComponent spriteComponent;

    /*
    This constructor is needed for MapEditor. Do not remove.
     */
    public TheEdge() {
        super(ZIndex.BACKGROUND);
        texture = new Texture(Gdx.files.internal(ID + ".png"));

        spriteComponent = new SpriteComponent(texture);
        addComponent(spriteComponent);
    }

    @Override
    public void added() {
        super.added();

        if (getScene().isEditor()) {
            Vector2 size = new Vector2(spriteComponent.getSprite().getWidth() * getMovementComponent().getScale().x,
                    spriteComponent.getSprite().getHeight() * getMovementComponent().getScale().y);
            Rectangle rect = new Rectangle(size.x, size.y);
            rect.translate(size.x / 2f, size.y /2f);
            BodyFixture fixture = new BodyFixture(rect);
            Body body = new Body();
            body.addFixture(fixture);

            PhysicsComponent physicsComponent = new PhysicsComponent(getScene().getWorld(), body, Tag.THE_EDGE);
            physicsComponent.enableDebugRenderer(true);
            addComponent(physicsComponent);
        }
    }

    public TheEdge(Texture texture, Vector2 position) {
        super(ZIndex.BACKGROUND);
        this.texture = texture;
        spriteComponent = new SpriteComponent(texture);
        addComponent(spriteComponent);

        this.getMovementComponent().setWorldPosition(position);
    }
}
