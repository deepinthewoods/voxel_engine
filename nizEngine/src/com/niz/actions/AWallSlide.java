package com.niz.actions;

import com.artemis.ComponentMapper;
import com.artemis.World;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.utils.Pools;
import com.niz.component.AABBBody;
import com.niz.component.Move;

public class AWallSlide extends Action {

	private static final String TAG = "wall slide action";
	private ComponentMapper<Move> moveMap;
	private ComponentMapper<AABBBody> bodyMap;

	@Override
	public void update(float dt) {
		if (bodyMap.get(parent).onGround){
			
			AJump stand = Pools.obtain(AJump.class);
			this.insertBeforeMe(stand);
			isFinished = true;
			bodyMap.get(parent).onWall = false;
		}
	}

	@Override
	public void onStart(World world) {

		bodyMap = world.getMapper(AABBBody.class);
		Gdx.app.log(TAG, "start wallslide");

		moveMap = world.getMapper(Move.class);
		//moveMap.get(parent).moving =false;
	}

	@Override
	public void onEnd() {
	}

}
