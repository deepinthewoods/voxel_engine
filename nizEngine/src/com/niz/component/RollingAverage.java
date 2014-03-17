package com.niz.component;

import com.artemis.Component;
import com.badlogic.gdx.math.Vector3;
import com.badlogic.gdx.utils.Array;

public class RollingAverage implements Component {
	public Vector3 target;
	public Array<Vector3> arr;
	
	public int progress;
	
	public Vector3 total = new Vector3();
	
	public void set(Vector3 target, int size){
		arr = new Array<Vector3>();
		for (int i = 0; i < size; i++){
			arr.add(new Vector3(target));
			total.add(target);
		}
		this.target = target;
	}
	@Override
	public void reset() {
	}

}
