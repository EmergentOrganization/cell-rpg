package io.github.emergentorganization.engine.events;


import com.emergentorganization.cellrpg.events.GameEvent;

public interface EventListener {

    void notify(GameEvent event);

}
