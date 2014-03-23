package com.badlogic.gdx.tests.g3d.voxel;

import com.badlogic.gdx.graphics.Mesh;

public class SimpleMesher implements Mesher{

	@Override
	public int calculateVertices(float[] vertices, VoxelChunk chunk, Mesh mesh,
			VoxelWorld voxelWorld) {
		return chunk.calculateVertices(vertices);
	}

	

	

}
