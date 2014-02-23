package com.niz.blocks;

import voxel.BlockDefinition;

import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.tests.g3d.voxel.VoxelWorld;

public class TopBottomBlock extends BlockDefinition {
	float[] su = new float[6], sv = new float[6], su2 = new float[6], sv2 = new float[6];
	
	
	public TopBottomBlock(TextureRegion[][] tiles, int tIndexTop, int tIndexBottom, int tIndexSide) {
		
		makeTile(tIndexTop, TOP, tiles);
		makeTile(tIndexBottom, BOTTOM, tiles);
		makeTile(tIndexSide, LEFT, tiles);
		makeTile(tIndexSide, RIGHT, tiles);
		makeTile(tIndexTop, FRONT, tiles);
		makeTile(tIndexSide, BACK, tiles);
		
		
		
		
	
	}
	
	private void makeTile(int tileIndex, int side, TextureRegion[][] tiles){
		int tileIndexX = tileIndex % tiles[0].length;
		int tileIndexY = tileIndex / tiles[0].length;
		TextureRegion region =  tiles[tileIndexY][tileIndexX];
		su[side] = region.getU();
		sv[side] = region.getV();
		su2[side] = region.getU2();
		sv2[side] = region.getV2();
	}
	
	/*public int getTileIndex(int orientation){
		switch (orientation){
		
		}
		return 0;
	}*/
	
	public float[] getUVs(int side) {
		uvs[0] = su[side];
		uvs[1] = sv[side];
		uvs[2] = su2[side];
		uvs[3] = sv2[side];
		return uvs;
	}

	@Override
	public void onUpdate(int x, int y, int z, VoxelWorld world) {
	}

	

}
