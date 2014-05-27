package com.badlogic.gdx.tests.g3d.voxel;

import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.math.Vector3;
import com.badlogic.gdx.tests.g3d.voxel.GreedyMesher.VoxelFace;

public abstract class BlockDefinition {
    public static Vector3[] reflectedNormals = new Vector3[6];
    protected float[] uvs = new float[4];
	public static float tile_size_px = 16;
	//protected static int tile_side_length = 8;
	public static final int TOP = 0, BOTTOM = 1, LEFT = 2, RIGHT = 3, FRONT = 4, BACK = 5;
	public int tileIndex;
	private float u, v;
	public int dayLightLoss = 15;
	public byte lightLoss = 15;
	public boolean isSolid = true;
	public int aoValue;
	public int lightValue;
	public boolean isEmpty = false;;
	private static BlockDefinition[] defs = new BlockDefinition[256];
	public BlockDefinition(){
		
	}
	public BlockDefinition(TextureRegion region, int tIndex){
		tileIndex = tIndex;
		//int tileIndexX = tileIndex % tiles[0].length;
		//int tileIndexY = tileIndex / tiles[0].length;
		//TextureRegion region =  tiles[tileIndexY][tileIndexX];
		//Gdx.app.log("index:"+tileIndex+"block def", "tx"+tileIndexX+"  ty"+tileIndexY);
		u = region.getU();
		v = region.getV();
		//u2 = region.getU2();
		//v2 = region.getV2();
		
	}
	
	public abstract void onUpdate(int x, int y, int z, VoxelWorld world);
		
	
		
	
	public String toString(){
		return "block Index:"+tileIndex;
	}

	

	public float[] getUVs(int side) {
		//Gdx.app.log("Block def", "UVs"+u+v+u2+v2);
		uvs[0] = u;
		uvs[1] = v;
		//uvs[2] = u2;
		//uvs[3] = v2;
		return uvs;
	}
	/*public static BlockDefinition get(byte block) {
		return defs[block];
	}
	public static void add(int id, BlockDefinition def){
		defs[id] = def;
	}
	public void clearDefs(){
		for (int i = 0; i < defs.length; i++){
			defs[i] = null;
		}
	}*/
	public boolean collide(int side, Vector3 v) {
		float x = v.x%1, y = v.y%1, z = v.z%1;
		return collide(side, x,y,z, v);
	}
	
	private boolean collide(int side, float x, float y, float z, Vector3 v) {
		switch (side){
		
			case BOTTOM:if (isSolid){
				v.add(0,-y,0).add(normals[side]);//0
				return true;
			}
			
			case LEFT:if (isSolid){
				v.add(-x,0,0).add(normals[side]);
				return true;
			}
			
			case BACK:if (isSolid){
				v.add(0,0,-z).add(normals[side]);
				return true;//z-1;//0;//-z;
			}
			
			case TOP:if (isSolid){
				v.add(0,1-y,0).add(normals[side]);
				return true;
			}
			
			case RIGHT:if (isSolid){
				v.add(1-x,0,0).add(normals[side]);
				return true;
			}
			case FRONT:if (isSolid){
				v.add(0,0,1-z).add(normals[side]);
				return true;
			}
		
		}
		return false;
		
	}
	public boolean collideLineSegment(Vector3 tStart, Vector3 tEnd, int side,
			Vector3 v) {
		
		float x0 = Math.max(0,Math.min(1,  tStart.x)), y0 = Math.max(0,Math.min(1, tStart.y)), z0 =  Math.max(0,Math.min(1, tStart.z))
				, x1 = Math.max(0,Math.min(1, tEnd.x)), y1 = Math.max(0,Math.min(1,  tEnd.y)), z1 = Math.max(0,Math.min(1,  tEnd.z));
		return collide(side, x0,y0,z0, v);
	}
	
	public boolean collideStair(int side, int bitmask, float posx, float posy, float posz, Vector3 v) {
		int gridX = 0, gridY = 0, depth = 0;
		switch (side){
		case TOP:case BOTTOM:
			gridX = (int) (posx*STAIRS_GRANULARITY);
			gridY = (int) (posz*STAIRS_GRANULARITY);
			break;
		case LEFT:case RIGHT:
			gridX = (int) (posz*STAIRS_GRANULARITY);
			gridY = (int) (posy*STAIRS_GRANULARITY);
			break;
		case FRONT:case BACK:
			gridX = (int) (posx*STAIRS_GRANULARITY);
			gridY = (int) (posy*STAIRS_GRANULARITY);
			break;
		}
		switch (side){
		case TOP:depth = (int) (-posy*STAIRS_GRANULARITY);
			break;
		case BOTTOM:depth = (int) (posy*STAIRS_GRANULARITY);
			break;
		case LEFT:depth = (int) (posx*STAIRS_GRANULARITY);
			break;
		case RIGHT:depth = (int) (-posx*STAIRS_GRANULARITY);
			break;
		case FRONT:depth = (int) (-posz*STAIRS_GRANULARITY);
			break;
		case BACK:depth = (int) (posz*STAIRS_GRANULARITY);
			break;
		}
		
		if (!collidesWithStair(bitmask, gridX, gridY, side, depth)){
			return false;
		}
		
		float x = stairs[bitmask][side][gridX][gridY];
		switch (side){
		
		case BOTTOM:if (isSolid){
			v.add(0,-x,0).add(normals[side]);//0
			return true;
		}
		
		case LEFT:if (isSolid){
			v.add(-x,0,0).add(normals[side]);
			return true;
		}
		case BACK:if (isSolid){
			v.add(0,0,-x).add(normals[side]);
			return true;//z-1;//0;//-z;
		}
		
		case TOP:if (isSolid){
			v.add(0,1-x,0).add(normals[side]);
			return true;
		}
		
		case RIGHT:if (isSolid){
			v.add(1-x,0,0).add(normals[side]);
			return true;
		}
		case FRONT:if (isSolid){
			v.add(0,0,1-x).add(normals[side]);
			return true;
		}
	
	}
	return false;
	
}
	
	
	private boolean collidesWithStair(int bitmask, int gridX, int gridY, int side, int depth) {
		
		return stairs[bitmask][side][gridX][gridY] > depth;//TODO reverse if this is wrong
	}


	public static Vector3[] normals = new Vector3[12];
	private static final int STAIRS_GRANULARITY = 8;
	private static final String TAG = "Block Definition";
	
	public static float[][][][] stairs = new float[64][6][STAIRS_GRANULARITY][STAIRS_GRANULARITY]; 
	static {
		normals[BlockDefinition.TOP] = new Vector3(0,1,0);
		normals[BlockDefinition.BOTTOM] = new Vector3(0,-1,0);
		normals[BlockDefinition.LEFT] = new Vector3(-1,0,0);
		normals[BlockDefinition.RIGHT] = new Vector3(1,0,0);
		normals[BlockDefinition.FRONT] = new Vector3(0,0,1);
		normals[BlockDefinition.BACK] = new Vector3(0,0,-1);
		
		for (int i = 0; i < 6; i++){
            reflectedNormals[i] = new Vector3(normals[i]);
			normals[i].scl(.00001f);
			normals[i+6] = new Vector3(normals[i]).scl(-1);
		}
		
		for (int i = 0; i < 64; i++){
			for (int side = 0; side < 6; side++)
				calculateStairOffsets(stairs[i], i, side);
		}
		
		
	}
	private static void calculateStairOffsets(float[][][] fs, int bitmask, int side) {
		boolean left = false, right = false, top = false, bottom = false;
		boolean[] sidep = new boolean[6];
		for (int i = 0; i < 6; i++){
			int bit = bitmask;
			bit >>= i;
			bit |= 1;
			if (bit == 1)
				sidep[i] = true;
			else sidep[i] = false;
		}
		switch (side){
		case TOP:
		case BOTTOM:
			left = sidep[LEFT];
			right = sidep[RIGHT];
			top = sidep[BACK];
			bottom = sidep[FRONT];
			break;
		case RIGHT:
		case LEFT:
			left = sidep[FRONT];
			right = sidep[BACK];
			top = sidep[TOP];
			bottom = sidep[BOTTOM];
			break;
		case FRONT:
		case BACK:
			left = sidep[LEFT];
			right = sidep[RIGHT];
			top = sidep[TOP];
			bottom = sidep[BOTTOM];
			break;
		}
		boolean reverse = (side == TOP || side == RIGHT || side == FRONT);
		if (sidep[side]){
			for (int x = 0; x < STAIRS_GRANULARITY; x++)
				for (int y = 0; y < STAIRS_GRANULARITY; y++)
					fs[side][x][y] = 1f;
		} else 
			calculateStairOffsets(fs[side], left, right, top, bottom, reverse);
	}
	
	




	private static int[][] tmpStair;// = new int[STAIRS_GRANULARITY+2][STAIRS_GRANULARITY+2]; 
	private static void calculateStairOffsets(float[][] fs, boolean left, boolean right, boolean top, boolean bottom, boolean reverse){
		tmpStair = new int[STAIRS_GRANULARITY+2][STAIRS_GRANULARITY+2]; 
		for (int i = 0; i < STAIRS_GRANULARITY+2; i++)//init grid with 0s
			for (int j = 0; j < STAIRS_GRANULARITY+2; j++)
				tmpStair[i][j] = 0;
		
		for (int i = 0; i < STAIRS_GRANULARITY+2; i++){//outside starting values
			tmpStair[i][0] = STAIRS_GRANULARITY;
			tmpStair[i][STAIRS_GRANULARITY+1] = STAIRS_GRANULARITY;
			tmpStair[0][i] = STAIRS_GRANULARITY;
			tmpStair[STAIRS_GRANULARITY+1][i] = STAIRS_GRANULARITY;
		}
		
		
		for (int r = 0; r < STAIRS_GRANULARITY; r++)//repeat 
		for (int x = 1; x < STAIRS_GRANULARITY+1; x++)
			for (int y = 1; y < STAIRS_GRANULARITY+1; y++){
				//one less than highest surrounding
				int highest = Math.max(Math.max(Math.max(tmpStair[x-1][y],tmpStair[x+1][y]), tmpStair[x][y-1]), tmpStair[x][y+1]);
				tmpStair[x][y] = highest-1;
				//Gdx.app.log(TAG, "stairs  x"+x+"  y"+y+"   hghest:"+highest);
			}
		
		for (int x = 0; x < STAIRS_GRANULARITY; x++)//put into results
			for (int y = 0; y < STAIRS_GRANULARITY; y++){
				fs[x][y] = tmpStair[x+1][y+1]/(float)STAIRS_GRANULARITY;
				if (reverse) fs[x][y] *= -1;
			}
	}
	
	public void setFace(VoxelFace face) {
		getUVs(face);
				
	}
	private void getUVs(VoxelFace face) {
		getUVs(face.side);
		face.u = u;
		//face.u2 = u2;
		face.v = v;
		//face.v2 = v2;
	}
	
	
	
}
