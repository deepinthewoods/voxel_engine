package com.niz;

import com.badlogic.gdx.scenes.scene2d.ui.Table;
import com.badlogic.gdx.utils.JsonValue;

public class ObjectEditManager extends Table{
	
	

	public ObjectEditManager() {
		// TODO Auto-generated constructor stub
		
	}

	public void set(Object o, JsonValue value){
		this.o = o;
		
		//edit.buildPojoEditor( this, o);
	}

	public Object o;
	
}
