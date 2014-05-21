package com.niz.component.systems;

import com.artemis.Aspect;
import com.artemis.ComponentMapper;
import com.artemis.Entity;
import com.artemis.systems.EntityProcessingSystem;
import com.badlogic.gdx.math.Vector3;
import com.niz.component.Physics;
import com.niz.component.Position;
import com.niz.component.VelocityPredictor;

public class VelocityPredictionSystem extends EntityProcessingSystem {
	private ComponentMapper<Position> posM;
	private ComponentMapper<VelocityPredictor> velM;
	private ComponentMapper<Physics> physM;
	public VelocityPredictionSystem() {
		super(Aspect.getAspectForAll(VelocityPredictor.class, Physics.class, Position.class));
	}

	

	public void initialize(){
		posM = world.getMapper(Position.class);
		velM = world.getMapper(VelocityPredictor.class);
		physM = world.getMapper(Physics.class);
	}



	@Override
	protected void process(Entity e) {
		VelocityPredictor vel = velM.get(e);
		Physics phys = physM.get(e);
		Vector3 ePos = posM.get(e).pos;
		vel.vel
		.set
		(ePos)
		.sub(
				phys.oldPosition).scl(
				vel.scale);
		if (!vel.x)vel.vel.x = 0;
		if (!vel.y)vel.vel.y = 0;
		if (!vel.z)vel.vel.z = 0;
		//vel.vel.add(ePos);
	}
}
