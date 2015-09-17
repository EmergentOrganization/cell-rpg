package com.emergentorganization.cellrpg.entities.characters;

import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.Animation;
import com.badlogic.gdx.graphics.g2d.TextureAtlas;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.emergentorganization.cellrpg.CellRpg;
import com.emergentorganization.cellrpg.components.entity.GraphicsComponent;
import com.emergentorganization.cellrpg.entities.Entity;
import com.emergentorganization.cellrpg.entities.ZIndex;

/**
 * Created by tylar on 6/2/15.
 * used for NPCs & players
 */
public class Character extends Entity {
    private GraphicsComponent graphicsComponent;

    public Character(Texture texture, int n_columns, int n_rows, float time_per_frame) {
        super(ZIndex.CHARACTER);
        graphicsComponent = new GraphicsComponent();
        graphicsComponent.register("idle", texture, n_columns, n_rows, time_per_frame);
        graphicsComponent.play("idle");
        addComponent(this.graphicsComponent);
    }

    public Character(String textureFileName){
        super(ZIndex.CHARACTER);
        graphicsComponent = new GraphicsComponent();
        graphicsComponent.register("idle", textureFileName);
        graphicsComponent.play("idle");
        addComponent(this.graphicsComponent);
    }

    public Character(String[] textureNames, float time_per_frame) {
        super(ZIndex.CHARACTER);
        graphicsComponent = new GraphicsComponent();
        graphicsComponent.register("idle", new Animation(time_per_frame, getTextureRegions(textureNames)));
        graphicsComponent.play("idle");
        addComponent(this.graphicsComponent);
    }

    public Character(String sheetFileName, int n_columns, int n_rows, float time_per_frame) {
        super(ZIndex.CHARACTER);
        graphicsComponent = new GraphicsComponent();
        graphicsComponent.register("idle", sheetFileName, n_columns, n_rows, time_per_frame);
        graphicsComponent.play("idle");
        addComponent(this.graphicsComponent);
    }

    public GraphicsComponent getGraphicsComponent() {
        return graphicsComponent;
    }

    public static TextureRegion[] getTextureRegions(String[] textureNames) {
        TextureAtlas atlas = CellRpg.fetch().getTextureAtlas();

        TextureRegion[] regions = new TextureRegion[textureNames.length];
        for (int i = 0; i < textureNames.length; i++) {
            regions[i] = atlas.findRegion(textureNames[i]);
        }

        return regions;
    }
}
