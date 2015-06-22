package com.emergentorganization.cellrpg.scenes;

import com.emergentorganization.cellrpg.physics.listeners.PlayerCollisionListener;
import com.emergentorganization.cellrpg.tools.mapeditor.map.Map;
import com.emergentorganization.cellrpg.tools.mapeditor.map.MapTools;
import org.dyn4j.geometry.Vector2;

/**
 * Created by Brian on 6/22/2015.
 */
public class CustomMap extends Scene {
    private String mapName;

    public CustomMap(String mapName) {
        this.mapName = mapName;
    }

    @Override
    public void create() {
        super.create();

        getWorld().setGravity(new Vector2(0, 0)); // defaults to -9.8 m/s
        getWorld().addListener(new PlayerCollisionListener()); // stops player from clipping through colliders
        Map map = MapTools.importMap(mapName);
        this.addEntities(map.getEntities());
    }

    /**
     * Updates all entities, and then renders all entities
     *
     * @param delta
     */
    @Override
    public void render(float delta) {
        super.render(delta);

        drawUI();
    }

    @Override
    public void hide() {

    }
}
