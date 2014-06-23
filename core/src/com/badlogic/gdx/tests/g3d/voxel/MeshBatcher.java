package com.badlogic.gdx.tests.g3d.voxel;


import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.Mesh;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.math.Vector3;
import com.badlogic.gdx.utils.Array;
import com.badlogic.gdx.utils.GdxRuntimeException;

public class MeshBatcher implements MeshBatch{
    private static final int VERTEX_SIZE = 8;
    public Array<Array<Mesh>> meshes;
	private int levels;
	public static int[] levelMaxSize;
	//public ObjectMap<VoxelChunk, IntervalInt> smallChunks, normalChunks;
	//StaticIntervalTreeInt<IntervalInt> smallChunksTree;
	private static final String TAG = "mesh batcher";
	public static final float[] highlightColors = {Color.RED.toFloatBits(), Color.GREEN.toFloatBits(), Color.BLUE.toFloatBits(), Color.CYAN.toFloatBits()};
    public static float whiteTextureU, whiteTextureV;

    public MeshBatcher(int vertexSize, int indexSize, int levels) {

		cachedVerts = new float[vertexSize];
		cachedIndexes = new short[indexSize];
		levelMaxSize = new int[levels];
		//this.size = indexSize;
		meshes = new Array<Array<Mesh>>();
		for (int i = 0; i < levels; i++){
			levelMaxSize[i] = (int) (vertexSize / Math.pow(2, i));
            Gdx.app.log(TAG,  "creating mesh slot "+levelMaxSize[i]);
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
				if (ar.size > 0){
                    Gdx.app.log(TAG,  "recycling mesh"+i);

                    return ar.pop();
                }
				Gdx.app.log(TAG,  "creating mesh"+i);
				return mesher.newMesh(levelMaxSize[i]);
				
				
			}
		}
		Gdx.app.log(TAG,  "no mesh"+vertexCount);
		return null;
	}

    public void freeMesh(VoxelChunk chunk){
        if (chunk.mesh != null){
            int vertexCount = chunk.mesh.getMaxVertices();
            for (int i = levels-1; i >=0; i--){
                if (vertexCount <= levelMaxSize[i]){
                    Array<Mesh> ar = meshes.get(i);
                    ar.add(chunk.mesh);
                    chunk.mesh = null;
                    Gdx.app.log(TAG,  "freeing mesh"+i);
                    return;


                }
            }
            throw new GdxRuntimeException("couldn't free mesh"+vertexCount);
        }

    }

	public Mesh getMesh2222(int vertexCount, Mesher mesher){
		return mesher.newMesh(vertexCount);
	}
	
	float[] cachedVerts;
	int cacheProgress = 0;
	protected short[] cachedIndexes;
	protected int indexProgress;
	protected int vertexTotal;
	/*public void addVertices(float[] vertices, int length) {
		
		for (int i = 0; i < length; i++)
			cachedVerts[cacheProgress++] = vertices[i];
	}*/
	
	

	@Override
	public int flushCache(VoxelChunk chunk, GreedyMesher mesher) {
        freeMesh(chunk);
        if (cacheProgress/VERTEX_SIZE == 0){
            return 0;
        }
		Mesh mesh = getMesh(cacheProgress/VERTEX_SIZE, mesher);

		
		mesh.setVertices(cachedVerts, 0, cacheProgress);
		int size = cacheProgress;
		mesh.setIndices(cachedIndexes, 0, indexProgress);
		
		cacheProgress = 0;
		vertexTotal = 0;
		indexProgress = 0;
		chunk.numVerts = size/VERTEX_SIZE;
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



	public void flushSimpleMesh(VoxelChunk chunk, float[] vertices, int count,
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

    public void addVertices(Vector3[] vertices, int[] colorArray, short[] indexes, boolean flip, GreedyMesher.VoxelFace voxel, int width, int height) {
        //if (true)throw new GdxRuntimeException("wrong");
        for (int i = 0; i < 4; i++){
            Vector3 v = vertices[i];
            if (colorArray[i] > 15) throw new GdxRuntimeException("light error "+colorArray[i]);
            float c = GreedyMesher.lightValues[colorArray[i]];//highlightColors[i];//

            //Gdx.app.log(TAG, "verts"+width+"  "+height);
            cachedVerts[cacheProgress++] = v.x;
            cachedVerts[cacheProgress++] = v.y;
            cachedVerts[cacheProgress++] = v.z;
            cachedVerts[cacheProgress++] = c;
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

    /*
     * In this test each voxel has a size of one world unit � in reality a voxel engine
     * might have larger voxels � and there�s a multiplication of the vertex coordinates
     * below to account for this.
     */
    protected static final int VOXEL_SIZE = 1;

    /**
     * This function renders a single quad in the scene. This quad may represent many adjacent voxel
     * faces � so in order to create the illusion of many faces, you might consider using a tiling
     * function in your voxel shader. For this reason I�ve included the quad width and height as parameters.
     *
     * For example, if your texture coordinates for a single voxel face were 0 � 1 on a given axis, they should now
     * be 0 � width or 0 � height. Then you can calculate the correct texture coordinate in your fragement
     * shader using coord.xy = fract(coord.xy).
     *
     *
     * @param bottomLeft
     * @param topLeft
     * @param topRight
     * @param bottomRight
     * @param width
     * @param height
     * @param voxel
     * @param backFace
     */
    @Override
    public void quad(final Vector3 bottomLeft,
                     final Vector3 topLeft,
                     final Vector3 topRight,
                     final Vector3 bottomRight,
                     final int width,
                     final int height,
                     final GreedyMesher.VoxelFace voxel,
                     final boolean backFace) {
        // Gdx.app.log(TAG, "type"+voxel.type+topLeft);
        final Vector3 [] vertices = new Vector3[4];

        vertices[2] = topLeft.scl(VOXEL_SIZE);
        vertices[3] = topRight.scl(VOXEL_SIZE);
        vertices[0] = bottomLeft.scl(VOXEL_SIZE);
        vertices[1] = bottomRight.scl(VOXEL_SIZE);
        //013 320  310 023
        boolean flip = voxel.shouldFlipTriangles();

        final short [] indexes = backFace ?flip?backIndices:fwdIndices

                : flip?flipBackIndices:flipFwdIndices;


        addVertices(vertices, voxel.vertex, indexes, flip, voxel, width, height);



    }
    short[] backIndices = new short[]{0,1,3, 3,2,0}, fwdIndices =  new short[] { 2,0,1, 1,3,2 }, flipBackIndices = new short[]{3,1,0, 0,2,3}, flipFwdIndices = new short[]{ 2,3,1, 1,0,2 };


}
