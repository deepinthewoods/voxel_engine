package com.badlogic.gdx.tests.g3d.voxel;

import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.math.Vector3;
import com.badlogic.gdx.tests.g3d.voxel.GreedyMesher;
import com.badlogic.gdx.tests.g3d.voxel.MeshBatcher;
import com.badlogic.gdx.tests.g3d.voxel.Mesher;

/**
 * Created by niz on 24/05/2014.
 */
public class EditGreedyMesher extends GreedyMesher {
    private final Color[] blockColors;

    public EditGreedyMesher(MeshBatcher batch, Color[] blockColors) {
        super(batch);
        this.blockColors = blockColors;
    }

    void quad(final Vector3 bottomLeft,
              final Vector3 topLeft,
              final Vector3 topRight,
              final Vector3 bottomRight,
              final int width,
              final int height,
              final VoxelFace voxel,
              final boolean backFace) {
        // Gdx.app.log(TAG, "type"+voxel.type+topLeft);
        final Vector3 [] vertices = new Vector3[4];

        vertices[2] = topLeft.scl(VOXEL_SIZE);
        vertices[3] = topRight.scl(VOXEL_SIZE);
        vertices[0] = bottomLeft.scl(VOXEL_SIZE);
        vertices[1] = bottomRight.scl(VOXEL_SIZE);
        //013 320  310 023
        boolean flip = voxel.shouldFlipTriangles();

        final int [] indexes = backFace ?flip?new int[]{0,1,3, 3,2,0}: new int[] { 2,0,1, 1,3,2 }

                : flip?new int[]{3,1,0, 0,2,3}:new int[]{ 2,3,1, 1,0,2 };


        meshBatch.addVerticesColored(vertices, voxel.vertex, indexes, flip, voxel, width, height, blockColors);

    }
}
