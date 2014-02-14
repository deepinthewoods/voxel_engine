package com.niz.component;

import com.artemis.Component;
import com.artemis.Entity;
import com.badlogic.gdx.math.Vector3;
import com.niz.physics.JPhysicsEngine;

public class Physics implements Component {
	//private static final int POS_HASH = hash("position");
	//private static IntArray siblings = new IntArray(new int[]{POS_HASH});
	public int id;
	public Vector3 oldPosition;
	public static JPhysicsEngine physics;

	public void set(Entity e, Position pos) {
		
		id = physics.addParticle(pos.pos.x, pos.pos.y, pos.pos.z, pos.pos);
		//pos.pos = physics.getPositionVector(id);
		oldPosition = physics.getOldPositionVector(id);
		//Gdx.app.log("physics", "+set "+id);
		
	}

	@Override
	public void reset() {
		// TODO Auto-generated method stub
		
	}


}
