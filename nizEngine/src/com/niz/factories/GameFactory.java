package com.niz.factories;

import com.artemis.Component;
import com.artemis.Entity;
import com.artemis.World;
import com.badlogic.gdx.assets.AssetManager;
import com.badlogic.gdx.graphics.Camera;
import com.badlogic.gdx.graphics.g3d.ModelBatch;
import com.badlogic.gdx.tests.g3d.voxel.VoxelWorld;
import com.badlogic.gdx.utils.Array;

public abstract class GameFactory {

public abstract void init(World world, AssetManager assets, Camera camera) ;

public abstract void doneLoading(float timeStep, World world, AssetManager assets, Camera camera, ModelBatch modelBatch);

public abstract void newGame(World world);

public abstract void load(World world);

	Array<Component> components = new Array<Component>();
	public void save(World world){
		Array<Entity> es = world.getEntityManager().getEntities();
		for (Entity e : es){
			e.getComponents(components);
			//write components
			
		}
	}

	
}
