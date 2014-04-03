package com.niz.commands;

import com.artemis.Component;
import com.artemis.Entity;
import com.artemis.World;
import com.badlogic.gdx.utils.Array;

public abstract class Command {
	//public String pre, post;
	public int hash;
	public abstract int execute(World  world);
	protected CommandData data = new CommandData();
	//public abstract Array<Command> getChildren();
	
	
	//public execute(World world, String )
	
	
}
