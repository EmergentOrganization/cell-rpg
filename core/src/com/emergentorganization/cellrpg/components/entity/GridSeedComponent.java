package com.emergentorganization.cellrpg.components.entity;

import com.badlogic.gdx.graphics.glutils.ShapeRenderer;
import com.badlogic.gdx.math.Vector2;
import com.emergentorganization.cellrpg.components.EntityComponent;
import com.emergentorganization.cellrpg.entities.ca.NoBufferCAGrid;
import com.emergentorganization.cellrpg.scenes.CALayer;
import com.emergentorganization.cellrpg.scenes.Scene;
import com.emergentorganization.cellrpg.scenes.CAScene;

/**
 * Created by 7yl4r 2015-08-06.
 * component used to seed states into CAScene CA layers from entities.
 */
public class GridSeedComponent extends EntityComponent {
    private MovementComponent moveComponent;
    private boolean rendering = false;

    private boolean reseed;
    private int reseedPeriod;
    private Vector2 seedPos;
    private CALayer seedLayer;

    private int[][] seedPattern;
    private long lastSeededGeneration = 0;

    public GridSeedComponent(int[][] seed_pattern, int reseed_period, Vector2 seed_pos, CALayer seed_layer) {
        /*
        :param seed_pattern:  2d pattern to stamp
        :param reseed_period: period of time between stampings
        :param seed_pos:      relative position of stamp from entity origin
        :param seed_layer:    CA grid layer to seed into
         */

        if (reseed_period < 1){
            reseed = false;
        } else {
            reseed = true;
            reseedPeriod = reseed_period;
        }

        seedPattern = seed_pattern;
        seedPos = seed_pos;
        seedLayer = seed_layer;
    }

    @Override
    public void debugRender(ShapeRenderer renderer) {
        super.debugRender(renderer);
        renderer.set(ShapeRenderer.ShapeType.Filled);
        // TODO: draw bounding box(es) and seeded area
    }

    @Override
    public boolean shouldDebugRender() {
        return rendering;
    }

    public void enableDebugRenderer(){
        enableDebugRenderer(true);
    }
    public void enableDebugRenderer(boolean on) {
        rendering = on;
    }

    @Override
    public void added() {
        super.added();

        moveComponent = getFirstSiblingByType(MovementComponent.class);

    }

    @Override
    public void update(float deltaTime) {
        super.update(deltaTime);

        Vector2 pos = moveComponent.getWorldPosition();
        try {
            seedIt();
        } catch(RuntimeException err){
            // non-CAGrid scene. no problem.
            return;
        }

        // moveComponent.getRotationRad();
    }

    private NoBufferCAGrid getGrid(){
        Scene scene = getEntity().getScene();
        if (scene instanceof CAScene) {
            CAScene caScene = (CAScene) scene;
            return caScene.getLayer(seedLayer);
        } else {
            throw new RuntimeException("Returned scene is not a CAScene");
        }
    }

    private void seedIt(){
        // puts the seed into the grid
        NoBufferCAGrid seedGrid = getGrid();
        long currentGen = seedGrid.generation;
        if ( currentGen - lastSeededGeneration >= reseedPeriod){
            lastSeededGeneration = currentGen;
            // TODO: use position relative to Entity here:
            Vector2 pos = moveComponent.getWorldPosition();// + seedPos.x;
            seedGrid.stampState(seedPattern, pos);
        }
    }

    @Override
    public void dispose() {
        super.dispose();
    }
}
