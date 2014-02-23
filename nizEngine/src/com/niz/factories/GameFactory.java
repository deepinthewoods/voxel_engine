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

public abstract void init(World world, float timeStep, AssetManager assets,
		Camera worldCamera, ModelBatch modelBatch) ;

public abstract void doneLoading(AssetManager assets, World world, Camera camera, ModelBatch modelBatch);

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
