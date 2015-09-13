package com.emergentorganization.cellrpg.entities.buildings;

import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.math.Vector2;
import com.emergentorganization.cellrpg.components.entity.GridSeedComponent;
import com.emergentorganization.cellrpg.components.entity.MovementComponent;
import com.emergentorganization.cellrpg.components.entity.PhysicsComponent;
import com.emergentorganization.cellrpg.components.entity.SpriteComponent;
import com.emergentorganization.cellrpg.entities.CAGrid;
import com.emergentorganization.cellrpg.entities.Entity;
import com.emergentorganization.cellrpg.entities.ZIndex;
import com.emergentorganization.cellrpg.physics.Tag;
import com.emergentorganization.cellrpg.scenes.CALayer;
import com.emergentorganization.cellrpg.scenes.CAScene;
import com.emergentorganization.cellrpg.scenes.Scene;
import com.emergentorganization.cellrpg.tools.physics.BodyLoader;

/**
 * Created by BrianErikson on 6/12/2015.
 */
public class VyroidGenerator extends Entity {
    public static final String ID = "vyroid-generator";
    Texture texture;
    int genSinceLastSpawn = 0;
    final int GEN_BTWN_SPAWNS = 1;  // generations between spawn drop-ins
    final int[][] pattern = {  // pre-pulsar
            {0,1,0,0,0,0,0,1,0},
            {1,1,1,0,0,0,1,1,1}
    };
    final float MAX_SPAWN_DIST = 500;  // max distance away from building to spawn

    /*
    This constructor is needed for MapEditor. Do not remove.
     */
    public VyroidGenerator() {
        super(ZIndex.BUILDING);
        texture = new Texture(ID + ".png");
        addComponent(new SpriteComponent(texture));
    }

    public VyroidGenerator(Texture texture, Vector2 position) {
        super(ZIndex.BUILDING);
        this.texture = texture;
        getFirstComponentByType(MovementComponent.class).setWorldPosition(position);

        addComponent(new SpriteComponent(texture));
    }

    @Override
    public void update(float deltaTime){
        super.update(deltaTime);

        if (genSinceLastSpawn > GEN_BTWN_SPAWNS) {
            // add randomly generated spawn
            try {
                CAScene scen = (CAScene) getScene();
                MovementComponent mc = getFirstComponentByType(MovementComponent.class);
                float x = mc.getWorldPosition().x + (float) (Math.random() - .5) * MAX_SPAWN_DIST;
                float y = mc.getWorldPosition().y + (float) (Math.random() - .5) * MAX_SPAWN_DIST;
                CAGrid layr = scen.getLayer(CALayer.VYROIDS);
                layr.stampState(pattern, x, y);
            } catch(ClassCastException err){
                // scene is not CSScene
                return;
            }
            genSinceLastSpawn = 0;
        } else {
            genSinceLastSpawn += 1;
        }

    }

    @Override
    public void added() {
        super.added();

        float scale = texture.getWidth() * Scene.scale;
        PhysicsComponent phys = new PhysicsComponent(getScene().getWorld(),
                BodyLoader.fetch().generateBody(ID, scale), Tag.STATIC);
        addComponent(phys);
    }
}
