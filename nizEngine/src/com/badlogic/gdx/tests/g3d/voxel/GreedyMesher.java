package com.badlogic.gdx.tests.g3d.voxel;

import voxel.BlockDefinition;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.Mesh;
import com.badlogic.gdx.math.Vector3;



 
/**
 * This is a Java greedy meshing implementation based on the javascript implementation
 * written by Mikola Lysenko and described in this blog post:
 *
 * <a href="http://0fps.wordpress.com/2012/06/30/meshing-in-a-minecraft-game/" rel="nofollow">http://0fps.wordpress.com/2012/06/30/meshing-in-a-minecraft-game/</a>
 *
 * The principal changes are:
 *
 *  – Porting to Java
 *  – Modification in order to compare *voxel faces*, rather than voxels themselves
 *  – Modification to provide for comparison based on multiple attributes simultaneously
 *
 * This class is ready to be used on the JMonkey platform – but the algorithm should be
 * usable in any case.
 *
 * @author Rob O’Leary
 */
public class GreedyMesher implements Mesher{
	UberMesh uberMesh;
	public GreedyMesher(UberMesh mesh) {
		int facesTotal = CHUNK_WIDTH*CHUNK_HEIGHT*CHUNK_DEPTH;
		tmpFace = new VoxelFace[facesTotal];
		tmpFace2 = new VoxelFace[facesTotal];
		for (int i = 0; i < facesTotal; i++){
			tmpFace[i] = new VoxelFace();
			tmpFace2[i] = new VoxelFace();
		}
		lighting = new int[CHUNK_WIDTH+4][CHUNK_HEIGHT+4][CHUNK_DEPTH+4];
    	ao = new int[CHUNK_WIDTH+4][CHUNK_HEIGHT+4][CHUNK_DEPTH+4];
    	ids = new byte[CHUNK_WIDTH+4][CHUNK_HEIGHT+4][CHUNK_DEPTH+4];

    	returnV = new Vector3[4];
    	for (int i = 0; i < 4; i++)
    		returnV[i] = new Vector3();
    	this.uberMesh = mesh;
	}
    /*
     * In this test each voxel has a size of one world unit – in reality a voxel engine
     * might have larger voxels – and there’s a multiplication of the vertex coordinates
     * below to account for this.
     */
    private static final int VOXEL_SIZE = 1;
 
    /*
     * These are the chunk dimensions – it may not be the case in every voxel engine that
     * the data is rendered in chunks – but this demo assumes so.  Anyway the chunk size is
     * just used to populate the sample data array.  Also, in reality the chunk size will likely
     * be larger – for example, in my voxel engine chunks are 16x16x16 – but the small size
     * here allows for a simple demostration.
     */
    public static final int CHUNK_WIDTH = VoxelWorld.CHUNK_SIZE_X;
    private static final int CHUNK_HEIGHT = VoxelWorld.CHUNK_SIZE_Y;
    private static final int CHUNK_DEPTH = VoxelWorld.CHUNK_SIZE_Z;    
 
    /*
     * This is a 3D array of sample data – I’m using voxel faces here because I’m returning
     * the same data for each face in this example – but calls to the getVoxelFace function below
     * will return variations on voxel data per face in a real engine.  For example, in my system
     * each voxel has a type, temperature, humidity, etc – which are constant across all faces, and
     * then attributes like sunlight, artificial light which face per face or even per vertex.
     */
    private final BlockDefinition [][][] voxels = new BlockDefinition [CHUNK_WIDTH][CHUNK_HEIGHT][CHUNK_WIDTH];

	private int[][][] ao;

	private int[][][] lighting;

	private VoxelFace[] tmpFace, tmpFace2 ;

	private Vector3[] returnV;

	private int tmpFaceIndex, tmpFaceIndex2;

	private byte[][][] ids;
 
    /*
     * These are just constants to keep track of which face we’re dealing with – their actual
     * values are unimportantly – only that they’re constant.
     */
    private static final int SOUTH      = 0;
    private static final int NORTH      = 1;
    private static final int EAST       = 2;
    private static final int WEST       = 3;
    private static final int TOP        = 4;
    private static final int BOTTOM     = 5;

	private static final String TAG = "greedy mesher";

 
    /**
     * This class is used to encapsulate all information about a single voxel face.  Any number of attributes can be
     * included – and the equals function will be called in order to compare faces.  This is important because it
     * allows different faces of the same voxel to be merged based on varying attributes.
     *
     * Each face can contain vertex data – for example, int[] sunlight, in order to compare vertex attributes.
     *
     * Since it’s optimal to combine greedy meshing with face culling, I have included a “transparent” attribute here
     * and the mesher skips transparent voxel faces.  The getVoxelData function below – or whatever it’s equivalent
     * might be when this algorithm is used in a real engine – could set the transparent attribute on faces based
     * on whether they should be visible or not.
     */
    class VoxelFace {
 
        public boolean transparent;
        public int type;
        public int side;
		public float c0 = Color.WHITE.toFloatBits(), c1 = c0, c2 = c0, c3 = c0;
		public float u0, u1, u2, u3;
		public float v0, v1, v2, v3;
        
        public boolean equals(final VoxelFace face) { return face.transparent == this.transparent && face.type == this.type; }

		public void setCornerColors(int i, int j, int k, int l) {
			c0 = i;
			c1 = j;
			c2 = k;
			c3 = l;
			
		}

		public void set(BlockDefinition def, byte id) {
			type = id;
		}
    }
 
    public int calculateVertices(VoxelChunk chunk, UberMesh uberMesh, Mesh mesh, VoxelWorld voxelWorld) {
		int i = 0;
		int vertexOffset = 0;
		tmpFaceIndex = 0;
		tmpFaceIndex2 = 0;
		init(chunk);
		greedy(chunk, uberMesh);
		return uberMesh.flushCache(voxelWorld.getMesh(chunk.index));
		
		//return vertexOffset;
    }
    /**
	 * This function returns an instance of VoxelFace containing the attributes for
	 * one side of a voxel.  In this simple demo we just return a value from the
	 * sample data array.  However, in an actual voxel engine, this function would
	 * check if the voxel face should be culled, and set per-face and per-vertex
	 * values as well as voxel values in the returned instance.
	 *
	 * @param x
	 * @param y
	 * @param z
	 * @param voxelFace 
	 * @param face
	 * @return
	 */
	VoxelFace getVoxelFace(final VoxelChunk chunk, final int x, final int y, final int z, final int side, VoxelFace voxelFace) {
		byte voxel = chunk.get(x,y,z);
		
		
		
	    BlockDefinition def = voxels[x][y][z];
	   // voxelFace = getFace(def, side);
	    //Gdx.app.log(TAG,  "id"+CHUNK_DEPTH);
	    voxelFace.set(def, ids[x][y][z]);
	    
	    //Vector3 bottomLeft,
		// Vector3 topLeft,
        //Vector3 topRight,
        //Vector3 bottomRight,
	    switch (side){
	    case NORTH:
	    	
	       /* voxelFace.setCornerColors(
	        		lighting[x][y][z]
	        		, lighting[x][y][z]
	        		, lighting[x][y][z]
	        		, lighting[x][y][z]
	        		);
	       voxelFace.setCornerAO();*/
	
	    	break;
	    case SOUTH:
	    	
	    	break;
	    case EAST:
	    	
	    	break;
	    case WEST:
	    	
	    	break;
	    case TOP:
	    	
	    	break;
	    case BOTTOM:
	    	
	    	break;
	    }
	
	
	    return voxelFace;
	}
	/**
     * This is an initialization function used here to set up the sample voxel data
     * and launch the greedy meshing.
     */
    
 
    private void init(VoxelChunk chunk) {
    	for (int x = 0; x < CHUNK_WIDTH; x++)
    		for (int y = 0; y < CHUNK_HEIGHT; y++)
    			for (int z = 0; z < CHUNK_DEPTH; z++){
    				
    			}
    	for (int x = 0; x < CHUNK_WIDTH+4; x++){
    		for (int y = 0; y < CHUNK_HEIGHT+4; y++){
    			for (int z = 0; z < CHUNK_DEPTH+4; z++){
    				
    	    		//ao map;per vertex array of totals
    	    		//0 for air, 1 for solid
    	    		ao[x][y][z] = 0;
    	    		
    	    		//lighting map, per vertex
    	    		//brightest of neighboring blocks
    	    		lighting[x][y][z] = 0;
    	    		
    	    	}
        	}
    	}
    	for (int x = 0; x < CHUNK_WIDTH; x++)
    		for (int y = 0; y < CHUNK_HEIGHT; y++)
    			for (int z = 0; z < CHUNK_DEPTH; z++){
    				byte b = chunk.get(x,y,z);
    				BlockDefinition def = VoxelChunk.blockDef(b);
    	    		voxels[x][y][z] = def;
    	    		if (b != 0)Gdx.app.log(TAG, "id"+x+","+y+","+z+"   "+b);
    	    		ids[x][y][z] = b;
    	    		//ao map;per vertex array of totals
    	    		//0 for air, 1 for solid
    	    		int aoValue = def.aoValue;
    	    		for (int dx = -1; dx <= 1; dx++)
    	    			for (int dz = -1; dz <= 1; dz++)
    	    				for (int dy = 0; dy <= 0; dy++){
    	    				
    	    	    		 
    	    	    		int side1 = 1;
    	    	    		int side2 = 2;
    	    	    		if ((side1 & side2) > 0){
    	    	    			ao[x+dz+2][y+dy+2][z+dz+2] = 0;
    	    	    		}
    	    	    		int corner = 0;
    	    	    		ao[x+dz+2][y+dy+2][z+dz+2] = 3-(side1+side2+corner);
    	    	    		
    	    			}
    	    		
    	    		
    	    		//lighting map, per vertex
    	    		//brightest of neighboring blocks
    	    		for (int dx = 0; dx <= 1; dx++)
    	    			for (int dz = 0; dz <= 1; dz++)
    	    				for (int dy = 0; dy <= 1; dy++){
    	    					int lightValue = def.lightValue;
    	        	    		lighting[x+dz+2][y+dy+2][z+dz+2] = Math.max(lighting[x+dz+2][y+dy+2][z+dz+2], lightValue);
    	        			
    	    				}
    	    		
    		}
    	
	}


	/**
     *
     */
    void greedy(VoxelChunk chunk, UberMesh mesh) {
 
        /*
         * These are just working variables for the algorithm – almost all taken
         * directly from Mikola Lysenko’s javascript implementation.
         */
        int i, j, k, l, w, h, u, v, n, side = 0;
 
        final int[] x = new int []{0,0,0};
        final int[] q = new int []{0,0,0};
        final int[] du = new int[]{0,0,0};
        final int[] dv = new int[]{0,0,0};         
 
        /*
         * We create a mask – this will contain the groups of matching voxel faces
         * as we proceed through the chunk in 6 directions – once for each face.
         */
        final VoxelFace[] mask = new VoxelFace [CHUNK_WIDTH * CHUNK_HEIGHT];
 
        /*
         * These are just working variables to hold two faces during comparison.
         */
        VoxelFace voxelFace, voxelFace1;
 
        /**
         * We start with the lesser-spotted boolean for-loop (also known as the old flippy floppy).
         *
         * The variable backFace will be TRUE on the first iteration and FALSE on the second – this allows
         * us to track which direction the indices should run during creation of the quad.
         *
         * This loop runs twice, and the inner loop 3 times – totally 6 iterations – one for each
         * voxel face.
         */
        for (boolean backFace = true, b = false; b != backFace; backFace = backFace && b, b = !b) { 
 
            /*
             * We sweep over the 3 dimensions – most of what follows is well described by Mikola Lysenko
             * in his post – and is ported from his Javascript implementation.  Where this implementation
             * diverges, I’ve added commentary.
             */
            for(int d = 0; d < 3; d++) {
 
                u = (d + 1) % 3;
                v = (d + 2) % 3;
 
                x[0] = 0;
                x[1] = 0;
                x[2] = 0;
 
                q[0] = 0;
                q[1] = 0;
                q[2] = 0;
                q[d] = 1;
 
                /*
                 * Here we’re keeping track of the side that we’re meshing.
                 */
                if (d == 0)      { side = backFace ? WEST   : EAST;  }
                else if (d == 1) { side = backFace ? BOTTOM : TOP;   }
                else if (d == 2) { side = backFace ? SOUTH  : NORTH; }                
 
                /*
                 * We move through the dimension from front to back
                 */
                for(x[d] = -1; x[d] < CHUNK_WIDTH;) {
 
                    /*
                     * ——————————————————————-
                     *   We compute the mask
                     * ——————————————————————-
                     */
                    n = 0;
 
                    for(x[v] = 0; x[v] < CHUNK_HEIGHT; x[v]++) {
 
                        for(x[u] = 0; x[u] < CHUNK_WIDTH; x[u]++) {
 
                            /*
                             * Here we retrieve two voxel faces for comparison.
                             */
                        	
                            voxelFace  = (x[d] >= 0 )             ? getVoxelFace(chunk, x[0], x[1], x[2], side, tmpFace[tmpFaceIndex++] )                      : null;
                            voxelFace1 = (x[d] < CHUNK_WIDTH -1 ) ? getVoxelFace(chunk, x[0] + q[0], x[1] + q[1], x[2] + q[2], side, tmpFace2[tmpFaceIndex2++]) : null;
 
                            
                            /*
                             * Note that we’re using the equals function in the voxel face class here, which lets the faces
                             * be compared based on any number of attributes.
                             *
                             * Also, we choose the face to add to the mask depending on whether we’re moving through on a backface or not.
                             */
                            mask[n++] = ((voxelFace != null && voxelFace1 != null && voxelFace.equals(voxelFace1)))
                                        ? null
                                        : backFace ? voxelFace1 : voxelFace;
                        }
                    }
 
                    x[d]++;
 
                    /*
                     * Now we generate the mesh for the mask
                     */
                    n = 0;
 
                    for(j = 0; j < CHUNK_HEIGHT; j++) {
 
                        for(i = 0; i < CHUNK_WIDTH;) {
 
                            if(mask[n] != null) {
 
                                /*
                                 * We compute the width
                                 */
                                for(w = 1; i + w < CHUNK_WIDTH && mask[n + w] != null && mask[n + w].equals(mask[n]); w++) {}
 
                                /*
                                 * Then we compute height
                                 */
                                boolean done = false;
 
                                for(h = 1; j + h < CHUNK_HEIGHT; h++) {
 
                                    for(k = 0; k < w; k++) {
 
                                        if(mask[n + k + h * CHUNK_WIDTH] == null || !mask[n + k + h * CHUNK_WIDTH].equals(mask[n])) { done = true; break; }
                                    }
 
                                    if(done) { break; }
                                }
 
                                /*
                                 * Here we check the “transparent” attribute in the VoxelFace class to ensure that we don’t mesh
                                 * any culled faces.
                                 */
                                if (!mask[n].transparent) {
                                    /*
                                     * Add quad
                                     */
                                    x[u] = i;
                                    x[v] = j;
 
                                    du[0] = 0;
                                    du[1] = 0;
                                    du[2] = 0;
                                    du[u] = w;
 
                                    dv[0] = 0;
                                    dv[1] = 0;
                                    dv[2] = 0;
                                    dv[v] = h;
 
                                    /*
                                     * And here we call the quad function in order to render a merged quad in the scene.
                                     *
                                     * We pass mask[n] to the function, which is an instance of the VoxelFace class containing
                                     * all the attributes of the face – which allows for variables to be passed to shaders – for
                                     * example lighting values used to create ambient occlusion.
                                     */
                                  
                                    returnV[0].set(x[0],                 x[1],                   x[2]);
                                    returnV[1].set(x[0] + du[0],         x[1] + du[1],           x[2] + du[2]);
                                    returnV[2].set(x[0] + du[0] + dv[0], x[1] + du[1] + dv[1],   x[2] + du[2] + dv[2]);
                                    returnV[3].set(x[0] + dv[0],         x[1] + dv[1],           x[2] + dv[2]);
                                    quad(returnV[0], returnV[1], returnV[2], returnV[3],
                                    		
                                         w,
                                         h,
                                         mask[n],
                                         backFace
                                         , mesh);
                                }
 
                                /*
                                 * We zero out the mask
                                 */
                                for(l = 0; l < h; ++l) {
 
                                    for(k = 0; k < w; ++k) { mask[n + k + l * CHUNK_WIDTH] = null; }
                                }
 
                                /*
                                 * And then finally increment the counters and continue
                                 */
                                i += w;
                                n += w;
 
                            } else {
 
                              i++;
                              n++;
                            }
                        }
                    }
                }
            }
        }
    }
 
    private int CHUNK_WIDTH(int i) {
		return 0;
	}
	final float[] vertices = new float
    		[VoxelChunk.VERTEX_SIZE * 6 * VoxelWorld.CHUNK_SIZE_X * VoxelWorld.CHUNK_SIZE_Y * VoxelWorld.CHUNK_SIZE_Z*2];
    /**
     * This function renders a single quad in the scene. This quad may represent many adjacent voxel
     * faces – so in order to create the illusion of many faces, you might consider using a tiling
     * function in your voxel shader. For this reason I’ve included the quad width and height as parameters.
     *
     * For example, if your texture coordinates for a single voxel face were 0 – 1 on a given axis, they should now
     * be 0 – width or 0 – height. Then you can calculate the correct texture coordinate in your fragement
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
     * @param mesh 
     */
    void quad(Vector3 bottomLeft,
              Vector3 topLeft,
              Vector3 topRight,
              Vector3 bottomRight,
              final int width,
              final int height,
              final VoxelFace voxel,
              final boolean backFace, UberMesh mesh) {
    	Gdx.app.log("greedy", "quad"+bottomLeft+"  +   "+topRight);
    	
    	int vertexOffset;
    	Vector3 offset;
    	
    	
    	vertexOffset = 0;
    	offset = bottomLeft;
    	float c = Color.WHITE.toFloatBits();
    	vertices[vertexOffset++] = offset.x;
		vertices[vertexOffset++] = offset.y;
		vertices[vertexOffset++] = offset.z;
		vertices[vertexOffset++] = 0;
		vertices[vertexOffset++] = 1;
		vertices[vertexOffset++] = 0;
		vertices[vertexOffset++] = voxel.c0;//lightColors[lightAverages[yp]+TOP_FACE_BRIGHTNESS_BONUS];//(light(c[BlockDefinition.TOP])+light(c[BlockDefinition.LEFT])+light(c[BlockDefinition.BACK]))/3];//lightColors[(light(c[BlockDefinition.TOP]))];//
		vertices[vertexOffset++] = voxel.u0;
		vertices[vertexOffset++] = voxel.v0;
		mesh.addVertices(vertices, vertexOffset);
		
		vertexOffset = 0;
    	offset = bottomRight;
    	c = Color.WHITE.toFloatBits();
    	vertices[vertexOffset++] = offset.x;
		vertices[vertexOffset++] = offset.y;
		vertices[vertexOffset++] = offset.z;
		vertices[vertexOffset++] = 0;
		vertices[vertexOffset++] = 1;
		vertices[vertexOffset++] = 0;
		vertices[vertexOffset++] = voxel.c1;//lightColors[lightAverages[yp]+TOP_FACE_BRIGHTNESS_BONUS];//(light(c[BlockDefinition.TOP])+light(c[BlockDefinition.LEFT])+light(c[BlockDefinition.BACK]))/3];//lightColors[(light(c[BlockDefinition.TOP]))];//
		vertices[vertexOffset++] = voxel.u1;
		vertices[vertexOffset++] = voxel.v1;
		mesh.addVertices(vertices, vertexOffset);
		
		vertexOffset = 0;
    	offset = topLeft;
    	c = Color.WHITE.toFloatBits();
    	vertices[vertexOffset++] = offset.x;
		vertices[vertexOffset++] = offset.y;
		vertices[vertexOffset++] = offset.z;
		vertices[vertexOffset++] = 0;
		vertices[vertexOffset++] = 1;
		vertices[vertexOffset++] = 0;
		vertices[vertexOffset++] = voxel.c2;//lightColors[lightAverages[yp]+TOP_FACE_BRIGHTNESS_BONUS];//(light(c[BlockDefinition.TOP])+light(c[BlockDefinition.LEFT])+light(c[BlockDefinition.BACK]))/3];//lightColors[(light(c[BlockDefinition.TOP]))];//
		vertices[vertexOffset++] = voxel.u2;
		vertices[vertexOffset++] = voxel.v2;
		mesh.addVertices(vertices, vertexOffset);
		
		vertexOffset = 0;
    	offset = topRight;
    	c = Color.WHITE.toFloatBits();
    	vertices[vertexOffset++] = offset.x;
		vertices[vertexOffset++] = offset.y;
		vertices[vertexOffset++] = offset.z;
		vertices[vertexOffset++] = 0;
		vertices[vertexOffset++] = 1;
		vertices[vertexOffset++] = 0;
		vertices[vertexOffset++] = voxel.c3;//lightColors[lightAverages[yp]+TOP_FACE_BRIGHTNESS_BONUS];//(light(c[BlockDefinition.TOP])+light(c[BlockDefinition.LEFT])+light(c[BlockDefinition.BACK]))/3];//lightColors[(light(c[BlockDefinition.TOP]))];//
		vertices[vertexOffset++] = voxel.u3;
		vertices[vertexOffset++] = voxel.v3;
		mesh.addVertices(vertices, vertexOffset);
		
    	
       // vertices[2] = topLeft.scl(VOXEL_SIZE);
       // vertices[3] = topRight.scl(VOXEL_SIZE);
       // vertices[0] = bottomLeft.scl(VOXEL_SIZE);
       // vertices[1] = bottomRight.scl(VOXEL_SIZE);
        
 
 
        
 
        /*
         * To see the actual rendered quads rather than the wireframe, just comment outthis line.
         *
        mat.getAdditionalRenderState().setWireframe(true);
 
        geo.setMaterial(mat);
 
        rootNode.attachChild(geo);
        */
    }
	@Override
	public int calculateVertices(float[] vertices, VoxelChunk chunk, Mesh mesh,
			VoxelWorld voxelWorld) {
		return calculateVertices(chunk, uberMesh, mesh, voxelWorld);

	}
}

