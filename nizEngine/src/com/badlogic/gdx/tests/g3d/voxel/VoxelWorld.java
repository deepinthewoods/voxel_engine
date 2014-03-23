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

import voxel.BlockDefinition;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.graphics.Mesh;
import com.badlogic.gdx.graphics.VertexAttribute;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.graphics.g3d.Material;
import com.badlogic.gdx.graphics.g3d.Renderable;
import com.badlogic.gdx.graphics.g3d.RenderableProvider;
import com.badlogic.gdx.graphics.g3d.attributes.ColorAttribute;
import com.badlogic.gdx.graphics.g3d.attributes.TextureAttribute;
import com.badlogic.gdx.math.Matrix4;
import com.badlogic.gdx.math.Vector3;
import com.badlogic.gdx.utils.Array;
import com.badlogic.gdx.utils.GdxRuntimeException;
import com.badlogic.gdx.utils.Pool;

public class VoxelWorld implements RenderableProvider {
	public static final int CHUNK_SIZE_X = 32;
	public static final int CHUNK_SIZE_Y = 32;
	public static final int CHUNK_SIZE_Z = 32;

	public final VoxelChunk[] chunks;
	public final Mesh[] meshes;
	//public final Material[] materials;
	public final boolean[] dirty;
	public final int[] numVertices;
	public float[] vertices;
	public final int chunksX;
	public final int chunksY;
	public final int chunksZ;
	public final int voxelsX;
	public final int voxelsY;
	public final int voxelsZ;
	public int renderedChunks;
	public int numChunks;
	private final Material material;
	public int offsetX, offsetY, offsetZ;
	private Mesher mesher;
	
	public VoxelWorld(BlockDefinition[] blockDefs, Material material, int chunksX, int chunksY, int chunksZ, Mesher mesher) {
		VoxelChunk.defs = blockDefs;
		if (blockDefs == null) throw new GdxRuntimeException("nill init");
		this.material = material;
		this.chunks = new VoxelChunk[chunksX * chunksY * chunksZ];
		this.chunksX = chunksX;
		this.chunksY = chunksY;
		this.chunksZ = chunksZ;
		this.numChunks = chunksX * chunksY * chunksZ;
		this.voxelsX = chunksX * CHUNK_SIZE_X;
		this.voxelsY = chunksY * CHUNK_SIZE_Y;
		this.voxelsZ = chunksZ * CHUNK_SIZE_Z;
		int i = 0;
		for(int y = 0; y < chunksY; y++) {
			for(int z = 0; z < chunksZ; z++) {
				for(int x = 0; x < chunksX; x++) {
					VoxelChunk chunk = new VoxelChunk(CHUNK_SIZE_X, CHUNK_SIZE_Y, CHUNK_SIZE_Z, i);
					chunk.offset.set(x * CHUNK_SIZE_X, y * CHUNK_SIZE_Y, z * CHUNK_SIZE_Z);
					chunks[i++] = chunk;
				}
			}
		}
		int len = CHUNK_SIZE_X * CHUNK_SIZE_Y * CHUNK_SIZE_Z * 6 * 6 / 3;
		short[] indices = new short[len];
		short j = 0;
		for (i = 0; i < len; i += 6, j += 4) {
			indices[i + 0] = (short)(j + 0);
			indices[i + 1] = (short)(j + 1);
			indices[i + 2] = (short)(j + 2);
			indices[i + 3] = (short)(j + 2);
			indices[i + 4] = (short)(j + 3);
			indices[i + 5] = (short)(j + 0);
		}
		this.meshes = new Mesh[chunksX * chunksY * chunksZ];
		for(i = 0; i < meshes.length; i++) {
			meshes[i] = new Mesh(true, 
										CHUNK_SIZE_X * CHUNK_SIZE_Y * CHUNK_SIZE_Z * 6 * 4, 
										CHUNK_SIZE_X * CHUNK_SIZE_Y * CHUNK_SIZE_Z * 36 / 3,
										VertexAttribute.Position(), VertexAttribute.Normal()
										, VertexAttribute.Color(), VertexAttribute.TexCoords(0));
			meshes[i].setIndices(indices);
		}
		this.dirty = new boolean[chunksX * chunksY * chunksZ];
		for(i = 0; i < dirty.length; i++) dirty[i] = true;

		this.numVertices = new int[chunksX * chunksY * chunksZ];
		for(i = 0; i < numVertices.length; i++) numVertices[i] = 0;

		this.vertices = new float[VoxelChunk.VERTEX_SIZE * 6 * CHUNK_SIZE_X * CHUNK_SIZE_Y * CHUNK_SIZE_Z*2];
		this.mesher = mesher;
	}

	public void set(float x, float y, float z, byte voxel) {
		int ix = (int)x;
		int iy = (int)y;
		int iz = (int)z;
		int chunkX = ix / CHUNK_SIZE_X;
		if(chunkX < offsetX || chunkX >= chunksX + offsetX) return;
		int chunkY = iy / CHUNK_SIZE_Y;
		if(chunkY < offsetY || chunkY >= chunksY + offsetY) return;
		int chunkZ = iz / CHUNK_SIZE_Z;
		if(chunkZ < offsetZ || chunkZ >= chunksZ + offsetZ) return;
		chunkX &= chunksX;
		chunkY &= chunksY;
		chunkZ &= chunksZ;
		int index = chunkX + chunkZ * chunksX + chunkY * chunksX * chunksZ;
		chunks[index].set(ix % CHUNK_SIZE_X, iy % CHUNK_SIZE_Y, iz % CHUNK_SIZE_Z, voxel);
		dirty[index] = true;
	}

	public byte get(float x, float y, float z) {
		int ix = (int)x;
		int iy = (int)y;
		int iz = (int)z;
		int chunkX = ix / CHUNK_SIZE_X;
		if(chunkX < offsetX || chunkX >= chunksX + offsetX) return 0;
		int chunkY = iy / CHUNK_SIZE_Y;
		if(chunkY < offsetY || chunkY >= chunksY + offsetY) return 0;
		int chunkZ = iz / CHUNK_SIZE_Z;
		if(chunkZ < offsetZ || chunkZ >= chunksZ + offsetZ) return 0;
		chunkX &= chunksX;
		chunkY &= chunksY;
		chunkZ &= chunksZ;
		int index = chunkX + chunkZ * chunksX + chunkY * chunksX * chunksZ;
		return chunks[index].get(ix % CHUNK_SIZE_X, iy % CHUNK_SIZE_Y, iz % CHUNK_SIZE_Z);
	}

	public float getHighest (float x, float z) {
		int ix = (int)x;
		int iz = (int)z;
		if(ix < 0 || ix >= voxelsX) return 0;
		if(iz < 0 || iz >= voxelsZ) return 0;
		// FIXME optimize
		for(int y = voxelsY - 1; y > 0; y--) {
			if(get(ix, y, iz) > 0) return y + 1;
		}
		return 0;
	}

	public void setColumn(float x, float y, float z, byte voxel) {
		int ix = (int)x;
		int iy = (int)y;
		int iz = (int)z;
		if(ix < 0 || ix >= voxelsX) return;
		if(iy < 0 || iy >= voxelsY) return;
		if(iz < 0 || iz >= voxelsZ) return;
		// FIXME optimize
		for(; iy > 0; iy--) {
			set(ix, iy, iz, voxel);
		}
	}

	public void setCube(float x, float y, float z, float width, float height, float depth, byte voxel) {
		int ix = (int)x;
		int iy = (int)y;
		int iz = (int)z;
		int iwidth = (int)width;
		int iheight = (int)height;
		int idepth = (int)depth;
		int startX = Math.max(ix, 0);
		int endX = Math.min(voxelsX, ix + iwidth);
		int startY = Math.max(iy, 0);
		int endY = Math.min(voxelsY, iy + iheight);
		int startZ = Math.max(iz, 0);
		int endZ = Math.min(voxelsZ, iz + idepth);
		// FIXME optimize
		for(iy = startY; iy < endY; iy++) {
			for(iz = startZ; iz < endZ; iz++) {
				for(ix = startX; ix < endX; ix++) {
					set(ix, iy, iz, voxel);
				}
			}
		}	
	}

	@Override
	public void getRenderables (Array<Renderable> renderables, Pool<Renderable> pool) {
		renderedChunks = 0;
		for(int i = 0; i < chunks.length; i++) {
			VoxelChunk chunk = chunks[i];
			Mesh mesh = meshes[i];
			if(dirty[i]) {
				
				int numVerts = mesher.calculateVertices(vertices, chunk, mesh, this);
				numVertices[i] = numVerts / 4 * 6;
				mesh.setVertices(vertices, 0, numVerts * VoxelChunk.VERTEX_SIZE);
				dirty[i] = false;
			}
			if(numVertices[i] == 0) continue;
			Renderable renderable = pool.obtain();
			renderable.material = material;
			renderable.mesh = mesh;
			renderable.meshPartOffset = 0;
			renderable.meshPartSize = numVertices[i];
			renderable.primitiveType = GL20.GL_TRIANGLES;
			renderable.worldTransform.set(idMatrix);
			renderables.add(renderable);
			renderedChunks++;
		}
	}
	private Matrix4 idMatrix = new Matrix4().idt();
	public int get(Vector3 p) {
		return get(p.x, p.y, p.z);
	}

	public VoxelChunk getClosestDirtyChunk(Vector3 pos) {
		float dist = 0;
		VoxelChunk closestChunk = null;
		for(int i = 0; i < chunks.length; i++) {
			VoxelChunk chunk = chunks[i];
			Mesh mesh = meshes[i];
			if(dirty[i]) {
				
				float d = pos.dst2(chunk.offset);
				if (d < dist || closestChunk == null){
					//Gdx.app.log("dirsty", "found");
					closestChunk = chunk;
					dist = d;
				}
			}
		}
		return closestChunk;
	}

	public Mesh getMesh(int index) {
		return meshes[index];
	}

	public void setDirty(int index, boolean b) {
		dirty[index] = b;
	}

	public void setNumVertices(int i, int c) {
		numVertices[i] = c;
	}
}