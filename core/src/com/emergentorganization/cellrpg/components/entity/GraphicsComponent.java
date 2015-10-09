package com.emergentorganization.cellrpg.components.entity;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.*;
import com.emergentorganization.cellrpg.CellRpg;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.util.HashMap;

/**
 * A component that serves as both SpriteComponent and AnimationComponent
 * Utilizes libgdx's animation class
 * 
 * Created by OrelBitton on 06/06/2015.
 */
public class GraphicsComponent extends SpriteComponent{
    private final Logger logger = LogManager.getLogger(getClass());
    private final TextureAtlas textureAtlas;
    private HashMap<String, Animation> anims = new HashMap<String, Animation>();
    private Animation playing;
    private float stateTime = 0f;
    private TextureRegion curFrame;

    public GraphicsComponent() {
        this.textureAtlas = CellRpg.fetch().getTextureAtlas();
    }

    public org.dyn4j.geometry.Vector2 getSize(){
        return new org.dyn4j.geometry.Vector2(curFrame.getRegionWidth(), curFrame.getRegionHeight());
    }

    @Override
    public void update(float deltaTime) {
        stateTime += deltaTime;
    }

    @Override
    public void render(SpriteBatch batch) {
        if(playing == null)
            return;
        checkKeyFrames();

        setSpriteTransform();
        Sprite sprite = getSprite();
        sprite.draw(batch);
    }

    public void setKeyFrame(TextureRegion region) {
        try {
            curFrame = region;
            Sprite sprite = getSprite();
            sprite.setRegion(region);
            sprite.setSize(curFrame.getRegionWidth(), curFrame.getRegionHeight());
            // NOTE: might also want to do these?:
            //sprite.setColor(1, 1, 1, 1);
        } catch(NullPointerException ex){
            logger.error("Texture is null! Perhaps you forgot to pack it?");
            throw ex;
        }
    }

    public void checkKeyFrames() {
        if (curFrame == null) {
            //System.out.println("Setting key frame. curFrame is null");
            setKeyFrame(playing.getKeyFrame(stateTime));
        }
        else if (playing.getKeyFrame(stateTime) != curFrame) {
            //System.out.println("Key frame is diff than curFrame");
            setKeyFrame(playing.getKeyFrame(stateTime));
        }
    }

    public void register(String name, Texture texture){
        register(name, new Animation(0, new TextureRegion(texture)));
    }

    public void register(String name, TextureRegion region){
        register(name, new Animation(0, region));
    }

    public void register(String name, Animation anim){
        if(anims.containsKey(name))
            throw new RuntimeException("Animation titled "+ name + " is already registered.");

        anims.put(name, anim);
    }

    public void register(String name, String spriteFile){
        // add a static sprite
        register(name, textureAtlas.findRegion(spriteFile));
    }

    public void register(String name, String spriteFile, float timePerFrame){
        // add a sprite animation
        register(name, textureAtlas.findRegion(spriteFile));
    }

    public void register(String name, String sheetFile, int n_columns, int n_rows, float time_per_frame){
        // add a sprite animation
        Texture sheet = new Texture(Gdx.files.internal(sheetFile));
        TextureRegion[][] spriteFrames = TextureRegion.split(sheet, sheet.getWidth()/n_columns, sheet.getHeight()/n_rows);
        TextureRegion[] firstAnimation = spriteFrames[0];  // assume exactly 1 animation per row
        Animation anim = new Animation(time_per_frame, firstAnimation);
        anim.setPlayMode(Animation.PlayMode.LOOP_PINGPONG);
        register(name, anim);
        sheet.dispose();
    }

    public void register(String name, Texture texture, int n_columns, int n_rows, float time_per_frame){
        // add a sprite animation
        TextureRegion[][] spriteFrames = TextureRegion.split(texture, texture.getWidth()/n_columns, texture.getHeight()/n_rows);
        TextureRegion[] firstAnimation = spriteFrames[0];  // assume exactly 1 animation per row
        Animation anim = new Animation(time_per_frame, firstAnimation);
        anim.setPlayMode(Animation.PlayMode.LOOP_PINGPONG);
        register(name, anim);
    }

    public void play(String name){
        if(!anims.containsKey(name))
            throw new RuntimeException("Animation titled "+ name + " isn't registered.");

        playing = anims.get(name);
        stateTime = 0f;
        setKeyFrame(playing.getKeyFrame(stateTime));
    }

    public TextureRegion getCurrentFrame() {
        return curFrame;
    }

    @Override
    public boolean shouldRender() {
        return true;
    }

    @Override
    public void dispose(){
        super.dispose();
    }
}

