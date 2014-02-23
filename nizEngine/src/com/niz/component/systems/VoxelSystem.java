package com.niz.component.systems;

import voxel.BlockDefinition;

import com.artemis.Aspect;
import com.artemis.Entity;
import com.artemis.systems.DrawSystem;
import com.badlogic.gdx.graphics.Camera;
import com.badlogic.gdx.graphics.Pixmap;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.math.Vector3;
import com.badlogic.gdx.utils.Array;

public class VoxelSystem extends DrawSystem {
	
	public VoxelSystem(int x, int y, int z, boolean left, boolean right, boolean back, boolean front
			, boolean bottom, boolean top) {
		super(Aspect.getEmpty());
	}
	
	Vector3 tmp = new Vector3();

	
	
	@Override
	protected void processEntities(Array<Entity> entities) {
		//batch.render(voxelWorld, env);
		//voxelWorld.processRandomUpdates();
	}
	public void set(TextureRegion[][] textures, BlockDefinition[] blockDefs,
			Pixmap fades, Camera camera) {
		//voxelWorld.init(textures,  blockDefs,  fades, camera);
		
	}

}
