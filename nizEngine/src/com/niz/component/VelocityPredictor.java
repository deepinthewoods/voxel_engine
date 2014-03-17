package com.niz.component;

import com.artemis.Component;
import com.artemis.Entity;
import com.badlogic.gdx.math.Vector3;

public class VelocityPredictor implements Component {
	public Vector3 vel = new Vector3();
	public Entity e;
	public float scale;
	public boolean x = true, y = true, z = true;
	
	public void set(Entity e, float scale){
		this.e = e;
		this.scale = scale;
	}
	
	@Override
	public void reset() {
	}

}
