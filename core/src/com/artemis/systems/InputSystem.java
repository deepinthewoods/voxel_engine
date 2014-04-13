package com.artemis.systems;

import com.artemis.Aspect;
import com.badlogic.gdx.InputProcessor;
import com.niz.component.Player;

public abstract class InputSystem extends EntitySystem implements InputProcessor{

	public InputSystem(Aspect aspect) {
		super(aspect);
	}

	
}
