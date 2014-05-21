package com.niz.actions;

import com.artemis.ComponentMapper;
import com.artemis.World;
import com.badlogic.gdx.math.Vector3;
import com.badlogic.gdx.utils.Pools;
import com.niz.component.*;

public class ARun extends Action {
	protected static final float MIN_RUN_DIST = 0.1264f;
	private static final String TAG = "ARun";
	private ComponentMapper<Position> positionMap;
	private ComponentMapper<ModelInfo> animM;
	private ComponentMapper<Move> moveMap;
	private ComponentMapper<AABBBody> bodyMap;
	private ComponentMapper<Brain> brainM;
	
	

	
	
	@Override
	public void update(float dt) {
		//check position and stop
		Brain brain = brainM.get(parent);
		Vector3 v = positionMap.get(parent).pos, targetPos = brain.getShortTarget();
		Move move = moveMap.get(parent);
		AABBBody body = bodyMap.get(parent);
		float dist = Math.abs(targetPos.x-v.x);

		if (!body.onGround){
			Action node = Pools.obtain(AJump.class);
			//move.moving = false;
			this.insertBeforeMe(node);
			isFinished = true;
			return;
			//move.moving = false;
		}
		
		if (dist < MIN_RUN_DIST){
			//Gdx.app.log(TAG, "arrive"+dist);
			Action node = Pools.obtain(AStand.class);
			this.insertBeforeMe(node);
			isFinished = true;
			move.moving = false;
			return;
		}
		float dir = (targetPos.x - positionMap.get(parent).pos.x);
		//Gdx.app.log(TAG, "dssit "+dist+ "  dir "+dir);
		dir = Math.min(Math.max(dir, -1), 1);
		move.moving = true;
		move.rotation = (dir < 0? 180:0);
		
	}

	
	@Override
	public void onStart(World world) {
		positionMap = world.getMapper(Position.class);
		animM = world.getMapper(ModelInfo.class);
		moveMap = world.getMapper(Move.class);
		bodyMap = world.getMapper(AABBBody.class);
		brainM = world.getMapper(Brain.class);
		//switch gravity
		float speed = 0.1f;
		//int id = parent.get(Physics.class).id;
		//Gdx.app.log(TAG, "start"+(parent));
		
		//int dir = (int) (targetMap.get(parent).v.x - positionMap.get(parent).pos.x);
		//dir = Math.min(Math.max(dir, -1), 1);
		//moveMap.get(parent).rotation = (dir < 0? 180:0);
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
