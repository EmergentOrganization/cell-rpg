package com.emergentorganization.cellrpg.core.entityfactory;

import com.artemis.Entity;
import com.artemis.World;
import com.badlogic.gdx.math.MathUtils;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.physics.box2d.BodyDef;
import com.badlogic.gdx.physics.box2d.FixtureDef;
import com.emergentorganization.cellrpg.managers.BodyManager;

/**
 * Created by brian on 11/20/15.
 */
public class PhysicsBuilder {
    private final World world;
    private final Entity entity;
    private final Vector2 position;
    private final String entityId;

    private boolean allowSleep = true;
    private BodyDef.BodyType bodyType = BodyDef.BodyType.DynamicBody;
    private boolean fixedRotation = false;
    private float angleRad = 0f;
    private float density = 1.0f;
    private float friction = 0.0f;
    private float resitution = 0.1f;

    public PhysicsBuilder(World world, Entity entity, Vector2 position, String entityId) {
        this.world = world;
        this.entity = entity;
        this.position = position;
        this.entityId = entityId;
    }

    /**
     * Default is to allow sleeping
     */
    public PhysicsBuilder denySleep() {
        allowSleep = false;
        return this;
    }

    /**
     * Default BodyType is Dynamic
     */
    public PhysicsBuilder bodyType(BodyDef.BodyType bodyType) {
        this.bodyType = bodyType;
        return this;
    }

    /**
     * Default allows full 2D rotation
     */
    public PhysicsBuilder setFixedRotation(boolean fixed) {
        fixedRotation = fixed;
        return this;
    }

    /**
     * Default angle is zero
     */
    public PhysicsBuilder setAngleRad(float angleRad) {
        this.angleRad = angleRad;
        return this;
    }

    /**
     * Default angle is zero
     */
    public PhysicsBuilder setAngle(float angleDeg) {
        this.angleRad = MathUtils.degreesToRadians * angleDeg;
        return this;
    }

    /**
     * Default body density is 1 kg/m^2 (1.0f)
     */
    public PhysicsBuilder bodyDensity(float density) {
        this.density = density;
        return this;
    }

    /**
     * Default is to have no friction
     */
    public PhysicsBuilder bodyFriction(float friction) {
        this.friction = friction;
        return this;
    }

    /**
     * Default is 0.1f (low elasticity)
     */
    public PhysicsBuilder bodyResitution(float resitution) {
        this.resitution = resitution;
        return this;
    }

    public void build() {
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
