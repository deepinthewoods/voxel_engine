package com.badlogic.gdx.tests.g3d.voxel;

import com.badlogic.gdx.graphics.Mesh;
import com.badlogic.gdx.math.Vector3;

/**
 * Created by niz on 14/06/2014.
 */
public interface MeshBatch {

    Mesh newMesh(int size);

    int flushCache(VoxelChunk chunk, GreedyMesher greedyMesher);

    void quad(Vector3 bottomLeft,
              Vector3 topLeft,
              Vector3 topRight,
              Vector3 bottomRight,
              int width,
              int height,
              GreedyMesher.VoxelFace voxel,
              boolean backFace);
}
