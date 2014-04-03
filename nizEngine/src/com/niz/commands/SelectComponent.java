package com.niz.commands;

import com.artemis.Component;
import com.artemis.World;
import com.badlogic.gdx.utils.reflect.ClassReflection;

public class SelectComponent extends Command {
	
	private Class<Component> cl;

	@Override
	public int execute(World  world) {
		data.c = data.e.get(cl);
		//ClassReflection.;
		
		
		return 0;
	}

}
