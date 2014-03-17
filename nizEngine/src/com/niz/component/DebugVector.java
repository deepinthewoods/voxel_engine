package com.niz.component;

import com.artemis.Component;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.math.Vector3;
import com.badlogic.gdx.utils.Array;

public class DebugVector implements Component {
	public Array<Vector3> arr = new Array<Vector3>();
	public Array<Color> colors = new Array<Color>();
	
	public DebugVector add(Vector3 v, Color c){
		arr.add(v);
		colors.add(c);
		return this;
	}
	
	@Override
	public void reset() {
	}

}
