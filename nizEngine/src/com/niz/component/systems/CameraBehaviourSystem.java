package com.niz.component.systems;

import com.artemis.Aspect;
import com.artemis.ComponentMapper;
import com.artemis.Entity;
import com.artemis.systems.EntityProcessingSystem;
import com.badlogic.gdx.math.Vector3;
import com.niz.component.CameraController;
import com.niz.component.CameraInfluences;
import com.niz.component.Position;

public class CameraBehaviourSystem extends EntityProcessingSystem {

	private ComponentMapper<Position> posM;
	private ComponentMapper<CameraInfluences> infM;

	public CameraBehaviourSystem() {
		super(Aspect.getAspectForAll(CameraController.class, Position.class, CameraInfluences.class));
		// TODO Auto-generated constructor stub
	}

	Vector3 tmpV = new Vector3(), tot = new Vector3();
	
	@Override
	protected void process(Entity e) {
		Position p = posM.get(e);
		CameraInfluences inf = infM.get(e);
		float weightTotal = 0;
		tot.set(0, 0, 0);
		for (int i = 0; i < inf.influences.size; i++){
			float w = inf.weights.get(i);
			tmpV.set(inf.influences.get(i)).scl(w);
			tot.add(tmpV);
			weightTotal += w;
		}
		tot.scl(1f/weightTotal);
		p.pos.set(tot);
	}
	
	public void initialize(){
		posM = world.getMapper(Position.class);
		infM = world.getMapper(CameraInfluences.class);
	}
	
}
