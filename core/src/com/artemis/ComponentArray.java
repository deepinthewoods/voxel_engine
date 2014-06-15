package com.artemis;

import com.badlogic.gdx.utils.Array;
import com.niz.component.Transient;

/**
 * Created by niz on 26/05/2014.
 */
public class ComponentArray {
    public Array<Component> c = new Array<Component>();

    public boolean addAll(Array<Component> cs) {
        boolean valid = true;
        for (int i = 0, n = cs.size; i < n; i++){
            Component com = cs.get(i);
            if (com instanceof Transient)
                valid = false;
            c.add(com);
        }
        return valid;


    }
}
