package com.niz.component;

import com.artemis.Component;
import com.badlogic.gdx.math.Vector3;

public class Position implements Component {
	public Vector3 pos = new Vector3();
    public int plane;
	@Override
	public void reset() {
		pos.set(0,0,0);

	}

}
