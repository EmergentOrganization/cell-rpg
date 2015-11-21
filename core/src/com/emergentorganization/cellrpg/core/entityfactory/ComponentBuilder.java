package com.emergentorganization.cellrpg.core.entityfactory;

import com.artemis.Entity;
import com.artemis.World;
import com.artemis.managers.TagManager;
import com.badlogic.gdx.graphics.g2d.Animation;
import com.badlogic.gdx.math.MathUtils;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.physics.box2d.BodyDef;
import com.badlogic.gdx.physics.box2d.FixtureDef;
import com.emergentorganization.cellrpg.components.*;
import com.emergentorganization.cellrpg.core.RenderIndex;
import com.emergentorganization.cellrpg.managers.AssetManager;
import com.emergentorganization.cellrpg.managers.BodyManager;

import java.util.List;

/**
 * Created by brian on 11/21/15.
 */
public class ComponentBuilder {
    // REQUIRED
    private final World world;
    private final Entity entity;
    private final Vector2 position;
    private final String entityId;

    // PHYSICS
    private boolean allowSleep = true;
    private BodyDef.BodyType bodyType = BodyDef.BodyType.DynamicBody;
    private boolean fixedRotation = false;
    private float angleRad = 0f;
    private float density = 1.0f;
    private float friction = 0.0f;
    private float resitution = 0.1f;

    // MISC
    private String tag = null;
    private boolean isAnimation = false;
    private float frameDuration;
    private List<String> frames;
    private Animation.PlayMode playMode = Animation.PlayMode.LOOP_PINGPONG;
    private String texturePath = null;
    private float scale = EntityFactory.SCALE_WORLD_TO_BOX;
    private float angleDeg = 0f;
    private RenderIndex renderIndex = RenderIndex.BUILDING;
    private Vector2 velocity = new Vector2();


    public ComponentBuilder(World world, Entity entity, String entityId, Vector2 position) {
        this.world = world;
        this.entity = entity;
        this.position = position;
        this.entityId = entityId;
    }

    public ComponentBuilder tag(String tag) {
        this.tag = tag;
        return this;
    }

    public ComponentBuilder animation(List<String> frames, Animation.PlayMode playMode, float frameDuration) {
        if (texturePath != null)
            throw new RuntimeException("ERROR: Cannot define both an animation and a texture");
        this.isAnimation = true;
        this.frames = frames;
        this.playMode = playMode;
        this.frameDuration = frameDuration;
        return this;
    }

    /**
     * Defaults to SCALE_WORLD_TO_BOX
     */
    public ComponentBuilder scale(float scale) {
        this.scale = scale;
        return this;
    }

    public ComponentBuilder angle(float angleDeg) {
        this.angleDeg = angleDeg;
        return this;
    }

    public ComponentBuilder texture(String texturePath) {
        if (frames != null)
            throw new RuntimeException("ERROR: Cannot define both a texture and an animation");
        this.texturePath = texturePath;
        return this;
    }

    /**
     * Defaults to RenderIndex.BUILDING
     */
    public ComponentBuilder renderIndex(RenderIndex renderIndex) {
        this.renderIndex = renderIndex;
        return this;
    }

    public ComponentBuilder velocity(Vector2 vel) {
        this.velocity.set(vel);
        return this;
    }

    public ComponentBuilder velocity(float speed, Vector2 dir) {
        this.velocity.set(dir).scl(speed);
        return this;
    }

    /**
     * Default is to allow sleeping
     */
    public ComponentBuilder denySleep() {
        allowSleep = false;
        return this;
    }

    /**
     * Default BodyType is Dynamic
     */
    public ComponentBuilder bodyType(BodyDef.BodyType bodyType) {
        this.bodyType = bodyType;
        return this;
    }

    /**
     * Default allows full 2D rotation
     */
    public ComponentBuilder setFixedRotation(boolean fixed) {
        fixedRotation = fixed;
        return this;
    }

    /**
     * Default angle is zero
     */
    public ComponentBuilder setAngleRad(float angleRad) {
        this.angleRad = angleRad;
        return this;
    }

    /**
     * Default body density is 1 kg/m^2 (1.0f)
     */
    public ComponentBuilder bodyDensity(float density) {
        this.density = density;
        return this;
    }

    /**
     * Default is to have no friction
     */
    public ComponentBuilder bodyFriction(float friction) {
        this.friction = friction;
        return this;
    }

    /**
     * Default is 0.1f (low elasticity)
     */
    public ComponentBuilder bodyResitution(float resitution) {
        this.resitution = resitution;
        return this;
    }

    public void build() {
        if (tag != null) {
            world.getSystem(TagManager.class).register(tag, entity);
        }

        Visual v = entity.getComponent(Visual.class);
        if (v != null) {
            v.index = renderIndex;

            if (isAnimation) {
                v.setAnimation(entityId);
                Animation animation = world.getSystem(AssetManager.class).defineAnimation(
                        entityId,
                        frameDuration,
                        frames.toArray(new String[frames.size()]),
                        playMode
                );
                entity.getComponent(Bounds.class).setFromRegion(animation.getKeyFrames()[0]);
            } else if (texturePath != null){
                v.setTexture(texturePath);
                entity.getComponent(Bounds.class).setFromRegion(
                        world.getSystem(AssetManager.class).getRegion(texturePath)
                );
            } else {
                throw new RuntimeException("ERROR: Need to set a texture or animation on entity " + entityId);
            }
        }

        entity.getComponent(Position.class).position.set(position);
        Scale sc = entity.getComponent(Scale.class);
        if (sc != null) {
            sc.scale = scale; // player ends up being 1 meter in size
        }

        Rotation rc = entity.getComponent(Rotation.class);
        if (rc != null) {
            rc.angle = angleDeg;
        }

        Velocity vc = entity.getComponent(Velocity.class);
        if (vc != null) {
            vc.velocity.set(velocity);
        }
        
        // Physics
        PhysicsBody pb = entity.getComponent(PhysicsBody.class);
        if (pb != null) {
            BodyDef bDef = new BodyDef();
            bDef.allowSleep = allowSleep;
            bDef.type = bodyType;
            bDef.fixedRotation = fixedRotation;
            bDef.position.set(position);
            bDef.angle = angleRad;
            FixtureDef fDef = new FixtureDef();
            fDef.density = density;
            fDef.friction = friction;
            fDef.restitution = resitution;
            world.getSystem(BodyManager.class).createBody(entity.getId(), entityId, bDef, fDef);
        }
    }
}
