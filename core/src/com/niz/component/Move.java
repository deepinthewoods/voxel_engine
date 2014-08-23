package com.niz.component;

import com.artemis.Component;
import com.artemis.Entity;
import com.badlogic.gdx.Gdx;


public class Move implements Component {

    private static final String TAG = "Move";
    public float rotation;
	public float moveAcceleration = .02f;
	public boolean moving, jumpQueued;
	public float jumpStrength =     0.07f
            , jumpStrengthMoving =  0.07f;
	public boolean jumping;
    public float jumpTime = 1000000.5f;
    public long jumpEndTick;
    public float jumpForce =        0.0012f;
    public float jumpForceMoving =  0.0011f;

    public int jumpForceEndTick;
    public float moveSpeed = .04f;
    //public float jumpingSpeed = .1f;
	
	@Override
	public void reset() {
        moveSpeed = .04f;
        jumpForce = 0.002f;
        jumpTime = .5f;
        jumpStrength = .07f;
        moveAcceleration = .01f;

	}

    public void startJumping(Entity e){

        //Gdx.app.log(TAG, "strat jumping");

        jumpQueued = true;
    }

    public void stopJumping(Entity e) {
        jumpEndTick = e.tick -1;
        jumpQueued = false;
    }
}
