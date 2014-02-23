package com.niz.actions;

import com.artemis.ComponentMapper;
import com.artemis.World;
import com.badlogic.gdx.math.Vector3;
import com.badlogic.gdx.utils.Pools;
import com.niz.component.Position;

public class AStand extends Action {
	private static final float MIN_RUN_DIST = 0.02f;

	private ComponentMapper<Position> positionMap;
	private ComponentMapper<Target> targetMap;
	
	

	@Override
	public void onAddToWorld(World world){
		positionMap = world.getMapper(Position.class);
		targetMap = world.getMapper(Target.class);
		
	}
	@Override
	public void update(float dt) {
		Vector3 v = positionMap.get(parent.e).pos, targetPos = targetMap.get(parent.e).v;
		if (v.dst2(targetPos) < MIN_RUN_DIST){
			Action node = Pools.obtain(AStand.class);
			this.insertBeforeMe(node);
			this.removeSelf();
		}
		
	}

	@Override
	public void onStart() {
		//animation
		
	}

	@Override
	public void onEnd() {

	}

	
}
