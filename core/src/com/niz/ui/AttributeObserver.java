package com.niz.ui;

import com.artemis.Component;
import com.artemis.Entity;
import com.niz.component.Attributes;
import com.niz.observer.AutoObserver;
import com.niz.observer.Observer;
import com.niz.observer.Subject;

/**
 * Created by niz on 27/05/2014.
 */
public class AttributeObserver extends AutoObserver {
    public String attributeName;
    private transient int attributeHash;
    public float attributeLevel, attributeMax;

    public AttributeObserver(){


    }



    @Override
    public void onNotify(Entity e, Subject.Event event, Component c) {
        Attributes att = (Attributes) c;
        attributeLevel = att.current.get(attributeHash, 1);
        attributeMax = att.max.get(attributeHash, 1);
    }
}
