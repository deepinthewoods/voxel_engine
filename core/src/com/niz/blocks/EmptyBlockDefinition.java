package com.niz.blocks;

import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.tests.g3d.voxel.BlockDefinition;
import com.badlogic.gdx.tests.g3d.voxel.VoxelWorld;

/**
 * Created by niz on 24/05/2014.
 */
public class EmptyBlockDefinition extends BlockDefinition {
    public EmptyBlockDefinition(TextureRegion empty, int i) {
        super(empty, i);
    }

    @Override
    public void onUpdate(int x, int y, int z, VoxelWorld world) {

    }
}
