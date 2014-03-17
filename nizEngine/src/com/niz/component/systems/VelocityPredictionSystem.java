package com.niz.component.systems;

import com.artemis.Aspect;
import com.artemis.ComponentMapper;
import com.artemis.Entity;
import com.artemis.systems.EntityProcessingSystem;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.math.Vector3;
import com.niz.component.Physics;
import com.niz.component.Position;
import com.niz.component.VelocityPredictor;

public class VelocityPredictionSystem extends EntityProcessingSystem {

	private static final String TAG = "velocity prediction system";
	private ComponentMapper<Position> posM;
	private ComponentMapper<VelocityPredictor> velM;

	public VelocityPredictionSystem() {
		super(Aspect.getAspectForAll(VelocityPredictor.class, Position.class));
	}

	@Override
	protected void process(Entity e) {
		Vector3 pos = posM.get(e).pos;
		VelocityPredictor vel = velM.get(e);
		Physics phys = vel.e.get(Physics.class);
		Vector3 ePos = vel.e.get(Position.class).pos;
		vel.vel.set(ePos).sub(phys.oldPosition).mul(vel.scale);
		if (!vel.x)vel.vel.x = 0;
		if (!vel.y)vel.vel.y = 0;
		if (!vel.z)vel.vel.z = 0;
		vel.vel.add(ePos);
		
		//Gdx.app.log(TAG, "vel"+vel.vel);
		//pos.set(ePos).add(vel.vel);
	}

	public void initialize(){
		posM = world.getMapper(Position.class);
		velM = world.getMapper(VelocityPredictor.class);
		
	}
}
