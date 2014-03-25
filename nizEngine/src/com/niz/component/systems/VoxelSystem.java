package com.niz.component.systems;

import voxel.BlockDefinition;

import com.artemis.Aspect;
import com.artemis.Entity;
import com.artemis.systems.EntitySystem;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.graphics.g3d.Material;
import com.badlogic.gdx.graphics.g3d.attributes.ColorAttribute;
import com.badlogic.gdx.graphics.g3d.attributes.TextureAttribute;
import com.badlogic.gdx.math.Vector3;
import com.badlogic.gdx.tests.g3d.voxel.GreedyMesher;
import com.badlogic.gdx.tests.g3d.voxel.MeshBatcher;
import com.badlogic.gdx.tests.g3d.voxel.SimpleMesher;
import com.badlogic.gdx.tests.g3d.voxel.VoxelWorld;
import com.badlogic.gdx.utils.Array;

public class VoxelSystem extends EntitySystem {
	
	public VoxelWorld voxelWorld;
	private BlockDefinition[] defs;
	private TextureRegion[] tiles;

	public VoxelSystem(int x, int y, int z, boolean left, boolean right, boolean back, boolean front
			, boolean bottom, boolean top, BlockDefinition[] defs, TextureRegion[] tiles) {
		super(Aspect.getEmpty());
		Material material = new Material(new ColorAttribute(ColorAttribute.Diffuse,  1f, 1f, 1f, 1)
		//, new TextureAttribute(TextureAttribute.Diffuse, tiles[0].getTexture()) 
		);
		MeshBatcher batch = new MeshBatcher(10000000, 10000000, 13);
		voxelWorld = new VoxelWorld(defs, material, x, y, z, 
				new GreedyMesher(batch)
				//new SimpleMesher(VoxelWorld.CHUNK_SIZE_X, VoxelWorld.CHUNK_SIZE_Y, VoxelWorld.CHUNK_SIZE_Z)
				, batch 
				);

	}
	
	Vector3 tmp = new Vector3();
	
	
	
	@Override
	protected void processEntities(Array<Entity> entities) {
		//batch.render(voxelWorld, env);
		//voxelWorld.processRandomUpdates();
	}
	

}
