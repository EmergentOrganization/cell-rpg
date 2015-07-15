package com.emergentorganization.cellrpg.components;

import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.glutils.ShapeRenderer;
import com.emergentorganization.cellrpg.components.BaseComponent;
import com.emergentorganization.cellrpg.components.entity.ComponentType;
import com.emergentorganization.cellrpg.scenes.Scene;

/**
 * Created by OrelBitton on 23/06/2015.
 */
public class GlobalComponent implements BaseComponent{
    //TODO

    private boolean enabled = false;
    private Scene scene;

    @Override
    public void added() {

    }

    @Override
    public void update(float deltaTime) {

    }

    @Override
    public void render(SpriteBatch batch) {

    }

    public void onToggle(boolean enabled){}

    public void setEnabled(boolean enabled){
        this.enabled = enabled;
        onToggle(enabled);
    }

    public boolean isEnabled() {
     return enabled;
    }

    @Override
    public boolean shouldRender() {
        return false;
    }

    @Override
    public void debugRender(ShapeRenderer renderer) {

    }

    @Override
    public boolean shouldDebugRender() {
        return false;
    }

    public void setScene(Scene scene){
        this.scene = scene;
    }

    protected Scene getScene(){
        return scene;
    }

    @Override
    public ComponentType getType() {
        return null;
    }

    @Override
    public void dispose() {

    }

}
