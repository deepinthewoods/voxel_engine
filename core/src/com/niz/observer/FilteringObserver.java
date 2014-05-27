package com.niz.observer;

import com.artemis.Component;
import com.artemis.Entity;

/**
 * Created by niz on 27/05/2014.
 */
public abstract class FilteringObserver implements Observer {
    public MessageFilter[] filters;

    @Override
    public void onNotify(Entity e, Subject.Event event, Component c) {

        for (MessageFilter f : filters){
            if (!f.isValid(e, event, c)) return;
        }
        onFilteredNotify(e, event, c);
    }

    public abstract void onFilteredNotify(Entity e, Subject.Event event, Component c);
}
