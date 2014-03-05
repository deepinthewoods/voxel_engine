package com.niz.actions;

import com.artemis.ComponentMapper;
import com.artemis.World;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.math.Vector3;
import com.badlogic.gdx.utils.Pools;
import com.niz.component.ModelInfo;
import com.niz.component.Move;
import com.niz.component.Position;
import com.niz.component.Target;

public class AStand extends Action {
	private static final float MIN_RUN_DIST = 0.0852f;

	private static final String TAG = "AStand";

	private ComponentMapper<Position> positionMap;
	private ComponentMapper<Target> targetMap;

	private ComponentMapper<ModelInfo> animM;

	private ComponentMapper<Move> moveMap;
	
	

	
	@Override
	public void update(float dt) {
		//Gdx.app.log(TAG, "RUN"+(positionMap == null));
		Vector3 v = positionMap.get(parent)
				.pos,
				targetPos = targetMap.get(parent).v;
		float dist = Math.abs(targetPos.x-v.x);

		if (dist > MIN_RUN_DIST){
			Action node = Pools.obtain(ARun.class);
			this.insertBeforeMe(node);
			isFinished = true;
			//Gdx.app.log(TAG, "change to run"+dist);

		}
		
	}

	@Override
	public void onStart(World world) {
		//animation
		positionMap = world.getMapper(Position.class);
		targetMap = world.getMapper(Target.class);
		animM = world.getMapper(ModelInfo.class);
		moveMap = world.getMapper(Move.class);
		moveMap.get(parent).moving = false;
		//Gdx.app.log(TAG, "stsrt");
		animM.get(parent).anim.animate("Stand", -1, null,  ANIM_TRANSITION_TIME);
	}

	@Override
	public void onEnd() {
		//Gdx.app.log(TAG, "end");
	}

	
}
