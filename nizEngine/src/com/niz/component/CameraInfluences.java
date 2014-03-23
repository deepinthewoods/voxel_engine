package com.niz.component;

import com.artemis.Component;
import com.artemis.Entity;
import com.badlogic.gdx.math.Vector3;
import com.badlogic.gdx.utils.Array;
import com.badlogic.gdx.utils.FloatArray;

public class CameraInfluences implements Component {
	public Array<Vector3> influences = new Array<Vector3>();
	public FloatArray weights = new FloatArray();
	//public float xLimit, yLimit;
	public void add(Entity ent, float weight){
		
		influences.add(ent.get(Position.class).pos);
		
		weights.add(weight);
	}
	@Override
	public void reset() {
	}

}
