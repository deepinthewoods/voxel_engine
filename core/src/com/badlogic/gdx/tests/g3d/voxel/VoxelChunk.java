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
import com.badlogic.gdx.utils.GdxRuntimeException;

public class VoxelChunk {
	public static final int VERTEX_SIZE = 4;
    private static final String TAG ="voxel chunk";
    public final byte[] voxels;
	public final int width;
	public final int height;
	public final int depth;
	public final Vector3 offset = new Vector3();
	private final int widthTimesDepth;
	private final int topOffset;
	private final int bottomOffset;
	private final int leftOffset;
	private final int rightOffset;
	private final int frontOffset;
	private final int backOffset;
	public int index;
	public Mesh mesh;
	public int numVerts;
	
	
	public static BlockDefinition[] defs;
	
	public VoxelChunk(int width, int height, int depth, int index) {
		this.voxels = new byte[width * height * depth];
		this.width = width;
		this.height = height;
		this.depth = depth;
		this.topOffset = width * depth;
		this.bottomOffset = -width * depth;
		this.leftOffset = -1;
		this.rightOffset = 1;
		this.frontOffset = - width;
		this.backOffset = width;
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
		voxels[x + z * width + y * widthTimesDepth] = voxel;
	}

	/**
	 * Creates a mesh out of the chunk, returning the number of
	 * indices produced
	 * @return the number of vertices produced
	 */
	public int calculateVertices(float[] vertices) {
		int i = 0;
		int vertexOffset = 0;
		for(int y = 0; y < height; y++) {
			for(int z = 0; z < depth; z++) {
				for(int x = 0; x < width; x++, i++) {
					byte voxel = voxels[i];
					if(voxel == 0) continue;
					BlockDefinition def = defs[voxel];
					
					//Gdx.app.log("voxelChunk", "def"+voxel);
					if(y < height - 1) {
						if(voxels[i+topOffset] == 0) vertexOffset = createTop(offset, x, y, z, def, vertices, vertexOffset);
					} else {
						vertexOffset = createTop(offset, x, y, z, def, vertices, vertexOffset);
					}
					if(y > 0) {
						
						if(voxels[i+bottomOffset] == 0) vertexOffset = createBottom(offset, x, y, z, def, vertices, vertexOffset);
					} else {
						vertexOffset = createBottom(offset, x, y, z, def, vertices, vertexOffset);
					}
					if(x > 0) {
						
						//Gdx.app.log("voxelChunk", "def"+voxel);
						if(voxels[i+leftOffset] == 0) vertexOffset = createLeft(offset, x, y, z, def, vertices, vertexOffset);
					} else {
						vertexOffset = createLeft(offset, x, y, z, def, vertices, vertexOffset);
					}
					if(x < width - 1) {
						//Gdx.app.log("voxelChunk", "def"+voxel);
						if(voxels[i+rightOffset] == 0) vertexOffset = createRight(offset, x, y, z, def, vertices, vertexOffset);
					} else {
						vertexOffset = createRight(offset, x, y, z, def, vertices, vertexOffset);
					}
					if(z > 0) {
						//Gdx.app.log("voxelChunk", "def"+voxel);
						if(voxels[i+frontOffset] == 0) vertexOffset = createFront(offset, x, y, z, def, vertices, vertexOffset);
					} else {
						vertexOffset = createFront(offset, x, y, z, def, vertices, vertexOffset);
					}
					if(z < depth - 1) {
						//Gdx.app.log("voxelChunk", "def"+voxel);
						if(voxels[i+backOffset] == 0) vertexOffset = createBack(offset, x, y, z, def, vertices, vertexOffset);
					} else {
						vertexOffset = createBack(offset, x, y, z, def, vertices, vertexOffset);
					}
				}
			}
		}
		return vertexOffset / VERTEX_SIZE;
	}

	public static int createTop(Vector3 offset, int x, int y, int z, BlockDefinition def, float[] vertices, int vertexOffset) {
		int  side =  BlockDefinition.TOP;
		float[] uvs = def.getUVs(side);
		float u = uvs[0], v = uvs[1], 
				u2 = uvs[2], v2 = uvs[3];
		float c = Color.WHITE.toFloatBits();
		vertices[vertexOffset++] = offset.x + x;
		vertices[vertexOffset++] = offset.y + y + 1;
		vertices[vertexOffset++] = offset.z + z;
		//vertices[vertexOffset++] = 0;
		//vertices[vertexOffset++] = 1;
		//vertices[vertexOffset++] = 0;
		vertices[vertexOffset++] = c;//lightColors[lightAverages[yp]+TOP_FACE_BRIGHTNESS_BONUS];//(light(c[BlockDefinition.TOP])+light(c[BlockDefinition.LEFT])+light(c[BlockDefinition.BACK]))/3];//lightColors[(light(c[BlockDefinition.TOP]))];//
		//vertices[vertexOffset++] = u;
		//vertices[vertexOffset++] = v;
		
		vertices[vertexOffset++] = offset.x + x + 1;
		vertices[vertexOffset++] = offset.y + y + 1;
		vertices[vertexOffset++] = offset.z + z;
		//vertices[vertexOffset++] = 0;
		//vertices[vertexOffset++] = 1;
		//vertices[vertexOffset++] = 0;
		vertices[vertexOffset++] = c;//lightColors[lightAverages[xp+yp]+TOP_FACE_BRIGHTNESS_BONUS];//(light(c[BlockDefinition.TOP])+light(block)/2];//lightColors[(light(c[BlockDefinition.TOP]))];//
		//vertices[vertexOffset++] = u2;
		//vertices[vertexOffset++] = v;
		//Gdx.app.log("chunk", ""+(c[BlockDefinition.TOP]+c[BlockDefinition.RIGHT]+c[BlockDefinition.BACK])/3);
		vertices[vertexOffset++] = offset.x + x + 1;
		vertices[vertexOffset++] = offset.y + y + 1;
		vertices[vertexOffset++] = offset.z + z + 1;
		//vertices[vertexOffset++] = 0;
		//vertices[vertexOffset++] = 1;
		//vertices[vertexOffset++] = 0;
		vertices[vertexOffset++] = c;
		//vertices[vertexOffset++] = u2;
		//vertices[vertexOffset++] = v2;
		//Gdx.app.log("chunk", ""+(c[BlockDefinition.TOP]+c[BlockDefinition.RIGHT]+c[BlockDefinition.FRONT])/3);
		vertices[vertexOffset++] = offset.x + x;
		vertices[vertexOffset++] = offset.y + y + 1;
		vertices[vertexOffset++] = offset.z + z + 1;
//		vertices[vertexOffset++] = 0;
//		vertices[vertexOffset++] = 1;
//		vertices[vertexOffset++] = 0;
		vertices[vertexOffset++] = c;
//		vertices[vertexOffset++] = u;
//		vertices[vertexOffset++] = v2;
		//Gdx.app.log("chunk", ""+(c[BlockDefinition.TOP]+c[BlockDefinition.LEFT]+c[BlockDefinition.FRONT])/3);
		//flipVertices(yp, xp+yp, xp+yp+zp, yp+zp, vertices, vertexOffset, lightAverages);
		return vertexOffset;
	}
	
	public static int createBottom(Vector3 offset, int x, int y, int z, BlockDefinition def, float[] vertices, int vertexOffset) {
		int  side =  BlockDefinition.BOTTOM;
		float[] uvs = def.getUVs(side);
		float u = uvs[0], v = uvs[1], 
				u2 = uvs[2], v2 = uvs[3];
		float c = Color.WHITE.toFloatBits();

		vertices[vertexOffset++] = offset.x + x;
		vertices[vertexOffset++] = offset.y + y;//d
		vertices[vertexOffset++] = offset.z + z;
//		vertices[vertexOffset++] = 0;
//		vertices[vertexOffset++] = -1;
//		vertices[vertexOffset++] = 0;
		vertices[vertexOffset++] = c;
//		vertices[vertexOffset++] = u;
//		vertices[vertexOffset++] = v;
		
		vertices[vertexOffset++] = offset.x + x;
		vertices[vertexOffset++] = offset.y + y;
		vertices[vertexOffset++] = offset.z + z + 1;
//		vertices[vertexOffset++] = 0;
//		vertices[vertexOffset++] = -1;
//		vertices[vertexOffset++] = 0;
		vertices[vertexOffset++] = c;
//		vertices[vertexOffset++] = u2;
//		vertices[vertexOffset++] = v;
		
		vertices[vertexOffset++] = offset.x + x + 1;
		vertices[vertexOffset++] = offset.y + y;
		vertices[vertexOffset++] = offset.z + z + 1;
//		vertices[vertexOffset++] = 0;
//		vertices[vertexOffset++] = -1;
//		vertices[vertexOffset++] = 0;
		vertices[vertexOffset++] = c;
//		vertices[vertexOffset++] = u2;
//		vertices[vertexOffset++] = v2;
		
		vertices[vertexOffset++] = offset.x + x + 1;
		vertices[vertexOffset++] = offset.y + y;
		vertices[vertexOffset++] = offset.z + z;
//		vertices[vertexOffset++] = 0;
//		vertices[vertexOffset++] = -1;
//		vertices[vertexOffset++] = 0;
		vertices[vertexOffset++] = c;
//		vertices[vertexOffset++] = u;
//		vertices[vertexOffset++] = v2;
		//flipVertices(0, zp, xp+zp, xp, vertices, vertexOffset, lightAverages);
		return vertexOffset;
	}
	
	public static int createLeft(Vector3 offset, int x, int y, int z, BlockDefinition def, float[] vertices, int vertexOffset) {
		int  side =  BlockDefinition.LEFT;
		float[] uvs = def.getUVs(side);
		float u = uvs[0], v = uvs[1], 
				u2 = uvs[2], v2 = uvs[3];
		float c = Color.WHITE.toFloatBits();

		vertices[vertexOffset++] = offset.x + x;
		vertices[vertexOffset++] = offset.y + y;
		vertices[vertexOffset++] = offset.z + z;
//		vertices[vertexOffset++] = -1;
//		vertices[vertexOffset++] = 0;
//		vertices[vertexOffset++] = 0;
		vertices[vertexOffset++] = c;
//		vertices[vertexOffset++] = u;
//		vertices[vertexOffset++] = v2;
		
		
		vertices[vertexOffset++] = offset.x + x;
		vertices[vertexOffset++] = offset.y + y + 1;
		vertices[vertexOffset++] = offset.z + z;
//		vertices[vertexOffset++] = -1;
//		vertices[vertexOffset++] = 0;
//		vertices[vertexOffset++] = 0;
		vertices[vertexOffset++] = c;
//		vertices[vertexOffset++] = u;
//		vertices[vertexOffset++] = v;
		
		vertices[vertexOffset++] = offset.x + x;
		vertices[vertexOffset++] = offset.y + y + 1;
		vertices[vertexOffset++] = offset.z + z + 1;
//		vertices[vertexOffset++] = -1;
//		vertices[vertexOffset++] = 0;
//		vertices[vertexOffset++] = 0;
		vertices[vertexOffset++] = c;
//		vertices[vertexOffset++] = u2;
//		vertices[vertexOffset++] = v;
		
		vertices[vertexOffset++] = offset.x + x;
		vertices[vertexOffset++] = offset.y + y;
		vertices[vertexOffset++] = offset.z + z + 1;
//		vertices[vertexOffset++] = -1;
//		vertices[vertexOffset++] = 0;
//		vertices[vertexOffset++] = 0;
		vertices[vertexOffset++] = c;
//		vertices[vertexOffset++] = u2;
//		vertices[vertexOffset++] = v2;
		//flipVertices(0, yp, yp+zp, zp, vertices, vertexOffset, lightAverages);
		return vertexOffset;
	}
	
	public static int createRight(Vector3 offset, int x, int y, int z, BlockDefinition def, float[] vertices, int vertexOffset) {
		int  side =  BlockDefinition.RIGHT;
		float[] uvs = def.getUVs(side);
		float u = uvs[0], v = uvs[1], 
				u2 = uvs[2], v2 = uvs[3];
		float c = Color.WHITE.toFloatBits();

		vertices[vertexOffset++] = offset.x + x + 1;
		vertices[vertexOffset++] = offset.y + y;
		vertices[vertexOffset++] = offset.z + z;
//		vertices[vertexOffset++] = 0;
//		vertices[vertexOffset++] = 1;
//		vertices[vertexOffset++] = 0;
		vertices[vertexOffset++] = c;
//		vertices[vertexOffset++] = u2;
//		vertices[vertexOffset++] = v2;
		
		vertices[vertexOffset++] = offset.x + x + 1;
		vertices[vertexOffset++] = offset.y + y;
		vertices[vertexOffset++] = offset.z + z + 1;
//		vertices[vertexOffset++] = 0;
//		vertices[vertexOffset++] = 1;
//		vertices[vertexOffset++] = 0;
		vertices[vertexOffset++] = c;
//		vertices[vertexOffset++] = u;
//		vertices[vertexOffset++] = v2;
		
		vertices[vertexOffset++] = offset.x + x + 1;
		vertices[vertexOffset++] = offset.y + y + 1;
		vertices[vertexOffset++] = offset.z + z + 1;
//		vertices[vertexOffset++] = 0;
//		vertices[vertexOffset++] = 1;
//		vertices[vertexOffset++] = 0;
		vertices[vertexOffset++] = c;
//		vertices[vertexOffset++] = u;
//		vertices[vertexOffset++] = v;
		
		vertices[vertexOffset++] = offset.x + x + 1;
		vertices[vertexOffset++] = offset.y + y + 1;
		vertices[vertexOffset++] = offset.z + z;
//		vertices[vertexOffset++] = 0;
//		vertices[vertexOffset++] = 1;
//		vertices[vertexOffset++] = 0;
		vertices[vertexOffset++] = c;
//		vertices[vertexOffset++] = u2;
//		vertices[vertexOffset++] = v;
		//flipVertices(xp, xp+zp, xp+yp+zp, xp+yp, vertices, vertexOffset, lightAverages);
		return vertexOffset;
		
	}
	
	//n
	public static int createFront(Vector3 offset, int x, int y, int z, BlockDefinition def, float[] vertices, int vertexOffset) {
		int  side =  BlockDefinition.FRONT;
		float[] uvs = def.getUVs(side);
		float u = uvs[0], v = uvs[1], 
				u2 = uvs[2], v2 = uvs[3];
		float c = Color.WHITE.toFloatBits();

		vertices[vertexOffset++] = offset.x + x;
		vertices[vertexOffset++] = offset.y + y;
		vertices[vertexOffset++] = offset.z + z;
//		vertices[vertexOffset++] = 0;
//		vertices[vertexOffset++] = 0;
//		vertices[vertexOffset++] = 1;
		vertices[vertexOffset++] = c;
//		vertices[vertexOffset++] = u;
//		vertices[vertexOffset++] = v2;
		//wus
		vertices[vertexOffset++] = offset.x + x + 1;
		vertices[vertexOffset++] = offset.y + y;
		vertices[vertexOffset++] = offset.z + z;
//		vertices[vertexOffset++] = 0;
//		vertices[vertexOffset++] = 0;
//		vertices[vertexOffset++] = 1;
		vertices[vertexOffset++] = c;
//		vertices[vertexOffset++] = u2;
//		vertices[vertexOffset++] = v2;
		
		vertices[vertexOffset++] = offset.x + x + 1;
		vertices[vertexOffset++] = offset.y + y + 1;
		vertices[vertexOffset++] = offset.z + z;
//		vertices[vertexOffset++] = 0;
//		vertices[vertexOffset++] = 0;
//		vertices[vertexOffset++] = 1;
		vertices[vertexOffset++] = c;
//		vertices[vertexOffset++] = u2;
//		vertices[vertexOffset++] = v;
		
		vertices[vertexOffset++] = offset.x + x;
		vertices[vertexOffset++] = offset.y + y + 1;
		vertices[vertexOffset++] = offset.z + z;
//		vertices[vertexOffset++] = 0;
//		vertices[vertexOffset++] = 0;
//		vertices[vertexOffset++] = 1;
		vertices[vertexOffset++] = c;
//		vertices[vertexOffset++] = u;
//		vertices[vertexOffset++] = v;
		//flipVertices(0, xp, xp+yp, yp, vertices, vertexOffset, lightAverages);
		return vertexOffset;
	}
	
	public static int createBack(Vector3 offset, int x, int y, int z, BlockDefinition def, float[] vertices, int vertexOffset) {
		int  side =  BlockDefinition.BACK;
		float[] uvs = def.getUVs(side);
		float u = uvs[0], v = uvs[1], 
				u2 = uvs[2], v2 = uvs[3];
		float c = Color.WHITE.toFloatBits();

		vertices[vertexOffset++] = offset.x + x;
		vertices[vertexOffset++] = offset.y + y;
		vertices[vertexOffset++] = offset.z + z + 1;
//		vertices[vertexOffset++] = 0;
//		vertices[vertexOffset++] = 0;
//		vertices[vertexOffset++] = -1;
		vertices[vertexOffset++] = c;
//		vertices[vertexOffset++] = u;
//		vertices[vertexOffset++] = v2;
		
		vertices[vertexOffset++] = offset.x + x;
		vertices[vertexOffset++] = offset.y + y + 1;
		vertices[vertexOffset++] = offset.z + z + 1;
//		vertices[vertexOffset++] = 0;
//		vertices[vertexOffset++] = 0;
//		vertices[vertexOffset++] = -1;
		vertices[vertexOffset++] = c;
//		vertices[vertexOffset++] = u;
//		vertices[vertexOffset++] = v;
		
		vertices[vertexOffset++] = offset.x + x + 1;
		vertices[vertexOffset++] = offset.y + y + 1;
		vertices[vertexOffset++] = offset.z + z + 1;
//		vertices[vertexOffset++] = 0;
//		vertices[vertexOffset++] = 0;
//		vertices[vertexOffset++] = -1;
		vertices[vertexOffset++] = c;
//		vertices[vertexOffset++] = u2;
//		vertices[vertexOffset++] = v;
		
		vertices[vertexOffset++] = offset.x + x + 1;
		vertices[vertexOffset++] = offset.y + y;
		vertices[vertexOffset++] = offset.z + z + 1;
//		vertices[vertexOffset++] = 0;
//		vertices[vertexOffset++] = 0;
//		vertices[vertexOffset++] = -1;
		vertices[vertexOffset++] = c;
//		vertices[vertexOffset++] = u2;
//		vertices[vertexOffset++] = v2;
		//flipVertices(zp, yp+zp, xp+yp+zp, xp+zp, vertices, vertexOffset, lightAverages);
		return vertexOffset;
	}

	public static BlockDefinition blockDef(int i) {
		return defs[i];
	}
	
	public static int getAO(int side1, int side2, int corner){
		if (side1 != 0 && side2!= 0){
			return 0;
		} else {
			return 3-(side1+side2+corner);
		}
	}
	
	//static final int[] xo = {0,0,0,0,0,0,0,0}, yo = {0,0,0,0,0,0,0,0}, zo = {0,0,0,0,0,0,0,0};
	public void visibility(int[] mask, int[][][] light, VoxelFace[][][][] faces, VoxelWorld world
    ){
    	int mx = 0;
    	int yM = depth+2;
		int  maskIndex = 0;


        //outside blocks, east and west
        for(int y = -1; y < height+1; y++) {

            for(int z = -1; z < depth+1; z++) {

                maskIndex = z+1+(y+1)*yM;
                mx = 0;//mask[maskIndex];
                boolean flip = false;
                for(int x = -1; x < width+2; x+= width+1, flip = !flip) {
                  //  Gdx.app.log(TAG, "x "+x + " shitf "+(2 << -1));
                    int bx = (int) (x + offset.x), by = (int) (y + offset.y), bz = (int) (z + offset.z);

                    int b = world.get(bx,by,bz);
                    if (b > 0){
                        if (x < 0) mx &= 1;
                        else
                            mx &= 2<<x;
                    }
                    BlockDefinition def = blockDef(b);

                    //light totals for verts
                    if (flip){
                        light[x + 1 + 1][y + 1][z + 1 + 1] += def.lightValue;
                    }
                    else
                        light[x + 1 + 1][y + 1][z + 1 + 1] += def.lightValue;
                    light[x +  1][y + 1][z +  1] += def.lightValue;
                    light[x +  1][y + 1][z + 1 + 1] += def.lightValue;
                    light[x + 1 + 1][y + 1][z +  1] += def.lightValue;

                }
                mask[maskIndex] = mx;

            }

        }
    int i = 0;
        //inside blocks
		for(int y = 0; y < height; y++) {
			
			for(int z = 0; z < depth; z++) {

                maskIndex = z+1+(y+1)*yM;
                mx = mask[maskIndex];
				for(int x = 0; x < width; x++) {
					int b = voxels[i];
					if (b > 0){
						mx &= 2<<x;
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

    //outside blocks, top and bottom
        for(int y = -1; y < height+2; y+=height+1) {

            for(int z = -1; z < depth+1; z++) {

                maskIndex = z+1+(y+1)*yM;
                mx = mask[maskIndex];;
                for(int x = 0; x < width+1; x++) {
                    //
                    int bx = (int) (x + offset.x), by = (int) (y + offset.y), bz = (int) (z + offset.z);
                    int b = world.get(bx,by,bz);
                    if (b > 0){
                        if (x == -1) mx &= 1;
                        else
                            mx &= 2<<x;
                    }
                    BlockDefinition def = blockDef(b);

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

        //outside blocks, front and back
        for(int y = 0; y < height; y++) {

            for(int z = -1; z < depth+2; z+= depth+1) {

                maskIndex = z+1+(y+1)*yM;
                mx = mask[maskIndex];
                for(int x = 0; x < width; x++) {
                    int bx = (int) (x + offset.x), by = (int) (y + offset.y), bz = (int) (z + offset.z);

                    int b = world.get(bx, by, bz);
                    if (b > 0){
                        if (x == -1) mx &= 1;
                        else
                            mx &= 2<<x;
                    }
                    BlockDefinition def = blockDef(b);

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