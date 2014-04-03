package com.niz.commands;

import com.artemis.World;
import com.badlogic.gdx.utils.reflect.Field;
import com.badlogic.gdx.utils.reflect.ReflectionException;

public class ComponentSetField extends Command {
	
	
	private Object[] value;
	private Field field;
	
	@Override
	public int execute(World  world) {
		
		try {
			field.set(data.c, value);
			
			
		} catch (ReflectionException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return 0;
	}

}
