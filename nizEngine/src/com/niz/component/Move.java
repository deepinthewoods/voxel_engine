package com.niz.component;

import com.artemis.Component;



public class Move implements Component {

	public float rotation;
	public float speed = .04f, speedLimit = .07f;;
	public boolean moving, jumpQueued;
	public float jumpStrength = 2f;
	public boolean jumping;
	//public float jumpingSpeed = .1f;
	
	@Override
	public void reset() {
		// TODO Auto-generated method stub
		
	}

}
