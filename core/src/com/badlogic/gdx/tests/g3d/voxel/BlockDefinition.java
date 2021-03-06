package com.badlogic.gdx.tests.g3d.voxel;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.g2d.TextureAtlas;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.math.Vector3;
import com.badlogic.gdx.tests.g3d.voxel.GreedyMesher.VoxelFace;

public class BlockDefinition {
    public static Vector3[] reflectedNormals = new Vector3[6];
    //protected float[] uvs = new float[4];
	//public static float tile_size_px = 16;
	//protected static int tile_side_length = 8;
	public static final int //TOP = 0, BOTTOM = 1, LEFT = 2, RIGHT = 3, FRONT = 4, BACK = 5,
            ALL = 6;
    public static final int BACK      = 0;
    public static final int FRONT      = 1;
    public static final int RIGHT       = 2;
    public static final int LEFT       = 3;
    public static final int TOP        = 4;
    public static final int BOTTOM     = 5;
	public int id;
	//private float u, v;
	//public int dayLightLoss = 15;
	//public byte lightLoss = 15;
	public boolean isSolid = true;
	public int lightValue = 0;
	public boolean isEmpty = false;;
	private static BlockDefinition[] defs = new BlockDefinition[256];
    public float[] faceU = new float[6], faceV = new float[6];
    //public int id;


	public BlockDefinition(int id){

        this.id = id;
		//u = region.getU();
		//v = region.getV();
		//u2 = region.getU2();
		//v2 = region.getV2();
		
	}
    public BlockDefinition(int id, String all, TextureAtlas atlas){
        for (int i = 0; i < 6; i++){
            TextureAtlas.AtlasRegion region = atlas.findRegion(all);
            faceU[i] = region.getU();
            faceV[i] = region.getV();
        }
        this.id = id;
    }

    public BlockDefinition(int id, String top, String side, String bottom, TextureAtlas atlas){

        TextureAtlas.AtlasRegion region = atlas.findRegion(top);
        faceU[TOP] = region.getU();
        faceV[TOP] = region.getV();

        region = atlas.findRegion(side);
        faceU[RIGHT] = region.getU();
        faceV[RIGHT] = region.getV();
        faceU[LEFT] = region.getU();
        faceV[LEFT] = region.getV();
        faceU[BACK] = region.getU();
        faceV[BACK] = region.getV();
        faceU[FRONT] = region.getU();
        faceV[FRONT] = region.getV();

        region = atlas.findRegion(bottom);
        faceU[BOTTOM] = region.getU();
        faceV[BOTTOM] = region.getV();


        this.id = id;
    }

	
	public void onUpdate(int x, int y, int z, VoxelWorld world){};
		
	
		
	
	public String toString(){
		return "block Index:"+id;
	}

	


	public boolean collide(int side, Vector3 v) {
		float x = v.x%1, y = v.y%1, z = v.z%1;
		return collide(side, x,y,z, v);
	}
	
	private boolean collide(int side, float x, float y, float z, Vector3 v) {
        switch (side){

            case BOTTOM:

                //Gdx.app.log(TAG, "collide bottom "+v);
                if (isSolid){
                    v.add(0,-y,0).add(normals[side]);//0

                    return true;
                }
                break;

            case LEFT:
                if (isSolid){
                    v.add(-x,0,0).add(normals[side]);
                    //Gdx.app.log(TAG, "collide left "+v);
                    return true;
                }
                break;

            case BACK:

                if (isSolid){
                    v.add(0,0,-z).add(normals[side]);
                    //Gdx.app.log(TAG, "collide baqc "+v);
                    return true;//z-1;//0;//-z;
                }
                break;

            case TOP:
                //Gdx.app.log(TAG, "collide top notsolid "+v);
                if (isSolid){
                    v.add(0,1-y,0).add(normals[side]);
                    //Gdx.app.log(TAG, "collide top"+v);
                    return true;
                }
                break;

            case RIGHT:
                if (isSolid){
                    v.add(1-x,0,0).add(normals[side]);
                    //Gdx.app.log(TAG, "collide r "+v);
                    return true;
                }
                break;

            case FRONT:
                if (isSolid){
                    v.add(0,0,1-z).add(normals[side]);
                    //Gdx.app.log(TAG, "collide fr "+v);
                    return true;
                }
                break;

        }
        return false;

	}
	public boolean collideLineSegment(Vector3 tStart, Vector3 tEnd, int side,
			Vector3 v) {
		
		float x0 = Math.max(0,Math.min(1,  tStart.x)), y0 = Math.max(0,Math.min(1, tStart.y)), z0 =  Math.max(0,Math.min(1, tStart.z))
				, x1 = Math.max(0,Math.min(1, tEnd.x)), y1 = Math.max(0,Math.min(1,  tEnd.y)), z1 = Math.max(0,Math.min(1,  tEnd.z));
		return collide(side, x0,y0,z0, v);
	}
	

	public static Vector3[] normals = new Vector3[12];
	private static final String TAG = "Block Definition";

    public static int flipFace(int face) {
        if (face == ALL) return face;
        if (face % 2 == 0) return face+1;
        return face-1;

    }

    static {
        normals[BlockDefinition.TOP] = new Vector3(0,1,0);
        normals[BlockDefinition.BOTTOM] = new Vector3(0,-1,0);
        normals[BlockDefinition.LEFT] = new Vector3(-1,0,0);
        normals[BlockDefinition.RIGHT] = new Vector3(1,0,0);
        normals[BlockDefinition.FRONT] = new Vector3(0,0,1);
        normals[BlockDefinition.BACK] = new Vector3(0,0,-1);

        for (int i = 0; i < 6; i++){
            reflectedNormals[i] = new Vector3(normals[i]);
            normals[i].scl(.0001f);
            normals[i+6] = new Vector3(normals[i]).scl(-1);
        }




    }
}
