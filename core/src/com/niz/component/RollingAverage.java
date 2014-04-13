package com.niz.component;

import com.artemis.Component;
import com.badlogic.gdx.math.Vector3;
import com.badlogic.gdx.utils.Array;

public abstract class RollingAverage implements Component {
	//public Vector3 target;
	public Array<Vector3> arr = new Array<Vector3>();;
	
	public int progress;
	
	
	public Vector3 total = new Vector3();

	public int size = 1;

	public Vector3 result = new Vector3();;
	
	
	@Override
	public void reset() {
		
	}

}
