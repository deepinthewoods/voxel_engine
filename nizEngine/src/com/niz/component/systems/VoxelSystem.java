package com.niz.component.systems;

import voxel.BlockDefinition;
import voxel.VoxelWorld;

import com.artemis.Aspect;
import com.artemis.Entity;
import com.artemis.systems.DrawSystem;
import com.badlogic.gdx.graphics.OrthographicCamera;
import com.badlogic.gdx.graphics.Pixmap;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.graphics.g3d.Environment;
import com.badlogic.gdx.graphics.g3d.ModelBatch;
import com.badlogic.gdx.math.Vector3;
import com.badlogic.gdx.utils.Array;
import com.sun.xml.internal.ws.api.pipe.Engine;

public class VoxelSystem extends DrawSystem {
	public VoxelWorld voxelWorld;
	
	Engine engine;
	public VoxelSystem(int x, int y, int z, boolean left, boolean right, boolean back, boolean front
			, boolean bottom, boolean top) {
		super(Aspect.getEmpty());
		voxelWorld = new VoxelWorld(x, y, z, left, right, back, front, bottom, top);
	}
	
	Vector3 tmp = new Vector3();

	
	
	@Override
	protected void processEntities(Array<Entity> entities) {
		//batch.render(voxelWorld, env);
		voxelWorld.processRandomUpdates();
	}
	public void set(TextureRegion[][] textures, BlockDefinition[] blockDefs,
			Pixmap fades) {
		voxelWorld.init(textures,  blockDefs,  fades);
		
	}

}
