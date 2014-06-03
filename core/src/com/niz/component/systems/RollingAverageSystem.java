package com.niz.component.systems;

import com.artemis.Aspect;
import com.artemis.ComponentMapper;
import com.artemis.Entity;
import com.artemis.systems.EntityProcessingSystem;
import com.badlogic.gdx.math.Vector3;
import com.badlogic.gdx.utils.Pools;
import com.niz.component.RollingAverage;

public abstract class RollingAverageSystem extends EntityProcessingSystem {

	private static final String TAG = "rolling average sys";
	protected ComponentMapper<? extends RollingAverage> raM;

	/**
	 * uses Vector3s to create a rolling aversge, stored in RollingAverage.result
	 */
	public RollingAverageSystem(Aspect aspect) {
		super(aspect);
	}
	public abstract Vector3 getTarget(Entity e);

	@Override
	protected void process(Entity e) {
		RollingAverage av = raM.get(e);
		Vector3 result = av.result;//
		Vector3 target = getTarget(e);
		if (av.progress >= av.arr.size-1){
			av.arr.add(Pools.obtain(Vector3.class).set(0,0,0));
		}
		//Gdx.app.log(TAG, "size "+av.arr.size + "  prog "+ av.result);
		av.total.add(target);
		av.total.sub(av.arr.get(av.progress));
		av.arr.get(av.progress).set(target);
		result.set(av.total);
		result.scl(1f/av.size);
		
		av.progress++;
		if (av.progress >= av.size) av.progress = 0;
		
		//Gdx.app.log(TAG, "av "+av.result);

	}
	
	
}
