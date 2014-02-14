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

package voxel;

import com.badlogic.gdx.graphics.Mesh;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.math.Vector3;

public class VoxelChunk32 {
	public static final int VERTEX_SIZE = 9;
	public static TextureRegion[][] tiles;
	private static final int TOP_FACE_BRIGHTNESS_BONUS = 0;
	public final byte[] voxels;//, lights;
	//private int[] x;
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
	public final int index;
	public Mesh mesh = null;
	
	public static BlockDefinition[] blockDefinitions;
	private static int[] lightAverages = new int[8];
	

	public VoxelChunk32(int width, int height, int depth, int index) {
		this.index = index;
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
		//Color c = new Color;
		for (int i = 0; i < 16; i++){
			//lightColors[i] = colors.getPixel(i, 0);
			//torchColors[i] = colors.getPixel(i, 1);
			
		}
	}
	
	public byte get(int x, int y, int z) {
		if(x < 0 || x >= width) {
			//Gdx.app.log("chunk", "return zero");
			return 0;
		}
		if(y < 0 ){
			//Gdx.app.log("chunk", "return zero2");
			return 0;
		}
		if ( y >= height) {
			//Gdx.app.log("chunk", "return zero3");
			return 0;
		}
		if(z < 0 ){
			//Gdx.app.log("chunk", "return ze43r4o");
			//throw new GdxRuntimeException("blocok fuck");
			//return 0;
			return 0;//VoxelChunk.setLight(0,  7);
			
		}
		if (z >= depth) {
			//Gdx.app.log("chunk", "return zer4o");
			return 0;
		}
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
	
	public int calculateVertices(float[] vertices, VoxelWorld world) {
		int i = 0;
		int vertexOffset = 0;
		for(int y = 0; y < height; y++) {
			for(int z = 0; z < depth; z++) {
				for(int x = 0; x < width; x++, i++) {
					int voxel = voxels[i];
					if((id(voxel)) == 0) continue;
					//int c = light(voxel);
					BlockDefinition def = blockDefinitions[id(voxel)];
					//float u = def.u, v = def.v, u2 = def.u2, v2 = def.v2, 
					//byte[] c = world.getSurrounds((int)offset.x + x,(int)offset.y+y,(int)offset.z+z);
					//if (voxel == 0)
						//Gdx.app.log("ggg", "def"+(voxel&BLOCK_MASK));
					int idx = 0;
					
					idx = 0;
					for (int nz = 0; nz <= 1; nz++)
						for (int ny = 0; ny <= 1; ny++)
								for (int nx = 0; nx <= 1; nx++){
									if (x==0||x==width-1||y==0||y==height-1||z==0||z==depth-1){
										idx = nz*zp+ny*yp+nx*xp;
										nx--;nz--;
										lightAverages[idx] = light(world.get(offset.x+x+nx,offset.y+y+ny,offset.z+z+nz));
										lightAverages[idx] += light(world.get(offset.x+x+nx+1,offset.y+y+ny,offset.z+z+nz));
										lightAverages[idx] += light(world.get(offset.x+x+nx,offset.y+y+ny,z+offset.z+nz+1));
										lightAverages[idx] += light(world.get(offset.x+x+nx+1,offset.y+y+ny,offset.z+z+nz+1));
										lightAverages[idx] /= 4;
										
										nx++;nz++;
									} else {
										idx = nz*zp+ny*yp+nx*xp;
										nx--;nz--;
										lightAverages[idx] = light(get(x+nx,y+ny,z+nz));
										lightAverages[idx] += light(get(x+nx+1,y+ny,z+nz));
										lightAverages[idx] += light(get(x+nx,y+ny,z+nz+1));
										lightAverages[idx] += light(get(x+nx+1,y+ny,z+nz+1));
										lightAverages[idx] /= 4;
										
										nx++;nz++;
									}
									//reduce average so top faces can be brighter
									if (lightAverages[idx] >= TOP_FACE_BRIGHTNESS_BONUS)lightAverages[idx] -= TOP_FACE_BRIGHTNESS_BONUS;
									else lightAverages[idx] = 0;
							}
								
					if(y < height - 1) {
						if(id(voxels[i+topOffset]) == 0) vertexOffset = createTop(offset, x, y, z, lightAverages, def, vertices, vertexOffset);
					} else {
						vertexOffset = createTop(offset, x, y, z, lightAverages, def, vertices, vertexOffset);
					}
					if(y > 0) {
						if(id(voxels[i+bottomOffset]) == 0) vertexOffset = createBottom(offset, x, y, z, lightAverages, def, vertices, vertexOffset);
					} else {
						vertexOffset = createBottom(offset, x, y, z, lightAverages, def, vertices, vertexOffset);
					}
					if(x > 0) {
						if(id(voxels[i+leftOffset]) == 0) vertexOffset = createLeft(offset, x, y, z, lightAverages, def, vertices, vertexOffset);
					} else {
						vertexOffset = createLeft(offset, x, y, z, lightAverages, def, vertices, vertexOffset);
					}
					if(x < width - 1) {
						if(id(voxels[i+rightOffset]) == 0) vertexOffset = createRight(offset, x, y, z, lightAverages, def, vertices, vertexOffset);
					} else {
						vertexOffset = createRight(offset, x, y, z, lightAverages, def, vertices, vertexOffset);
					}
					if(z > 0) {
						if(id(voxels[i+frontOffset]) == 0) vertexOffset = createFront(offset, x, y, z, lightAverages, def, vertices, vertexOffset);
					} else {
						vertexOffset = createFront(offset, x, y, z, lightAverages, def, vertices, vertexOffset);
					}
					if(z < depth - 1) {
						if(id(voxels[i+backOffset]) == 0) vertexOffset = createBack(offset, x, y, z, lightAverages, def, vertices, vertexOffset);
					} else {
						vertexOffset = createBack(offset, x, y, z, lightAverages, def, vertices, vertexOffset);
					}
				}
			}
		}
		return vertexOffset / VERTEX_SIZE;
	}
	
	/*private byte[] getColors(int x, int y, int z) {
		colors[BlockDefinition.TOP] = light(get(x,y+1,z));
		colors[BlockDefinition.BOTTOM] = light(get(x,y-1,z));
		colors[BlockDefinition.LEFT] = light(get(x-1,y,z));
		colors[BlockDefinition.RIGHT] = light(get(x+1,y,z));
		colors[BlockDefinition.FRONT] = light(get(x,y,z+1));
		colors[BlockDefinition.BACK] = light(get(x,y,z-1));
		for (int i = 0; i < 6; i++)
			Gdx.app.log("chunk", ""+colors[i]);
		return colors;
	}*/
	public final static float[] lightColors = new float[16];
	public static final float[] torchColors = new float[16];/* = {
		
		 new Color(.1f,.1f,.1f,1f).toFloatBits()
		, new Color(.1f,.1f,.1f,1f).toFloatBits()
		, new Color(.15f,.5f,.5f,1f).toFloatBits()
		, new Color(.15f,.5f,.5f,1f).toFloatBits()
		, new Color(.6f,.26f,.6f,1f).toFloatBits()
		, new Color(.6f,.26f,.6f,1f).toFloatBits()
		, new Color(.7f,.7f,.37f,1f).toFloatBits()
		, new Color(.8f,.8f,.8f,1f).toFloatBits()
		, new Color(.87f,.87f,.87f,1f).toFloatBits()
		, new Color(1f,1f,1f,1f).toFloatBits()
		, new Color(1,1,1,1f).toFloatBits()};/**/
	
	
	public static final int 
		
		 LIGHT_BIT = 16
		, LIGHT_MASK = 0xF0000
		, NOT_LIGHT_MASK = LIGHT_MASK ^ 0xFFFFFFFF
		
		, TORCHLIGHT_BIT = 20
		, TORCHLIGHT_MASK = 0xF00000
		, NOT_TORCHLIGHT_MASK = TORCHLIGHT_MASK ^ 0xFFFFFFFF
		
		, ID_BIT = 0
		, ID_MASK = 0xFF
		, NOT_ID_MASK = ID_MASK ^ 0xFFFFFFFF
		
		;
	public static int light(byte voxel) {
		if (id(voxel) != 0) return 0;
		return 15;
		//return (voxel & LIGHT_MASK)>>LIGHT_BIT;
	}
	
	public static int id(int voxel) {
		return voxel & ID_MASK;
	}

	public static int setLight(int b, int light) {
		b &= NOT_LIGHT_MASK;
		b |= light << LIGHT_BIT;
		return b;
	}

	public static BlockDefinition blockDef(int voxel){
		int i = id(voxel);
		return blockDefinitions[i];
	}
	public static int xp = 1, yp = 2, zp = 4;;

	public static int createTop(Vector3 offset, int x, int y, int z, int[] lightAverages, BlockDefinition def, float[] vertices, int vertexOffset) {
		int  side =  BlockDefinition.TOP;
		float[] uvs = def.getUVs(side);
		float u = uvs[0], v = uvs[1], 
				u2 = uvs[2], v2 = uvs[3];
		
		vertices[vertexOffset++] = offset.x + x;
		vertices[vertexOffset++] = offset.y + y + 1;
		vertices[vertexOffset++] = offset.z + z;
		vertices[vertexOffset++] = 0;
		vertices[vertexOffset++] = 1;
		vertices[vertexOffset++] = 0;
		vertices[vertexOffset++] = lightColors[lightAverages[yp]+TOP_FACE_BRIGHTNESS_BONUS];//(light(c[BlockDefinition.TOP])+light(c[BlockDefinition.LEFT])+light(c[BlockDefinition.BACK]))/3];//lightColors[(light(c[BlockDefinition.TOP]))];//
		vertices[vertexOffset++] = u;
		vertices[vertexOffset++] = v;
		
		vertices[vertexOffset++] = offset.x + x + 1;
		vertices[vertexOffset++] = offset.y + y + 1;
		vertices[vertexOffset++] = offset.z + z;
		vertices[vertexOffset++] = 0;
		vertices[vertexOffset++] = 1;
		vertices[vertexOffset++] = 0;
		vertices[vertexOffset++] = lightColors[lightAverages[xp+yp]+TOP_FACE_BRIGHTNESS_BONUS];//(light(c[BlockDefinition.TOP])+light(block)/2];//lightColors[(light(c[BlockDefinition.TOP]))];//
		vertices[vertexOffset++] = u2;
		vertices[vertexOffset++] = v;
		//Gdx.app.log("chunk", ""+(c[BlockDefinition.TOP]+c[BlockDefinition.RIGHT]+c[BlockDefinition.BACK])/3);
		vertices[vertexOffset++] = offset.x + x + 1;
		vertices[vertexOffset++] = offset.y + y + 1;
		vertices[vertexOffset++] = offset.z + z + 1;
		vertices[vertexOffset++] = 0;
		vertices[vertexOffset++] = 1;
		vertices[vertexOffset++] = 0;
		vertices[vertexOffset++] = lightColors[lightAverages[xp+yp+zp]+TOP_FACE_BRIGHTNESS_BONUS];//(light(c[BlockDefinition.TOP])+light(c[BlockDefinition.RIGHT])+light(c[BlockDefinition.FRONT]))/3];//lightColors[(light(c[BlockDefinition.TOP]))];
		vertices[vertexOffset++] = u2;
		vertices[vertexOffset++] = v2;
		//Gdx.app.log("chunk", ""+(c[BlockDefinition.TOP]+c[BlockDefinition.RIGHT]+c[BlockDefinition.FRONT])/3);
		vertices[vertexOffset++] = offset.x + x;
		vertices[vertexOffset++] = offset.y + y + 1;
		vertices[vertexOffset++] = offset.z + z + 1;
		vertices[vertexOffset++] = 0;
		vertices[vertexOffset++] = 1;
		vertices[vertexOffset++] = 0;
		vertices[vertexOffset++] = lightColors[lightAverages[yp+zp]+TOP_FACE_BRIGHTNESS_BONUS];//(light(c[BlockDefinition.TOP])+light(c[BlockDefinition.LEFT])+light(c[BlockDefinition.FRONT]))/3];//lightColors[(light(c[BlockDefinition.TOP]))];//
		vertices[vertexOffset++] = u;
		vertices[vertexOffset++] = v2;
		//Gdx.app.log("chunk", ""+(c[BlockDefinition.TOP]+c[BlockDefinition.LEFT]+c[BlockDefinition.FRONT])/3);
		flipVertices(yp, xp+yp, xp+yp+zp, yp+zp, vertices, vertexOffset, lightAverages);
		return vertexOffset;
	}
	
	public static int createBottom(Vector3 offset, int x, int y, int z, int[] lightAverages, BlockDefinition def, float[] vertices, int vertexOffset) {
		int  side =  BlockDefinition.BOTTOM;
		float[] uvs = def.getUVs(side);
		float u = uvs[0], v = uvs[1], 
				u2 = uvs[2], v2 = uvs[3];
		vertices[vertexOffset++] = offset.x + x;
		vertices[vertexOffset++] = offset.y + y;//d
		vertices[vertexOffset++] = offset.z + z;
		vertices[vertexOffset++] = 0;
		vertices[vertexOffset++] = -1;
		vertices[vertexOffset++] = 0;
		vertices[vertexOffset++] = lightColors[lightAverages[0]];
		vertices[vertexOffset++] = u;
		vertices[vertexOffset++] = v;
		
		vertices[vertexOffset++] = offset.x + x;
		vertices[vertexOffset++] = offset.y + y;
		vertices[vertexOffset++] = offset.z + z + 1;
		vertices[vertexOffset++] = 0;
		vertices[vertexOffset++] = -1;
		vertices[vertexOffset++] = 0;
		vertices[vertexOffset++] = lightColors[lightAverages[zp]];
		vertices[vertexOffset++] = u2;
		vertices[vertexOffset++] = v;
		
		vertices[vertexOffset++] = offset.x + x + 1;
		vertices[vertexOffset++] = offset.y + y;
		vertices[vertexOffset++] = offset.z + z + 1;
		vertices[vertexOffset++] = 0;
		vertices[vertexOffset++] = -1;
		vertices[vertexOffset++] = 0;
		vertices[vertexOffset++] = lightColors[lightAverages[xp+zp]];
		vertices[vertexOffset++] = u2;
		vertices[vertexOffset++] = v2;
		
		vertices[vertexOffset++] = offset.x + x + 1;
		vertices[vertexOffset++] = offset.y + y;
		vertices[vertexOffset++] = offset.z + z;
		vertices[vertexOffset++] = 0;
		vertices[vertexOffset++] = -1;
		vertices[vertexOffset++] = 0;
		vertices[vertexOffset++] = lightColors[lightAverages[xp]];
		vertices[vertexOffset++] = u;
		vertices[vertexOffset++] = v2;
		flipVertices(0, zp, xp+zp, xp, vertices, vertexOffset, lightAverages);
		return vertexOffset;
	}
	
	public static int createLeft(Vector3 offset, int x, int y, int z, int[] lightAverages, BlockDefinition def, float[] vertices, int vertexOffset) {
		int  side =  BlockDefinition.LEFT;
		float[] uvs = def.getUVs(side);
		float u = uvs[0], v = uvs[1], 
				u2 = uvs[2], v2 = uvs[3];
		vertices[vertexOffset++] = offset.x + x;
		vertices[vertexOffset++] = offset.y + y;
		vertices[vertexOffset++] = offset.z + z;
		vertices[vertexOffset++] = -1;
		vertices[vertexOffset++] = 0;
		vertices[vertexOffset++] = 0;
		vertices[vertexOffset++] = lightColors[lightAverages[0]];
		vertices[vertexOffset++] = u;
		vertices[vertexOffset++] = v2;
		
		
		vertices[vertexOffset++] = offset.x + x;
		vertices[vertexOffset++] = offset.y + y + 1;
		vertices[vertexOffset++] = offset.z + z;
		vertices[vertexOffset++] = -1;
		vertices[vertexOffset++] = 0;
		vertices[vertexOffset++] = 0;
		vertices[vertexOffset++] = lightColors[lightAverages[yp]];
		vertices[vertexOffset++] = u;
		vertices[vertexOffset++] = v;
		
		vertices[vertexOffset++] = offset.x + x;
		vertices[vertexOffset++] = offset.y + y + 1;
		vertices[vertexOffset++] = offset.z + z + 1;
		vertices[vertexOffset++] = -1;
		vertices[vertexOffset++] = 0;
		vertices[vertexOffset++] = 0;
		vertices[vertexOffset++] = lightColors[lightAverages[yp+zp]];
		vertices[vertexOffset++] = u2;
		vertices[vertexOffset++] = v;
		
		vertices[vertexOffset++] = offset.x + x;
		vertices[vertexOffset++] = offset.y + y;
		vertices[vertexOffset++] = offset.z + z + 1;
		vertices[vertexOffset++] = -1;
		vertices[vertexOffset++] = 0;
		vertices[vertexOffset++] = 0;
		vertices[vertexOffset++] = lightColors[lightAverages[zp]];
		vertices[vertexOffset++] = u2;
		vertices[vertexOffset++] = v2;
		flipVertices(0, yp, yp+zp, zp, vertices, vertexOffset, lightAverages);
		return vertexOffset;
	}
	
	public static int createRight(Vector3 offset, int x, int y, int z, int[] lightAverages, BlockDefinition def, float[] vertices, int vertexOffset) {
		int  side =  BlockDefinition.RIGHT;
		float[] uvs = def.getUVs(side);
		float u = uvs[0], v = uvs[1], 
				u2 = uvs[2], v2 = uvs[3];
		vertices[vertexOffset++] = offset.x + x + 1;
		vertices[vertexOffset++] = offset.y + y;
		vertices[vertexOffset++] = offset.z + z;
		vertices[vertexOffset++] = 0;
		vertices[vertexOffset++] = 1;
		vertices[vertexOffset++] = 0;
		vertices[vertexOffset++] = lightColors[lightAverages[xp]];
		vertices[vertexOffset++] = u2;
		vertices[vertexOffset++] = v2;
		
		vertices[vertexOffset++] = offset.x + x + 1;
		vertices[vertexOffset++] = offset.y + y;
		vertices[vertexOffset++] = offset.z + z + 1;
		vertices[vertexOffset++] = 0;
		vertices[vertexOffset++] = 1;
		vertices[vertexOffset++] = 0;
		vertices[vertexOffset++] = lightColors[lightAverages[xp+zp]];
		vertices[vertexOffset++] = u;
		vertices[vertexOffset++] = v2;
		
		vertices[vertexOffset++] = offset.x + x + 1;
		vertices[vertexOffset++] = offset.y + y + 1;
		vertices[vertexOffset++] = offset.z + z + 1;
		vertices[vertexOffset++] = 0;
		vertices[vertexOffset++] = 1;
		vertices[vertexOffset++] = 0;
		vertices[vertexOffset++] = lightColors[lightAverages[xp+yp+zp]];
		vertices[vertexOffset++] = u;
		vertices[vertexOffset++] = v;
		
		vertices[vertexOffset++] = offset.x + x + 1;
		vertices[vertexOffset++] = offset.y + y + 1;
		vertices[vertexOffset++] = offset.z + z;
		vertices[vertexOffset++] = 0;
		vertices[vertexOffset++] = 1;
		vertices[vertexOffset++] = 0;
		vertices[vertexOffset++] = lightColors[lightAverages[xp+yp]];
		vertices[vertexOffset++] = u2;
		vertices[vertexOffset++] = v;
		flipVertices(xp, xp+zp, xp+yp+zp, xp+yp, vertices, vertexOffset, lightAverages);
		return vertexOffset;
		
	}
	
	//n
	public static int createFront(Vector3 offset, int x, int y, int z, int[] lightAverages, BlockDefinition def, float[] vertices, int vertexOffset) {
		int  side =  BlockDefinition.FRONT;
		float[] uvs = def.getUVs(side);
		float u = uvs[0], v = uvs[1], 
				u2 = uvs[2], v2 = uvs[3];
		vertices[vertexOffset++] = offset.x + x;
		vertices[vertexOffset++] = offset.y + y;
		vertices[vertexOffset++] = offset.z + z;
		vertices[vertexOffset++] = 0;
		vertices[vertexOffset++] = 0;
		vertices[vertexOffset++] = 1;
		vertices[vertexOffset++] = lightColors[lightAverages[0]];
		vertices[vertexOffset++] = u;
		vertices[vertexOffset++] = v2;
		//wus
		vertices[vertexOffset++] = offset.x + x + 1;
		vertices[vertexOffset++] = offset.y + y;
		vertices[vertexOffset++] = offset.z + z;
		vertices[vertexOffset++] = 0;
		vertices[vertexOffset++] = 0;
		vertices[vertexOffset++] = 1;
		vertices[vertexOffset++] = lightColors[lightAverages[xp]];
		vertices[vertexOffset++] = u2;
		vertices[vertexOffset++] = v2;
		
		vertices[vertexOffset++] = offset.x + x + 1;
		vertices[vertexOffset++] = offset.y + y + 1;
		vertices[vertexOffset++] = offset.z + z;
		vertices[vertexOffset++] = 0;
		vertices[vertexOffset++] = 0;
		vertices[vertexOffset++] = 1;
		vertices[vertexOffset++] = lightColors[lightAverages[xp+yp]];
		vertices[vertexOffset++] = u2;
		vertices[vertexOffset++] = v;
		
		vertices[vertexOffset++] = offset.x + x;
		vertices[vertexOffset++] = offset.y + y + 1;
		vertices[vertexOffset++] = offset.z + z;
		vertices[vertexOffset++] = 0;
		vertices[vertexOffset++] = 0;
		vertices[vertexOffset++] = 1;
		vertices[vertexOffset++] = lightColors[lightAverages[yp]];
		vertices[vertexOffset++] = u;
		vertices[vertexOffset++] = v;
		flipVertices(0, xp, xp+yp, yp, vertices, vertexOffset, lightAverages);
		return vertexOffset;
	}
	
	public static int createBack(Vector3 offset, int x, int y, int z, int[] lightAverages, BlockDefinition def, float[] vertices, int vertexOffset) {
		int  side =  BlockDefinition.BACK;
		float[] uvs = def.getUVs(side);
		float u = uvs[0], v = uvs[1], 
				u2 = uvs[2], v2 = uvs[3];
		vertices[vertexOffset++] = offset.x + x;
		vertices[vertexOffset++] = offset.y + y;
		vertices[vertexOffset++] = offset.z + z + 1;
		vertices[vertexOffset++] = 0;
		vertices[vertexOffset++] = 0;
		vertices[vertexOffset++] = -1;
		vertices[vertexOffset++] = lightColors[lightAverages[zp]];
		vertices[vertexOffset++] = u;
		vertices[vertexOffset++] = v2;
		
		vertices[vertexOffset++] = offset.x + x;
		vertices[vertexOffset++] = offset.y + y + 1;
		vertices[vertexOffset++] = offset.z + z + 1;
		vertices[vertexOffset++] = 0;
		vertices[vertexOffset++] = 0;
		vertices[vertexOffset++] = -1;
		vertices[vertexOffset++] = lightColors[lightAverages[yp+zp]];
		vertices[vertexOffset++] = u;
		vertices[vertexOffset++] = v;
		
		vertices[vertexOffset++] = offset.x + x + 1;
		vertices[vertexOffset++] = offset.y + y + 1;
		vertices[vertexOffset++] = offset.z + z + 1;
		vertices[vertexOffset++] = 0;
		vertices[vertexOffset++] = 0;
		vertices[vertexOffset++] = -1;
		vertices[vertexOffset++] = lightColors[lightAverages[xp+yp+zp]];
		vertices[vertexOffset++] = u2;
		vertices[vertexOffset++] = v;
		
		vertices[vertexOffset++] = offset.x + x + 1;
		vertices[vertexOffset++] = offset.y + y;
		vertices[vertexOffset++] = offset.z + z + 1;
		vertices[vertexOffset++] = 0;
		vertices[vertexOffset++] = 0;
		vertices[vertexOffset++] = -1;
		vertices[vertexOffset++] = lightColors[lightAverages[xp+zp]];
		vertices[vertexOffset++] = u2;
		vertices[vertexOffset++] = v2;
		flipVertices(zp, yp+zp, xp+yp+zp, xp+zp, vertices, vertexOffset, lightAverages);
		return vertexOffset;
	}
	static float[] tmp = new float[VERTEX_SIZE];
	
	private static void flipVertices(int i, int j, int k, int l, float[] vertices, int vertexOffset, int[] lightAverages) {
		if (lightAverages[i]+ lightAverages[k] <  lightAverages[j]+ lightAverages[l]){
			vertexOffset-= VERTEX_SIZE*4;
			for (int c = 0; c < VERTEX_SIZE; c++){
				tmp[c] = vertices[vertexOffset+c];
			}
			
			for (int reps = 0; reps < 3; reps++, vertexOffset+= VERTEX_SIZE){
				for (int c = 0; c < VERTEX_SIZE; c++){
					vertices[vertexOffset+c] = vertices[vertexOffset+VERTEX_SIZE+c];
					
				}
				;
			}
			for (int c = 0; c < VERTEX_SIZE; c++){
				vertices[vertexOffset+c] = tmp[c];
			}
		}
		
	}
	
	
}