package io.github.emergentorganization.cellrpg.core.entityfactory.builder.componentbuilder;

import com.artemis.Aspect;
import com.artemis.Component;
import com.artemis.Entity;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.physics.box2d.BodyDef;
import com.badlogic.gdx.physics.box2d.FixtureDef;
import io.github.emergentorganization.cellrpg.managers.PhysicsSystem;
import io.github.emergentorganization.engine.components.*;


public class PhysicsBodyBuilder extends BaseComponentBuilder {
    private final PhysicsSystem physicsSystem;
    private final Vector2 velocity = new Vector2(0, 0);
    private final Vector2 position = new Vector2(0, 0);

    private boolean allowSleep = true;
    private BodyDef.BodyType bodyType = BodyDef.BodyType.DynamicBody;
    private boolean fixedRotation = false;
    private float angleRad = 0f;
    private float density = 1.0f;
    private float friction = 0.0f;
    private float restitution = 0.1f;
    private boolean isBullet = false;
    private Vector2 rectSize;

    public PhysicsBodyBuilder(PhysicsSystem physicsSystem) {
        super(Aspect.all(Bounds.class, PhysicsBody.class, Name.class), 0);
        this.physicsSystem = physicsSystem;
    }

    /**
     * Default BodyType is Dynamic
     *
     * @param bodyType If static, most physics parameters are useless
     */
    public PhysicsBodyBuilder bodyType(BodyDef.BodyType bodyType) {
        this.bodyType = bodyType;
        return this;
    }

    /**
     * Default allows full 2D rotation
     */
    public PhysicsBodyBuilder setFixedRotation(boolean fixed) {
        fixedRotation = fixed;
        return this;
    }

    /**
     * Default angle is zero
     */
    public PhysicsBodyBuilder setAngleRad(float angleRad) {
        this.angleRad = angleRad;
        return this;
    }

    public PhysicsBodyBuilder velocity(Vector2 vel) {
        this.velocity.set(vel);
        return this;
    }

    public PhysicsBodyBuilder velocity(float speed, Vector2 dir) {
        this.velocity.set(dir).scl(speed);
        return this;
    }

    public PhysicsBodyBuilder position(float x, float y) {
        this.position.set(x, y);
        return this;
    }

    public PhysicsBodyBuilder position(Vector2 pos) {
        this.position.set(pos);
        return this;
    }

    /**
     * Default body density is 1 kg/m^2 (1.0f)
     */
    public PhysicsBodyBuilder bodyDensity(float density) {
        this.density = density;
        return this;
    }

    /**
     * Default is to have no friction
     */
    public PhysicsBodyBuilder bodyFriction(float friction) {
        this.friction = friction;
        return this;
    }

    /**
     * Default is 0.1f (low elasticity)
     */
    public PhysicsBodyBuilder bodyRestitution(float restitution) {
        this.restitution = restitution;
        return this;
    }

    public PhysicsBodyBuilder bullet(boolean bullet) {
        isBullet = bullet;
        return this;
    }

    public PhysicsBodyBuilder boundsBody(Vector2 rectSize) {
        this.rectSize = rectSize;
        return this;
    }

    /**
     * Default is to allow sleeping
     */
    public PhysicsBodyBuilder denySleep() {
        allowSleep = false;
        return this;
    }

    @Override
    public void build(Entity entity) {
        super.build(entity);

        BodyDef bDef = new BodyDef();
        bDef.bullet = isBullet;
        bDef.allowSleep = allowSleep;
        bDef.type = bodyType;
        bDef.fixedRotation = fixedRotation;
        if (position.len() != 0.0f) {
            bDef.position.set(position);
        } else {
            bDef.position.set(entity.getComponent(Position.class).position);
        }
        if (velocity.len() != 0.0f) {
            bDef.linearVelocity.set(velocity);
        } else {
            bDef.linearVelocity.set(entity.getComponent(Velocity.class).velocity);
        }
        bDef.angle = angleRad;
        FixtureDef fDef = new FixtureDef();
        fDef.density = density;
        fDef.friction = friction;
        fDef.restitution = restitution;
        if (rectSize != null) {
            Bounds b = entity.getComponent(Bounds.class);
            b.width = rectSize.x;
            b.height = rectSize.y;
            physicsSystem.createBoundsBody(entity.getId(), bDef, fDef);
        } else {
            physicsSystem.createBody(entity.getId(), entity.getComponent(Name.class).internalID, bDef, fDef);
        }
    }

    @Override
    public Class<? extends Component> getComponentClass() {
        return PhysicsBody.class;
    }
}
