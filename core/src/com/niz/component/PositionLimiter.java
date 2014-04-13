package com.niz.component;

import com.artemis.Component;
import com.badlogic.gdx.math.Vector3;

public class PositionLimiter implements Component {
	Vector3 target;
	float distance;
	@Override
	public void reset() {
	}
	public void set(Vector3 pos, float dist) {
		distance = dist;
		target = pos;
	}

}
