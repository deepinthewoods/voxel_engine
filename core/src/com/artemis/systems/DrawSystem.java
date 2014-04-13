package com.artemis.systems;

import com.artemis.Aspect;
import com.badlogic.gdx.graphics.Camera;
import com.badlogic.gdx.graphics.g3d.Environment;
import com.badlogic.gdx.graphics.g3d.ModelBatch;
import com.niz.ShapeBatch;

public abstract class DrawSystem extends EntitySystem {
	
	protected ModelBatch modelBatch;
	protected Camera camera;
	protected Environment env;
	protected ShapeBatch shapeBatch;
	public void set(ModelBatch modelBatch, Camera cam, Environment env, ShapeBatch shapeBatch){
		this.modelBatch = modelBatch;
		this.camera = cam;
		this.env = env;
		this.shapeBatch = shapeBatch;
	}

	public DrawSystem(Aspect aspect) {
		super(aspect);
	}

	
	@Override public void initialize(){
		
	}
}
