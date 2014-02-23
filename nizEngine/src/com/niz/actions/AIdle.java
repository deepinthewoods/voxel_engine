package com.niz.actions;

import com.artemis.World;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.g3d.utils.AnimationController;
import com.niz.component.Body;
import com.niz.component.ModelInfo;
import com.niz.component.Move;
import com.niz.component.Physics;
import com.niz.component.Position;

public class AIdle extends Action {

	private static final String idleName = "stand";
	private static final float ANIM_SPEED = .1f;
	private static final String TAG = "idle action ";
	public AIdle(){
		super();
		lanes = LANE_MOVING;
		
	}
	@Override
	public void update(float dt) {
		Move mov = parent.e.get(Move.class);
		mov.moving = false;
		Body body = parent.e.get(Body.class);
		body.onGround = true;
		Position position = parent.e.get(Position.class);
		Physics physics = parent.e.get(Physics.class);
		physics.oldPosition.set(position.pos);
		//setTargetAngle();
		AnimationController anim = parent.e.get(ModelInfo.class).anim;
		anim.animate(idleName, -1, ANIM_SPEED, null, .1f);
		//Gdx.app.log(TAG, "start Idling");
		
		//delay(16);
	}

	@Override
	public void onStart() {
		
	}

	@Override
	public void onEnd() {
		
	}
	@Override
	public void onAddToWorld(World world) {
		// TODO Auto-generated method stub
		
	}

}
