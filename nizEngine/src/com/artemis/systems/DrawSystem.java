package com.artemis.systems;

import com.artemis.Aspect;

public abstract class DrawSystem extends EntitySystem {
	
	

	public DrawSystem(Aspect aspect) {
		super(aspect);
	}

	public float delta;
}
