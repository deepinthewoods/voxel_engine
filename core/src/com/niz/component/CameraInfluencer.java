package com.niz.component;

import com.artemis.Component;
import com.badlogic.gdx.math.Vector3;
import com.badlogic.gdx.utils.Array;
import com.badlogic.gdx.utils.FloatArray;

public class CameraInfluencer implements Component {

	//public float weight;
	public FloatArray weights = new FloatArray();
	public Array<Vector3> values = new Array<Vector3>();
	//public Vector3 value = new Vector3();

	@Override
	public void reset() {
		values.clear();
		weights.clear();
		//value.set(0,0,0);
		//weight = 0f;
	}

}
