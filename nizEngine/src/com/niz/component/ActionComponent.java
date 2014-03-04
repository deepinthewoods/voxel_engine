package com.niz.component;

import com.artemis.Component;
import com.artemis.Entity;
import com.niz.actions.ActionList;


public class ActionComponent implements Component{
	public ActionList action = new ActionList();
	
	@Override
	public void reset() {
		// TODO Auto-generated method stub
		
	}
}
