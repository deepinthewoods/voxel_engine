/*******************************************************************************
 * Copyright 2011 See AUTHORS file.
 * 
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 * 
 *   http://www.apache.org/licenses/LICENSE-2.0
 * 
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 ******************************************************************************/

package com.badlogic.gdx.tests.g3d.voxel;


import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.Mesh;
import com.badlogic.gdx.math.Vector3;
import com.badlogic.gdx.tests.g3d.voxel.GreedyMesher.VoxelFace;

public class VoxelChunk {
	public static final int VERTEX_SIZE = 4;
    private static final String TAG ="voxel chunk";
    protected static final int VISIBILITY_SUBDIVISIONS = 8;
    public final byte[] voxels;
	public final int width;
	public final int height;
	public final int depth;
	public final Vector3 offset = new Vector3();
	private final int widthTimesDepth;
	public int plane;
	public int index;
	public Mesh mesh;
	public int numVerts;
	
	
	public static BlockDefinition[] defs;
    private boolean dirty, valid;

    public VoxelChunk(int width, int height, int depth, int index, int plane) {
		this.voxels = new byte[width * height * depth];
		this.width = width;
		this.height = height;
		this.depth = depth;
		this.plane = plane;
		this.widthTimesDepth = width * depth;
		this.index = index;
	}

	public byte get(int x, int y, int z) {
		if(x < 0 || x >= width) return 0;
		if(y < 0 || y >= height) return 0;
		if(z < 0 || z >= depth) return 0;
		return getFast(x, y, z);
	}

	public byte getFast(int x, int y, int z) {
		return voxels[x + z * width + y * widthTimesDepth];
	}

	public void set(int x, int y, int z, byte voxel) {
		if(x < 0 || x >= width) return;
		if(y < 0 || y >= height) return;
		if(z < 0 || z >= depth) return;
		setFast(x, y, z, voxel);
	}

	public void setFast(int x, int y, int z, byte voxel) {
        //Gdx.app.log(TAG, "x"+x+" y"+y+" z"+z);
		voxels[x + z * width + y * widthTimesDepth] = voxel;
	}



	public static BlockDefinition blockDef(int i) {
        //if (i != 0)
        //Gdx.app.log(TAG, "block def "+i+"  "+(i&0xff));

        return defs[i&0xff];
	}
	

	
	//static final int[] xo = {0,0,0,0,0,0,0,0}, yo = {0,0,0,0,0,0,0,0}, zo = {0,0,0,0,0,0,0,0};
	public void visibility(int[] mask, int[][][] light, VoxelFace[][][][] faces, VoxelWorld world, int progress
    ){
    	int mx = 0;
    	int yM = depth+2;
		int  maskIndex = 0;
        if (progress == 0){
            //outside blocks, east and west
            for(int y = -1; y < height+1; y++) {

                for(int z = -1; z < depth+1; z++) {

                    maskIndex = z+1+(y+1)*yM;
                    mx = 0;//mask[maskIndex];

                    for(int x = -1; x < width+2; x+= width+1) {
                        //  Gdx.app.log(TAG, "x "+x + " shitf "+(2 << -1));
                        int bx = (int) (x + offset.x), by = (int) (y + offset.y), bz = (int) (z + offset.z);

                        int b = world.get(bx,by,bz, plane);
                        if (b > 0){
                            if (x < 0) mx |= 1;
                            else
                                mx |= 2<<x;
                        }
                        BlockDefinition def = blockDef(b);
                        if (x >= 0 && y >= 0 && z >= 0)
                            for (int faceID = 0; faceID < 6; faceID++){
                                VoxelFace face = faces[x][y][z][faceID];

                                face.set(def, faceID, b);
                            }
                        //light totals for verts

                        light[x + 1 + 1][y + 1][z + 1 + 1] += def.lightValue;

                        light[x +  1][y + 1][z +  1] += def.lightValue;
                        light[x +  1][y + 1][z + 1 + 1] += def.lightValue;
                        light[x + 1 + 1][y + 1][z +  1] += def.lightValue;

                    }
                    mask[maskIndex] = mx;

                }

            }
        } else if (progress < VISIBILITY_SUBDIVISIONS+1){
            int prog = progress-1;
            int progSize = height/VISIBILITY_SUBDIVISIONS;
            //inside blocks
            //Gdx.app.log(TAG, "prog"+prog + "  n  "+((prog+1)*progSize));
            int i = depth*width*prog*progSize;
            for(int y = prog*progSize, n = (prog+1)*progSize; y < n; y++) {

                for(int z = 0; z < depth; z++) {

                    maskIndex = z+1+(y+1)*yM;
                    mx = mask[maskIndex];
                    for(int x = 0; x < width; x++) {
                        int b = voxels[i];
                        if (b > 0){
                            mx |= 2<<x;

                            //Gdx.app.log(TAG, "inc mx"+mx);
                        }
                        BlockDefinition def = blockDef(b);

                        for (int faceID = 0; faceID < 6; faceID++){
                            VoxelFace face = faces[x][y][z][faceID];

                            face.set(def, faceID, b);
                        }
                        //light totals for verts
                        light[x + 1 + 1][y + 1][z + 1 + 1] += def.lightValue;
                        light[x +  1][y + 1][z +  1] += def.lightValue;
                        light[x +  1][y + 1][z + 1 + 1] += def.lightValue;
                        light[x + 1 + 1][y + 1][z +  1] += def.lightValue;

                        i++;
                    }
                    mask[maskIndex] = mx;
                }

            }
        } else if (progress < VISIBILITY_SUBDIVISIONS+2){
            //outside blocks, top and bottom
            for(int y = -1; y < height+2; y+=height+1) {

                for(int z = -1; z < depth+1; z++) {

                    maskIndex = z+1+(y+1)*yM;
                    mx = mask[maskIndex];;
                    for(int x = 0; x < width+1; x++) {
                        //
                        int bx = (int) (x + offset.x), by = (int) (y + offset.y), bz = (int) (z + offset.z);
                        int b = world.get(bx,by,bz, plane);
                        if (b > 0){
                            if (x == -1) mx |= 1;
                            else
                                mx |= 2<<x;
                        }
                        BlockDefinition def = blockDef(b);
                        if (x >= 0 && y >= 0 && z >= 0)
                            for (int faceID = 0; faceID < 6; faceID++){
                                VoxelFace face = faces[x][y][z][faceID];
                                face.set(def, faceID, b);
                            }
                        //light totals for verts
                        light[x + 1 + 1][y + 1][z + 1 + 1] += def.lightValue;
                        light[x +  1][y + 1][z +  1] += def.lightValue;
                        light[x +  1][y + 1][z + 1 + 1] += def.lightValue;
                        light[x + 1 + 1][y + 1][z +  1] += def.lightValue;

                    }
                    mask[maskIndex] = mx;

                    mx = 0;
                }

            }
        } else {
            //outside blocks, front and back
            for(int y = 0; y < height; y++) {

                for(int z = -1; z < depth+2; z+= depth+1) {

                    maskIndex = z+1+(y+1)*yM;
                    mx = mask[maskIndex];
                    for(int x = 0; x < width; x++) {
                        int bx = (int) (x + offset.x), by = (int) (y + offset.y), bz = (int) (z + offset.z);

                        int b = world.get(bx, by, bz, plane);
                        if (b > 0){
                            if (x == -1) mx |= 1;
                            else
                                mx |= 2<<x;
                        }
                        BlockDefinition def = blockDef(b);
                        if (x >= 0 && y >= 0 && z >= 0)
                            for (int faceID = 0; faceID < 6; faceID++){
                                VoxelFace face = faces[x][y][z][faceID];

                                face.set(def, faceID, b);
                            }
                        //light totals for verts
                        light[x + 1 + 1][y + 1][z + 1 + 1] += def.lightValue;
                        light[x +  1][y + 1][z +  1] += def.lightValue;
                        light[x +  1][y + 1][z + 1 + 1] += def.lightValue;
                        light[x + 1 + 1][y + 1][z +  1] += def.lightValue;

                    }
                    mask[maskIndex] = mx;
                    mx = 0;
                }

            }
        }










	}

    public BlockDefinition getDef(int x, int y, int z) {
        return blockDef(get(x,y,z));
    }

    public void setByIndex(int i, byte value) {
        voxels[i] = value;
    }

    public byte getByIndex(int i) {
        return voxels[i];
    }

    public void setDirty(boolean value) {
        dirty = value;

    }

    public void setValid(boolean valid) {
        this.valid = valid;
    }

    public boolean getDirty() {
        return dirty;
    }

    public boolean isValid() {
        return valid;
    }
}