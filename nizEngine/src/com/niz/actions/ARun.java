package com.niz.actions;

import com.artemis.ComponentMapper;
import com.artemis.World;
import com.badlogic.gdx.math.Vector3;
import com.badlogic.gdx.utils.Pools;
import com.niz.component.ModelInfo;
import com.niz.component.Physics;
import com.niz.component.Position;

public class ARun extends Action {
	private static final float MIN_RUN_DIST = 0.02f;
	private ComponentMapper<Position> positionMap;
	private ComponentMapper<Target> targetMap;
	private ComponentMapper<ModelInfo> animM;
	
	

	@Override
	public void onAddToWorld(World world){
		positionMap = world.getMapper(Position.class);
		targetMap = world.getMapper(Target.class);
		animM = world.getMapper(ModelInfo.class);
		
	}
	
	@Override
	public void update(float dt) {
		//check position and stop
		Vector3 v = positionMap.get(parent.e).pos, targetPos = targetMap.get(parent.e).v;
		if (v.dst2(targetPos) < MIN_RUN_DIST){
			Action node = Pools.obtain(AStand.class);
			this.insertBeforeMe(node);
			this.removeSelf();
		}
	}

	@Override
	public void onStart() {
		//switch gravity
		float speed = 0.1f;
		int id = parent.e.get(Physics.class).id;
		float dir = (int) (targetMap.get(parent.e).v.x - positionMap.get(parent.e).pos.x);
		dir = Math.min(Math.max(dir, -1), 1);
		
		dir *= speed;
		this.physics.applyForce(id, dir, 0f, 0f);
		//animation
		
		animM.get(parent.e).anim.animate("Walk", ANIM_TRANSITION_TIME);
		
	}

	@Override
	public void onEnd() {
		// TODO Auto-generated method stub

	}

}
