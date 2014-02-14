package com.niz.component;

import com.artemis.Component;
import com.artemis.Entity;
import com.niz.actions.ActionList;


public class ActionComponent implements Component{
	public ActionList action = new ActionList();
	
	public ActionComponent set(Entity e){
		action.actions.e = e;
		
		return this;
	}

	@Override
	public void reset() {
		// TODO Auto-generated method stub
		
	}
}
