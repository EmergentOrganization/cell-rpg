package com.emergentorganization.cellrpg.components;

import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.Animation;
import com.badlogic.gdx.graphics.g2d.Sprite;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.math.Vector2;
import com.emergentorganization.cellrpg.components.BaseComponent;
import com.emergentorganization.cellrpg.components.ComponentType;
import com.emergentorganization.cellrpg.components.SpriteComponent;
import com.emergentorganization.cellrpg.components.messages.BaseComponentMessage;

import java.util.HashMap;

/**
 * A component that serves as both SpriteComponent and AnimationComponent
 * Utilizes libgdx's animation class
 * 
 * Created by OrelBitton on 06/06/2015.
 */
public class GraphicsComponent extends BaseComponent{
    private Sprite sprite = new Sprite();
    private HashMap<String, Animation> anims = new HashMap<String, Animation>();
    private Animation playing;
    private float stateTime = 0f;

    public GraphicsComponent() {
        type = ComponentType.GRAPHICS;
    }

    @Override
    public void update(float deltaTime) {
        stateTime++;
    }

    @Override
    public void render(SpriteBatch batch, Vector2 pos, float rot, Vector2 scale) {
        super.render(batch, pos, rot, scale);

        if(playing == null)
            return;

        sprite.setRegion(playing.getKeyFrame(stateTime));
        sprite.setScale(scale.x, scale.y);
        sprite.setRotation(rot);
        sprite.setCenter(pos.x, pos.y); // TODO: Isn't actually centered when scaled?
        sprite.draw(batch);
    }

    public void register(String name, Texture texture){
        register(name, new Animation(0, new TextureRegion(texture)));
    }

    public void register(String name, Animation anim){
        if(anims.containsKey(name))
            throw new RuntimeException("Animation titled "+ name + " is already registered.");

        anims.put(name, anim);
    }

    public void play(String name){
        if(!anims.containsKey(name))
            throw new RuntimeException("Animation titled "+ name + " isn't registered.");

        playing = anims.get(name);
        stateTime = 0;
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
