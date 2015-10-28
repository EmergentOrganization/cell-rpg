package com.emergentorganization.cellrpg.artemis.core;

import com.artemis.Archetype;
import com.artemis.ArchetypeBuilder;
import com.artemis.Entity;
import com.badlogic.gdx.graphics.g2d.*;
import com.badlogic.gdx.physics.box2d.Body;
import com.badlogic.gdx.physics.box2d.BodyDef;
import com.badlogic.gdx.physics.box2d.FixtureDef;
import com.badlogic.gdx.physics.box2d.World;
import com.badlogic.gdx.utils.Array;
import com.emergentorganization.cellrpg.CellRpg;
import com.emergentorganization.cellrpg.artemis.components.*;
import com.emergentorganization.cellrpg.tools.physics.BodyEditorLoader;

/**
 * Created by brian on 10/28/15.
 */
public class EntityFactory {
    public static float SCALE_BOX_TO_WORLD = 32f;
    public static float SCALE_WORLD_TO_BOX = 0.03125f;
    private final com.artemis.World world;
    private BodyEditorLoader bodyEditorLoader;
    public Archetype base;
    public Archetype object;
    public Archetype collidable;
    public Archetype physical;
    public Archetype character;

    public EntityFactory(com.artemis.World world, BodyEditorLoader bodyEditorLoader) {
        this.world = world;
        this.bodyEditorLoader = bodyEditorLoader;
        base = new ArchetypeBuilder().add(Position.class).build(world);
        object = new ArchetypeBuilder(base).add(Visual.class).add(Rotation.class).build(world);
        collidable = new ArchetypeBuilder(object).add(Collider.class).build(world);
        physical = new ArchetypeBuilder(collidable).add(Velocity.class).build(world);
        character = new ArchetypeBuilder(physical).build(world);
    }

    public Entity createPlayer(World physWorld, float x, float y) {
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

        final Entity player = world.createEntity(physical);
        player.getComponent(Position.class).position.set(x, y);

        TextureAtlas atlas = CellRpg.fetch().getTextureAtlas();
        player.getComponent(Visual.class).id = "player:player-walk";
        Array<TextureRegion> regions = new Array<TextureRegion>();
        for (String asset : assets) {
            regions.add(atlas.findRegion(asset));
        }

        com.badlogic.gdx.graphics.g2d.Animation animation = new com.badlogic.gdx.graphics.g2d.Animation(
                TPF,
                regions,
                com.badlogic.gdx.graphics.g2d.Animation.PlayMode.LOOP_PINGPONG
        );

        final float scale = animation.getKeyFrames()[0].getRegionWidth() * SCALE_WORLD_TO_BOX;
        player.getComponent(Scale.class).scale = scale;

        BodyDef bDef = new BodyDef();
        bDef.allowSleep = false;
        bDef.type = BodyDef.BodyType.DynamicBody;
        bDef.fixedRotation = true;
        bDef.position.set(x, y);
        Body body = physWorld.createBody(bDef);
        FixtureDef fDef = new FixtureDef();
        fDef.density = 1.0f;
        fDef.friction = 0.3f;
        fDef.restitution = 0.1f;
        bodyEditorLoader.attachFixture(body, ID, fDef, scale);
        player.getComponent(Collider.class).body = body;

        return player;
    }
}
