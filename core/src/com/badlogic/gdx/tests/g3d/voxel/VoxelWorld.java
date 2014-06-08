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


import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.graphics.g3d.Material;
import com.badlogic.gdx.graphics.g3d.Renderable;
import com.badlogic.gdx.graphics.g3d.RenderableProvider;
import com.badlogic.gdx.graphics.g3d.Shader;
import com.badlogic.gdx.math.Matrix4;
import com.badlogic.gdx.math.Vector3;
import com.badlogic.gdx.utils.Array;
import com.badlogic.gdx.utils.Pool;
import com.niz.component.Position;

public class VoxelWorld implements RenderableProvider {
	public final int CHUNK_SIZE_X;
	public final int CHUNK_SIZE_Y;
	public final int CHUNK_SIZE_Z;
    public final int PLANES;
	private static final String TAG = "VoxelWorld";

	public final VoxelChunk[] chunks;
	//public final Mesh[] meshes;
	//public final Material[] materials;
	public final boolean[] dirty;
	//public final int[] numVertices;
	//public float[] vertices;
	public final int chunksX;
	public final int chunksY;
	public final int chunksZ;
	public final int voxelsX;
	public final int voxelsY;
	public final int voxelsZ;
	public int renderedChunks;
	public final int numChunks;
	private Material material;
	public int offsetX, offsetY, offsetZ;
    private Shader shader;

    public VoxelWorld(int chunksX, int chunksY, int chunksZ, int sizeX, int sizeY, int planes) {
        PLANES = planes;

        CHUNK_SIZE_X = sizeX;
        CHUNK_SIZE_Y = sizeY;
        CHUNK_SIZE_Z = sizeX;
		//if (blockDefs == null) throw new GdxRuntimeException("nill init");
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
        for (int p = 0; p < planes; p++)
		for(int y = 0; y < chunksY; y++) {
			for(int z = 0; z < chunksZ; z++) {
				for(int x = 0; x < chunksX; x++) {
					VoxelChunk chunk = new VoxelChunk(CHUNK_SIZE_X, CHUNK_SIZE_Y, CHUNK_SIZE_Z, i, p);
					chunk.offset.set(x * CHUNK_SIZE_X, y * CHUNK_SIZE_Y, z * CHUNK_SIZE_Z);
					chunks[i++] = chunk;
				}
			}
		}
		
		//this.meshes = new Mesh[chunksX * chunksY * chunksZ];
		//for(i = 0; i < meshes.length; i++) {
		//	meshes[i] = mesher.newMesh(CHUNK_SIZE_X, CHUNK_SIZE_Y, CHUNK_SIZE_Z);
			
		//}
		this.dirty = new boolean[chunksX * chunksY * chunksZ];
		for(i = 0; i < dirty.length; i++) dirty[i] = true;

		//this.numVertices = new int[chunksX * chunksY * chunksZ];
		//for(i = 0; i < numVertices.length; i++) numVertices[i] = 0;

		//this.vertices = new float[VoxelChunk.VERTEX_SIZE * 6 * CHUNK_SIZE_X * CHUNK_SIZE_Y * CHUNK_SIZE_Z*2];
        
     
                

	}

	public void set(Vector3 p, int plane, byte voxel){
        set(p.x, p.y, p.z, plane, voxel);
    }

	public void set(float x, float y, float z, int p, byte voxel) {
		int ix = (int)x;
		int iy = (int)y;
		int iz = (int)z;
		int chunkX = ix / CHUNK_SIZE_X;
		if(chunkX < offsetX || chunkX >= chunksX + offsetX) return;
		int chunkY = iy / CHUNK_SIZE_Y;
		if(chunkY < offsetY || chunkY >= chunksY + offsetY) return;
		int chunkZ = iz / CHUNK_SIZE_Z;
		if(chunkZ < offsetZ || chunkZ >= chunksZ + offsetZ) return;
		chunkX %= chunksX;
		chunkY %= chunksY;
		chunkZ %= chunksZ;
		int index = chunkX + chunkZ * chunksX + chunkY * chunksX * chunksZ;// + numChunks*p;
		chunks[index].set(ix % CHUNK_SIZE_X, iy % CHUNK_SIZE_Y, iz % CHUNK_SIZE_Z, voxel);
		dirty[index] = true;
	}

    public int get(Vector3 p, int plane) {
        return get(p.x, p.y, p.z, plane);
    }


    public byte get(float x, float y, float z, int p) {
		int ix = (int)x;
		int iy = (int)y;
		int iz = (int)z;
		int chunkX = ix / CHUNK_SIZE_X;
		if(chunkX < offsetX || chunkX >= chunksX + offsetX) return 0;
		int chunkY = iy / CHUNK_SIZE_Y;
		if(chunkY < offsetY || chunkY >= chunksY + offsetY) return 0;
		int chunkZ = iz / CHUNK_SIZE_Z;
		if(chunkZ < offsetZ || chunkZ >= chunksZ + offsetZ) return 0;
		chunkX %= chunksX;
		chunkY %= chunksY;
		chunkZ %= chunksZ;
		int index = chunkX + chunkZ * chunksX + chunkY * chunksX * chunksZ ;//+ numChunks*p;
		return chunks[index].get(ix % CHUNK_SIZE_X, iy % CHUNK_SIZE_Y, iz % CHUNK_SIZE_Z);
	}





	@Override
	public void getRenderables (Array<Renderable> renderables, Pool<Renderable> pool) {
		renderedChunks = 0;
		for(int i = 0; i < chunks.length; i++) {
			VoxelChunk chunk = chunks[i];
			//Mesh mesh = meshes[i];
			if(dirty[i]) {
				
				continue;
			}


            //if(numVertices[i] == 0) continue;
			Renderable renderable = pool.obtain();
			renderable.material = material;
			//if (chunk.mesh == null) continue;
			renderable.mesh = chunk.mesh;
			renderable.meshPartOffset = 0;
			renderable.meshPartSize = chunk.numVerts/4*6;
			renderable.shader = shader;
			renderable.primitiveType = GL20.GL_TRIANGLES;
			renderable.worldTransform.idt().translate(chunk.offset);
			renderables.add(renderable);
          //  Gdx.app.log(TAG,  "verts"+new DefaultShaderProvider().getShader(renderable).toString());

			renderedChunks++;
		}
	}

	public VoxelChunk getClosestDirtyChunk(Position pos) {
		float dist = 0;
		VoxelChunk closestChunk = null;
		for(int i = 0; i < chunks.length; i++) {
			VoxelChunk chunk = chunks[i];
			//Mesh mesh = meshes[i];
            if (chunk.plane != pos.plane) continue;
			if(dirty[i]) {
				
				float d = pos.pos.dst2(chunk.offset);
				if (d < dist || closestChunk == null){
					//Gdx.app.log("dirsty", "found");
					closestChunk = chunk;
					dist = d;
				}
			}
		}
		return closestChunk;
	}


    public void setMaterial(Material material) {
        this.material = material;
    }

    public void setShader(Shader shader) {
        this.shader = shader;
    }

    public Shader getShader() {
        return shader;
    }


    //make mesh closest to position
    public void makeMesh(Position position, Mesher mesher, MeshBatcher batch) {

        VoxelChunk closest = getClosestDirtyChunk(position);

        if (closest != null){
            mesher.calculateVertices(closest, this, batch);
            dirty[closest.index] = false;
        }

    }
}