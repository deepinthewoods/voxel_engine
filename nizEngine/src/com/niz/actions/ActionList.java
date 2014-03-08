package com.niz.actions;

import com.artemis.Entity;
import com.artemis.World;


public class ActionList {
	private static final String TAG = "action list";
	public DoublyLinkedList actions;
	public Entity parent;
	public World world;
	
	public ActionList(){
		actions = new DoublyLinkedList(this);;
	}
	
	public void update(float dt){
		
		int lanes = 0;
		actions.iter();
		

		//Gdx.app.log(TAG, "update"+actions.e.getIndex()+"  size"+actions.size());
		while (actions.hasNext()){
			Action action = actions.next();
			if ((lanes & action.lanes) != 0){
				continue;
			}
			if (!action.delayed) action.update(dt);
			//Gdx.app.log(TAG, "update2");

			if (action.isBlocking)
				lanes |= action.lanes;
			
			if (action.isFinished){
				action.onEnd();
				actions.remove();
			}
		};
		//Gdx.app.log(TAG, "size");
	}
	
	
	
	void begin(){
		
	}
	
	void end(){
		
	}

	public boolean hasAction(Class<? extends Action> class1) {
		actions.iter();
		while (actions.hasNext()){
			Action act = actions.next();
			if (act.getClass() == class1) return true;
		}
		return false;
	}

	public<C extends Action> C get(Class<C> class1) {
		actions.iter();
		while (actions.hasNext()){
			Action act = actions.next();
			if (act.getClass() == class1) return (C) act;
		}
		
		
		return null;
	}
	
	
	
	
}
