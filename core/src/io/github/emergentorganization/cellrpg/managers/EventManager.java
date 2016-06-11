package io.github.emergentorganization.cellrpg.managers;

import com.artemis.BaseSystem;
import io.github.emergentorganization.cellrpg.core.events.EventListener;
import io.github.emergentorganization.cellrpg.events.EntityEvent;

import java.util.ArrayList;


public class EventManager extends BaseSystem {

    /*
     * Not sure if this is the best way to handle that
     * Going to research it
     */
    private final ArrayList<EntityEvent> events;
    private final ArrayList<EntityEvent> newEvents;
    private final ArrayList<EventListener> listenerInsertion;
    private final ArrayList<EventListener> listenerRemoval;
    private final ArrayList<EventListener> listeners;

    public EventManager() {
        events = new ArrayList<EntityEvent>();
        newEvents = new ArrayList<EntityEvent>();
        listenerInsertion = new ArrayList<EventListener>();
        listenerRemoval = new ArrayList<EventListener>();
        listeners = new ArrayList<EventListener>();
    }

    public void addListener(EventListener listener) {
        listenerInsertion.add(listener);
    }

    public void removeListener(EventListener listener) {
        listenerInsertion.remove(listener);
    }

    public void pushEvent(EntityEvent event) {
        newEvents.add(event);
    }

    @Override
    protected void processSystem() {
        for (EventListener l : listenerInsertion) {
            listeners.add(l);
        }
        listenerInsertion.clear();

        for (EventListener l : listenerRemoval) {
            listeners.remove(l);
        }
        listenerRemoval.clear();

        for (EntityEvent e : events) {
            for (EventListener l : listeners) {
                l.notify(e);
            }
        }
        events.clear();
        events.addAll(newEvents);
        newEvents.clear();
    }


}
