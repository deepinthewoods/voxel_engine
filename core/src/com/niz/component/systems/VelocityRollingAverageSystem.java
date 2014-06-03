package com.niz.component.systems;

import com.artemis.Aspect;
import com.artemis.Component;
import com.artemis.ComponentMapper;
import com.artemis.Entity;
import com.badlogic.gdx.math.Vector3;
import com.niz.component.*;
import com.niz.observer.Observer;
import com.niz.observer.Subject.Event;

public class VelocityRollingAverageSystem extends RollingAverageSystem implements Observer{

	private static final String TAG = "velocity prediction system";
	private ComponentMapper<VelocityPredictor> velM;
	private ComponentMapper<CameraPositionInfluencer> infM;
	private ComponentMapper<Position> posM;
	
	

	public VelocityRollingAverageSystem() {
		super(Aspect.getAspectForAll(VelocityRollingAverage.class, VelocityPredictor.class, Position.class));
	}

	@Override
	protected void process(Entity e) {
		//Vector3 pos = posM.get(e).pos;
		
		
		super.process(e);
		
		//Gdx.app.log(TAG, "vel"+vel.vel);
		//pos.set(ePos).add(vel.vel);
	}

	
	public void initialize(){
		
		raM = world.getMapper(VelocityRollingAverage.class);
		velM = world.getMapper(VelocityPredictor.class);
		infM = world.getMapper(CameraPositionInfluencer.class);
		posM = world.getMapper(Position.class);
		world.getSystem(CameraPositionInfluenceSystem.class).notifyAccumulateInfluencePosition.add(this);
	}

	@Override
	public Vector3 getTarget(Entity e) {
		return velM.get(e).vel;
	}

	@Override
	public void onNotify(Entity e, Event event, Component c) {
		//Gdx.app.log(TAG, "notified");
		CameraPositionInfluencer inf = (CameraPositionInfluencer) c;
		RollingAverage av = (RollingAverage) raM.get(e);
		if (av != null){
			inf.values.add(av.result.add(posM.get(e).pos));
			inf.weights.add(1f);
		}
	}
}
