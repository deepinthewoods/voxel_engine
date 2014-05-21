package com.artemis.systems.event;

import com.artemis.systems.VoidEntitySystem;
import com.badlogic.gdx.utils.Array;

/**
 * Void Entity System for processing a single event. Reduces boiler plate for event
 * processing systems.
 * 
 * @author apotapov
 *
 * @param <T> Event that this system processes.
 */
public abstract class EventVoidSystem2<T extends SystemEvent, U extends SystemEvent> extends VoidEntitySystem {

    Array<T> events;
    Class<T> eventType;

    Array<U> events2;
    Class<U> eventType2;

    /**
     * Constructs an event system
     * @param eventType Class of the first event this system will process.
     * @param eventType2 Class of the second event this system will process.
     */
    public EventVoidSystem2(Class<T> eventType, Class<U> eventType2) {
        this.eventType = eventType;
        this.events = new Array<T>();

        this.eventType2 = eventType2;
        this.events2 = new Array<U>();

    }

    @Override
    public final void processSystem() {
        world.getEvents(this, eventType, events);
        for (T event : events) {
            processEvent(event);
        }

        world.getEvents(this, eventType2, events2);
        for (U event : events2) {
            processEvent2(event);
        }
    }

    /**
     * Processes the first event.
     * 
     * @param event Event to process
     */
    protected abstract void processEvent(T event);

    /**
     * Processes the second event.
     * 
     * @param event Event to process
     */
    protected abstract void processEvent2(U event);
}
