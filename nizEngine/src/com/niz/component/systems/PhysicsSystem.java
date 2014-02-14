package com.niz.component.systems;

import com.artemis.systems.VoidEntitySystem;
import com.niz.physics.JPhysicsEngine;

public class PhysicsSystem extends VoidEntitySystem {
	public JPhysicsEngine engine;
	public PhysicsSystem(int reps, int max, float step){
		engine = new JPhysicsEngine(reps, max, step);
	}
	@Override
	protected void processSystem() {
		engine.step();
		
	}

	

}
