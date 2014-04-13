package com.niz.actions;

import com.badlogic.gdx.tests.g3d.voxel.VoxelWorld;

import com.artemis.World;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.Camera;
import com.badlogic.gdx.math.MathUtils;
import com.badlogic.gdx.math.Vector3;
import com.badlogic.gdx.utils.Pools;
import com.niz.component.Physics;
import com.niz.component.Position;


public class AInactive extends Action{
	private static final String TAG = "a inactive";
	private float delayTime = 1.35f;
	private VoxelWorld world;
	private Camera camera;
	public AInactive set(Camera cam, VoxelWorld vw){
		camera = cam;
		world = vw;
		return this;
	}
	public AInactive(){
		isBlocking = true;
		lanes = LANE_MOVING;
	}
	@Override
	public void update(float dt) {

		if (isBlocking){
			boolean withinRangeOfEnemy = false;
			boolean onScreen = false;
			Vector3 position = parent.get(Position.class).pos;
			if (camera.frustum.sphereInFrustum(position, 15f)) onScreen = true;
			
			
			if (withinRangeOfEnemy || onScreen){
				isBlocking = false;
				 
				//parent.e.add(Physics.class).set(parent.e, parent.e.get(Position.class));
				//removeSelf();
				//Pools.free(this);
				isFinished = true;
				return;
			}
			
		} else {
			//Gdx.app.log(TAG, "inactive");
		}
		
		delay(delayTime );
	}

	@Override
	public void onStart(World world) {
		delay(MathUtils.random(delayTime));
		
	}

	@Override
	public void onEnd() {
		// TODO Auto-generated method stub
		
	}


}
