package com.niz.component.systems;

import com.artemis.Aspect;
import com.artemis.ComponentMapper;
import com.artemis.Entity;
import com.artemis.systems.EntityProcessingSystem;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.math.Vector3;
import com.badlogic.gdx.tests.g3d.voxel.GreedyMesher;
import com.badlogic.gdx.tests.g3d.voxel.UberMesh;
import com.badlogic.gdx.tests.g3d.voxel.VoxelChunk;
import com.badlogic.gdx.tests.g3d.voxel.VoxelWorld;
import com.niz.component.Player;
import com.niz.component.Position;

public class VoxelMeshingSystem extends EntityProcessingSystem {
	private static final String TAG = "voxel mesher";





	public VoxelMeshingSystem() {
		super(Aspect.getAspectForAll(Player.class, Position.class));
	}
	GreedyMesher mesher;
	private ComponentMapper<Position> posM;
	private VoxelWorld voxelWorld;
	
	
		
	
	
	@Override
	public void initialize(){
		UberMesh mesh;

		mesh = new UberMesh(VoxelChunk.VERTEX_SIZE * 6 * VoxelWorld.CHUNK_SIZE_X * VoxelWorld.CHUNK_SIZE_Y * VoxelWorld.CHUNK_SIZE_Z*2);
		mesher = new GreedyMesher(mesh);
		posM = world.getMapper(Position.class);
		voxelWorld = world.getSystem(VoxelSystem.class).voxelWorld;
	}
	@Override
	protected void process(Entity e) {
		//find chunk
				
				VoxelChunk chunk = findChunk(posM.get(e).pos);
				if (chunk != null){
					//voxelWorld.setNumVertices(chunk.index, mesher.calculateVertices(chunk, mesh, voxelWorld));
					//voxelWorld.setDirty(chunk.index, false);
					Gdx.app.log(TAG, "mesh");
				}
	}
	private VoxelChunk findChunk(Vector3 pos) {
		//iterate through all voxels, find closest with flag
		return voxelWorld.getClosestDirtyChunk(pos);
	}

}
