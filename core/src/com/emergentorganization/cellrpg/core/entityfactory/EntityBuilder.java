package com.emergentorganization.cellrpg.core.entityfactory;

import com.artemis.Archetype;
import com.artemis.Entity;
import com.artemis.World;
import com.artemis.managers.TagManager;
import com.badlogic.gdx.graphics.g2d.Animation;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.physics.box2d.BodyDef;
import com.badlogic.gdx.physics.box2d.FixtureDef;
import com.emergentorganization.cellrpg.components.*;
import com.emergentorganization.cellrpg.components.SpontaneousGeneration.SpontaneousGenerationList;
import com.emergentorganization.cellrpg.core.RenderIndex;
import com.emergentorganization.cellrpg.managers.AssetManager;
import com.emergentorganization.cellrpg.managers.PhysicsSystem;

import java.util.List;

/**
 * Created by brian on 11/21/15.
 */
public class EntityBuilder {
    // ==================================================================
    // ================= ENTITY COMPONENT PROPERTIES ====================
    // === these are the properties to be added to the entity which   ===
    // === should be modified using their homonym-ous setter methods. ===
    // === Default values should be set here.                         ===
    // ==================================================================
    // REQUIRED
    private final World world;
    private final Vector2 position;
    private final String entityId;
    private final Archetype archetype;
    private final String friendlyName;

    // PHYSICS
    private boolean allowSleep = true;
    private BodyDef.BodyType bodyType = BodyDef.BodyType.DynamicBody;
    private boolean fixedRotation = false;
    private float angleRad = 0f;
    private float density = 1.0f;
    private float friction = 0.0f;
    private float restitution = 0.1f;

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
    private boolean isBullet = false;
    private long timeToDestruction = -1;
    private float speed = 1f;
    private int health = Integer.MAX_VALUE;  // still destructible TODO: fix?
    private int maxHealth = -1;  // defaults to full health unless other given.
    private float spawnFieldRadius = 0;
    private float spontGenRadius = 0;
    private int collideDamage = 0;
    private int collideSelfDamage = 0;
    private float maxDistanceFromPlayer = 20f;  // set to -1 for infinite distance

    // ==================================================================
    // ==================================================================

    public EntityBuilder(World world, Archetype archetype, String friendlyName, String entityId, Vector2 position) {
        this.world = world;
        this.archetype = archetype;
        this.position = position;
        this.entityId = entityId;
        this.friendlyName = friendlyName;
    }


    // ==================================================================
    // ====================== BUILD METHOD ==============================
    // ===    Called at the end of the builder chain, this method     ===
    // ===    actually builds and returns the entity using the        ===
    // ===    properties specified using the setter methods.          ===
    // ==================================================================

    public Entity build() {
        // called at the end of the builder chain, this actually builds and returns the entity
        // using the properties specified using the setter methods.
        Entity entity = world.createEntity(archetype);

        buildName(entity);
        buildLifecycle(entity);

        if (tag != null) {
            world.getSystem(TagManager.class).register(tag, entity);
        }

        buildVisual(entity);

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

        buildPhysicsBody(entity);
        buildDestructionTimer(entity);
        buildInput(entity);
        buildHealth(entity);
        buildSpontGen(entity);
        buildSpawnField(entity);
        buildCollideEffect(entity);

        return entity;
    }

    // === BUILD METHOD SUB-ROUTINES ===
    // ==================================================================
    // =================== BUILD METHOD SUB-ROUTINES ====================
    // === Build method sub-routines set up a particular component's  ===
    // === properties on the given entity. Generally, should check    ===
    // === that component exists on given entity first.               ===
    // ==================================================================

    private void buildLifecycle(Entity entity){
        entity.getComponent(Lifecycle.class).maxPlayerDist = maxDistanceFromPlayer;
    }

    private void buildCollideEffect(Entity entity){
        CollideEffect eff = entity.getComponent(CollideEffect.class);
        if (eff != null) {
            eff.damage = collideDamage;
            eff.selfDamage = collideSelfDamage;
        }
    }

    private void buildSpontGen(Entity entity){
        SpontaneousGenerationList genList = entity.getComponent(SpontaneousGenerationList.class);
        if (genList != null)
            genList.radius = spontGenRadius;
    }

    private void buildSpawnField(Entity entity){
        CollectibleSpawnField spawnField = entity.getComponent(CollectibleSpawnField.class);
        if (spawnField != null)
            spawnField.radius = spawnFieldRadius;
    }

    private void buildHealth(Entity entity){
        Health heal = entity.getComponent(Health.class);
        if(heal != null) {
            heal.health = health;

            if (maxHealth > -1){
                heal.maxHealth = maxHealth;
            } else {
                heal.maxHealth = health;
            }
        }
    }

    private void buildInput(Entity entity){
        InputComponent ic = entity.getComponent(InputComponent.class);
        if (ic != null)
            ic.speed = speed; // 2 meters per sec // a dedicated component?
    }

    private void buildName(Entity entity){
        Name name = entity.getComponent(Name.class);
        name.friendlyName = friendlyName;
        name.internalID = entityId;
    }

    private void buildVisual(Entity entity){
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
    }

    private void buildPhysicsBody(Entity entity){
        // === Physics
        PhysicsBody pb = entity.getComponent(PhysicsBody.class);
        if (pb != null) {
            BodyDef bDef = new BodyDef();
            bDef.bullet = isBullet;
            bDef.allowSleep = allowSleep;
            bDef.type = bodyType;
            bDef.fixedRotation = fixedRotation;
            bDef.position.set(position);
            bDef.linearVelocity.set(velocity);
            bDef.angle = angleRad;
            FixtureDef fDef = new FixtureDef();
            fDef.density = density;
            fDef.friction = friction;
            fDef.restitution = restitution;
            world.getSystem(PhysicsSystem.class).createBody(entity.getId(), entityId, bDef, fDef);
        }
    }

    private void buildDestructionTimer(Entity ent){
        destructionTimer destTimer = ent.getComponent(destructionTimer.class);
        if (destTimer != null){
            destTimer.timeToDestruction = timeToDestruction;
        }
    }

    // ==================================================================
    // ==================================================================

    // ==================================================================
    // ============================= SETTERS ============================
    // === Non-sorted mess of setter methods only below this line.    ===
    // ===              You have been warned.                         ===
    // ==================================================================

    public EntityBuilder maxDistanceFromPlayer( float maxDist){
        maxDistanceFromPlayer = maxDist;
        return this;
    }

    public EntityBuilder maxHealth( int maxHealth){
        this.maxHealth = maxHealth;
        return this;
    }

    public EntityBuilder health( int health){
        this.health = health;
        return this;
    }

    public EntityBuilder collideSelfDamage(int dam){
        collideSelfDamage = dam;
        return this;
    }

    public EntityBuilder collideDamage(int dam){
        this.collideDamage = dam;
        return this;
    }

    public EntityBuilder spontGenRadius(float rad){
        this.spontGenRadius = rad;
        return this;
    }

    public EntityBuilder spawnFieldRadius(float rad){
        this.spawnFieldRadius = rad;
        return this;
    }

    public EntityBuilder speed(float speed){
        this.speed = speed;
        return this;
    }

    public EntityBuilder tag(String tag) {
        this.tag = tag;
        return this;
    }

    public EntityBuilder animation(List<String> frames, Animation.PlayMode playMode, float frameDuration) {
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
    public EntityBuilder scale(float scale) {
        this.scale = scale;
        return this;
    }

    public EntityBuilder angle(float angleDeg) {
        this.angleDeg = angleDeg;
        return this;
    }

    public EntityBuilder texture(String texturePath) {
        if (frames != null)
            throw new RuntimeException("ERROR: Cannot define both a texture and an animation");
        this.texturePath = texturePath;
        return this;
    }

    /**
     * Defaults to RenderIndex.BUILDING
     */
    public EntityBuilder renderIndex(RenderIndex renderIndex) {
        this.renderIndex = renderIndex;
        return this;
    }

    public EntityBuilder velocity(Vector2 vel) {
        this.velocity.set(vel);
        return this;
    }

    public EntityBuilder velocity(float speed, Vector2 dir) {
        this.velocity.set(dir).scl(speed);
        return this;
    }

    /**
     * Default is to allow sleeping
     */
    public EntityBuilder denySleep() {
        allowSleep = false;
        return this;
    }

    /**
     * Default BodyType is Dynamic
     */
    public EntityBuilder bodyType(BodyDef.BodyType bodyType) {
        this.bodyType = bodyType;
        return this;
    }

    /**
     * Default allows full 2D rotation
     */
    public EntityBuilder setFixedRotation(boolean fixed) {
        fixedRotation = fixed;
        return this;
    }

    /**
     * Default angle is zero
     */
    public EntityBuilder setAngleRad(float angleRad) {
        this.angleRad = angleRad;
        return this;
    }

    /**
     * Default body density is 1 kg/m^2 (1.0f)
     */
    public EntityBuilder bodyDensity(float density) {
        this.density = density;
        return this;
    }

    /**
     * Default is to have no friction
     */
    public EntityBuilder bodyFriction(float friction) {
        this.friction = friction;
        return this;
    }

    /**
     * Default is 0.1f (low elasticity)
     */
    public EntityBuilder bodyRestitution(float restitution) {
        this.restitution = restitution;
        return this;
    }

    public EntityBuilder bullet(boolean bullet) {
        isBullet = bullet;
        return this;
    }

    public EntityBuilder timeToDestruction(long t2d){
        timeToDestruction = t2d;
        return this;
    }
}
