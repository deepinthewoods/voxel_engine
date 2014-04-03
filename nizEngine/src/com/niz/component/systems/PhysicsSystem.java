package com.niz.component.systems;

import com.artemis.Aspect;
import com.artemis.ComponentMapper;
import com.artemis.Entity;
import com.artemis.systems.EntitySystem;
import com.badlogic.gdx.utils.Array;
import com.badlogic.gdx.utils.Json;
import com.badlogic.gdx.utils.JsonValue;
import com.niz.component.Physics;
import com.niz.component.Position;
import com.niz.physics.JPhysicsEngine;

public class PhysicsSystem extends EntitySystem {
	public JPhysicsEngine engine;
	private ComponentMapper<Physics> physMap;
	private ComponentMapper<Position> posMap;
	int reps = 1; int max = 100; float step = 1f/128f;
	public PhysicsSystem(){
		super(Aspect.getAspectForOne(Physics.class));
		engine = new JPhysicsEngine(reps, max, step);
	}
	
	@Override
	public void initialize(){
		physMap = world.getMapper(Physics.class);
		posMap = world.getMapper(Position.class);
		Physics.physics = engine;
	}
	
	
	
	@Override
	protected void inserted(Entity e) {
		physMap.get(e).set(posMap.get(e));
	}

	@Override
	protected void processEntities(Array<Entity> entities) {
		engine.step();
	}

	

}
