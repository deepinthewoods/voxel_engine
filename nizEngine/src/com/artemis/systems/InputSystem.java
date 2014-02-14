package com.artemis.systems;

import com.artemis.Aspect;
import com.artemis.Entity;
import com.badlogic.gdx.InputProcessor;
import com.badlogic.gdx.utils.Array;

public abstract class InputSystem extends EntitySystem implements InputProcessor{

	public InputSystem() {
		super(Aspect.getEmpty());
		
	}

	@Override
	protected void processEntities(Array<Entity> entities) {
		// TODO Auto-generated method stub

	}

}
