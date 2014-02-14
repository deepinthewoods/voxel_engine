package com.niz.component.systems;

import voxel.VoxelWorld;

import com.artemis.Aspect;
import com.artemis.Entity;
import com.artemis.systems.DrawSystem;
import com.badlogic.gdx.graphics.Camera;
import com.badlogic.gdx.graphics.OrthographicCamera;
import com.badlogic.gdx.graphics.g3d.Environment;
import com.badlogic.gdx.graphics.g3d.ModelBatch;
import com.badlogic.gdx.utils.Array;

public class VoxelRenderingSystem extends DrawSystem {

	private VoxelWorld voxelWorld;

	public VoxelRenderingSystem(VoxelWorld voxelWorld) {
		super(Aspect.getEmpty());
		this.voxelWorld = voxelWorld;
		// TODO Auto-generated constructor stub
	}
	ModelBatch batch;
	Camera cam;
	Environment env;
	public void set(ModelBatch batch, Camera cam, Environment env){
		//this.world = world;
		this.batch = batch;
		this.cam = cam;
		this.env = env;
	}
	@Override
	protected void processEntities(Array<Entity> entities) {
		batch.render(voxelWorld, env);

	}
	

}
