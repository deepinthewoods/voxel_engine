package com.niz.component;

import com.artemis.Component;



public class Move implements Component {

	public float rotation;
	public float speed = .05f, speedLimit = .097f;;
	public boolean moving, jumpQueued;
	public float jumpStrength = 1f, jumpForce = 0.007f;
	public boolean jumping;
	public float jumpTime = .3f;
	public int jumpEndTick;
	//public float jumpingSpeed = .1f;
	
	@Override
	public void reset() {
		// TODO Auto-generated method stub
		
	}

}
