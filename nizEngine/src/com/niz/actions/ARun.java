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

public class ARun extends Action {
	private static final float MIN_RUN_DIST = 0.064f;
	private static final String TAG = "ARun";
	private ComponentMapper<Position> positionMap;
	private ComponentMapper<Target> targetMap;
	private ComponentMapper<ModelInfo> animM;
	private ComponentMapper<Move> moveMap;
	
	

	
	
	@Override
	public void update(float dt) {
		//check position and stop
		Vector3 v = positionMap.get(parent).pos, targetPos = targetMap.get(parent).v;
		Move move = moveMap.get(parent);
		float dist = Math.abs(targetPos.x-v.x);

		if (dist < MIN_RUN_DIST){
			//Gdx.app.log(TAG, "change to stand"+dist);
			Action node = Pools.obtain(AStand.class);
			this.insertBeforeMe(node);
			isFinished = true;
			move.moving = false;
			return;
		}
		float dir = (targetMap.get(parent).v.x - positionMap.get(parent).pos.x);
		//Gdx.app.log(TAG, "dssit "+dist+ "  dir "+dir);
		dir = Math.min(Math.max(dir, -1), 1);
		move.moving = true;
		move.rotation = (dir < 0? 180:0);
		
	}

	@Override
	public void onStart(World world) {
		positionMap = world.getMapper(Position.class);
		targetMap = world.getMapper(Target.class);
		animM = world.getMapper(ModelInfo.class);
		moveMap = world.getMapper(Move.class);
		//switch gravity
		float speed = 0.1f;
		//int id = parent.get(Physics.class).id;
		//Gdx.app.log(TAG, "start"+(parent));
		
		int dir = (int) (targetMap.get(parent).v.x - positionMap.get(parent).pos.x);
		dir = Math.min(Math.max(dir, -1), 1);
		
		//dir *= speed;
		//this.physics.applyForce(id, dir, 0f, 0f);
		//animation
		
		animM.get(parent).anim.animate("Walk", -1, null,  ANIM_TRANSITION_TIME);
		
	}

	@Override
	public void onEnd() {
		// TODO Auto-generated method stub
		//Gdx.app.log(TAG, "end");
	}

}
