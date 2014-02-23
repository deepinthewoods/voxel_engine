package com.niz.component.systems;

import com.artemis.Aspect;
import com.artemis.Entity;
import com.artemis.systems.DrawSystem;
import com.badlogic.gdx.graphics.OrthographicCamera;
import com.badlogic.gdx.graphics.g3d.Environment;
import com.badlogic.gdx.graphics.g3d.ModelBatch;
import com.badlogic.gdx.math.Vector3;
import com.badlogic.gdx.utils.Array;

public class VoxelRenderingSystem extends DrawSystem {
	public VoxelRenderingSystem() {
		super(Aspect.getEmpty());
		// TODO Auto-generated constructor stub
	}
	ModelBatch batch;
	OrthographicCamera cam;
	Environment env;
	//Engine engine;
	
	public void set(ModelBatch batch, OrthographicCamera cam, Environment env){
		this.batch = batch;
		this.cam = cam;
		this.env = env;
	}
	Vector3 tmp = new Vector3();
	@Override
	protected void processEntities(Array<Entity> entities) {
		
	}
	
	
	

}
