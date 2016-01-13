package com.emergentorganization.cellrpg.managers;

import com.artemis.BaseSystem;
import com.emergentorganization.cellrpg.events.EventListener;
import com.emergentorganization.cellrpg.events.GameEvent;

import java.awt.Event;
import java.util.ArrayList;


public class EventManager extends BaseSystem {

    /*
     * Not sure if this is the best way to handle that
     * Going to research it
     */
    private ArrayList<GameEvent> events, newEvents;
    private ArrayList<EventListener> listenerInsertion, listenerRemoval, listeners;

    public EventManager() {
        events = new ArrayList<GameEvent>();
        newEvents = new ArrayList<GameEvent>();
        listenerInsertion = new ArrayList<EventListener>();
        listenerRemoval = new ArrayList<EventListener>();
        listeners = new ArrayList<EventListener>();
    }

    public void addListener(EventListener listener){
        listenerInsertion.add(listener);
    }

    public void removeListener(EventListener listener){
        listenerInsertion.remove(listener);
    }

    public void pushEvent(GameEvent event)
    {
        newEvents.add(event);
    }

    @Override
    protected void processSystem() {
        for(EventListener l : listenerInsertion)
        {
            listeners.add(l);
        }
        listenerInsertion.clear();

        for(EventListener l : listenerRemoval)
        {
            listeners.remove(l);
        }
        listenerRemoval.clear();

        for(GameEvent e : events)
        {
            for(EventListener l : listeners)
            {
                l.notify(e);
            }
        }
        events.clear();
        events.addAll(newEvents);
        newEvents.clear();
    }


}
