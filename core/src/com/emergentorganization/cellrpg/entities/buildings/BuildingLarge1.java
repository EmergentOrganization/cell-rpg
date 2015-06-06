package com.emergentorganization.cellrpg.entities.buildings;

import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.emergentorganization.cellrpg.components.BaseComponent;
import com.emergentorganization.cellrpg.components.ComponentType;
import com.emergentorganization.cellrpg.components.PhysicsComponent;
import com.emergentorganization.cellrpg.components.SpriteComponent;
import com.emergentorganization.cellrpg.components.messages.BaseComponentMessage;
import com.emergentorganization.cellrpg.entities.Entity;
import com.emergentorganization.cellrpg.physics.Tag;
import com.emergentorganization.cellrpg.tools.BodyLoader;
import org.dyn4j.dynamics.Body;
import org.dyn4j.dynamics.World;

import java.util.ArrayList;

/**
 * Created by BrianErikson on 6/5/2015.
 */
public class BuildingLarge1 extends Entity {
    public static final String ID = "building-large-1";
    Texture texture;

    public BuildingLarge1() {
        texture = new Texture(ID + ".png");
        addComponent(new SpriteComponent(texture));

    }

    @Override
    public void added() {
        super.added();

        World world = getScene().getWorld();
        Body body = BodyLoader.fetch().generateBody(ID, texture.getWidth());
        PhysicsComponent phys = new PhysicsComponent(world, body, Tag.BUILDING_LARGE_1);
        phys.enableDebugRenderer(true);
        addComponent(phys);
    }

    @Override
    public void update(float deltaTime) {
        super.update(deltaTime);
    }

    @Override
    public void render(SpriteBatch batch) {
        super.render(batch);
    }

    @Override
    public ArrayList<BaseComponent> getComponentsByType(ComponentType type) {
        return super.getComponentsByType(type);
    }

    @Override
    public void broadcastMessage(BaseComponentMessage message) {
        super.broadcastMessage(message);
    }

    @Override
    public void broadcastMessage(ComponentType type, BaseComponentMessage message) {
        super.broadcastMessage(type, message);
    }

    @Override
    public void dispose() {
        super.dispose();
    }
}
