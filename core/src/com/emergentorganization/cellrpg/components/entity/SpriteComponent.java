package com.emergentorganization.cellrpg.components.entity;

import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.Sprite;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.math.Vector2;
import com.emergentorganization.cellrpg.components.EntityComponent;

/**
 * Created by BrianErikson on 6/3/2015.
 */
public class SpriteComponent extends EntityComponent {

    private Sprite sprite;

    public SpriteComponent() {
        this.sprite = new Sprite();
    }

    public SpriteComponent(Texture texture) {
        this.sprite = new Sprite(texture);
    }

    public Sprite getSprite() {
        return sprite;
    }

    @Override
    public void update(float deltaTime) {
        super.update(deltaTime);
    }

    @Override
    public void render(SpriteBatch batch) {
        super.render(batch);
        setSpriteTransform();
        sprite.draw(batch);
    }

    public void setSpriteTransform() {
        MovementComponent moveComponent = getEntity().getFirstComponentByType(MovementComponent.class);
        Vector2 scale = moveComponent.getScale();
        Vector2 pos = moveComponent.getWorldPosition();

        sprite.setSize(sprite.getRegionWidth() * scale.x, sprite.getRegionHeight() * scale.y);
        sprite.setOriginCenter();
        sprite.setRotation(moveComponent.getRotation());
        sprite.setCenter(pos.x, pos.y);
    }

    @Override
    public boolean shouldRender() {
        return true;
    }

    @Override
    public void dispose() {
        super.dispose();
    }
}
