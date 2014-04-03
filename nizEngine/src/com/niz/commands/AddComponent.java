package com.niz.commands;

import com.artemis.Component;
import com.artemis.World;

public class AddComponent extends Command {

	private Class<Component> cl;

	
	@Override
	public int execute(World  world) {
		data.c = data.e.add(cl);
		return 0;
	}


	
	

}
