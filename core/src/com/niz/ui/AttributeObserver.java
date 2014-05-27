package com.niz.ui;

import com.artemis.Component;
import com.artemis.Entity;
import com.niz.observer.FilteringObserver;
import com.niz.observer.Subject;

/**
 * Created by niz on 27/05/2014.
 */
public class AttributeObserver extends FilteringObserver{
    String attribute_name;
    float attribute_level;
    @Override
    public void onFilteredNotify(Entity e, Subject.Event event, Component c) {

    }
}
