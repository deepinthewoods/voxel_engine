package com.niz.component;

import com.artemis.Component;



public class Move implements Component {

	public float rotation;
	public float speed = .075f;
	public boolean moving, jumpQueued;
	@Override
	public void reset() {
		// TODO Auto-generated method stub
		
	}

}
