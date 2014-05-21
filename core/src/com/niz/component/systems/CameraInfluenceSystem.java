package com.niz.component.systems;

import com.artemis.Aspect;
import com.artemis.ComponentMapper;
import com.artemis.Entity;
import com.artemis.systems.EntitySystem;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.math.Vector3;
import com.badlogic.gdx.utils.Array;
import com.niz.component.CameraInfluencer;
import com.niz.component.Position;
import com.niz.observer.Subject;
import com.niz.observer.Subject.Event;

public class CameraInfluenceSystem extends EntitySystem{

	private static final String TAG = "camera influence system";
	
	private ComponentMapper<CameraInfluencer> infM;

	public CameraInfluenceSystem() {
		super(Aspect.getAspectForAll(CameraInfluencer.class));
	}

	Vector3 tmpV = new Vector3(), tot = new Vector3();
	private int weightTotal;
	
	public transient Subject notifyPositionChanged = new Subject();
	//private Vector3 v = new Vector3();
	private Position v = new Position();
	public transient Subject notifyAccumulateInfluence= new Subject();
	
	
	protected void process(Entity e, CameraInfluencer inf) {
		//Position p = posM.get(e);
		for (int i = 0; i < inf.weights.size; i++){
			float w = inf.weights.get(i);
			tmpV.set(inf.values.get(i)).scl(w);
			tot.add(tmpV);
			weightTotal += w;
		}
		
		
	}
	
	public void initialize(){
		//posM = world.getMapper(Position.class);
		infM = world.getMapper(CameraInfluencer.class);
	}

	
	@Override
	protected void processEntities(Array<Entity> entities) {
		weightTotal = 0;
		tot.set(0, 0, 0);
		for (Entity e : entities){
			//post message for inf
			CameraInfluencer inf = infM.get(e);
			inf.reset();
			notifyAccumulateInfluence.notify(e, Event.ACCUMULATE_INFLUENCE, inf);
			process(e, inf);
		}
		if (weightTotal > 0){} else return;
		tot.scl(1f/weightTotal);
		//set camera position or publich to observers
		v.pos.set(tot);
		//Gdx.app.log(TAG, "move"+weightTotal);
		notifyPositionChanged.notify(null, Subject.Event.POSITION_CHANGE, v);
		
	}
	
}
