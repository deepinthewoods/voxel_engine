package com.niz.component.systems;


import com.artemis.Aspect;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.tests.g3d.voxel.EditGreedyMesher;
import com.badlogic.gdx.tests.g3d.voxel.MeshBatcher;
import com.badlogic.gdx.tests.g3d.voxel.VoxelWorld;
import com.niz.component.Player;
import com.niz.component.Position;

public class EditVoxelSystem extends VoxelSystem {


    public final Color[] blockColors;

    public EditVoxelSystem() {
		super(Aspect.getAspectForAll(Player.class, Position.class));
        blockColors = new Color[256];
        for (int i = 0; i < 256; i++){
            blockColors[i] = new Color();
        }
		int x = 12, y = 2, z = 2;
		MeshBatcher batch = new MeshBatcher(10000000, 10000000, 13);
		voxelWorld = new VoxelWorld(x, y, z,
				new EditGreedyMesher(batch, blockColors)
				//new SimpleMesher(VoxelWorld.CHUNK_SIZE_X, VoxelWorld.CHUNK_SIZE_Y, VoxelWorld.CHUNK_SIZE_Z)
				, batch
                , 16, 16
				);
       // Gdx.app.log(TAG, "material"+voxelWorld.m);

	}


	
	
	




}
