package com.niz.component.systems;

import com.artemis.Aspect;
import com.artemis.Component;
import com.artemis.ComponentMapper;
import com.artemis.Entity;
import com.badlogic.gdx.math.Vector3;
import com.niz.component.CameraPositionInfluencer;
import com.niz.component.Position;
import com.niz.component.PositionRollingAverage;
import com.niz.observer.Observer;
import com.niz.observer.Subject.Event;

public class PositionRollingAverageSystem extends RollingAverageSystem implements Observer {

	private ComponentMapper<Position> posM;

	public PositionRollingAverageSystem() {
		super(Aspect.getAspectForAll(PositionRollingAverage.class, Position.class));
	}

	

	@Override
	public Vector3 getTarget(Entity e) {
		return posM.get(e).pos;
	}

	@Override
	public void initialize(){
		
		posM = world.getMapper(Position.class);
		raM = world.getMapper(PositionRollingAverage.class);
		CameraPositionInfluenceSystem infSys = world.getSystem(CameraPositionInfluenceSystem.class);
		infSys.notifyAccumulateInfluencePosition.add(this);
	}



	@Override
	public void onNotify(Entity e, Event event, Component c) {
		CameraPositionInfluencer inf = (CameraPositionInfluencer) c;
		PositionRollingAverage av = (PositionRollingAverage) raM.get(e);
		if (av != null){
			inf.values.add(av.result);
			inf.weights.add(1f);
		}
		
	}
}
