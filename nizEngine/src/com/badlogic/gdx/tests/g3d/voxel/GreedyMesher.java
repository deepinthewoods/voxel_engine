package com.badlogic.gdx.tests.g3d.voxel;


import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.Mesh;
import com.badlogic.gdx.graphics.VertexAttribute;
import com.badlogic.gdx.math.Vector3;
import com.badlogic.gdx.utils.GdxRuntimeException;

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
/**
 * @author User
 *
 */
/**
 * @author User
 *
 */
public class GreedyMesher implements Mesher {
 
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
    private static final int CHUNK_WIDTH = 16;
    private static final int CHUNK_HEIGHT = 16, CHUNK_DEPTH = 16;
 
    /*
     * This is a 3D array of sample data – I’m using voxel faces here because I’m returning
     * the same data for each face in this example – but calls to the getVoxelFace function below
     * will return variations on voxel data per face in a real engine.  For example, in my system
     * each voxel has a type, temperature, humidity, etc – which are constant across all faces, and
     * then attributes like sunlight, artificial light which face per face or even per vertex.
     */
    private final VoxelFace [][][][] voxels = new VoxelFace [CHUNK_WIDTH][CHUNK_HEIGHT][CHUNK_WIDTH][6];
    
	private MeshBatcher meshBatch;
 
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

	private static final String TAG = "Greedy Mesher";    
 
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
	public Color tc = new Color();

	private int[][][] lightCache;

	
	/**
	 * face offsets for vertices
	 * 0 for dont move in this plane
	 * snewtb
	 */
	private int[] fox = {1,1,1,1,1,1};
	
	private int[] foy = {1,1,1,1,1,1};
	private int[] foz = {1,1,1,1,1,1};

	

	private int width;
	private int height;
	private int depth;
	private int topOffset;
	private int bottomOffset;
	private int leftOffset;
	private int rightOffset;
	private int frontOffset;
	private int backOffset;
	private int widthTimesDepth;

	private int[] visibilityMask;
	
	
	public void setConstants(int width, int height, int depth){
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
	}
	
	private static float[] lightValues;
    class VoxelFace {
    	
		public BlockDefinition def;
        public boolean transparent;
        public int type;
        public int side;
		public float u , u2, v, v2;
		float[] c = new float[4];
		private float[] vertex = {Color.toFloatBits(1f, 1f, 1f, 1f), Color.toFloatBits(1f, 1f, 1f, 1f), Color.toFloatBits(1f, 1f, 1f, 1f), Color.toFloatBits(1f, 1f, 1f, 1f)};
		
        public boolean equals(final VoxelFace face) { 
        	boolean result = face.transparent == this.transparent && 
        			face.type == this.type;
        	//if (face.type != type) throw new GdxRuntimeException("right"+result+type);//Gdx.app.log(TAG,  "fal");
        	if (result){
        		
        		for (int i = 0; i < 4; i++){
        			if (vertex[i] != face.vertex[i])
            			result = false;
        		}
        			
        		
        		
        		
        			
        	}
        	return result;
        	
        }
        

		public void set(BlockDefinition def2, int faceID, int b) {
			this.def = def2;
			this.side = faceID;
			def.setFace(this);
			//corner colors

			if (b == 0){
				transparent = true;
				
				
			}else {
				
				

				transparent = false;
			}
			type = b;
			//offsets +1 0 1
			//look in cache[]
			
			
			c[0] = tc.toFloatBits();
		}


		public void vertex(int i, int light) {
			vertex [i] = lightValues[light];
		}
    }
 
    
 
    public void readBlocks(VoxelChunk chunk){
    	
    	//light level cache, per vert
    	
    	for (int i = 0; i < CHUNK_WIDTH; i++) {
    		
    		for (int j = 0; j < CHUNK_HEIGHT; j++) {
    			
    			for (int k = 0; k < CHUNK_DEPTH; k++) {
    	
    				lightCache[i][j][k] = 0;
    				
    				
    				
    	}}}   
    	
		chunk.visibility(visibilityMask, lightCache, voxels);
    	

    	for (int i = 0; i < CHUNK_WIDTH-1; i++) {
    		
    		for (int j = 0; j < CHUNK_HEIGHT-1; j++) {
    			
    			for (int k = 0; k < CHUNK_DEPTH-1; k++) {//looping vertices
    				//for (int faceID = 0; faceID < 6; faceID++){
    				int light = lightCache[i+2][j+2][k+2]/4;
    			
    				//setLighting(light);
    				if (light != 0)Gdx.app.log(TAG, "light"+light);
    				//starts bottom left, goes clockwise
    				for (int f = 0; f < 6; f++)
    					for (int v  = 0; v < 4; v++){
        				//voxels[i][j][k][f].vertex(v,v);
        				//voxels[i][j][k][EAST].vertex(v, 5);
    				}
    				//00
    				voxels[i][j+1][k][SOUTH].vertex(2,light);
    				voxels[i][j+1][k][WEST].vertex(1,light);
    				//01
    				voxels[i][j+1][k+1][NORTH].vertex(2,light);
    				voxels[i][j+1][k+1][WEST].vertex(0,light);
    				//10
    				voxels[i+1][j+1][k][SOUTH].vertex(0,light);
    				voxels[i+1][j+1][k][EAST].vertex(1,light);
    				//11
    				voxels[i+1][j+1][k+1][NORTH].vertex(0,light);
    				voxels[i+1][j+1][k+1][EAST].vertex(0,light);
    				
    				//top row
    				voxels[i][j][k][SOUTH].vertex(3,light);
    				voxels[i][j][k][WEST].vertex(3,light);
    				//01
    				voxels[i][j][k+1][NORTH].vertex(3,light);
    				voxels[i][j][k+1][WEST].vertex(2,light);
    				//10
    				voxels[i+1][j][k][SOUTH].vertex(1,light);
    				voxels[i+1][j][k][EAST].vertex(3,light);
    				//11
    				voxels[i+1][j][k+1][NORTH].vertex(1,light);
    				voxels[i+1][j][k+1][EAST].vertex(2,light);
    				
    				
    				
    				
    	}}}//}
    }
    	
    	 	
    	
    	
    	
    
    
    
		
		
		
		
		
//		if(voxels[i+topOffset] <= 0) my += ();
//		if(voxels[i+bottomOffset] <= 0) my += ();
//		if(voxels[i+leftOffset] <= 0) mx += ();
//		if(voxels[i+rightOffset] <= 0) mx += ();
//		if(voxels[i+frontOffset] <= 0) mz += ();
//		if(voxels[i+backOffset] <= 0) mz += ();
		
    
 
    public GreedyMesher(final MeshBatcher mesh) {
    	lightValues = new float[16];
    	float r,g,b,a = 1f;;
    	for (int i = 0; i < 16; i++){
    		r = i/16f;
    		g = r;
    		b = r;
    		
    		lightValues[i] = Color.BLACK.toFloatBits();//Color.toFloatBits(r, g, b, a);
    	}
		meshBatch = mesh;
		setConstants(16,16,16);
		VoxelFace face, face2;
		for (int i = 0; i < CHUNK_WIDTH; i++) {
			
			for (int j = 0; j < CHUNK_HEIGHT; j++) {
				
				for (int k = 0; k < CHUNK_DEPTH; k++) {
					
					for (int faceID = 0; faceID < 6; faceID++){
						
						
						/*
						 * To see an example of face culling being used in combination with
						 * greedy meshing, you could set the trasparent attribute to true.
						 */
						// face.transparent = true;
						
						
						
						
						face = new VoxelFace();
						face.side = faceID;
						face2 = new VoxelFace();
						face2.side = faceID;
						voxels[i][j][k][faceID] = face;
						voxels[i][j][k][faceID] = face2;
					}
				}
			}
		}
		visibilityMask = new int[CHUNK_WIDTH*CHUNK_HEIGHT*CHUNK_DEPTH];
		lightCache = new int[CHUNK_WIDTH+2][CHUNK_HEIGHT+2][CHUNK_DEPTH+2];
	}


	/**
     *
     */
    void greedy() {
 
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
                            voxelFace  = (x[d] >= 0 )             ? getVoxelFace(x[0], x[1], x[2], side)                      : null;
                            voxelFace1 = (x[d] < CHUNK_WIDTH - 1) ? getVoxelFace(x[0] + q[0], x[1] + q[1], x[2] + q[2], side) : null;
 
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
                                    quad(new Vector3(x[0],                 x[1],                   x[2]),
                                         new Vector3(x[0] + du[0],         x[1] + du[1],           x[2] + du[2]),
                                         new Vector3(x[0] + du[0] + dv[0], x[1] + du[1] + dv[1],   x[2] + du[2] + dv[2]),
                                         new Vector3(x[0] + dv[0],         x[1] + dv[1],           x[2] + dv[2]),
                                         w,
                                         h,
                                         mask[n],
                                         backFace);
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
     * @param face
     * @return
     */
    VoxelFace getVoxelFace(final int x, final int y, final int z, final int side) {
 
        VoxelFace voxelFace = voxels[x][y][z][side];
 
        //voxelFace.side = side;
        
        return voxelFace;
    }
 
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
     * @param meshBatch 
     */
    void quad(final Vector3 bottomLeft,
              final Vector3 topLeft,
              final Vector3 topRight,
              final Vector3 bottomRight,
              final int width,
              final int height,
              final VoxelFace voxel,
              final boolean backFace) {
    	// Gdx.app.log(TAG, "type"+voxel.type+topLeft);
        final Vector3 [] vertices = new Vector3[4];
 
        vertices[2] = topLeft.scl(VOXEL_SIZE);
        vertices[3] = topRight.scl(VOXEL_SIZE);
        vertices[0] = bottomLeft.scl(VOXEL_SIZE);
        vertices[1] = bottomRight.scl(VOXEL_SIZE);
 
        final int [] indexes = backFace ? new int[] { 2,0,1, 1,3,2 } : new int[]{ 2,3,1, 1,0,2 };
 
        //final float[] colorArray = new float[4*4];
 
        for (int i = 0; i < 4; i++) {
 
            /*
             * Here I set different colors for quads depending on the “type” attribute, just
             * so that the different groups of voxels can be clearly seen.
             *
             */
            
        	
           /* if (voxel.type == 1) {
 
                colorArray[i]   = 1.0f;
                colorArray[i+1] = 0.0f;
                colorArray[i+2] = 0.0f;
                colorArray[i+3] = 1.0f;  
               // Gdx.app.log(TAG, "ret");
              //  return;
 
            } else if (voxel.type == 2) {
 
                colorArray[i]   = 0.0f;
                colorArray[i+1] = 1.0f;
                colorArray[i+2] = 0.0f;
                colorArray[i+3] = 1.0f;
 
            } else {
 
                colorArray[i]   = 0.0f;
                colorArray[i+1] = 0.0f;
                colorArray[i+2] = 1.0f;
                colorArray[i+3] = 1.0f;
            }*/
        }
 
        /*Mesh mesh = new Mesh();
 
        mesh.setBuffer(Type.Position, 3, BufferUtils.newFloatBuffer(vertices.length));
        mesh.setBuffer(Type.Color,    4, colorArray);
        mesh.setBuffer(Type.Index,    3, BufferUtils.createIntBuffer(indexes));
        mesh.updateBound();
 
        Geometry geo = new Geometry(“ColoredMesh”, mesh);
        Material mat = new Material(assetManager, “Common/MatDefs/Misc/Unshaded.j3md”);
        mat.setBoolean(“VertexColor”, true);*/
        meshBatch.addVertices(vertices, voxel.vertex, indexes);
 
        /*
         * To see the actual rendered quads rather than the wireframe, just comment outthis line.
         */
       /* mat.getAdditionalRenderState().setWireframe(true);
 
        geo.setMaterial(mat);
 
        rootNode.attachChild(geo);*/
    }

	

	@Override
	public int calculateVertices( VoxelChunk chunk,
			VoxelWorld voxelWorld, MeshBatcher batch) {
		readBlocks(chunk);
		greedy();
		return meshBatch.flushCache(chunk, this);
		
		
	}

	@Override
	public Mesh newMesh(int size) {
		Mesh mesh = new Mesh(true, 
				size, 
				size / 4 * 6,
				VertexAttribute.Position(),
				VertexAttribute.Color());
		
		
		return mesh;
	}
}