package io.github.emergentorganization.cellrpg.core.entityfactory.builder;

import com.artemis.Archetype;
import com.artemis.Component;
import com.artemis.Entity;
import com.artemis.World;
import com.artemis.managers.TagManager;
import com.artemis.utils.Bag;
import com.badlogic.gdx.math.Vector2;
import io.github.emergentorganization.cellrpg.core.entityfactory.EntityFactory;
import io.github.emergentorganization.cellrpg.core.entityfactory.builder.componentbuilder.BuilderComparator;
import io.github.emergentorganization.cellrpg.core.entityfactory.builder.componentbuilder.IComponentBuilder;
import io.github.emergentorganization.cellrpg.core.components.*;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;


public class EntityBuilder {
    private static final ArrayList<Class<? extends Component>> ignoredComponentBuilders = new ArrayList<Class<? extends Component>>();

    static {
        ignoredComponentBuilders.add(Position.class);
        ignoredComponentBuilders.add(Rotation.class);
        ignoredComponentBuilders.add(Scale.class);
        ignoredComponentBuilders.add(Velocity.class);
        ignoredComponentBuilders.add(Bounds.class);
        ignoredComponentBuilders.add(Name.class);
    }

    private final Logger logger = LogManager.getLogger(getClass());
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
    // MISC
    private ArrayList<IComponentBuilder> builders;
    private String tag = null;
    private float scale = EntityFactory.SCALE_WORLD_TO_BOX;

    // ==================================================================
    // ==================================================================
    private float angleDeg = 0f;


    // ==================================================================
    // ====================== BUILD METHOD ==============================
    // ===  Called at the end of the builder chain, this method       ===
    // ===  actually builds and returns the entity using the          ===
    // ===  properties specified using the component builder classes. ===
    // ==================================================================
    private Vector2 velocity = new Vector2();

    public EntityBuilder(World world, Archetype archetype, String friendlyName, String entityId, Vector2 position) {
        this.world = world;
        this.archetype = archetype;
        this.position = position;
        this.entityId = entityId;
        this.friendlyName = friendlyName;
        this.builders = new ArrayList<IComponentBuilder>();
    }

    public Entity build() {
        // called at the end of the builder chain, this actually builds and returns the entity
        // using the properties specified using the setter methods.
        Collections.sort(builders, new BuilderComparator());
        Entity entity = world.createEntity(archetype);

        buildName(entity);

        if (tag != null) {
            world.getSystem(TagManager.class).register(tag, entity);
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

        for (IComponentBuilder builder : builders) {
            builder.build(entity);
        }

        checkForMissingBuilders(builders, entity, false);

        return entity;
    }

    /**
     * DEBUG ONLY: A method for checking the creation of arbitrary components
     */
    private void checkForMissingBuilders(ArrayList<IComponentBuilder> builders, Entity entity, boolean verbose) {
        Bag<Component> components = entity.getComponents(new Bag<Component>());
        for (Component component : components) {
            String cName = component.getClass().getName();
            if (!verbose && ignoredComponentBuilders.contains(component.getClass())) {
                continue;
            }
            boolean found = false;
            for (IComponentBuilder builder : builders) {
                if (cName.equals(builder.getComponentClass().getName())) {
                    found = true;
                    break;
                }
            }
            if (!found) {
                String[] packages = cName.split("\\.");
                logger.debug(
                        "Could not find builder for the " + packages[packages.length - 1] +
                                " component attached to " + entity.getComponent(Name.class).friendlyName
                );
            }
        }

    }

    private void buildName(Entity entity) {
        Name name = entity.getComponent(Name.class);
        name.friendlyName = friendlyName;
        name.internalID = entityId;
    }

    public EntityBuilder addBuilder(IComponentBuilder builder) {
        this.builders.add(builder);
        return this;
    }

    public EntityBuilder setBuilders(Collection<IComponentBuilder> builders) {
        this.builders.clear();
        this.builders.addAll(builders);
        return this;
    }

    public EntityBuilder tag(String tag) {
        this.tag = tag;
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

    public EntityBuilder velocity(Vector2 vel) {
        this.velocity.set(vel);
        return this;
    }

    public EntityBuilder velocity(float speed, Vector2 dir) {
        this.velocity.set(dir).scl(speed);
        return this;
    }
}
