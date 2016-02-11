package io.github.emergentorganization.cellrpg.events;

/**
 * Event with entity target. Can also be used for entity-less events by setting entityId < 0
 */
public class EntityEvent {
    public static final int NO_ENTITY = -1;

    public int entityId;
    public GameEvent event;

    public EntityEvent(final int entityId, final GameEvent event){
        this.entityId = entityId;
        this.event = event;
    }
}
