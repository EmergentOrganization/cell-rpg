package com.emergentorganization.cellrpg.core;

import com.artemis.Archetype;
import com.artemis.ArchetypeBuilder;
import com.artemis.Entity;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.physics.box2d.Body;
import com.badlogic.gdx.physics.box2d.BodyDef;
import com.badlogic.gdx.physics.box2d.FixtureDef;
import com.badlogic.gdx.physics.box2d.World;
import com.emergentorganization.cellrpg.components.*;
import com.emergentorganization.cellrpg.managers.AssetManager;
import com.emergentorganization.cellrpg.managers.BodyManager;
import com.emergentorganization.cellrpg.tools.physics.BodyEditorLoader;

/**
 * Created by brian on 10/28/15.
 */
public class EntityFactory {
    public static float SCALE_BOX_TO_WORLD = 40f;
    public static float SCALE_WORLD_TO_BOX = 0.025f;
    private final com.artemis.World world;

    public Archetype base;
    public Archetype object;
    public Archetype collidable;
    public Archetype physical;
    public Archetype character;
    private Archetype player;

    public EntityFactory(com.artemis.World world) {
        this.world = world;
        base = new ArchetypeBuilder().add(Position.class).build(world);
        object = new ArchetypeBuilder(base).add(Visual.class).add(Rotation.class).add(Scale.class).build(world);
        collidable = new ArchetypeBuilder(object).add(PhysicsBody.class).build(world);
        physical = new ArchetypeBuilder(collidable).add(Velocity.class).build(world);
        character = new ArchetypeBuilder(physical).add(Health.class).build(world);
        player = new ArchetypeBuilder(character).add(Input.class).build(world);
    }

    public int createPlayer(float x, float y) {
        final String ID = "char-player";
        final float TPF = 0.2f;  // time per frame of animation
        final String[] assets = new String[] {
                "game/char-player/0",
                "game/char-player/1",
                "game/char-player/2",
                "game/char-player/3",
                "game/char-player/4",
                "game/char-player/5",
                "game/char-player/6",
                "game/char-player/7",
                "game/char-player/8",
                "game/char-player/9"
        };

        final Entity player = world.createEntity(this.player);

        player.getComponent(Visual.class).setAnimation("player");
        player.getComponent(Position.class).position.set(x, y);
        player.getComponent(Scale.class).scale = SCALE_WORLD_TO_BOX; // player ends up being 1 meter in size

        BodyDef bDef = new BodyDef();
        bDef.allowSleep = true;
        bDef.type = BodyDef.BodyType.DynamicBody;
        bDef.fixedRotation = true;
        bDef.position.set(x, y);
        FixtureDef fDef = new FixtureDef();
        fDef.density = 1.0f;
        fDef.friction = 0.3f;
        fDef.restitution = 0.1f;
        world.getSystem(BodyManager.class).createBody(player.getId(), ID, bDef, fDef);

        Input ic = player.getComponent(Input.class);
        ic.speed = 2f; // 2 meters per sec // a dedicated component?

        return player.getId();
    }
}
