package com.artemis.systems;

import com.artemis.Aspect;
import com.badlogic.gdx.graphics.Camera;
import com.badlogic.gdx.graphics.g3d.Environment;
import com.badlogic.gdx.graphics.g3d.ModelBatch;
import com.niz.ShapeBatch;

public abstract class DrawSystem extends EntitySystem {
	


	public DrawSystem(Aspect aspect) {
		super(aspect);
	}

	
	@Override public void initialize(){
		
	}
}
