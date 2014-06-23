package com.niz.actions;

import com.artemis.Component;
import com.artemis.Entity;
import com.artemis.World;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.utils.Array;
import com.badlogic.gdx.utils.BinaryHeap;
import com.badlogic.gdx.utils.GdxRuntimeException;


public class ActionList implements Component{
	private static final String TAG = "action list";
	//public DoublyLinkedList actions;
	public transient Entity e;
	public transient World world;
	public ActionContainer actions = new ActionContainer();;
	public ActionList(){

	}
	
	public void update(float dt){
		currentTime += dt;
		int lanes = Action.LANE_DELAY;
		Array.ArrayIterator<Action> iter = actions.iter();

		while (iter.hasNext()){
			Action action = iter.next();
			if ((lanes & action.lanes) != 0){
				continue;
			}
			action.update(dt);

			if (action.isBlocking)
				lanes |= action.lanes;
			
			if (action.isFinished){
				action.onEnd();
                iter.remove();
			}
		};
		//Gdx.app.log(TAG, "update");
	}




	public<C extends Action> C get(Class<C> class1) {
        Array.ArrayIterator<Action> iter = actions.iter();
		while (iter.hasNext()){
			Action act = iter.next();
			if (act.getClass() == class1) return (C) act;
		}
		
		
		return null;
	}


    @Override
    public void reset() {

    }

    public void addToStart(Action a){
        actions.insert(0, a);
        a.parent = this;

        a.onStart(world);
    }
    public void addToEnd(Action a){
        actions.add(a);
        a.parent = this;

        a.onStart(world);

    }

    protected void addBefore(Action a, Action toAdd){
        int index = actions.indexOf(a, true);
        actions.insert(index, toAdd);
        toAdd.parent = this;
        toAdd.onStart(world);

    }
    protected void addAfter(Action a, Action toAdd){
        int index = actions.indexOf(a, true);
        actions.insert(index+1, toAdd);
        toAdd.parent = this;
        toAdd.onStart(world);
    }


    public void inserted(Entity e, World world) {
        this.e = e;
        this.world = world;
        Array.ArrayIterator<Action> iter = actions.iter();
        while (iter.hasNext()){
            Action a = iter.next();
            a.parent = this;
            a.onStart(world);
            if ((a.lanes & Action.LANE_DELAY) != 0){
                delayedActions.add(a);
            }
        }
        ///Gdx.app.log(TAG, "inserted!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!");
        actions.init(this);
    }

    public float currentTime;

    public transient BinaryHeap<Action> delayedActions;


    public void removed(Entity e, World world) {
        Array.ArrayIterator<Action> iter = actions.iter();
        while (iter.hasNext()){
            Action a = iter.next();
            a.onEnd();
            if ((a.lanes & Action.LANE_DELAY) != 0){
                delayedActions.remove(a);
            }
        }
    }

    public void addPre(Action a) {
        actions.insert(0, a);
    }
}
