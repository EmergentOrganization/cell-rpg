package io.github.emergentorganization.cellrpg.core.events;

import io.github.emergentorganization.cellrpg.events.EntityEvent;

public interface EventListener {

    void notify(EntityEvent event);
    // notifies listening object of an event

}
