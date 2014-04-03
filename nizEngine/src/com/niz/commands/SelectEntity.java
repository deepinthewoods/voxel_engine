package com.niz.commands;

import com.artemis.World;

public class SelectEntity extends Command {

	public int entityId;
	

	@Override
	public int execute(World  world) {
		data.e = world.getEntity(entityId);
		return 0;
	}

}
