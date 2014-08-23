package com.niz.component;

import com.artemis.Component;
import com.badlogic.gdx.math.Vector3;
import com.niz.physics.JPhysicsEngine;

public class Physics implements Component {
	//private static final int POS_HASH = hash("position");
	//private static IntArray siblings = new IntArray(new int[]{POS_HASH});
	public int id = -1;
	public Vector3 oldPosition;



	@Override
	public void reset() {
		id = -1;
		
	}


}
