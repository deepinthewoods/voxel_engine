package com.niz.actions;

import com.artemis.ComponentMapper;
import com.artemis.World;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.utils.Pools;
import com.niz.component.AABBBody;
import com.niz.component.Move;

public class AJump extends Action {

	private static final String TAG = "jump action";
	private ComponentMapper<AABBBody> bodyMap;
	private ComponentMapper<Move> moveMap;

	@Override
	public void update(float dt) {
		//Gdx.app.log(TAG, "body"+(bodyMap == null));
		AABBBody body = bodyMap.get(parent);
		if (body.onGround){
			insertAfterMe(Pools.obtain(AStand.class));
			isFinished = true;
		} else if (body.onWall){
			insertAfterMe(Pools.obtain(AWallSlide.class));
			isFinished = true;
		}
	}

	@Override
	public void onStart(World world) {
		bodyMap = world.getMapper(AABBBody.class);
		moveMap = world.getMapper(Move.class);
		moveMap.get(parent).moving = false;
		//Gdx.app.log(TAG, "start");
	}

	@Override
	public void onEnd() {
	}

}
