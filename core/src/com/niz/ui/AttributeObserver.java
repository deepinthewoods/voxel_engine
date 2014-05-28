package com.niz.ui;

import com.artemis.Component;
import com.artemis.Entity;
import com.niz.component.Attributes;
import com.niz.component.Physics;
import com.niz.component.Player;
import com.niz.observer.FilteringObserver;
import com.niz.observer.MessageFilter;
import com.niz.observer.Subject;

/**
 * Created by niz on 27/05/2014.
 */
public class AttributeObserver extends FilteringObserver{
    public String attributeName;
    private transient int attributeHash;
    public float attributeLevel;

    public AttributeObserver(){
        //FIXME only needed for initial serialization, can be deleted later
        MessageFilter filter = new MessageFilter();
       // filter.type = MessageFilter.FilterType.HAS_COMPONENT;
        filter.component = "com.niz.component.Player";
        filters = new MessageFilter[]{filter};

    }

    @Override
    public void onFilteredNotify(Entity e, Subject.Event event, Component c) {
        Attributes att = (Attributes) c;
        attributeLevel = att.current.get(attributeHash, 1) / att.max.get(attributeHash, 1);
    }
}
