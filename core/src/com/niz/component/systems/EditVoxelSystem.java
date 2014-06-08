package com.niz.component.systems;


import com.artemis.Aspect;
import com.artemis.Entity;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.tests.g3d.voxel.EditGreedyMesher;
import com.badlogic.gdx.tests.g3d.voxel.MeshBatcher;
import com.badlogic.gdx.tests.g3d.voxel.VoxelWorld;
import com.badlogic.gdx.utils.Array;
import com.niz.component.Player;
import com.niz.component.Position;

public class EditVoxelSystem extends VoxelSystem {


    public final Color[] BLOCK_COLORS;
    private final EditGreedyMesher mesher;
    private final MeshBatcher batch;

    public EditVoxelSystem() {
		super(Aspect.getAspectForAll(Player.class, Position.class));
        BLOCK_COLORS = new Color[256];
        for (int i = 0; i < 256; i++){
            BLOCK_COLORS[i] = new Color(Color.WHITE);
        }
		int x = 4, y = 4, z = 4;
		batch = new MeshBatcher(10000000, 10000000, 13);
        mesher = new EditGreedyMesher(batch, BLOCK_COLORS);
        voxelWorld = new VoxelWorld(x, y, z,


                 16, 16, 1
				);
       // Gdx.app.log(TAG, "material"+voxelWorld.m);

	}

    @Override
    protected void processEntities(Array<Entity> entities) {
        if (entities.size == 0) return;
        voxelWorld.makeMesh(posM.get(entities.get(0)), mesher, batch);


    }
	
	
	




}
