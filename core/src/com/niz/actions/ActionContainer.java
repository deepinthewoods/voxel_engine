package com.niz.actions;

import com.badlogic.gdx.utils.Array;

/**
 * Created by niz on 07/06/2014.
 */
public class ActionContainer extends Array<Action> {
    private transient final ArrayIterator<Action> iter;
    protected transient ActionList parentList;
    public ActionContainer(ActionList actionList) {
        super(true, 8);
        parentList = actionList;
        iter = new ArrayIterator<Action>(this);
    }

    public ArrayIterator<Action> iter() {
        iter.reset();
        return iter;
    }


}
