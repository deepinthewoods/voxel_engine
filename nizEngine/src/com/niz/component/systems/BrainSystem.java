package com.niz.component.systems;

import com.artemis.Aspect;
import com.artemis.ComponentMapper;
import com.artemis.Entity;
import com.artemis.systems.EntityProcessingSystem;
import com.niz.component.Brain;
import com.niz.component.Position;

public class BrainSystem extends EntityProcessingSystem {

	private ComponentMapper<Brain> brainM;

	public BrainSystem() {
		super(Aspect.getAspectForAll(Brain.class, Position.class));
		// TODO Auto-generated constructor stub
	}

	@Override
	protected void process(Entity e) {
		Brain brain = brainM.get(e);
		brain.setTargetAngle(e);
	}

	public void initialize(){
		brainM =world.getMapper(Brain.class);
	}
	
	


	
}
