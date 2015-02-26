package com.badlogic.gdx.tests.g3d.voxel;

import com.badlogic.gdx.graphics.Mesh;
import com.badlogic.gdx.math.Vector3;

public interface Mesher {

	void begin(VoxelChunk chunk, VoxelWorld voxelWorld);
    boolean process();
    int end();






}
