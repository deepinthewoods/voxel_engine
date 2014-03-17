package com.niz.component.systems;

import com.artemis.Aspect;
import com.artemis.ComponentMapper;
import com.artemis.Entity;
import com.artemis.systems.EntityProcessingSystem;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.math.Vector3;
import com.niz.component.Position;
import com.niz.component.RollingAverage;

public class RollingAverageSystem extends EntityProcessingSystem {

	private static final String TAG = "rolling average sys";
	private ComponentMapper<RollingAverage> raM;
	private ComponentMapper<Position> posM;

	public RollingAverageSystem() {
		super(Aspect.getAspectForAll(RollingAverage.class, Position.class));
		// TODO Auto-generated constructor stub
	}

	@Override
	protected void process(Entity e) {
		Vector3 pos = posM.get(e).pos;
		RollingAverage av = raM.get(e);
		av.progress++;
		if (av.progress >= av.arr.size) av.progress = 0;
		Gdx.app.log(TAG, "v"+av.arr.get(av.progress) + "  aver "+pos + "  target "+av.target);
		av.total.add(av.target);
		av.total.sub(av.arr.get(av.progress));
		av.arr.get(av.progress).set(av.target);
		pos.set(av.total);
		pos.scl(1f/av.arr.size);
	}
	
	public void initialize(){
		raM = world.getMapper(RollingAverage.class);
		posM = world.getMapper(Position.class);
	}
}
