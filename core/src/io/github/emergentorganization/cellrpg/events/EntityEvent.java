package io.github.emergentorganization.cellrpg.events;

/**
 * Event with entity target. Can also be used for entity-less events by setting entityId < 0
 */
public class EntityEvent {
    public static final int NO_ENTITY = -1;  // value to pass for entityId if event has no entity

    public final int entityId;
    public final GameEvent event;

    public EntityEvent(final int entityId, final GameEvent event) {
        this.entityId = entityId;
        this.event = event;
    }
}
