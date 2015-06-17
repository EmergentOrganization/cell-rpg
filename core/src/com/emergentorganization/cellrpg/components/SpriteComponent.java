package com.emergentorganization.cellrpg.components;

import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.Sprite;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.math.Vector2;
import com.emergentorganization.cellrpg.components.messages.BaseComponentMessage;

/**
 * Created by BrianErikson on 6/3/2015.
 */
public class SpriteComponent extends BaseComponent {

    private Sprite sprite;

    public SpriteComponent(Texture texture) {
        type = ComponentType.SPRITE;
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
    public void render(SpriteBatch batch, Vector2 pos, float rot, Vector2 scale) {
        super.render(batch, pos, rot, scale);

        sprite.setSize(sprite.getRegionWidth() * scale.x, sprite.getRegionHeight() * scale.y);
        sprite.setRotation(rot);
        sprite.setCenter(pos.x, pos.y);
        sprite.draw(batch);
    }

    @Override
    public boolean shouldRender() {
        return true;
    }

    @Override
    public ComponentType getType() {
        return super.getType();
    }

    @Override
    public void receiveMessage(BaseComponentMessage message) {
        super.receiveMessage(message);
    }

    @Override
    public void dispose() {
        super.dispose();
    }
}
