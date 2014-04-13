package com.niz.component;

import com.artemis.Component;
import com.badlogic.gdx.math.Vector3;

public class VelocityPredictor implements Component {
	public Vector3 vel = new Vector3();
	
	public float scale = 1f;
	public boolean x = true, y = true, z = true;
	
	
	@Override
	public void reset() {
	}

}
