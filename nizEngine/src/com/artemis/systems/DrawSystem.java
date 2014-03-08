package com.artemis.systems;

import com.artemis.Aspect;
import com.badlogic.gdx.graphics.Camera;
import com.badlogic.gdx.graphics.g3d.Environment;
import com.badlogic.gdx.graphics.g3d.ModelBatch;

public abstract class DrawSystem extends EntitySystem {
	
	protected ModelBatch modelBatch;
	protected Camera camera;
	protected Environment env;
	
	public void set(ModelBatch modelBatch, Camera cam, Environment env){
		this.modelBatch = modelBatch;
		this.camera = cam;
		this.env = env;
	}

	public DrawSystem(Aspect aspect) {
		super(aspect);
	}

	public float delta;
	
	@Override public void initialize(){
		
	}
}
