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


import com.artemis.Component;
import com.artemis.Entity;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.graphics.g3d.Material;
import com.badlogic.gdx.graphics.g3d.Renderable;
import com.badlogic.gdx.graphics.g3d.RenderableProvider;
import com.badlogic.gdx.graphics.g3d.Shader;
import com.badlogic.gdx.math.MathUtils;
import com.badlogic.gdx.math.Vector3;
import com.badlogic.gdx.utils.Array;
import com.badlogic.gdx.utils.GdxRuntimeException;
import com.badlogic.gdx.utils.IntMap;
import com.badlogic.gdx.utils.Pool;
import com.badlogic.gdx.utils.Pools;
import com.niz.component.Position;
import com.niz.observer.Observer;
import com.niz.observer.Subject.Event;

public class VoxelWorld implements RenderableProvider {

    public final int CHUNK_SIZE_X;
	public final int CHUNK_SIZE_Y;
	public final int CHUNK_SIZE_Z;
    public final int PLANES;
	private static final String TAG = "VoxelWorld";
    private final int maxChunks;

    private IntMap<VoxelChunk> chunks = new IntMap<VoxelChunk>();


	public int renderedChunks;
	private Material material;
	public int offsetX, offsetY, offsetZ;
    private Shader shader;
    private float drawDistance = 1000;
    private float drawDistance2 = drawDistance * drawDistance;
    public Vector3 worldCentrePoint = new Vector3();
    private int chunkTotal;
    private Pool<VoxelChunk> chunkPool;

    public VoxelWorld(int maxChunks, int sizeX, int sizeY, int planes) {
        PLANES = planes;
        this.maxChunks = maxChunks;
        CHUNK_SIZE_X = sizeX;
        CHUNK_SIZE_Y = sizeY;
        CHUNK_SIZE_Z = sizeX;

        chunks.ensureCapacity(1000);

		int i = 0;

        chunkTotal = 0;

        chunkPool = new Pool<VoxelChunk>(){
            int index = 0;
            @Override
            protected VoxelChunk newObject() {
                return new VoxelChunk(CHUNK_SIZE_X, CHUNK_SIZE_Y, CHUNK_SIZE_Z, index++, 0);
            }
        };

	}

	public void set(Vector3 p, int plane, byte voxel){
        set(p.x, p.y, p.z, plane, voxel);
    }

	public void set(float x, float y, float z, int p, byte voxel) {
		int ix = MathUtils.floor(x);
		int iy = MathUtils.floor(y);
		int iz = MathUtils.floor(z);
        int cx = ix / CHUNK_SIZE_X
                , cy = iy / CHUNK_SIZE_Y
                , cz = iz / CHUNK_SIZE_Z;

        ix = (ix % CHUNK_SIZE_X + CHUNK_SIZE_X) % CHUNK_SIZE_X;
        iy = (iy % CHUNK_SIZE_Y + CHUNK_SIZE_Y) % CHUNK_SIZE_Y;
        iz = (iz % CHUNK_SIZE_Z + CHUNK_SIZE_Z) % CHUNK_SIZE_Z;

		int hash = chunkHash(cx, cy, cz, p);
		VoxelChunk chunk = getChunk(hash);
        if (chunk == null) return;
        chunk.setFast(ix, iy, iz, voxel);

	}

    public int get(Vector3 p, int plane) {
        return get(p.x, p.y, p.z, plane);
    }


    public byte get(float x, float y, float z, int p) {
        int ix = MathUtils.floor(x);
        int iy = MathUtils.floor(y);
        int iz = MathUtils.floor(z);
        int cx = ix / CHUNK_SIZE_X
                , cy = iy / CHUNK_SIZE_Y
                , cz = iz / CHUNK_SIZE_Z;

        ix = (ix % CHUNK_SIZE_X + CHUNK_SIZE_X) % CHUNK_SIZE_X;
        iy = (iy % CHUNK_SIZE_Y + CHUNK_SIZE_Y) % CHUNK_SIZE_Y;
        iz = (iz % CHUNK_SIZE_Z + CHUNK_SIZE_Z) % CHUNK_SIZE_Z;



        //(i % n + n) % n;
        int hash = chunkHash(cx, cy, cz, p);
        if (!chunks.containsKey(hash)) {
            //Gdx.app.log(TAG, "get invalid "+x+" , "+y+"  ,  "+z);
            return 0;
        }
        VoxelChunk chunk = getChunk(hash);
        return chunk.getFast(ix, iy, iz);

    }

    private VoxelChunk getChunk(int hash) {
        VoxelChunk c = chunks.get(hash);
        return c;
    }
    static int p1 = 907, p2 = 6043, p3 = 510287, p4 = 86028157;
    public static int chunkHash(int ix, int iy, int iz, int p) {
        /*return
                ( (ix) & 0xFF ) +
                ( (iy >>> 8) & 0xFF )<<8 +
                ( (iz >>> 16) & 0xFF )<<16 +
                ( (p >>> 24) & 0xFF ) << 24;//*/

        /*return
        ( (ix % 0x3FF + 0x3FF )%0x3FF) ^
        (( (iy  % 0x3FF + 0x3FF )%0x3FF)<<10 )^
        (( (iz  % 0x3FF + 0x3FF )%0x3FF)<<20 );//^*/
        //jjjjjjjjjjjjjjj(( (p  % 0xFF + 0xFF )&0xFF) << 30);//
        //int h = (ix * p1)  ^ (iy * p2) ^ (iz * p3) ^ (p*p4) ;
        //return h;

        final int prime = 15667;
        int result = 1;
        result = prime * result + ix;
        result = prime * result + iy;
        result = prime * result + iz;
        return result;
    }

    public static int intHash(int a){

        a -= (a<<6);
        a ^= (a>>17);
        a -= (a<<9);
        a ^= (a<<4);
        a -= (a<<3);
        a ^= (a<<10);
        a ^= (a>>15);
        return a;
    }

    public int chunkHash(VoxelChunk c) {
        int x = MathUtils.floor(c.offset.x);
        int y = MathUtils.floor( c.offset.y);
        int z = MathUtils.floor( c.offset.z);
        x /= CHUNK_SIZE_X;
        y /= CHUNK_SIZE_Y;
        z /= CHUNK_SIZE_Z;
        return chunkHash(x,y,z,c.plane);
    }





	@Override
	public void getRenderables (Array<Renderable> renderables, Pool<Renderable> pool) {
		renderedChunks = 0;
        IntMap.Values<VoxelChunk> iter = chunks.values();
        //Gdx.app.log(TAG,  "Renderable provided" + iter.hasNext());
		while (iter.hasNext()){

            VoxelChunk chunk = iter.next();
			//Mesh mesh = meshes[i];
			if(chunk.mesh == null 
					//|| chunk.offset.dst2(worldCentrePoint) > drawDistance2
					) 
			{
				//Gdx.app.log(TAG,  "Renderable SKIPPED" + chunk.offset);

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
			//renderable.worldTransform.idt().translate(chunk.offset);
            renderable.worldTransform.setTranslation(chunk.offset);
			renderables.add(renderable);
           //Gdx.app.log(TAG,  "Renderable provided" + chunk.offset);

			renderedChunks++;
		}

        //Gdx.app.log(TAG, "chunks total:"+renderedChunks);
	}

	public VoxelChunk getClosestDirtyChunk(Position pos) {
        synchronized (dirtyLock) {
            float dist = 0;
            VoxelChunk closestChunk = null;
            IntMap.Values<VoxelChunk> iter = chunks.values();
            while (iter.hasNext()){
                //Gdx.app.log("dirsty", "f/////////yuyuuuuuuuuuuuuuuffffffffffffffffffffffffffffffff");

                VoxelChunk chunk = iter.next();
                //Mesh mesh = meshes[i];
                if (chunk.plane != pos.plane) continue;
                //Gdx.app.log("dirsty", "f "+chunk.getDirty() + allValidSurrounding(chunk));
                if (chunk.getDirty() && allValidSurrounding(chunk)) {

                    float d = pos.pos.dst2(chunk.offset);
                    if (d < dist || closestChunk == null) {
                        //Gdx.app.log("dirsty", "foundffffffffffffffffffffffffffffffffff");
                        closestChunk = chunk;
                        dist = d;
                    }
                }
            }
            return closestChunk;
        }
	}

    private boolean allValidSurrounding(VoxelChunk c) {
    	//if (true) return true;
    	
        int x = (int) c.offset.x;
        int y = (int) c.offset.y;
        int z = (int) c.offset.z;
        x /= CHUNK_SIZE_X;
        y /= CHUNK_SIZE_Y;
        z /= CHUNK_SIZE_Z;
        
        if (false){
        	return 
        			getChunk(x-1, y-1, z+1, 0) == null ||
        			getChunk(x-1, y+1, z-1, 0) == null ||
        			getChunk(x+1, y-1, z-1, 0) == null ||
        			getChunk(x+1, y+1, z-1, 0) == null ||
        			getChunk(x+1, y-1, z+1, 0) == null ||
        			getChunk(x-1, y+1, z+1, 0) == null ||
        			getChunk(x, y-1, z-1, 0) == null ||
        			getChunk(x-1, y, z-1, 0) == null ||
        			getChunk(x-1, y-1, z, 0) == null || 
        			getChunk(x, y+1, z-1, 0) == null ||
        			getChunk(x+1, y, z-1, 0) == null ||
        			getChunk(x+1, y-1, z, 0) == null || 
        			getChunk(x, y-1, z+1, 0) == null ||
        			getChunk(x-1, y, z+1, 0) == null ||
        			getChunk(x-1, y+1, z, 0) == null ;
        }

        for (int ix = -1; ix < +2; ix++)
            for (int iy = -1; iy < +2; iy++)
                for (int iz =  - 1; iz < +2; iz++){
                	//Gdx.app.log(TAG, "get ch "+ x + " , "+y+" , "+z + "  result "+ ix + " , "+iy+" , "+iz);
                    VoxelChunk chunk = getChunk(ix + x, iy + y, iz + z, c.plane);                           ;
                    if (chunk == null && 
                    		(ix != 0 || iy != 0)
                    		&& ix != iy
                    		) return false;
                    //if (!chunk.isValid()) return false;
                }

        return true;
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






    public VoxelChunk getChunk(int x, int y, int z, int plane) {
        return getChunk(chunkHash(x, y, z, plane));
    }


    public boolean getDirtyfromVoxel(int x, int y, int z, int p) {
        return getChunkFromVoxel(x, y, z, p).getDirty();
    }


    public Object dirtyLock = new Object();
	

    public VoxelChunk getChunkFromVoxel(int x, int y, int z, int p) {
        int ix = MathUtils.floor(x);
        int iy = MathUtils.floor(y);
        int iz = MathUtils.floor(z);
        int cx = ix / CHUNK_SIZE_X
                , cy = iy / CHUNK_SIZE_Y
                , cz = iz / CHUNK_SIZE_Z;

        ix = (ix % CHUNK_SIZE_X + CHUNK_SIZE_X) % CHUNK_SIZE_X;
        iy = (iy % CHUNK_SIZE_Y + CHUNK_SIZE_Y) % CHUNK_SIZE_Y;
        iz = (iz % CHUNK_SIZE_Z + CHUNK_SIZE_Z) % CHUNK_SIZE_Z;

        int hash = chunkHash(cx, cy, cz, p);
        VoxelChunk chunk = getChunk(hash);
        return chunk;
    }

    public VoxelChunk getChunkFromVoxel(Vector3 p, int plane) {
        return getChunkFromVoxel(MathUtils.floor(p.x), MathUtils.floor(p.y), MathUtils.floor(p.z), plane);
    }


    public VoxelChunk createChunk() {
        chunkTotal++;
        return chunkPool.obtain();
    }



    public void removeChunk(VoxelChunk c){
        chunkTotal--;
        chunks.remove(chunkHash(c));
        chunkPool.free(c);
    }

    /*private int chunkHash(float x, float y, float z, int plane) {
        return chunkHash(MathUtils.floor( x), MathUtils.floor(y), MathUtils.floor(z), plane);

    }*/

    public boolean canCreateChunk() {
        return chunkTotal < maxChunks;
    }

    public void addChunk(VoxelChunk c) {
    	Gdx.app.log(TAG, "add Chunk" + c.mesh);
        int hash = chunkHash(c);
        if (chunks.containsKey(hash)) throw new GdxRuntimeException("hash collision"+c.offset+getChunk(hash).offset);
        chunks.put(hash, c);
    }


    public void clearAllMeshes() {
        IntMap.Values<VoxelChunk> iter = chunks.values();

        while (iter.hasNext()){
            VoxelChunk c = iter.next();
            if (c.mesh != null){
                //Pools.free(c.mesh);
                c.mesh = null;
                c.setDirty(true);
            }

        }
    }
    
    public int removeChunksBasedOnDistanceTo(Vector3 point, float maxDist){
    	int total = 0;
    	 float maxDist2 = maxDist * maxDist;
    	 while (total < 2 && maxDist2 > 8){
        	 IntMap.Values<VoxelChunk> iter = chunks.values();

    		 while (iter.hasNext()){
                 VoxelChunk c = iter.next();
                 if (c.offset.dst2(point) > maxDist2){
                	 iter.remove();
                	 chunkTotal--;
                	 Pools.free(c);
                	 total++;
                	 Gdx.app.log(TAG, "freed chunk");
                	 if (total > 8) return total;
                 }

             }
    		 maxDist2 *= 0.7f;

    	 }
    	 
             	return total;
    }
}