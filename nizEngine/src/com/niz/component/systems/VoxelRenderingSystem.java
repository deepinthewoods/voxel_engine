package com.niz.component.systems;

import voxel.VoxelWorld;

import com.artemis.Aspect;
import com.artemis.Entity;
import com.artemis.systems.DrawSystem;
import com.artemis.systems.EntityProcessingSystem;
import com.artemis.systems.VoidEntitySystem;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.OrthographicCamera;
import com.badlogic.gdx.graphics.g3d.Environment;
import com.badlogic.gdx.graphics.g3d.ModelBatch;
import com.badlogic.gdx.math.Vector3;
import com.badlogic.gdx.utils.Array;
import com.sun.xml.internal.ws.api.pipe.Engine;

public class VoxelRenderingSystem extends DrawSystem {
	public VoxelRenderingSystem(Aspect aspect) {
		super(Aspect.getEmpty());
		// TODO Auto-generated constructor stub
	}
	VoxelWorld world;
	ModelBatch batch;
	OrthographicCamera cam;
	Environment env;
	//Engine engine;
	
	public void set(VoxelWorld world, ModelBatch batch, OrthographicCamera cam, Environment env){
		this.world = world;
		this.batch = batch;
		this.cam = cam;
		this.env = env;
	}
	Vector3 tmp = new Vector3();
	@Override
	protected void processEntities(Array<Entity> entities) {
		batch.render(world, env);
	}
	
	
	

}
