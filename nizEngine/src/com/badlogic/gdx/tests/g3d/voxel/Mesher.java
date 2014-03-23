package com.badlogic.gdx.tests.g3d.voxel;

import com.badlogic.gdx.graphics.Mesh;

public interface Mesher {

	int calculateVertices(float[] vertices, VoxelChunk chunk, Mesh mesh, VoxelWorld voxelWorld);

}
