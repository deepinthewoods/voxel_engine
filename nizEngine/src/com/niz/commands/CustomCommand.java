package com.niz.commands;

import com.badlogic.gdx.utils.JsonValue;
import com.badlogic.gdx.utils.reflect.Method;
import com.badlogic.gdx.utils.reflect.ReflectionException;

public abstract class CustomCommand extends Command {
	public String word;
	//public String[] params;
	//public int[] paramTypes;
	//public static final int PARAM_INT = 0, PARAM_FLOAT = 1, PARAM_ENTITY_NAME = 2, PARAM_ACTION_NAME = 3;
	
	
	public void foo(JsonValue json){
		String s = json.asString();
		
	}
	
	
}
