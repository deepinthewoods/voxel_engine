package com.badlogic.gdx.tests.g3d.voxel;

import com.badlogic.gdx.graphics.Mesh;

public interface Mesher {

	int calculateVertices(VoxelChunk chunk, VoxelWorld voxelWorld, MeshBatcher batch);


	Mesh newMesh(int vertexCount);

}
