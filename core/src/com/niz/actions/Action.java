package com.niz.actions;

import com.artemis.World;
import com.badlogic.gdx.utils.BinaryHeap;

/**
 * Created by niz on 07/06/2014.
 */
public abstract class Action extends BinaryHeap.Node{
    public static final int LANE_DELAY = 1;
    public boolean isBlocking, isFinished;
    public int lanes;
    public transient ActionList parent;

    public Action() {
        super(0f);
    }


    public abstract void update(float dt);

    public abstract void onEnd();

    public abstract void onStart(World world);

    public void addBeforeMe(Action a){
        parent.addBefore(this, a);
    }
    public void addAfterMe(Action a){
        parent.addAfter(this, a);
    }



    public void delay(float f){
        if (f == 0) return;
        //delayed = true;
        lanes |= LANE_DELAY;
        //removeSelf();
        float value = parent.currentTime+f;
        parent.delayedActions.add(this, value);
        //Gdx.app.log(TAG, "delay"+delayedActions.size);
        //Gdx.app.log(TAG, "delay"+ticks);
    }

    public void unDelay(){

        lanes &= ~LANE_DELAY;

    }
}
