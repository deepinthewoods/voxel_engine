package com.badlogic.gdx.tests.g3d.voxel;




import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.Mesh;
import com.badlogic.gdx.math.Vector3;
import com.badlogic.gdx.utils.Array;
import com.badlogic.gdx.utils.ObjectMap;

public class MeshBatcher{
	public Array<Array<Mesh>> meshes;
	private int levels;
	public static int[] levelMaxSize;
	//public ObjectMap<VoxelChunk, IntervalInt> smallChunks, normalChunks;
	//StaticIntervalTreeInt<IntervalInt> smallChunksTree;
	private static final String TAG = "mesh batcher";
	public static final float[] highlightColors = {Color.RED.toFloatBits(), Color.GREEN.toFloatBits(), Color.BLUE.toFloatBits(), Color.CYAN.toFloatBits()};
	public MeshBatcher(int vertexSize, int indexSize, int levels) {
		cachedVerts = new float[vertexSize];
		cachedIndexes = new short[indexSize];
		levelMaxSize = new int[levels];
		//this.size = indexSize;
		meshes = new Array<Array<Mesh>>();
		for (int i = 0; i < levels; i++){
			levelMaxSize[i] = (int) (vertexSize / Math.pow(2, i));
			meshes.add(new Array<Mesh>());
		}
		this.levels = levels;
		//smallChunks = new ObjectMap<VoxelChunk, IntervalInt>();
		//normalChunks = new ObjectMap<VoxelChunk, IntervalInt>();
		//smallChunksTree = new StaticIntervalTreeInt<IntervalInt>();
	}
	
	public Mesh getMesh(int vertexCount, Mesher mesher){
		for (int i = levels-1; i >=0; i--){
			if (vertexCount < levelMaxSize[i]){
				Array<Mesh> ar = meshes.get(i);
				if (ar.size > 0) return ar.pop();
				//Gdx.app.log(TAG,  "creating mesh");
				return mesher.newMesh(levelMaxSize[i]);
				
				
			}
		}
		Gdx.app.log(TAG,  "no mesh"+vertexCount);
		return null;
	}
	
	public Mesh getMesh2222(int vertexCount, Mesher mesher){
		return mesher.newMesh(vertexCount);
	}
	
	float[] cachedVerts;
	int cacheProgress = 0;
	private short[] cachedIndexes;
	private int indexProgress;
	private int vertexTotal;
	/*public void addVertices(float[] vertices, int length) {
		
		for (int i = 0; i < length; i++)
			cachedVerts[cacheProgress++] = vertices[i];
	}*/
	
	

	
	public int flushCache(VoxelChunk chunk, Mesher mesher) {
		Mesh mesh = getMesh(cacheProgress, mesher);
		
		mesh.setVertices(cachedVerts, 0, cacheProgress);
		int size = cacheProgress;
		mesh.setIndices(cachedIndexes, 0, indexProgress);
		
		cacheProgress = 0;
		vertexTotal = 0;
		indexProgress = 0;
		chunk.numVerts = size/6*2;
		chunk.mesh = mesh;
		//Gdx.app.log(TAG, "mesh");
		return 0;
	}

	/** adds verts from a quad
	 * @param vertices position vectors
	 * @param colorArray 4-digit float values
	 * @param indexes
	 */
	public void addVerticesRGBA(Vector3[] vertices, float[] colorArray,
			int[] indexes) {
		for (int i = 0; i < 4; i++){
			Vector3 v = vertices[i];
			float c = Color.toFloatBits(colorArray[i*4], colorArray[i*4+1], colorArray[i*4+2], colorArray[i*4+3]);
			//Color.WHITE.toFloatBits();//
			cachedVerts[cacheProgress++] = v.x;
			cachedVerts[cacheProgress++] = v.y;
			cachedVerts[cacheProgress++] = v.z;
			cachedVerts[cacheProgress++] = c;
		}
		
		for (int i = 0; i < 6; i++){
			cachedIndexes[indexProgress++] = (short) (indexes[i]+vertexTotal);
			//Gdx.app.log(TAG, "index  "+cachedIndexes[indexProgress-1]);

		}
		//Gdx.app.log(TAG, "index length "+vertexTotal);
		vertexTotal += 4;

	}
	
	/**
	 * @param vertices positions
	 * @param colorArray 1-digit colors
	 * @param indexes
	 */
	public void addVertices(Vector3[] vertices, float[] colorArray,
			int[] indexes) {
		for (int i = 0; i < 4; i++){
			Vector3 v = vertices[i];
			float c = colorArray[i];//highlightColors[i];//
			//Gdx.app.log(TAG, "color array"+colorArray[i]);
			cachedVerts[cacheProgress++] = v.x;
			cachedVerts[cacheProgress++] = v.y;
			cachedVerts[cacheProgress++] = v.z;
			cachedVerts[cacheProgress++] = c;
		}
		
		for (int i = 0; i < 6; i++){
			cachedIndexes[indexProgress++] = (short) (indexes[i]+vertexTotal);
			//Gdx.app.log(TAG, "index  "+cachedIndexes[indexProgress-1]);

		}
		//Gdx.app.log(TAG, "index length "+vertexTotal);
		vertexTotal += 4;

	}



	public void flush(VoxelChunk chunk, float[] vertices, int count,
			short[] indices, Mesher mesher) {
		
		chunk.mesh = getMesh(count, mesher);
		chunk.mesh.setVertices(vertices, 0, count);
		chunk.mesh.setIndices(indices, 0, (count*6)/4);
		chunk.numVerts = count/4*6;
	}

	public void addVertices(Vector3[] vertices, int[] colorArray, int[] indexes,
			boolean shouldFlipTriangles) {
		
		
		for (int i = 0; i < 4; i++){
			Vector3 v = vertices[i];
			float c = GreedyMesher.lightValues[colorArray[i]];//highlightColors[i];//
			//Gdx.app.log(TAG, "color array"+colorArray[i]);
			cachedVerts[cacheProgress++] = v.x;
			cachedVerts[cacheProgress++] = v.y;
			cachedVerts[cacheProgress++] = v.z;
			cachedVerts[cacheProgress++] = c;
		}
		for (int i = 0; i < 6; i++){
			cachedIndexes[indexProgress++] = (short) (indexes[i]+vertexTotal);	
			
		}
		
		
		
		//Gdx.app.log(TAG, "index length "+vertexTotal);
		vertexTotal += 4;

	
	}

    public void addVertices(Vector3[] vertices, int[] colorArray, int[] indexes, boolean flip, GreedyMesher.VoxelFace voxel, int width, int height) {
        for (int i = 0; i < 4; i++){
            Vector3 v = vertices[i];
            float c = GreedyMesher.lightValues[colorArray[i]];//highlightColors[i];//

            Gdx.app.log(TAG, "verts"+width+"  "+height);
            cachedVerts[cacheProgress++] = v.x;
            cachedVerts[cacheProgress++] = v.y;
            cachedVerts[cacheProgress++] = v.z;
            cachedVerts[cacheProgress++] = highlightColors[i];//c;
            if (voxel.side == 2
                    || voxel.side == 3
                    ){
                switch (i){
                    case 0:
                        cachedVerts[cacheProgress++] = 0;
                        cachedVerts[cacheProgress++] = width;
                        break;
                    case 2:
                        cachedVerts[cacheProgress++] = 0;
                        cachedVerts[cacheProgress++] = 0;
                        break;
                    case 1:
                        cachedVerts[cacheProgress++] = height;
                        cachedVerts[cacheProgress++] = width;
                        break;
                    case 3:
                        cachedVerts[cacheProgress++] = height;
                        cachedVerts[cacheProgress++] = 0;
                        break;
                }
            } else {
                switch (i){
                    case 0://bl
                        cachedVerts[cacheProgress++] = 0;
                        cachedVerts[cacheProgress++] = height;
                        break;
                    case 1://tl
                        cachedVerts[cacheProgress++] = 0;
                        cachedVerts[cacheProgress++] = 0;
                        break;
                    case 2://tr
                        cachedVerts[cacheProgress++] = width;
                        cachedVerts[cacheProgress++] = height;
                        break;
                    case 3://br
                        cachedVerts[cacheProgress++] = width;
                        cachedVerts[cacheProgress++] = 0;
                        break;
                }
            }


            cachedVerts[cacheProgress++] = voxel.u;
            cachedVerts[cacheProgress++] = voxel.v;

        }

        for (int i = 0; i < 6; i++){
            cachedIndexes[indexProgress++] = (short) (indexes[i]+vertexTotal);
            //Gdx.app.log(TAG, "index  "+cachedIndexes[indexProgress-1]);

        }
        //Gdx.app.log(TAG, "index length "+vertexTotal);
        vertexTotal += 4;



    }
}
