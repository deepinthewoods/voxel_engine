package com.badlogic.gdx.tests.g3d.voxel;

import com.badlogic.gdx.math.Vector3;

/**
 * Created by niz on 14/06/2014.
 */
public interface IVoxelPreprocessor {
    void process(GreedyMesher.VoxelFace[][][][] voxels, int w, int h, Vector3 offset);
}
