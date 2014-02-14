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

import java.awt.geom.Area;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.Camera;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.graphics.Mesh;
import com.badlogic.gdx.graphics.OrthographicCamera;
import com.badlogic.gdx.graphics.PerspectiveCamera;
import com.badlogic.gdx.graphics.Pixmap;
import com.badlogic.gdx.graphics.VertexAttribute;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.graphics.g3d.Material;
import com.badlogic.gdx.graphics.g3d.Renderable;
import com.badlogic.gdx.graphics.g3d.RenderableProvider;
import com.badlogic.gdx.graphics.g3d.attributes.ColorAttribute;
import com.badlogic.gdx.graphics.g3d.attributes.TextureAttribute;
import com.badlogic.gdx.math.Intersector;
import com.badlogic.gdx.math.Matrix4;
import com.badlogic.gdx.math.Plane;
import com.badlogic.gdx.math.Vector3;
import com.badlogic.gdx.math.collision.Ray;
import com.badlogic.gdx.utils.Array;
import com.badlogic.gdx.utils.GdxRuntimeException;
import com.badlogic.gdx.utils.IntMap;
import com.badlogic.gdx.utils.Pool;
import com.badlogic.gdx.utils.Pools;
import com.buckets.Buckets;
import com.niz.RayCaster;
import com.niz.Vec3i;

public class VoxelWorld implements RenderableProvider {
	public static final int CHUNK_SIZE_X = 16;
	public static final int CHUNK_SIZE_Y = 4;
	public static final int CHUNK_SIZE_Z = 16;
	private static final String TAG = "VoxelWorld";
	private static final int MESH_PER_FRAME = 2
			;
	public static final int VERTEX_ARRAY_SIZE = VoxelChunk.VERTEX_SIZE * 6 * CHUNK_SIZE_X * CHUNK_SIZE_Y * CHUNK_SIZE_Z;
	public final VoxelChunk[] chunks;
	//public final Mesh[] meshes;
	public final Material material;
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
	private TextureRegion[][] tiles;
	private Pool<BlockVector> blockVectorPool;
	private static final int AREA_MESH_COUNT = 0;
	//private OrthographicCamera camera;
	public Buckets buckets;
	
	
	public VoxelWorld(int chunksX, int chunksY, int chunksZ) {
		//this.camera = camera2;
		//this.meshCountXZ = meshCountXZ;
		//this.meshCountY = meshCountY;
		buckets = new Buckets();
		blockVectorPool = Pools.get(BlockVector.class);
		
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
		int len = CHUNK_SIZE_X * CHUNK_SIZE_Y * CHUNK_SIZE_Z * 6;
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
		//this.meshes = new Mesh[meshCountXZ*meshCountXZ*meshCountY*10];
		for(i = 0; i < chunksX*chunksY*chunksZ + AREA_MESH_COUNT; i++) {
			Mesh mesh = new Mesh(true, 
										CHUNK_SIZE_X * CHUNK_SIZE_Y * CHUNK_SIZE_Z * 2 * 4, 
										CHUNK_SIZE_X * CHUNK_SIZE_Y * CHUNK_SIZE_Z * 6,
										VertexAttribute.Position()//, VertexAttribute.Normal()
										, VertexAttribute.Color(), VertexAttribute.TexCoords(0)
			);
			mesh.setIndices(indices);
			spareMeshes.add(mesh);
		}
		bottomMesh = spareMeshes.pop();
		this.dirty = new boolean[chunksX * chunksY * chunksZ];
		for(i = 0; i < dirty.length; i++) dirty[i] = true;
		
		
		
		 
		
		this.numVertices = new int[chunksX * chunksY * chunksZ];
		for(i = 0; i < numVertices.length; i++) numVertices[i] = 0;
		
		this.vertices = new float[VERTEX_ARRAY_SIZE];
		
		this.material = new Material(
				new ColorAttribute(ColorAttribute.Diffuse, Color.WHITE)
				);
		
		
	}
	
	
	public void init(TextureRegion[][] tiles, BlockDefinition[] blockDefs,
			Pixmap fades) {
		this.tiles = tiles;
		VoxelChunk.blockDefinitions = blockDefs;
		VoxelChunk.tiles = tiles;
		Color c = new Color();
		for (int i = 0; i < 16; i++){
			c.set(fades.getPixel(i, 2));
			VoxelChunk.lightColors[i] = c.toFloatBits();
			c.set(fades.getPixel(i, 1));
			VoxelChunk.torchColors[i] = c.toFloatBits();
		}
		material.set(
				new TextureAttribute(TextureAttribute.Diffuse, tiles[0][0].getTexture())
				//TextureAttribute.createSpecular(tiles[0][0].getTexture())
				//, TextureAttribute.createDiffuse(tiles[0][0].getTexture())
				);
		
		
	}


	public void set(float x, float y, float z, byte voxel) {
		int ix = (int)x;
		int iy = (int)y;
		int iz = (int)z;
		int chunkX = ix / CHUNK_SIZE_X;
		if(chunkX < 0 || chunkX >= chunksX) return;
		int chunkY = iy / CHUNK_SIZE_Y;
		if(chunkY < 0 || chunkY >= chunksY) return;
		int chunkZ = iz / CHUNK_SIZE_Z;
		if(chunkZ < 0 || chunkZ >= chunksZ) return;
		chunks[chunkX + chunkZ * chunksX + chunkY * chunksX * chunksZ].set(ix % CHUNK_SIZE_X, iy % CHUNK_SIZE_Y, iz % CHUNK_SIZE_Z, voxel);
	}
	
	public void place(float x, float y, float z, byte voxel){
		int ix = (int)x;
		int iy = (int)y;
		int iz = (int)z;
		int chunkX = ix / CHUNK_SIZE_X;
		if(chunkX < 0 || chunkX >= chunksX) return;
		int chunkY = iy / CHUNK_SIZE_Y;
		if(chunkY < 0 || chunkY >= chunksY) return;
		int chunkZ = iz / CHUNK_SIZE_Z;
		if(chunkZ < 0 || chunkZ >= chunksZ) return;
		int index = chunkX + chunkZ * chunksX + chunkY * chunksX * chunksZ;
		chunks[index].set(ix % CHUNK_SIZE_X, iy % CHUNK_SIZE_Y, iz % CHUNK_SIZE_Z, voxel);
		dirty[index] = true;
	}
	
	public byte get(float x, float y, float z) {
		int ix = (int)x;
		int iy = (int)y;
		int iz = (int)z;
		int chunkY = iy / CHUNK_SIZE_Y;
		if(chunkY < 0 )return 0;
		if (chunkY >= chunksY)return 0;//VoxelChunk.setLight(0,  15);
		
		int chunkX = ix / CHUNK_SIZE_X;
		if(chunkX < 0 || chunkX >= chunksX) return 0;
		
					
		
		int chunkZ = iz / CHUNK_SIZE_Z;
		if(chunkZ < 0 || chunkZ >= chunksZ) return 0;
		return chunks[chunkX + chunkZ * chunksX + chunkY * chunksX * chunksZ].get(ix % CHUNK_SIZE_X, iy % CHUNK_SIZE_Y, iz % CHUNK_SIZE_Z);
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
		
		for (int cy = iy-1; cy < voxelsY; cy++){
			set(ix, cy, iz, (byte) 0);//VoxelChunk.setLight(0, 15));
		}
		//addLightUpdate(ix, iy, iz);
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
	
	private Array<VoxelChunk> meshedChunks = new Array<VoxelChunk>();
	@Override
	public void getRenderables (Array<Renderable> renderables, Pool<Renderable> pool) {
		//if (FPSMode) getRenderablesForFPS(renderables, pool);
	//	else 
			particleIndex = 3;
			//getRenderablesForTopDown(renderables, pool);//TODO should use worldCentrePoint
			int x0 = (int) viewCentrePoint.x, x1 = x0, y0 = (int) viewCentrePoint.y, y1 = y0,  z0 = (int) viewCentrePoint.z, z1 = z0;
			x0 -= viewSizeX;
			x1 += viewSizeX+1;
			y0 -= viewSizeY;
			y1 += viewSizeY+1;
			z0 -= viewSizeZ;
			z1 += viewSizeZ+1;
			getRenderables(x0, x1, y0, y1, z0, z1, renderables, pool);
	}
	Vector3 cornerPt0Near = new Vector3(), cornerPt1Near = new Vector3(), cornerPt0Far = new Vector3()
	, cornerPt1Far = new Vector3(), cornerTmp = new Vector3();
	private void getRenderablesForTopDown(Array<Renderable> renderables,
			Pool<Renderable> pool, Camera camera) {
		//Gdx.app.log(TAG, "corner11 "+ "  dir "+camera.direction);

		//camera.direction.set(0,-1,0);
		//camera.update();
		//Gdx.app.log(TAG, "corner12 "+ "  dir "+camera.direction);

		//Gdx.app.log(TAG, "camera"+camera.direction);
		//Gdx.app.log(TAG,  "camera dir"+camera.direction + "     "+camera.viewportHeight);
		renderedChunks = 0;
		int x0, x1, z0, z1;
		cornerPt0Far.set(0,0,1);
		cornerPt1Far.set(Gdx.graphics.getWidth(), Gdx.graphics.getHeight(), 1);
		cornerPt0Near.set(0,0,0);
		cornerPt1Near.set(Gdx.graphics.getWidth(), Gdx.graphics.getHeight(), 0);
		camera.unproject(cornerPt0Far);
		camera.unproject(cornerPt1Far);
		camera.unproject(cornerPt0Near);
		camera.unproject(cornerPt1Near);
		
		//Gdx.app.log(TAG, "unproj  "+cornerPt1Near +"  "+camera.viewportHeight+ "  "+cornerPt1Far);
		viewRayOrigin.set(cornerPt0Near);
		viewRayDirection.set(cornerPt0Far.sub(cornerPt0Near)).nor();
		viewPlanePoint.set(0, 1, 0);
		viewRay.set(viewRayOrigin, viewRayDirection);
		viewPlane.set(viewPlanePoint, viewPlaneNormal);
		Intersector.intersectRayPlane(viewRay, viewPlane, cornerTmp);
		x0 = (int) cornerTmp.x;
		z0 = (int) cornerTmp.z;
		//Gdx.app.log(TAG, "corner1 "+x0+","+z0+"  ,  "+cornerTmp + "  dir "+camera.direction);
		
		viewRayOrigin.set(cornerPt1Near);
		viewRayDirection.set(cornerPt1Far.sub(cornerPt1Near)).nor();
		viewPlanePoint.set(0, 1, 0);
		viewRay.set(viewRayOrigin, viewRayDirection);
		viewPlane.set(viewPlanePoint, viewPlaneNormal);
		Intersector.intersectRayPlane(viewRay, viewPlane, cornerTmp);
		x1 = (int) cornerTmp.x;
		z1 = (int) cornerTmp.z;

		
		x0 /= CHUNK_SIZE_X;
		x1 /= CHUNK_SIZE_X;
		z0 /= CHUNK_SIZE_Z;
		z1 /= CHUNK_SIZE_Z;
		x1++;
		z1++;
		//z1++;
		//x0++;
		//z1--;
		if (x0 > x1){
			int tmp = x0;
			x0 = x1;
			x1 = tmp;
		}
		if (z0 > z1){
			int tmp = z0;
			z0 = z1;
			z1 = tmp;
		}
		x0 = Math.max(x0,  0);
		z0 = Math.max(0, z0);
		x1 = Math.max(x1,  0);
		z1 = Math.max(0, z1);
		x0 = Math.min(x0,  chunksX+3);
		z0 = Math.min(chunksZ+3, z0);
		x1 = Math.min(x1,  chunksX+3);
		z1 = Math.min(chunksZ+3, z1);
		getRenderables(x0, x1, 0, 0, z0, z1, renderables, pool);
	}
	
	public void getRenderables(int x0, int x1, int y0, int y1, int z0, int z1, 
			Array<Renderable> renderables,
			Pool<Renderable> pool){
		int meshedCount = 0;

		for (int x = x0; x <= x1; x++)
			for (int y = y0; y <= y1; y++)
				for (int z = z0; z <= z1; z++){
					//Gdx.app.log(TAG, "chunk  x"+x+",  z"+z);
					VoxelChunk chunk = getChunk(x, y, z);
					if (chunk == null) continue;
					Mesh mesh = chunk.mesh;
					int i = chunk.index;
					if (mesh == null){
						if (spareMeshes.size == 0) continue;
						chunk.mesh = spareMeshes.pop();
						meshedChunks.add(chunk);
						mesh = chunk.mesh;
						dirty[chunk.index]= true; 
					}
					if(dirty[i]) {
						//if (meshedCount > MESH_PER_FRAME) continue;
						//Gdx.app.log(TAG, "dirty");
						int numVerts = chunk.calculateVertices(vertices, this);
						numVertices[i] = numVerts / 4 * 6;
						mesh.setVertices(vertices, 0, numVerts * VoxelChunk.VERTEX_SIZE);
						meshedCount++;
						dirty[i] = false;
						
					}
					if(numVertices[i] == 0) continue;
					Renderable renderable = pool.obtain();
					renderable.material = material;
					renderable.mesh = mesh;
					renderable.meshPartOffset = 0;
					renderable.meshPartSize = numVertices[i];
					renderable.primitiveType = GL20.GL_TRIANGLES;
					renderable.worldTransform.set(zeroMatrix);
					
					renderables.add(renderable);
					renderedChunks++;
				}
	}

	/*public void getRenderablesForFPS(Array<Renderable> renderables, Pool<Renderable> pool){
		renderedChunks = 0;
		int meshedCount = 0;// meshedAlready = false;
		int cx = (int) (viewCentrePoint.x+CHUNK_SIZE_X/2)/CHUNK_SIZE_X
				, cy = (int) (viewCentrePoint.y+CHUNK_SIZE_Y/2)/CHUNK_SIZE_Y 
				, cz = (int) (viewCentrePoint.z+CHUNK_SIZE_Z/2)/CHUNK_SIZE_Z
				;
		int cx0 = Math.max(0,  cx-meshCountXZ)
		, cy0 = Math.max(0,  cy-meshCountY)
		, cz0 = Math.max(0,  cz-meshCountXZ)
		, cx1 = Math.min(chunksX-1, cx+meshCountXZ) 
		, cy1 = Math.min(chunksY-1, cy+meshCountY) 
		, cz1 = Math.min(chunksZ-1, cz+meshCountXZ) 
		;
		for (int x = cx0; x < cx1; x++)
			for (int z = cz0; z < cz1; z++)
				for (int y = cy0; y < cy1; y++){

		//for(int i = 0; i < chunks.length; i++) {
			int i = getChunkIndex(x,y,z);
			VoxelChunk chunk = chunks[i];
			Mesh mesh = chunk.mesh;
			
			if (
					//chunk.offset.y > viewYLevelMax*CHUNK_SIZE_Y
					//|| 
					//chunk.offset.y < viewYLevelMin*CHUNK_SIZE_Y
					false
					) continue;
			if (mesh == null){
				//float distX = Math.abs(viewCentrePoint.x - chunk.offset.x)/CHUNK_SIZE_X
				//		, distY = Math.abs(viewCentrePoint.y - chunk.offset.y)/CHUNK_SIZE_Y
				//		, distZ = Math.abs(viewCentrePoint.z - chunk.offset.z)/CHUNK_SIZE_Z;
				//if (distX > meshCountXZ-1 || distY > meshCountY-1 || distZ > meshCountXZ-1)continue;
				chunk.mesh = spareMeshes.pop();
				meshedChunks.add(chunk);
				//Gdx.app.log(TAG, "pop"+viewCentrePoint);
				mesh = chunk.mesh;
				dirty[i] = true;
			}
			if(dirty[i]) {
				if (meshedCount > MESH_PER_FRAME) continue;
				//Gdx.app.log(TAG, "dirty");
				int numVerts = chunk.calculateVertices(vertices, this);
				numVertices[i] = numVerts / 4 * 6;
				mesh.setVertices(vertices, 0, numVerts * VoxelChunk.VERTEX_SIZE);
				meshedCount++;
				dirty[i] = false;
			}
			if(numVertices[i] == 0) continue;
			Renderable renderable = pool.obtain();
			renderable.material = material;
			renderable.mesh = mesh;
			renderable.meshPartOffset = 0;
			renderable.meshPartSize = numVertices[i];
			renderable.primitiveType = GL20.GL_TRIANGLES;
			renderable.worldTransform.set(zeroMatrix);
			
			renderables.add(renderable);
			renderedChunks++;
		}/**/
		
		
		//renderables.add(bottom);
	//}
	
	
	Mesh bottomMesh;
	private int bottomVertSize, bottomLastY;
	private float[] bottomVerts = new float[1000];
	private void setBottomMesh(Mesh m, float[] uvs, int y) {
		if (y == bottomLastY) return;
		bottomLastY = y;
		float size = 100f;
		int ind = 0;
		float u = uvs[0], v = uvs[1], u2 = uvs[2], v2 = uvs[3];
		
		bottomVerts[ind++] = -size;
		bottomVerts[ind++] = y-.1f;
		bottomVerts[ind++] = -size;
		bottomVerts[ind++] = 0;
		bottomVerts[ind++] = 1;
		bottomVerts[ind++] = 0;/**/
		bottomVerts[ind++] = Color.WHITE.toFloatBits();;
		bottomVerts[ind++] = u;
		bottomVerts[ind++] = v;
		
		
		bottomVerts[ind++] = size;
		bottomVerts[ind++] = y-.1f;
		bottomVerts[ind++] = -size;
		bottomVerts[ind++] = 0;
		bottomVerts[ind++] = 1;
		bottomVerts[ind++] = 0;/**/
		bottomVerts[ind++] = Color.WHITE.toFloatBits();;
		bottomVerts[ind++] = u2;
		bottomVerts[ind++] = v;
		
		bottomVerts[ind++] = size;
		bottomVerts[ind++] = y-.1f;
		bottomVerts[ind++] = size;
		bottomVerts[ind++] = 0;
		bottomVerts[ind++] = 1;
		bottomVerts[ind++] = 0;/**/
		bottomVerts[ind++] = Color.WHITE.toFloatBits();;
		bottomVerts[ind++] = u2;
		bottomVerts[ind++] = v2;
		
		bottomVerts[ind++] = -size;
		bottomVerts[ind++] = y-.1f;
		bottomVerts[ind++] = size;
		bottomVerts[ind++] = 0;
		bottomVerts[ind++] = 1;
		bottomVerts[ind++] = 0;/**/
		bottomVerts[ind++] = Color.WHITE.toFloatBits();;
		bottomVerts[ind++] = u;
		bottomVerts[ind++] = v2;
		
		ind = ind / 4 * 6;
		m.setVertices(bottomVerts, 0, ind+VoxelChunk.VERTEX_SIZE);
		bottomVertSize = ind;
	}
	Matrix4 zeroMatrix = new Matrix4().idt();
	
	public VoxelChunk getChunk(int ix, int iy, int iz) {
		//int chunkY = iy / CHUNK_SIZE_Y;
		if(iy < 0 )return null;
		if (iy >= chunksY)return null;
		
		//int chunkX = ix / CHUNK_SIZE_X;
		if(ix < 0 || ix >= chunksX) return null;
		
					
		
		//int chunkZ = iz / CHUNK_SIZE_Z;
		if(iz < 0 || iz >= chunksZ) return null;
		return chunks[ix + iz * chunksX + iy * chunksX * chunksZ];
	}
	
	private int getChunkIndex(int x, int y, int z) {
		//if (x < 0 || x >= chunksX) return 0;
		int index = x + z * chunksX + y * chunksX * chunksZ;
		
		return index;	
	}

	//int meshCountXZ, meshCountY;
	Array<Mesh> spareMeshes = new Array<Mesh>();
	/*public void arrangeMeshes(){
		Vector3 point = viewCentrePoint;
		//Gdx.app.log(TAG, "arrange mesh "+viewCentrePoint);
		int pointX = (int) point.x + CHUNK_SIZE_X/2
				, pointY = (int) point.y
				, pointZ = (int) point.z + CHUNK_SIZE_Z/2;
		pointX /= CHUNK_SIZE_X;
		pointY /= CHUNK_SIZE_Y;
		pointZ /= CHUNK_SIZE_Z;
		//Gdx.app.log(TAG, "mesh start rearr");

		for (int i = meshedChunks.size-1; i >= 0; i--){
					VoxelChunk chunk = meshedChunks.get(i);
					if (chunk.mesh != null){
						float distX = Math.abs(viewCentrePoint.x - chunk.offset.x)/CHUNK_SIZE_X
								, distY = Math.abs(viewCentrePoint.y - chunk.offset.y)/CHUNK_SIZE_Y
								, distZ = Math.abs(viewCentrePoint.z - chunk.offset.z)/CHUNK_SIZE_Z;
						if (distX > meshCountXZ || distY > meshCountY || distZ > meshCountXZ){
							
							spareMeshes.add(chunk.mesh);
							chunk.mesh = null;
							meshedChunks.removeIndex(i);
							//deepinthewoodsGdx.app.log(TAG, "mesh removed");
							//Gdx.app.log(TAG, "mesh dist"+distX+","+distY+","+distZ + "   @  "+point);

						}
						
					}
				}
		
			
		
	}*/
	
	/*private Array<BlockVector> lightUpdates = new Array<BlockVector>(true, 16);
	public int lightUpdatesTotal = 0;
	public void updateLight(int total){
		//total = 5;
		//Entries e =	lightUpdates.;
		int count = 0;
		while (lightUpdates.size > 0 && count < total){
			BlockVector key = lightUpdates.pop();//e.next().value;
			//e.remove();
			//int x = key&BLOCK_POSITION_MASK, y = (key>>10)&BLOCK_POSITION_MASK, z = (key>>20)&BLOCK_POSITION_MASK;
			int[] surr = getSurrounds(key.x,key.y,key.z);
			updateBlockLight(key.x,key.y,key.z, surr);
			lightUpdatesTotal++;
			count++;
			//if (lightUpdatesTotal > 10000000) 
				//Gdx.app.log(TAG, "updatingx"+key);
			blockVectorPool.free(key);
		}
		
	}*/
	
	int[] tmpSurrounds = new int[6];
	public int[] getSurrounds(float x, float y, float z) {
		tmpSurrounds[BlockDefinition.TOP] = get(x,y+1,z);
		tmpSurrounds[BlockDefinition.BOTTOM] = get(x,y-1,z);
		tmpSurrounds[BlockDefinition.LEFT] = get(x-1,y,z);
		tmpSurrounds[BlockDefinition.RIGHT] = get(x+1,y,z);
		tmpSurrounds[BlockDefinition.FRONT] = get(x,y,z+1);
		tmpSurrounds[BlockDefinition.BACK] = get(x,y,z-1);
		return tmpSurrounds;
	}
	
	/*public void setLight(int x, int y, int z, int light){
		int ix = x, iy = y, iz = z;
		//Gdx.app.log("vw", "set light to "+light);
		int chunkX = ix / CHUNK_SIZE_X;
		if(chunkX < 0 || chunkX >= chunksX) throw new GdxRuntimeException("set light: block out of range");//return;
		int chunkY = iy / CHUNK_SIZE_Y;
		if(chunkY < 0 || chunkY >= chunksY) throw new GdxRuntimeException("set light: block out of range");//return;
		int chunkZ = iz / CHUNK_SIZE_Z;
		if(chunkZ < 0 || chunkZ >= chunksZ) throw new GdxRuntimeException("set light: block out of range");//return;
		int chunkIndex = chunkX + chunkZ * chunksX + chunkY * chunksX * chunksZ;
		ix %= CHUNK_SIZE_X;
		iy %= CHUNK_SIZE_Y;
		iz %= CHUNK_SIZE_Z;
		int b = chunks[chunkIndex].get(ix, iy, iz);
		if (VoxelChunk.light(b) == light) return;
		chunks[chunkIndex].set(ix, iy, iz, VoxelChunk.setLight(b, light));
		queueSurroundingLightUpdates(x, y, z);
		dirty[chunkIndex] = true;
		//Gdx.app.log("vw", "setl mnodified");
		//}
		//chunks[chunkX + chunkZ * chunksX + chunkY * chunksX * chunksZ].set(byte) (b & light);
	}*/

	



	/*private void queueSurroundingLightUpdates(int x, int y, int z) {
		addLightUpdate(x+1,y,z);
		addLightUpdate(x-1,y,z);
		addLightUpdate(x,y,z+1);
		addLightUpdate(x,y,z-1);
		addLightUpdate(x,y-1,z);
		addLightUpdate(x,y+1,z);
	}
	public void addLightUpdate(int x, int y, int z){
		//int key = x + ((y&BLOCK_POSITION_MASK)<<10) + ((z&BLOCK_POSITION_MASK)<<20);
		if (x < 0 || y < 0 || z < 0 || x >= voxelsX || y >= voxelsY || z >= voxelsZ)return;
		BlockVector key = blockVectorPool.obtain();
		key.set(x,y,z);
		//if (lightUpdatesTotal > 10000000) 
			//Gdx.app.log(TAG, "add light update"+key + "  chunk x"+x+" Y"+y+"  z"+z);
		lightUpdates.add(key);
	}*/


	/*private void updateBlockLight(float x, float y, float z, int[] surr) {
		
		
		//if (y == voxelsY-1) return;
		if (x == 10 && z == 10) ;
			
		int ix = (int)x;
		int iy = (int)y;
		int iz = (int)z;
		int chunkX = ix / CHUNK_SIZE_X;
		if(chunkX < 0 || chunkX >= chunksX) return;
		int chunkY = iy / CHUNK_SIZE_Y;
		if(chunkY < 0 || chunkY >= chunksY) return;
		int chunkZ = iz / CHUNK_SIZE_Z;
		if(chunkZ < 0 || chunkZ >= chunksZ) return;
		int chunkIndex = chunkX + chunkZ * chunksX + chunkY * chunksX * chunksZ;
		int block =  (VoxelChunk.id(chunks[chunkIndex].get(ix % CHUNK_SIZE_X, iy % CHUNK_SIZE_Y, iz % CHUNK_SIZE_Z)));
		BlockDefinition def = VoxelChunk.blockDefinitions[block];
		//Gdx.app.log(TAG, "updateblocklight"+block+ " "+VoxelChunk.light(block));
		if (VoxelChunk.light(surr[BlockDefinition.TOP]) == 15 && block == 0){
			setLight(ix,iy,iz, 15-def.dayLightLoss);
			return;
		}
		int light = 0;
		
		for (int i = 0; i < 6; i++)
			light = Math.max(light, VoxelChunk.light(surr[i]));
		//light /= 6;
		light -= def.lightLoss;
		if (light < 0) light = 0;
		//if (light != 0)
		//if (x == 10 && z == 10)
		//Gdx.app.log(TAG, "newLight"+light+" y"+y);
		setLight(ix,iy,iz, light);
	}
*/


	public byte get(Vector3 position) {
		return get(position.x, position.y, position.z);
	}


	private RayCaster ray = new RayCaster();
	private Vector3 rayTmp = new Vector3();
	private Vec3i rayResult = new Vec3i();
	public boolean raycastForJump(Vector3 position, float rot) {
		rayTmp.set(1,0,0).rotate(rot, 0, 1, 0);
		ray.traceWithDirection(position.x, position.y+.5f, position.z, rayTmp);
		//Gdx.app.log(TAG, "raycast styrat "+position + "  dir "+rayTmp);
		for (int i = 0; i < 2; i++){
			ray.next();
			rayResult.set(ray.x, ray.y, ray.z);
			//Vec3i pos = ray.get();
			//Gdx.app.log(TAG, "raycast"+rayResult);
			//int face = ray.face();
			//pos.add(BlockDefinition.normals[face+6]);
			byte block = get(rayResult);
			BlockDefinition def = VoxelChunk.blockDef(block);
			if (def.isSolid)return true; 
		}
		return false;
	}
	private Vector3 viewPlaneNormal = new Vector3(0,1,0), viewPlanePoint = new Vector3()
	, viewRayOrigin = new Vector3(),  viewRayDirection = new Vector3();
	public Vector3 viewCentrePoint = new Vector3();
	private int viewSizeX = 2, viewSizeY = 2, viewSizeZ = 0;
	private Plane viewPlane = new Plane(viewPlaneNormal, viewPlanePoint);
	private Ray viewRay = new Ray(viewRayOrigin, viewRayDirection);
	//private Intersector intersector = new Intersector();
	//public int viewYLevelMax, viewYLevelMin, topDownYLevel;
	
	public void setViewSize(int x, int y, int z){
		viewSizeX = x;
		viewSizeY = y;
		viewSizeZ = z;
	}
	
	private byte get(Vec3i pos) {
		return get(pos.x, pos.y, pos.z);
	}

	public void calculateViewCentreForTopDownCamera(OrthographicCamera camera2){
		viewRayOrigin.set(camera2.position);
		viewRayDirection.set(camera2.direction);
		viewPlanePoint.set(0, 2, 0);
		viewRay.set(viewRayOrigin, viewRayDirection);
		viewPlane.set(viewPlanePoint, viewPlaneNormal);
		Intersector.intersectRayPlane(viewRay, viewPlane, viewCentrePoint);
		
		//viewYLevelMax = topDownYLevel;
		//viewYLevelMin = Math.max(0, topDownYLevel-1);
		//setBottomMesh(bottomMesh, VoxelChunk.blockDef(1).getUVs(0), viewYLevelMin) ;
		//Gdx.app.log(TAG, "mesh top down"+viewYLevelMin);
	}
	
	public void getGroundPointBetween(Vector3 tmp, Vector3 tmp2,
			Vector3 res) {
		viewRayOrigin.set(tmp);
		viewRayDirection.set(tmp2).sub(tmp);
		viewRay.set(viewRayOrigin, viewRayDirection);
		Intersector.intersectRayPlane(viewRay, viewPlane, res);
		//Gdx.app.log(TAG, "ground point"+res);
	}

	int areaMeshesCompleted;
	IntMap<Array<Area>> areas = new IntMap<Array<Area>>();
	public void moveTopDownLevelUp(){
		//topDownYLevel ++;
		areaMeshesCompleted = 0;
	}
	
	public void moveTopDownLevelDown(){
		//topDownYLevel --;
		areaMeshesCompleted = 0;
	}
	
	public void calculateViewCentreForFirstPersonCamera(PerspectiveCamera cam){
		viewCentrePoint.set(cam.position);
		//viewYLevelMax = 10000;
		//viewYLevelMin = -10000;
	}


	private boolean raycastForSimplePath(Vector3 position, Vector3 target) {
		return this.raycastForSimplePath(position.x, position.y, position.z, target.x, target.y, target.z);
	}
	int particleIndex = 3;
	private boolean raycastForSimplePath(float px, float py, float pz, float targetX, float targetY, float targetZ) {
		return this.raycastForSimplePath(px, py, pz, targetX, targetY, targetZ, false);
	}
	private boolean raycastForSimplePath(float px, float py, float pz, float targetX, float targetY, float targetZ, boolean path) {
		ray.trace(px, py, pz, targetX, targetY, targetZ);
		//MyGdxGame.particles.clear();
		//MyGdxGame.particles.flipColor();
		
		//if (path)MyGdxGame.arrowParticles.addArrow(px, py, pz, targetX, targetY, targetZ, .2f);
		while (ray.hasNext){
			
			//MyGdxGame.particles.addBlock(ray.x+.5f, ray.y+.5f, ray.z+.5f);
			
			rayResult.set(ray.x, ray.y, ray.z);
			byte block = get(rayResult);
			BlockDefinition def = VoxelChunk.blockDef(block);
			//MyGdxGame.particles.setBlock(particleIndex++, ray.x+.5f, ray.y+.5f, ray.z+.5f);
			
			if (def.isSolid){
				
				//MyGdxGame.particles.setSize(particleIndex);
				//if (path)MyGdxGame.arrowParticles.addArrow(px, py, pz, targetX, targetY, targetZ, .2f);
				
				//if (ray.hasNext)
				//Gdx.app.log(TAG, "raycast solid");
				return false; 
			}
			ray.next();
		}
		//if (path)MyGdxGame.arrowParticles.addArrow(px, py, pz, targetX, targetY, targetZ, .2f);
		return true;
	}


	public boolean raycastForClearPath(Vector3 position, Vector3 target) {
		return raycastForClearPath(position.x, position.y, position.z, target.x, target.y, target.z);
	}

	public boolean raycastForClearPath(float x, float y, float z, float x2,
			float y2, float z2){
		return raycastForClearPath(x, y, z, x2, y2, z2, false);
	}
	
	public boolean raycastForClearPath(float x, float y, float z, float x2,
			float y2, float z2, boolean draw) {
		float scale = .25f;
		//MyGdxGame.arrowParticles.addArrow(x, y, z, x2, y2, z2, scale);
		float dx = Math.abs( x - x2 )
				, dy = Math.abs( y-y2 )
				, dz = Math.abs( z - z2 ), tmp;
		
		if (dx > dz){
			dz /= dx; 
			dx = 1;
		} else {
			dx /= dz; 
			dz = 1;
		}
		
		tmp = dx;
		dx = dz;
		dz = tmp;
		
		dx *= scale;
		dz *= scale;
		if (dx > 1.1f || dz > 1.1f)
			throw new GdxRuntimeException("");
		boolean b = raycastForSimplePath(x+dx,y,z-dz, x2+dx, y2, z2-dz)
				&& raycastForSimplePath(x-dz,y,z+dz, x2-dx, y2, z2+dz);
		if (draw) b = raycastForSimplePath(x+dx,y,z-dz, x2+dx, y2, z2-dz, true)
				&& raycastForSimplePath(x-dz,y,z+dz, x2-dx, y2, z2+dz, true);
		return b;
		
	}


	public void processRandomUpdates() {
		// TODO block updates
		
	}


	
	


	

}