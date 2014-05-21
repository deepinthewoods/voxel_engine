package com.niz.actions;

import com.artemis.ComponentMapper;
import com.artemis.World;
import com.badlogic.gdx.math.Vector3;
import com.badlogic.gdx.utils.Pools;
import com.niz.component.*;

public class AStand extends Action {
	//private static final float MIN_RUN_DIST = 0.0852f;

	private static final String TAG = "AStand";

	private ComponentMapper<Position> positionMap;

	private ComponentMapper<ModelInfo> animM;

	private ComponentMapper<Move> moveMap;

	private ComponentMapper<Physics> physMap;

	private ComponentMapper<Brain> brainMap;
	
	
	

	
	@Override
	public void update(float dt) {
		//Gdx.app.log(TAG, "RUN"+(positionMap == null));
		Vector3 v = positionMap.get(parent)
				.pos,
				targetPos = brainMap.get(parent).getShortTarget();
		float dist = Math.abs(targetPos.x-v.x);

		if (dist > ARun.MIN_RUN_DIST){
			Action node = Pools.obtain(ARun.class);
			this.insertBeforeMe(node);
			isFinished = true;
			//Gdx.app.log(TAG, "change to run"+dist);
			return;

		}
		v.set(physMap.get(parent).oldPosition);
		
	}

	@Override
	public void onStart(World world) {
		//animation
		positionMap = world.getMapper(Position.class);
		brainMap = world.getMapper(Brain.class);
		animM = world.getMapper(ModelInfo.class);
		moveMap = world.getMapper(Move.class);
		physMap = world.getMapper(Physics.class);
		moveMap.get(parent).moving = false;
		//Gdx.app.log(TAG, "stsrt");
		animM.get(parent).anim.animate("Stand", -1, null,  ANIM_TRANSITION_TIME);
	}

	@Override
	public void onEnd() {
		//Gdx.app.log(TAG, "end");
	}

	
}
