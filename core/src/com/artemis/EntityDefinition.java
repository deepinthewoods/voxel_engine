package com.artemis;

import com.badlogic.gdx.utils.Array;
import com.badlogic.gdx.utils.Pools;

/**
 * Created by niz on 26/05/2014.
 */
public class EntityDefinition {
    String name;
    int id;
    ComponentArray components = new ComponentArray();

    //@returns true if valid
    public boolean setFrom(Entity e){

        components.c.clear();
        Array<Component> cs = e.getComponents();
        return components.addAll(cs);

    }

    public void setEntityFromThisOnce(Entity e){
        for (Component c : components.c){
            e.addComponent(c);
        }
        components.c.clear();
        Pools.free(components);
        components = null;
    }

    public void setEntityFromThisRepeatable(Entity e){
        for (Component c : components.c){
            e.add(c.getClass());
        }
    }


}
