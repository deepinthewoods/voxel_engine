package com.niz.component.systems;

import com.artemis.Aspect;
import com.artemis.ComponentMapper;
import com.artemis.Entity;
import com.artemis.systems.DrawSystem;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.math.Vector3;
import com.badlogic.gdx.utils.Array;
import com.niz.component.DebugVector;

public class DebugVectorSystem extends DrawSystem {

	private static final String TAG = "debug position system";
	//private ComponentMapper<Position> posM;
	private ComponentMapper<DebugVector> vecM;
	public DebugVectorSystem() {
		
		super(Aspect.getAspectForAll(DebugVector.class));
		// TODO Auto-generated constructor stub
	}

	@Override
	protected void processEntities(Array<Entity> entities) {
		

		for (Entity e : entities){
			//Vector3 pos = posM.get(e).pos;
			DebugVector vecs = vecM.get(e);
			for (int i = 0; i < vecs.arr.size; i++){
				Vector3 vec = vecs.arr.get(i);
				Color col = vecs.colors.get(i);
				shapeBatch.drawDebug(vec.x, vec.y, vec.z, col);
			}
			//Gdx.app.log(TAG, "debug draw");

		}
	}
	@Override
	public void initialize(){
		//posM = world.getMapper(Position.class);
		vecM = world.getMapper(DebugVector.class);
	}
}
