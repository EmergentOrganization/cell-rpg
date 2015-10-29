package com.emergentorganization.cellrpg.core;

import com.artemis.Archetype;
import com.artemis.ArchetypeBuilder;
import com.artemis.Entity;
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
    public static float SCALE_BOX_TO_WORLD = 32f;
    public static float SCALE_WORLD_TO_BOX = 0.03125f;
    private final com.artemis.World world;
    public Archetype base;
    public Archetype object;
    public Archetype collidable;
    public Archetype physical;
    public Archetype character;

    public EntityFactory(com.artemis.World world) {
        this.world = world;
        base = new ArchetypeBuilder().add(Position.class).build(world);
        object = new ArchetypeBuilder(base).add(Visual.class).add(Rotation.class).add(Scale.class).build(world);
        collidable = new ArchetypeBuilder(object).add(PhysicsBody.class).build(world);
        physical = new ArchetypeBuilder(collidable).add(Velocity.class).build(world);
        character = new ArchetypeBuilder(physical).build(world);
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

        final Entity player = world.createEntity(physical);
        player.getComponent(Visual.class).id = assets[0];
        player.getComponent(Position.class).position.set(x, y);

        /*
        TextureAtlas atlas = PixelonTransmission.fetch().getTextureAtlas();
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
        */
        final float scale = world.getSystem(AssetManager.class).
                            getRegion(assets[0]).getRegionWidth() * SCALE_WORLD_TO_BOX;
        player.getComponent(Scale.class).scale = scale;

        BodyDef bDef = new BodyDef();
        bDef.allowSleep = false;
        bDef.type = BodyDef.BodyType.DynamicBody;
        bDef.fixedRotation = true;
        bDef.position.set(x, y);
        FixtureDef fDef = new FixtureDef();
        fDef.density = 1.0f;
        fDef.friction = 0.3f;
        fDef.restitution = 0.1f;
        world.getSystem(BodyManager.class).createBody(player.getId(), ID, bDef, fDef);

        return player.getId();
    }
}
