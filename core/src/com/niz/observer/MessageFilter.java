package com.niz.observer;

import com.artemis.Component;
import com.artemis.ComponentMapper;
import com.artemis.Entity;
import com.badlogic.gdx.utils.reflect.ClassReflection;
import com.badlogic.gdx.utils.reflect.ReflectionException;

/**
 * Created by niz on 27/05/2014.
 */
public class MessageFilter {
    private transient ComponentMapper getter;

    public enum FilterType {EVENT_EQUALS, HAS_COMPONENT};
    public FilterType type;
    transient Class clas;
    public String component;

    //returns true if all conditions are met
    public boolean isValid(Entity e, Subject.Event event, Component c){
        switch (type){
            case HAS_COMPONENT:
                if (getter == null){

                    getter = e.getWorld().getMapper(clas);
                }
                return getter.get(e) != null;
        }

        return false;
    }

    public void init(){
        if (component != null) try {
            clas = ClassReflection.forName(component);
        } catch (ReflectionException e) {
            e.printStackTrace();
        }
    }

}
