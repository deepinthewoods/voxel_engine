package com.badlogic.gdx.tests.g3d.voxel;


import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.Mesh;
import com.badlogic.gdx.graphics.VertexAttribute;
import com.badlogic.gdx.graphics.VertexAttributes;
import com.badlogic.gdx.math.Vector3;

/**
 * This is a Java greedy meshing implementation based on the javascript implementation
 * written by Mikola Lysenko and described in this blog post:
 *
 * <a href="http://0fps.wordpress.com/2012/06/30/meshing-in-a-minecraft-game/" rel="nofollow">http://0fps.wordpress.com/2012/06/30/meshing-in-a-minecraft-game/</a>
 *
 * The principal changes are:
 *
 *  � Porting to Java
 *  � Modification in order to compare *voxel faces*, rather than voxels themselves
 *  � Modification to provide for comparison based on multiple attributes simultaneously
 *
 * This class is ready to be used on the JMonkey platform � but the algorithm should be
 * usable in any case.
 *
 * @author Rob O�Leary
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


    private static final int FACE_SUBDIVISIONS = 8;

    /*
     * These are the chunk dimensions � it may not be the case in every voxel engine that
     * the data is rendered in chunks � but this demo assumes so.  Anyway the chunk size is
     * just used to populate the sample data array.  Also, in reality the chunk size will likely
     * be larger � for example, in my voxel engine chunks are 16x16x16 � but the small size
     * here allows for a simple demostration.
     */
    private static int CHUNK_WIDTH = 16;
    private static int CHUNK_HEIGHT = 16;

    /*
     * This is a 3D array of sample data � I�m using voxel faces here because I�m returning
     * the same data for each face in this example � but calls to the getVoxelFace function below
     * will return variations on voxel data per face in a real engine.  For example, in my system
     * each voxel has a type, temperature, humidity, etc � which are constant across all faces, and
     * then attributes like sunlight, artificial light which face per face or even per vertex.
     */
    private final VoxelFace [][][][] voxels = new VoxelFace [CHUNK_WIDTH+2][CHUNK_HEIGHT+2][CHUNK_WIDTH+2][6];
    protected static Color[] lightColors;

    public MeshBatch meshBatch;

    /*
     * These are just constants to keep track of which face we�re dealing with � their actual
     * values are unimportantly � only that they�re constant.
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
     * included � and the equals function will be called in order to compare faces.  This is important because it
     * allows different faces of the same voxel to be merged based on varying attributes.
     *
     * Each face can contain vertex data � for example, int[] sunlight, in order to compare vertex attributes.
     *
     * Since it�s optimal to combine greedy meshing with face culling, I have included a �transparent� attribute here
     * and the mesher skips transparent voxel faces.  The getVoxelData function below � or whatever it�s equivalent
     * might be when this algorithm is used in a real engine � could set the transparent attribute on faces based
     * on whether they should be visible or not.
     */
	//public Color tc = new Color();





    public IVoxelPreprocessor preprocessor = null;

	private int[][][] lightCache;


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

	static float[] lightValues;

    public class VoxelFace {

		public BlockDefinition def;
        public boolean transparent;
        public int type;
        public int side;
		//public float u, v;
		float[] c = new float[4];
		protected int[] vertex = new int[4];

        public boolean equals(final VoxelFace face) {
            //if (face.transparent && this.transparent) return true;
        	boolean result = face.transparent == this.transparent &&
        			face.type == this.type;
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

			//corner colors

			if (b == 0){
				transparent = true;
                //Gdx.app.log(TAG, "transp");

			}else {



				transparent = false;
			}
			type = b;
			//offsets +1 0 1
			//look in cache[]


			//c[0] = tc.toFloatBits();
		}


		public final void vertex(int i, int light) {

            vertex [i] = light;

		}

        public void vertex(int index, int side1, int side2, int corner, int x){
            //side1 = side1>>(x+1)& 1;
            //side2 = side2>>(x+1)& 1;
            //corner = corner>>(x+1)& 1;
            if((side1 & side2) == 1) {
                vertex(index, 0);
            }
            vertex(index, (3 - (side1 + side2 + corner))*5);
        }


		public boolean shouldFlipTriangles() {
			if (vertex[0]+vertex[3] > vertex[1] + vertex[2]) return true;
			return false;
		}


        public void vertex(int index, BlockDefinition a, BlockDefinition b, BlockDefinition c, BlockDefinition x) {
        	vertex(index
        			, a.lightValue > 1?0:1
        					, b.lightValue > 1?0:1
        							, c.lightValue > 1?0:1
        									, x.lightValue > 1?0:1
        			);
            vertex[index] = (a.lightValue + b.lightValue + c.lightValue + x.lightValue)/4;
        }

        public float u() {
            if (def.faceU != null){
                return def.faceU[side];
            };
            return 0f;
        }
        public float v() {
            if (def.faceV != null){
                return def.faceV[side];
            };
            return 0f;
        }
    }



    public boolean readBlocks(VoxelChunk chunk, VoxelWorld voxelWorld, int progress){

    	int progressVisibilityMax = VoxelChunk.VISIBILITY_SUBDIVISIONS+3;
    	if (progress < progressVisibilityMax) {
            chunk.visibility(visibilityMask, lightCache, voxels, voxelWorld, progress);
            return false;
        }
        int prog = progress - progressVisibilityMax;
        int my = CHUNK_WIDTH+2;
        int ox = (int) chunk.offset.x;
        int oy = (int) chunk.offset.y;
        int oz = (int) chunk.offset.z;
        if (prog < FACE_SUBDIVISIONS){
            int progSize = CHUNK_HEIGHT / FACE_SUBDIVISIONS;
            //Gdx.app.log(TAG, "prog"+prog+"  /  "+FACE_SUBDIVISIONS + "  progrsize "+(prog * progSize));

            int n = (prog+1) * progSize;
            if (prog == FACE_SUBDIVISIONS-1)n--;

            for(int y = prog * progSize; y < n; y++) {
                for(int z = 0; z < CHUNK_WIDTH-1; z++) {
                    int mask = visibilityMask[(y+1)*my+(z+1)];
                    int maskNorth = visibilityMask[(y+1)*my+(z+2)];
                    int maskSouth = visibilityMask[(y+1)*my+(z)];
                    int maskTop = visibilityMask[(y+2)*my+(z+1)];
                    int maskBottom = visibilityMask[(y)*my+(z+1)];
                    int maskEast = mask >> 1;
                    int maskWest = mask << 1;





                    maskNorth &= mask;
                    maskSouth &= mask;
                    maskTop &= mask;
                    maskBottom &= mask;
                    maskEast &= mask;
                    maskWest &= mask;

                    for(int x = 0; x < CHUNK_WIDTH-1; x++) {

                        if ((maskTop>>(x+1)& 1 )> 0 ){
                            voxels[x][y][z][TOP].transparent = true;
                        } else{
                            //voxels[x][y][z][TOP].transparent = false;
                        }

                        if ((maskBottom>>(x+1)& 1 )> 0 ){
                            voxels[x][y][z][BOTTOM].transparent = true;
                        } else{
                            //voxels[x][y][z][BOTTOM].transparent = false;
                        }

                        if ((maskNorth>>(x+1)& 1 )> 0 ){
                            voxels[x][y][z][NORTH].transparent = true;
                        } else{
                            //voxels[x][y][z][NORTH].transparent = false;
                        }

                        if ((maskSouth>>(x+1)& 1) > 0 ){
                            voxels[x][y][z][SOUTH].transparent = true;
                        } else{
                            //voxels[x][y][z][SOUTH].transparent = false;
                        }

                        if ((maskEast>>(x+1)& 1) > 0 ){
                            voxels[x][y][z][EAST].transparent = true;
                        } else{
                            //voxels[x][y][z][EAST].transparent = false;
                        }

                        if ((maskWest>>(x+1)& 1) > 0 ){
                            voxels[x][y][z][WEST].transparent = true;
                        } else{
                            //voxels[x][y][z][WEST].transparent = false;
                        }


                        BlockDefinition ao00 = chunk.getDef(x, y, z);
                        BlockDefinition ao01 = chunk.getDef(x, y, z + 1);
                        BlockDefinition ao10 = chunk.getDef(x + 1, y, z);
                        BlockDefinition ao11 = chunk.getDef(x + 1, y, z + 1);

                        BlockDefinition ao00T = chunk.getDef(x, y + 1, z);
                        BlockDefinition ao01T = chunk.getDef(x, y + 1, z + 1);
                        BlockDefinition ao10T = chunk.getDef(x + 1, y + 1, z);
                        BlockDefinition ao11T = chunk.getDef(x + 1, y + 1, z + 1);//*/
                        
                        

                        /*int ao11 = visibilityMask[(y+1)*my+(z+2)] >>1;
                        int ao01 = visibilityMask[(y+1)*my+(z+2)];
                        int ao10 = visibilityMask[(y+1)*my+(z+1)]>>1;
                        int ao00 = visibilityMask[(y+1)*my+(z+1)];

                        int ao11T = visibilityMask[(y+2)*my+(z+2)] >>1;
                        int ao01T = visibilityMask[(y+2)*my+(z+2)];
                        int ao10T = visibilityMask[(y+2)*my+(z+1)]>>1;
                        int ao00T = visibilityMask[(y+2)*my+(z+1)];*/
                        

                        voxels[x][y][z][TOP].vertex(3,ao01T, ao10T, ao11T, ao00T);
                        voxels[x][y][z][EAST].vertex(3,ao10T, ao11, ao11T, ao10);
                        voxels[x][y][z][NORTH].vertex(3,ao01T, ao11, ao11T, ao01);

                        voxels[x+1][y][z][TOP].vertex(2,ao00T, ao11T, ao01T, ao10T);
                        voxels[x+1][y][z][WEST].vertex(3,ao00T, ao01, ao01T, ao00);
                        voxels[x+1][y][z][NORTH].vertex(1,ao11T, ao01, ao01T, ao11);


                        voxels[x][y][z+1][TOP].vertex(1,ao00T, ao11T, ao10T, ao01T);
                        voxels[x][y][z+1][EAST].vertex(2,ao11T, ao10, ao10T, ao11);
                        voxels[x][y][z+1][SOUTH].vertex(3,ao00T, ao10, ao10T, ao00);

                        voxels[x+1][y][z+1][TOP].vertex(0,ao01T, ao10T, ao00T, ao11T);
                        voxels[x+1][y][z+1][WEST].vertex(2,ao01T, ao00, ao00T, ao01);
                        voxels[x+1][y][z+1][SOUTH].vertex(1,ao10T, ao00, ao00T, ao10);

                        voxels[x][y+1][z][BOTTOM].vertex(3,ao01, ao10, ao11, ao01);
                        voxels[x][y+1][z][EAST].vertex(1,ao10, ao11T, ao11, ao10T);
                        voxels[x][y+1][z][NORTH].vertex(2,ao01, ao11T, ao11, ao01T);

                        voxels[x+1][y+1][z][BOTTOM].vertex(2,ao00, ao11, ao01, ao10);
                        voxels[x+1][y+1][z][WEST].vertex(1,ao00, ao01T, ao01, ao00T);
                        voxels[x+1][y+1][z][NORTH].vertex(0,ao01T, ao11, ao01, ao11T);

                        voxels[x][y+1][z+1][BOTTOM].vertex(1,ao00, ao11, ao10, ao01);
                        voxels[x][y+1][z+1][EAST].vertex(0,ao11, ao10T, ao10, ao11T);
                        voxels[x][y+1][z+1][SOUTH].vertex(2,ao00, ao10T, ao10, ao00T);

                        voxels[x+1][y+1][z+1][BOTTOM].vertex(0,ao01, ao10, ao00, ao11);
                        voxels[x+1][y+1][z+1][WEST].vertex(0,ao01, ao00T, ao00, ao01T);
                        voxels[x+1][y+1][z+1][SOUTH].vertex(0,ao10, ao00T, ao00, ao10T);

                    }}}//}
        } else if (prog < FACE_SUBDIVISIONS+1){
            //tops with visibility
            for(int y = CHUNK_HEIGHT-1; y < CHUNK_HEIGHT; y++) {
                for(int z = 0; z < CHUNK_WIDTH; z++) {
                    int mask = visibilityMask[(y+1)*my+(z+1)];
                    int maskNorth = visibilityMask[(y+1)*my+(z+2)];
                    int maskSouth = visibilityMask[(y+1)*my+(z)];
                    int maskTop = visibilityMask[(y+2)*my+(z+1)];
                    int maskBottom = visibilityMask[(y)*my+(z+1)];
                    int maskEast = mask >> 1;
                    int maskWest = mask << 1;

                    maskNorth &= mask;
                    maskSouth &= mask;
                    maskTop &= mask;
                    maskBottom &= mask;
                    maskEast &= mask;
                    maskWest &= mask;

                    for(int x = 0; x < CHUNK_WIDTH; x++) {

                        if ((maskTop>>(x+1)& 1 )> 0 ){
                            voxels[x][y][z][TOP].transparent = true;
                        } else{
                            //voxels[x][y][z][TOP].transparent = false;
                        }

                        if ((maskBottom>>(x+1)& 1 )> 0 ){
                            voxels[x][y][z][BOTTOM].transparent = true;
                        } else{
                            //voxels[x][y][z][BOTTOM].transparent = false;
                        }

                        if ((maskNorth>>(x+1)& 1 )> 0 ){
                            voxels[x][y][z][NORTH].transparent = true;
                        } else{
                            //voxels[x][y][z][NORTH].transparent = false;
                        }

                        if ((maskSouth>>(x+1)& 1) > 0 ){
                            voxels[x][y][z][SOUTH].transparent = true;
                        } else{
                            //voxels[x][y][z][SOUTH].transparent = false;
                        }

                        if ((maskEast>>(x+1)& 1) > 0 ){
                            voxels[x][y][z][EAST].transparent = true;
                        } else{
                           // voxels[x][y][z][EAST].transparent = false;
                        }

                        if ((maskWest>>(x+1)& 1) > 0 ){
                            voxels[x][y][z][WEST].transparent = true;
                        } else{
                           // voxels[x][y][z][WEST].transparent = false;
                        }

                        BlockDefinition ao00 = VoxelChunk.blockDef(voxelWorld.get(ox + x, oy + y, oz + z, chunk.plane));
                        BlockDefinition ao01 = VoxelChunk.blockDef(voxelWorld.get(ox + x, oy + y, oz + z + 1, chunk.plane));
                        BlockDefinition ao10 = VoxelChunk.blockDef(voxelWorld.get(ox + x + 1, oy + y, oz + z, chunk.plane));
                        BlockDefinition ao11 = VoxelChunk.blockDef(voxelWorld.get(ox + x + 1, oy + y, oz + z + 1, chunk.plane));

                        BlockDefinition ao00T = VoxelChunk.blockDef(voxelWorld.get(ox + x, oy + y + 1, oz + z, chunk.plane));
                        BlockDefinition ao01T = VoxelChunk.blockDef(voxelWorld.get(ox + x, oy + y + 1, oz + z + 1, chunk.plane));
                        BlockDefinition ao10T = VoxelChunk.blockDef(voxelWorld.get(ox + x + 1, oy + y + 1, oz + z, chunk.plane));
                        BlockDefinition ao11T = VoxelChunk.blockDef(voxelWorld.get(ox + x + 1, oy + y + 1, oz + z + 1, chunk.plane));

                        voxels[x][y][z][TOP].vertex(3,ao01T, ao10T, ao11T, ao00T);
                        voxels[x][y][z][EAST].vertex(3,ao10T, ao11, ao11T, ao10);
                        voxels[x][y][z][NORTH].vertex(3,ao01T, ao11, ao11T, ao01);

                        voxels[x+1][y][z][TOP].vertex(2,ao00T, ao11T, ao01T, ao10T);
                        voxels[x+1][y][z][WEST].vertex(3,ao00T, ao01, ao01T, ao00);
                        voxels[x+1][y][z][NORTH].vertex(1,ao11T, ao01, ao01T, ao11);


                        voxels[x][y][z+1][TOP].vertex(1,ao00T, ao11T, ao10T, ao01T);
                        voxels[x][y][z+1][EAST].vertex(2,ao11T, ao10, ao10T, ao11);
                        voxels[x][y][z+1][SOUTH].vertex(3,ao00T, ao10, ao10T, ao00);

                        voxels[x+1][y][z+1][TOP].vertex(0,ao01T, ao10T, ao00T, ao11T);
                        voxels[x+1][y][z+1][WEST].vertex(2,ao01T, ao00, ao00T, ao01);
                        voxels[x+1][y][z+1][SOUTH].vertex(1,ao10T, ao00, ao00T, ao10);

                        voxels[x][y+1][z][BOTTOM].vertex(3,ao01, ao10, ao11, ao01);
                        voxels[x][y+1][z][EAST].vertex(1,ao10, ao11T, ao11, ao10T);
                        voxels[x][y+1][z][NORTH].vertex(2,ao01, ao11T, ao11, ao01T);

                        voxels[x+1][y+1][z][BOTTOM].vertex(2,ao00, ao11, ao01, ao10);
                        voxels[x+1][y+1][z][WEST].vertex(1,ao00, ao01T, ao01, ao00T);
                        voxels[x+1][y+1][z][NORTH].vertex(0,ao01T, ao11, ao01, ao11T);

                        voxels[x][y+1][z+1][BOTTOM].vertex(1,ao00, ao11, ao10, ao01);
                        voxels[x][y+1][z+1][EAST].vertex(0,ao11, ao10T, ao10, ao11T);
                        voxels[x][y+1][z+1][SOUTH].vertex(2,ao00, ao10T, ao10, ao00T);

                        voxels[x+1][y+1][z+1][BOTTOM].vertex(0,ao01, ao10, ao00, ao11);
                        voxels[x+1][y+1][z+1][WEST].vertex(0,ao01, ao00T, ao00, ao01T);
                        voxels[x+1][y+1][z+1][SOUTH].vertex(0,ao10, ao00T, ao00, ao10T);

                    }}}//}
        } else if (prog < FACE_SUBDIVISIONS+2){
            //norths
            for(int y = 0; y < CHUNK_HEIGHT-1; y++) {
                for(int z = CHUNK_WIDTH-1; z < CHUNK_WIDTH; z++) {
                    int mask = visibilityMask[(y+1)*my+(z+1)];
                    int maskNorth = visibilityMask[(y+1)*my+(z+2)];
                    int maskSouth = visibilityMask[(y+1)*my+(z)];
                    int maskTop = visibilityMask[(y+2)*my+(z+1)];
                    int maskBottom = visibilityMask[(y)*my+(z+1)];
                    int maskEast = mask >> 1;
                    int maskWest = mask << 1;





                    maskNorth &= mask;
                    maskSouth &= mask;
                    maskTop &= mask;
                    maskBottom &= mask;
                    maskEast &= mask;
                    maskWest &= mask;
                    
                    

                    for(int x = 0; x < CHUNK_WIDTH; x++) {

                        if ((maskTop>>(x+1)& 1 )> 0 ){
                            voxels[x][y][z][TOP].transparent = true;
                        } else{
                            //voxels[x][y][z][TOP].transparent = false;
                        }

                        if ((maskBottom>>(x+1)& 1 )> 0 ){
                            voxels[x][y][z][BOTTOM].transparent = true;
                        } else{
                            //voxels[x][y][z][BOTTOM].transparent = false;
                        }

                        if ((maskNorth>>(x+1)& 1 )> 0 ){
                            voxels[x][y][z][NORTH].transparent = true;
                        } else{
                           // voxels[x][y][z][NORTH].transparent = false;
                        }

                        if ((maskSouth>>(x+1)& 1) > 0 ){
                            voxels[x][y][z][SOUTH].transparent = true;
                        } else{
                            //voxels[x][y][z][SOUTH].transparent = false;
                        }

                        if ((maskEast>>(x+1)& 1) > 0 ){
                            voxels[x][y][z][EAST].transparent = true;
                        } else{
                            //voxels[x][y][z][EAST].transparent = false;
                        }

                        if ((maskWest>>(x+1)& 1) > 0 ){
                            voxels[x][y][z][WEST].transparent = true;
                        } else{
                            //voxels[x][y][z][WEST].transparent = false;
                        }

                        BlockDefinition ao00 = VoxelChunk.blockDef(voxelWorld.get(ox + x, oy + y, oz + z, chunk.plane));
                        BlockDefinition ao01 = VoxelChunk.blockDef(voxelWorld.get(ox + x, oy + y, oz + z + 1, chunk.plane));
                        BlockDefinition ao10 = VoxelChunk.blockDef(voxelWorld.get(ox + x + 1, oy + y, oz + z, chunk.plane));
                        BlockDefinition ao11 = VoxelChunk.blockDef(voxelWorld.get(ox + x + 1, oy + y, oz + z + 1, chunk.plane));

                        BlockDefinition ao00T = VoxelChunk.blockDef(voxelWorld.get(ox + x, oy + y + 1, oz + z, chunk.plane));
                        BlockDefinition ao01T = VoxelChunk.blockDef(voxelWorld.get(ox + x, oy + y + 1, oz + z + 1, chunk.plane));
                        BlockDefinition ao10T = VoxelChunk.blockDef(voxelWorld.get(ox + x + 1, oy + y + 1, oz + z, chunk.plane));
                        BlockDefinition ao11T = VoxelChunk.blockDef(voxelWorld.get(ox + x + 1, oy + y + 1, oz + z + 1, chunk.plane));

                        voxels[x][y][z][TOP].vertex(3,ao01T, ao10T, ao11T, ao00T);
                        voxels[x][y][z][EAST].vertex(3,ao10T, ao11, ao11T, ao10);
                        voxels[x][y][z][NORTH].vertex(3,ao01T, ao11, ao11T, ao01);

                        voxels[x+1][y][z][TOP].vertex(2,ao00T, ao11T, ao01T, ao10T);
                        voxels[x+1][y][z][WEST].vertex(3,ao00T, ao01, ao01T, ao00);
                        voxels[x+1][y][z][NORTH].vertex(1,ao11T, ao01, ao01T, ao11);


                        voxels[x][y][z+1][TOP].vertex(1,ao00T, ao11T, ao10T, ao01T);
                        voxels[x][y][z+1][EAST].vertex(2,ao11T, ao10, ao10T, ao11);
                        voxels[x][y][z+1][SOUTH].vertex(3,ao00T, ao10, ao10T, ao00);

                        voxels[x+1][y][z+1][TOP].vertex(0,ao01T, ao10T, ao00T, ao11T);
                        voxels[x+1][y][z+1][WEST].vertex(2,ao01T, ao00, ao00T, ao01);
                        voxels[x+1][y][z+1][SOUTH].vertex(1,ao10T, ao00, ao00T, ao10);

                        voxels[x][y+1][z][BOTTOM].vertex(3,ao01, ao10, ao11, ao01);
                        voxels[x][y+1][z][EAST].vertex(1,ao10, ao11T, ao11, ao10T);
                        voxels[x][y+1][z][NORTH].vertex(2,ao01, ao11T, ao11, ao01T);

                        voxels[x+1][y+1][z][BOTTOM].vertex(2,ao00, ao11, ao01, ao10);
                        voxels[x+1][y+1][z][WEST].vertex(1,ao00, ao01T, ao01, ao00T);
                        voxels[x+1][y+1][z][NORTH].vertex(0,ao01T, ao11, ao01, ao11T);

                        voxels[x][y+1][z+1][BOTTOM].vertex(1,ao00, ao11, ao10, ao01);
                        voxels[x][y+1][z+1][EAST].vertex(0,ao11, ao10T, ao10, ao11T);
                        voxels[x][y+1][z+1][SOUTH].vertex(2,ao00, ao10T, ao10, ao00T);

                        voxels[x+1][y+1][z+1][BOTTOM].vertex(0,ao01, ao10, ao00, ao11);
                        voxels[x+1][y+1][z+1][WEST].vertex(0,ao01, ao00T, ao00, ao01T);
                        voxels[x+1][y+1][z+1][SOUTH].vertex(0,ao10, ao00T, ao00, ao10T);

                    }}}//}
        } else if (prog < FACE_SUBDIVISIONS+3){
            //e? with visibility
            for(int y = 0; y < CHUNK_HEIGHT-1; y++) {
                for(int z = 0; z < CHUNK_WIDTH-1; z++) {
                    int mask = visibilityMask[(y+1)*my+(z+1)];
                    int maskNorth = visibilityMask[(y+1)*my+(z+2)];
                    int maskSouth = visibilityMask[(y+1)*my+(z)];
                    int maskTop = visibilityMask[(y+2)*my+(z+1)];
                    int maskBottom = visibilityMask[(y)*my+(z+1)];
                    int maskEast = mask >> 1;
                    int maskWest = mask << 1;





                    maskNorth &= mask;
                    maskSouth &= mask;
                    maskTop &= mask;
                    maskBottom &= mask;
                    maskEast &= mask;
                    maskWest &= mask;

                    for(int x = CHUNK_WIDTH-1; x < CHUNK_WIDTH; x++) {

                        if ((maskTop>>(x+1)& 1 )> 0 ){
                            voxels[x][y][z][TOP].transparent = true;
                        } else{
                           // voxels[x][y][z][TOP].transparent = false;
                        }

                        if ((maskBottom>>(x+1)& 1 )> 0 ){
                            voxels[x][y][z][BOTTOM].transparent = true;
                        } else{
                            //voxels[x][y][z][BOTTOM].transparent = false;
                        }

                        if ((maskNorth>>(x+1)& 1 )> 0 ){
                            voxels[x][y][z][NORTH].transparent = true;
                        } else{
                            //voxels[x][y][z][NORTH].transparent = false;
                        }

                        if ((maskSouth>>(x+1)& 1) > 0 ){
                            voxels[x][y][z][SOUTH].transparent = true;
                        } else{
                            //voxels[x][y][z][SOUTH].transparent = false;
                        }

                        if ((maskEast>>(x+1)& 1) > 0 ){
                            voxels[x][y][z][EAST].transparent = true;
                        } else{
                            //voxels[x][y][z][EAST].transparent = false;
                        }

                        if ((maskWest>>(x+1)& 1) > 0 ){
                            voxels[x][y][z][WEST].transparent = true;
                        } else{
                           //voxels[x][y][z][WEST].transparent = false;
                        }

                        BlockDefinition ao00 = VoxelChunk.blockDef(voxelWorld.get(ox + x, oy + y, oz + z, chunk.plane));
                        BlockDefinition ao01 = VoxelChunk.blockDef(voxelWorld.get(ox + x, oy + y, oz + z + 1, chunk.plane));
                        BlockDefinition ao10 = VoxelChunk.blockDef(voxelWorld.get(ox + x + 1, oy + y, oz + z, chunk.plane));
                        BlockDefinition ao11 = VoxelChunk.blockDef(voxelWorld.get(ox + x + 1, oy + y, oz + z + 1, chunk.plane));

                        BlockDefinition ao00T = VoxelChunk.blockDef(voxelWorld.get(ox + x, oy + y + 1, oz + z, chunk.plane));
                        BlockDefinition ao01T = VoxelChunk.blockDef(voxelWorld.get(ox + x, oy + y + 1, oz + z + 1, chunk.plane));
                        BlockDefinition ao10T = VoxelChunk.blockDef(voxelWorld.get(ox + x + 1, oy + y + 1, oz + z, chunk.plane));
                        BlockDefinition ao11T = VoxelChunk.blockDef(voxelWorld.get(ox + x + 1, oy + y + 1, oz + z + 1, chunk.plane));

                        voxels[x][y][z][TOP].vertex(3,ao01T, ao10T, ao11T, ao00T);
                        voxels[x][y][z][EAST].vertex(3,ao10T, ao11, ao11T, ao10);
                        voxels[x][y][z][NORTH].vertex(3,ao01T, ao11, ao11T, ao01);

                        voxels[x+1][y][z][TOP].vertex(2,ao00T, ao11T, ao01T, ao10T);
                        voxels[x+1][y][z][WEST].vertex(3,ao00T, ao01, ao01T, ao00);
                        voxels[x+1][y][z][NORTH].vertex(1,ao11T, ao01, ao01T, ao11);


                        voxels[x][y][z+1][TOP].vertex(1,ao00T, ao11T, ao10T, ao01T);
                        voxels[x][y][z+1][EAST].vertex(2,ao11T, ao10, ao10T, ao11);
                        voxels[x][y][z+1][SOUTH].vertex(3,ao00T, ao10, ao10T, ao00);

                        voxels[x+1][y][z+1][TOP].vertex(0,ao01T, ao10T, ao00T, ao11T);
                        voxels[x+1][y][z+1][WEST].vertex(2,ao01T, ao00, ao00T, ao01);
                        voxels[x+1][y][z+1][SOUTH].vertex(1,ao10T, ao00, ao00T, ao10);

                        voxels[x][y+1][z][BOTTOM].vertex(3,ao01, ao10, ao11, ao01);
                        voxels[x][y+1][z][EAST].vertex(1,ao10, ao11T, ao11, ao10T);
                        voxels[x][y+1][z][NORTH].vertex(2,ao01, ao11T, ao11, ao01T);

                        voxels[x+1][y+1][z][BOTTOM].vertex(2,ao00, ao11, ao01, ao10);
                        voxels[x+1][y+1][z][WEST].vertex(1,ao00, ao01T, ao01, ao00T);
                        voxels[x+1][y+1][z][NORTH].vertex(0,ao01T, ao11, ao01, ao11T);

                        voxels[x][y+1][z+1][BOTTOM].vertex(1,ao00, ao11, ao10, ao01);
                        voxels[x][y+1][z+1][EAST].vertex(0,ao11, ao10T, ao10, ao11T);
                        voxels[x][y+1][z+1][SOUTH].vertex(2,ao00, ao10T, ao10, ao00T);

                        voxels[x+1][y+1][z+1][BOTTOM].vertex(0,ao01, ao10, ao00, ao11);
                        voxels[x+1][y+1][z+1][WEST].vertex(0,ao01, ao00T, ao00, ao01T);
                        voxels[x+1][y+1][z+1][SOUTH].vertex(0,ao10, ao00T, ao00, ao10T);

                    }}}//}
        }


        else if (prog < FACE_SUBDIVISIONS+4 ){
//BOTTOMS
            for(int y = -1; y < 0; y++) {
                for(int z = 0; z < CHUNK_WIDTH; z++) {
                	
                    
                    for(int x = 0; x < CHUNK_WIDTH; x++) {

                    	

                        BlockDefinition ao00 = VoxelChunk.blockDef(voxelWorld.get(ox+x, oy+y, oz+z, chunk.plane));
                        BlockDefinition ao01 = VoxelChunk.blockDef(voxelWorld.get(ox+x, oy+y, oz+z + 1, chunk.plane));
                        BlockDefinition ao10 = VoxelChunk.blockDef(voxelWorld.get(ox+x + 1, oy+y, oz+z, chunk.plane));
                        BlockDefinition ao11 = VoxelChunk.blockDef(voxelWorld.get(ox+x + 1, oy+y, oz+z + 1, chunk.plane));

                        BlockDefinition ao00T = VoxelChunk.blockDef(voxelWorld.get(ox+x, oy+y + 1, oz+z, chunk.plane));
                        BlockDefinition ao01T = VoxelChunk.blockDef(voxelWorld.get(ox+x, oy+y + 1, oz+z + 1, chunk.plane));
                        BlockDefinition ao10T = VoxelChunk.blockDef(voxelWorld.get(ox+x + 1, oy+y + 1, oz+z, chunk.plane));
                        BlockDefinition ao11T = VoxelChunk.blockDef(voxelWorld.get(ox+x + 1, oy+y + 1, oz+z + 1, chunk.plane));


                        voxels[x][y+1][z][BOTTOM].vertex(3,ao01, ao10, ao11, ao01);
                        voxels[x][y+1][z][EAST].vertex(1,ao10, ao11T, ao11, ao10T);
                        voxels[x][y+1][z][NORTH].vertex(2,ao01, ao11T, ao11, ao01T);

                        voxels[x+1][y+1][z][BOTTOM].vertex(2,ao00, ao11, ao01, ao10);
                        voxels[x+1][y+1][z][WEST].vertex(1,ao00, ao01T, ao01, ao00T);
                        voxels[x+1][y+1][z][NORTH].vertex(0,ao01T, ao11, ao01, ao11T);

                        voxels[x][y+1][z+1][BOTTOM].vertex(1,ao00, ao11, ao10, ao01);
                        voxels[x][y+1][z+1][EAST].vertex(0,ao11, ao10T, ao10, ao11T);
                        voxels[x][y+1][z+1][SOUTH].vertex(2,ao00, ao10T, ao10, ao00T);

                        voxels[x+1][y+1][z+1][BOTTOM].vertex(0,ao01, ao10, ao00, ao11);
                        voxels[x+1][y+1][z+1][WEST].vertex(0,ao01, ao00T, ao00, ao01T);
                        voxels[x+1][y+1][z+1][SOUTH].vertex(0,ao10, ao00T, ao00, ao10T);


                    }}}//}
        } else if (prog < FACE_SUBDIVISIONS+5 ){
            for(int y = 0; y < CHUNK_HEIGHT; y++) {
                for(int z = 0; z < CHUNK_WIDTH; z++) {
//w?
       
                    
                    for(int x = -1; x < 0; x++) {
                    	
                    	
                    	
                        BlockDefinition ao00 = VoxelChunk.blockDef(voxelWorld.get(ox+x, oy+y, oz+z, chunk.plane));
                        BlockDefinition ao01 = VoxelChunk.blockDef(voxelWorld.get(ox+x, oy+y, oz+z + 1, chunk.plane));
                        BlockDefinition ao10 = VoxelChunk.blockDef(voxelWorld.get(ox+x + 1, oy+y, oz+z, chunk.plane));
                        BlockDefinition ao11 = VoxelChunk.blockDef(voxelWorld.get(ox+x + 1, oy+y, oz+z + 1, chunk.plane));

                        BlockDefinition ao00T = VoxelChunk.blockDef(voxelWorld.get(ox+x, oy+y + 1, oz+z, chunk.plane));
                        BlockDefinition ao01T = VoxelChunk.blockDef(voxelWorld.get(ox+x, oy+y + 1, oz+z + 1, chunk.plane));
                        BlockDefinition ao10T = VoxelChunk.blockDef(voxelWorld.get(ox+x + 1, oy+y + 1, oz+z, chunk.plane));
                        BlockDefinition ao11T = VoxelChunk.blockDef(voxelWorld.get(ox+x + 1, oy+y + 1, oz+z + 1, chunk.plane));

                        voxels[x+1][y][z][TOP].vertex(2,ao00T, ao11T, ao01T, ao10T);
                        voxels[x+1][y][z][WEST].vertex(3,ao00T, ao01, ao01T, ao00);
                        voxels[x+1][y][z][NORTH].vertex(1,ao11T, ao01, ao01T, ao11);




                        voxels[x+1][y][z+1][TOP].vertex(0,ao01T, ao10T, ao00T, ao11T);
                        voxels[x+1][y][z+1][WEST].vertex(2,ao01T, ao00, ao00T, ao01);
                        voxels[x+1][y][z+1][SOUTH].vertex(1,ao10T, ao00, ao00T, ao10);


                        voxels[x+1][y+1][z][BOTTOM].vertex(2,ao00, ao11, ao01, ao10);
                        voxels[x+1][y+1][z][WEST].vertex(1,ao00, ao01T, ao01, ao00T);
                        voxels[x+1][y+1][z][NORTH].vertex(0,ao01T, ao11, ao01, ao11T);



                        voxels[x+1][y+1][z+1][BOTTOM].vertex(0,ao01, ao10, ao00, ao11);
                        voxels[x+1][y+1][z+1][WEST].vertex(0,ao01, ao00T, ao00, ao01T);
                        voxels[x+1][y+1][z+1][SOUTH].vertex(0,ao10, ao00T, ao00, ao10T);

                    }}}//}
        } else if (prog < FACE_SUBDIVISIONS+6 ){
//SOUTHS
            for(int y = 0; y < CHUNK_HEIGHT; y++) {
                for(int z = -1; z < 0; z++) {
                	
                	

                	
                    for(int x = 0; x < CHUNK_WIDTH; x++) {

                    	
                    	
                    	
                        BlockDefinition ao00 = VoxelChunk.blockDef(voxelWorld.get(ox+x, oy+y, oz+z, chunk.plane));
                        BlockDefinition ao01 = VoxelChunk.blockDef(voxelWorld.get(ox+x, oy+y, oz+z + 1, chunk.plane));
                        BlockDefinition ao10 = VoxelChunk.blockDef(voxelWorld.get(ox+x + 1, oy+y, oz+z, chunk.plane));
                        BlockDefinition ao11 = VoxelChunk.blockDef(voxelWorld.get(ox+x + 1, oy+y, oz+z + 1, chunk.plane));

                        BlockDefinition ao00T = VoxelChunk.blockDef(voxelWorld.get(ox+x, oy+y + 1, oz+z, chunk.plane));
                        BlockDefinition ao01T = VoxelChunk.blockDef(voxelWorld.get(ox+x, oy+y + 1, oz+z + 1, chunk.plane));
                        BlockDefinition ao10T = VoxelChunk.blockDef(voxelWorld.get(ox+x + 1, oy+y + 1, oz+z, chunk.plane));
                        BlockDefinition ao11T = VoxelChunk.blockDef(voxelWorld.get(ox+x + 1, oy+y + 1, oz+z + 1, chunk.plane));

                        voxels[x][y][z+1][TOP].vertex(1,ao00T, ao11T, ao10T, ao01T);
                        voxels[x][y][z+1][EAST].vertex(2,ao11T, ao10, ao10T, ao11);
                        voxels[x][y][z+1][SOUTH].vertex(3,ao00T, ao10, ao10T, ao00);

                        voxels[x+1][y][z+1][TOP].vertex(0,ao01T, ao10T, ao00T, ao11T);
                        voxels[x+1][y][z+1][WEST].vertex(2,ao01T, ao00, ao00T, ao01);
                        voxels[x+1][y][z+1][SOUTH].vertex(1,ao10T, ao00, ao00T, ao10);



                        voxels[x][y+1][z+1][BOTTOM].vertex(1,ao00, ao11, ao10, ao01);
                        voxels[x][y+1][z+1][EAST].vertex(0,ao11, ao10T, ao10, ao11T);
                        voxels[x][y+1][z+1][SOUTH].vertex(2,ao00, ao10T, ao10, ao00T);

                        voxels[x+1][y+1][z+1][BOTTOM].vertex(0,ao01, ao10, ao00, ao11);
                        voxels[x+1][y+1][z+1][WEST].vertex(0,ao01, ao00T, ao00, ao01T);
                        voxels[x+1][y+1][z+1][SOUTH].vertex(0,ao10, ao00T, ao00, ao10T);




                    }}}//}

        } else if (prog < FACE_SUBDIVISIONS+7 ){
//0 diagonal x
            for(int y = -1; y < 0; y++) {
                for(int z = -1; z < 0; z++) {


                    for (int x = 0; x < width; x++){

                        BlockDefinition ao00 = VoxelChunk.blockDef(voxelWorld.get(ox+x, oy+y, oz+z, chunk.plane));
                        BlockDefinition ao01 = VoxelChunk.blockDef(voxelWorld.get(ox+x, oy+y, oz+z + 1, chunk.plane));
                        BlockDefinition ao10 = VoxelChunk.blockDef(voxelWorld.get(ox+x + 1, oy+y, oz+z, chunk.plane));
                        BlockDefinition ao11 = VoxelChunk.blockDef(voxelWorld.get(ox+x + 1, oy+y, oz+z + 1, chunk.plane));

                        BlockDefinition ao00T = VoxelChunk.blockDef(voxelWorld.get(ox+x, oy+y + 1, oz+z, chunk.plane));
                        BlockDefinition ao01T = VoxelChunk.blockDef(voxelWorld.get(ox+x, oy+y + 1, oz+z + 1, chunk.plane));
                        BlockDefinition ao10T = VoxelChunk.blockDef(voxelWorld.get(ox+x + 1, oy+y + 1, oz+z, chunk.plane));
                        BlockDefinition ao11T = VoxelChunk.blockDef(voxelWorld.get(ox+x + 1, oy+y + 1, oz+z + 1, chunk.plane));


                        voxels[x][y+1][z+1][BOTTOM].vertex(1,ao00, ao11, ao10, ao01);
                        voxels[x][y+1][z+1][EAST].vertex(0,ao11, ao10T, ao10, ao11T);
                        voxels[x][y+1][z+1][SOUTH].vertex(2,ao00, ao10T, ao10, ao00T);

                        voxels[x+1][y+1][z+1][BOTTOM].vertex(0,ao01, ao10, ao00, ao11);
                        voxels[x+1][y+1][z+1][WEST].vertex(0,ao01, ao00T, ao00, ao01T);
                        voxels[x+1][y+1][z+1][SOUTH].vertex(0,ao10, ao00T, ao00, ao10T);

                    }}}//}
        } else if (prog < FACE_SUBDIVISIONS+8 ){
//diag y
            for(int y = 0; y < CHUNK_HEIGHT; y++) {
                for(int z = -1; z < 0; z++) {


                    for(int x = -1; x < 0; x++) {

                        BlockDefinition ao00 = VoxelChunk.blockDef(voxelWorld.get(ox+x, oy+y, oz+z, chunk.plane));
                        BlockDefinition ao01 = VoxelChunk.blockDef(voxelWorld.get(ox+x, oy+y, oz+z + 1, chunk.plane));
                        BlockDefinition ao10 = VoxelChunk.blockDef(voxelWorld.get(ox+x + 1, oy+y, oz+z, chunk.plane));
                        BlockDefinition ao11 = VoxelChunk.blockDef(voxelWorld.get(ox+x + 1, oy+y, oz+z + 1, chunk.plane));

                        BlockDefinition ao00T = VoxelChunk.blockDef(voxelWorld.get(ox+x, oy+y + 1, oz+z, chunk.plane));
                        BlockDefinition ao01T = VoxelChunk.blockDef(voxelWorld.get(ox+x, oy+y + 1, oz+z + 1, chunk.plane));
                        BlockDefinition ao10T = VoxelChunk.blockDef(voxelWorld.get(ox+x + 1, oy+y + 1, oz+z, chunk.plane));
                        BlockDefinition ao11T = VoxelChunk.blockDef(voxelWorld.get(ox+x + 1, oy+y + 1, oz+z + 1, chunk.plane));


                        voxels[x+1][y][z+1][TOP].vertex(0,ao01T, ao10T, ao00T, ao11T);
                        voxels[x+1][y][z+1][WEST].vertex(2,ao01T, ao00, ao00T, ao01);
                        voxels[x+1][y][z+1][SOUTH].vertex(1,ao10T, ao00, ao00T, ao10);

                        voxels[x+1][y+1][z+1][BOTTOM].vertex(0,ao01, ao10, ao00, ao11);
                        voxels[x+1][y+1][z+1][WEST].vertex(0,ao01, ao00T, ao00, ao01T);
                        voxels[x+1][y+1][z+1][SOUTH].vertex(0,ao10, ao00T, ao00, ao10T);


                    }}}//}

        } else if (prog < FACE_SUBDIVISIONS+9){
            //diag z
            for(int y = -1; y < 0; y++) {
                for(int z = 0; z < CHUNK_WIDTH; z++) {


                    for(int x = -1; x < 0; x++) {

                        BlockDefinition ao00 = VoxelChunk.blockDef(voxelWorld.get(ox+x, oy+y, oz+z, chunk.plane));
                        BlockDefinition ao01 = VoxelChunk.blockDef(voxelWorld.get(ox+x, oy+y, oz+z + 1, chunk.plane));
                        BlockDefinition ao10 = VoxelChunk.blockDef(voxelWorld.get(ox+x + 1, oy+y, oz+z, chunk.plane));
                        BlockDefinition ao11 = VoxelChunk.blockDef(voxelWorld.get(ox+x + 1, oy+y, oz+z + 1, chunk.plane));

                        BlockDefinition ao00T = VoxelChunk.blockDef(voxelWorld.get(ox+x, oy+y + 1, oz+z, chunk.plane));
                        BlockDefinition ao01T = VoxelChunk.blockDef(voxelWorld.get(ox+x, oy+y + 1, oz+z + 1, chunk.plane));
                        BlockDefinition ao10T = VoxelChunk.blockDef(voxelWorld.get(ox+x + 1, oy+y + 1, oz+z, chunk.plane));
                        BlockDefinition ao11T = VoxelChunk.blockDef(voxelWorld.get(ox+x + 1, oy+y + 1, oz+z + 1, chunk.plane));

                        voxels[x+1][y+1][z][BOTTOM].vertex(2,ao00, ao11, ao01, ao10);
                        voxels[x+1][y+1][z][WEST].vertex(1,ao00, ao01T, ao01, ao00T);
                        voxels[x+1][y+1][z][NORTH].vertex(0,ao01T, ao11, ao01, ao11T);

                        voxels[x+1][y+1][z+1][BOTTOM].vertex(0,ao01, ao10, ao00, ao11);
                        voxels[x+1][y+1][z+1][WEST].vertex(0,ao01, ao00T, ao00, ao01T);
                        voxels[x+1][y+1][z+1][SOUTH].vertex(0,ao10, ao00T, ao00, ao10T);


                    }}}//}
        }else if (prog < FACE_SUBDIVISIONS+10){
            for(int y = -1; y < 0; y++) {
                for(int z = -1; z < 0; z++) {

                    for(int x = -1; x < 0; x++) {

                        BlockDefinition ao00 = VoxelChunk.blockDef(voxelWorld.get(ox+x, oy+y, oz+z, chunk.plane));
                        BlockDefinition ao01 = VoxelChunk.blockDef(voxelWorld.get(ox+x, oy+y, oz+z + 1, chunk.plane));
                        BlockDefinition ao10 = VoxelChunk.blockDef(voxelWorld.get(ox+x + 1, oy+y, oz+z, chunk.plane));
                        BlockDefinition ao11 = VoxelChunk.blockDef(voxelWorld.get(ox+x + 1, oy+y, oz+z + 1, chunk.plane));

                        BlockDefinition ao00T = VoxelChunk.blockDef(voxelWorld.get(ox+x, oy+y + 1, oz+z, chunk.plane));
                        BlockDefinition ao01T = VoxelChunk.blockDef(voxelWorld.get(ox+x, oy+y + 1, oz+z + 1, chunk.plane));
                        BlockDefinition ao10T = VoxelChunk.blockDef(voxelWorld.get(ox+x + 1, oy+y + 1, oz+z, chunk.plane));
                        BlockDefinition ao11T = VoxelChunk.blockDef(voxelWorld.get(ox+x + 1, oy+y + 1, oz+z + 1, chunk.plane));

                        voxels[x+1][y+1][z+1][BOTTOM].vertex(0,ao01, ao10, ao00, ao11);
                        voxels[x+1][y+1][z+1][WEST].vertex(0,ao01, ao00T, ao00, ao01T);
                        voxels[x+1][y+1][z+1][SOUTH].vertex(0,ao10, ao00T, ao00, ao10T);

                    }}}//}
        }

        else {
            return true;
        }
        return false;



    }




//		if(voxels[i+topOffset] <= 0) my += ();
//		if(voxels[i+bottomOffset] <= 0) my += ();
//		if(voxels[i+leftOffset] <= 0) mx += ();
//		if(voxels[i+rightOffset] <= 0) mx += ();
//		if(voxels[i+frontOffset] <= 0) mz += ();
//		if(voxels[i+backOffset] <= 0) mz += ();



    public GreedyMesher(final MeshBatch mesh) {
    	lightValues = new float[16];
        lightColors = new Color[16];
    	float r,g,b,a = 1f;;
        Color c = new Color(Color.WHITE);
    	for (int i = 15; i >= 0; i--){


    		lightValues[i] = c.toFloatBits();
            lightColors[i] = new Color(c);
            c.mul(.95f);

    	}

		meshBatch = mesh;
		setConstants(16,16,16);
		VoxelFace face, face2;
		for (int i = 0; i < CHUNK_WIDTH+1; i++) {

			for (int j = 0; j < CHUNK_HEIGHT+1; j++) {

				for (int k = 0; k < CHUNK_WIDTH+1; k++) {

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
		visibilityMask = new int[(CHUNK_HEIGHT+3)*(CHUNK_WIDTH+3)];
		lightCache = new int[CHUNK_WIDTH+4][CHUNK_HEIGHT+4][CHUNK_WIDTH+4];
	}


	/**
     *
     */
    final int[] x = new int []{0,0,0};
    final int[] q = new int []{0,0,0};
    final int[] du = new int[]{0,0,0};
    final int[] dv = new int[]{0,0,0};
    Vector3 cornerA = new Vector3(), cornerB = new Vector3(), cornerC = new Vector3(), cornerD = new Vector3();
    void greedy(int progress) {

        /*
         * These are just working variables for the algorithm � almost all taken
         * directly from Mikola Lysenko�s javascript implementation.
         */
        int i, j, k, l, w, h, u, v, n, side = 0;



        /*
         * We create a mask � this will contain the groups of matching voxel faces
         * as we proceed through the chunk in 6 directions � once for each face.
         */
        final VoxelFace[] mask = new VoxelFace [CHUNK_WIDTH * CHUNK_HEIGHT];

        /*
         * These are just working variables to hold two faces during comparison.
         */
        VoxelFace voxelFace, voxelFace1;

        /**
         * We start with the lesser-spotted boolean for-loop (also known as the old flippy floppy).
         *
         * The variable backFace will be TRUE on the first iteration and FALSE on the second � this allows
         * us to track which direction the indices should run during creation of the quad.
         *
         * This loop runs twice, and the inner loop 3 times � totally 6 iterations � one for each
         * voxel face.
         */
       // for (boolean backFace = true, b = false; b != backFace; backFace = backFace && b, b = !b)
        {

            /*
             * We sweep over the 3 dimensions � most of what follows is well described by Mikola Lysenko
             * in his post � and is ported from his Javascript implementation.  Where this implementation
             * diverges, I�ve added commentary.
             */
            //for(int d = 0; d < 3; d++)
            {
                int d = progress%3;
                boolean backFace = progress < 3;
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
                 * Here we�re keeping track of the face that we�re meshing.
                 */
                if (d == 0)      { side = backFace ? WEST   : EAST;  }
                else if (d == 1) { side = backFace ? BOTTOM : TOP;   }
                else if (d == 2) { side = backFace ? SOUTH  : NORTH; }

                /*
                 * We move through the dimension from front to back
                 */
                for(x[d] = -1; x[d] < CHUNK_WIDTH;) {

                    /*
                     * ����������������������-
                     *   We compute the mask
                     * ����������������������-
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
                             * Note that we�re using the equals function in the voxel face class here, which lets the faces
                             * be compared based on any number of attributes.
                             *
                             * Also, we choose the face to add to the mask depending on whether we�re moving through on a backface or not.
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
                                 * Here we check the �transparent� attribute in the VoxelFace class to ensure that we don�t mesh
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
                                     * all the attributes of the face � which allows for variables to be passed to shaders � for
                                     * example lighting values used to create ambient occlusion.
                                     */
                                    meshBatch.quad(cornerA.set(x[0],                 x[1],                   x[2]),
                                         cornerB.set(x[0] + du[0],         x[1] + du[1],           x[2] + du[2]),
                                         cornerC.set(x[0] + du[0] + dv[0], x[1] + du[1] + dv[1],   x[2] + du[2] + dv[2]),
                                         cornerD.set(x[0] + dv[0],         x[1] + dv[1],           x[2] + dv[2]),
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
     * one face of a voxel.  In this simple demo we just return a value from the
     * sample data array.  However, in an actual voxel engine, this function would
     * check if the voxel face should be culled, and set per-face and per-vertex
     * values as well as voxel values in the returned instance.
     *
     * @param x
     * @param y
     * @param z
     * @param side
     * @return
     */
    VoxelFace getVoxelFace(final int x, final int y, final int z, final int side) {

        VoxelFace voxelFace = voxels[x][y][z][side];

        //voxelFace.face = face;

        return voxelFace;
    }

    int progress, progressCoarse;
    public VoxelChunk chunk;
    VoxelWorld voxelWorld;
    @Override
    public void begin(VoxelChunk chunk, VoxelWorld voxelWorld) {
        CHUNK_WIDTH = chunk.width;
        CHUNK_HEIGHT = chunk.height;
        this.chunk = chunk;
        progress = 0;
        progressCoarse = 0;
        this.voxelWorld = voxelWorld;
       // Gdx.app.log(TAG, "start"+chunk.offset);
        //for (int i = 0; i < CHUNK_WIDTH+1; i++) {

        for (int k = 0; k < visibilityMask.length; k++) {
            visibilityMask[k] = 0;
        }

    }

    @Override
    public boolean process() {

        switch (progressCoarse){
            case 0:
                if (!readBlocks(chunk, voxelWorld, progress)){
                    progress++;
                } else {
                    progress = 0;
                    progressCoarse++;
                }
                break;
            case 1:
                if (preprocessor != null)
                    preprocessor.process(voxels, CHUNK_WIDTH, CHUNK_HEIGHT, chunk.offset);
                progressCoarse++;
                progress = 0;
                break;
            case 2:
                greedy(progress++);
                if (progress >= 6) return true;

        }

        return false;
    }

    @Override
    public int end() {
        //Gdx.app.log(TAG, "end"+chunk.offset);

        return meshBatch.flushCache(chunk, this);
    }

    public int calculateVertices( VoxelChunk chunk,
			VoxelWorld voxelWorld, MeshBatcher batch) {
        CHUNK_WIDTH = chunk.width;
        CHUNK_HEIGHT = chunk.height;
        int progress = 0;
		while (!readBlocks(chunk, voxelWorld, progress)){
            progress++;
        };
        if (preprocessor != null)
            preprocessor.process(voxels, CHUNK_WIDTH, CHUNK_HEIGHT, chunk.offset);

        for (int i = 0; i < 6; i++)
		    greedy(i);
		return meshBatch.flushCache(chunk, this);


	}




    public boolean readBlocksAO(VoxelChunk chunk, VoxelWorld voxelWorld, int progress){

        int progressVisibilityMax = VoxelChunk.VISIBILITY_SUBDIVISIONS+3;
        if (progress < progressVisibilityMax) {
            chunk.visibility(visibilityMask, lightCache, voxels, voxelWorld, progress);
            return false;
        }
        int prog = progress - progressVisibilityMax;
        int my = CHUNK_WIDTH+2;

        if (prog < FACE_SUBDIVISIONS){
            int progSize = CHUNK_HEIGHT / FACE_SUBDIVISIONS;
            for(int y = prog * progSize, n = (prog+1) * progSize; y < n; y++) {
                for(int z = 0; z < CHUNK_WIDTH; z++) {
                    int mask = visibilityMask[(y+1)*my+(z+1)];
                    int maskNorth = visibilityMask[(y+1)*my+(z+2)];
                    int maskSouth = visibilityMask[(y+1)*my+(z)];
                    int maskTop = visibilityMask[(y+2)*my+(z+1)];
                    int maskBottom = visibilityMask[(y)*my+(z+1)];
                    int maskEast = mask >> 1;
                    int maskWest = mask << 1;

                    int ao00 = mask;
                    int ao01 = maskNorth;
                    int ao10 = maskEast;
                    int ao11 = maskNorth >> 1;

                    int ao00T = maskTop;
                    int ao01T = visibilityMask[(y+2)*my+(z+2)];
                    int ao10T = maskTop >> 1;
                    int ao11T = ao01T >> 1;



                    maskNorth &= mask;
                    maskSouth &= mask;
                    maskTop &= mask;
                    maskBottom &= mask;
                    maskEast &= mask;
                    maskWest &= mask;

                    for(int x = 0; x < CHUNK_WIDTH; x++) {

                        if ((maskTop>>(x+1)& 1 )> 0 ){
                            voxels[x][y][z][TOP].transparent = true;
                        }

                        if ((maskBottom>>(x+1)& 1 )> 0 ){
                            voxels[x][y][z][BOTTOM].transparent = true;
                        }

                        if ((maskNorth>>(x+1)& 1 )> 0 ){
                            voxels[x][y][z][NORTH].transparent = true;
                        }

                        if ((maskSouth>>(x+1)& 1) > 0 ){
                            voxels[x][y][z][SOUTH].transparent = true;
                        }

                        if ((maskEast>>(x+1)& 1) > 0 ){
                            voxels[x][y][z][EAST].transparent = true;
                        }

                        if ((maskWest>>(x+1)& 1) > 0 ){
                            voxels[x][y][z][WEST].transparent = true;
                        }



                        voxels[x][y][z][TOP].vertex(3,ao01T, ao10T, ao11T, x);
                        voxels[x][y][z][EAST].vertex(3,ao10T, ao11, ao11T, x);
                        voxels[x][y][z][NORTH].vertex(3,ao01T, ao11, ao11T, x);

                        voxels[x+1][y][z][TOP].vertex(2,ao00T, ao11T, ao01T, x);
                        voxels[x+1][y][z][WEST].vertex(3,ao00T, ao01, ao01T, x);
                        voxels[x+1][y][z][NORTH].vertex(1,ao11T, ao01, ao01T, x);


                        voxels[x][y][z+1][TOP].vertex(1,ao00T, ao11T, ao10T, x);
                        voxels[x][y][z+1][EAST].vertex(2,ao11T, ao10, ao10T, x);
                        voxels[x][y][z+1][SOUTH].vertex(3,ao00T, ao10, ao10T, x);

                        voxels[x+1][y][z+1][TOP].vertex(0,ao01T, ao10T, ao00T, x);
                        voxels[x+1][y][z+1][WEST].vertex(2,ao01T, ao00, ao00T, x);
                        voxels[x+1][y][z+1][SOUTH].vertex(1,ao10T, ao00, ao00T, x);

                        voxels[x][y+1][z][BOTTOM].vertex(3,ao01, ao10, ao11, x);
                        voxels[x][y+1][z][EAST].vertex(1,ao10, ao11T, ao11, x);
                        voxels[x][y+1][z][NORTH].vertex(2,ao01, ao11T, ao11, x);

                        voxels[x+1][y+1][z][BOTTOM].vertex(2,ao00, ao11, ao01, x);
                        voxels[x+1][y+1][z][WEST].vertex(1,ao00, ao01T, ao01, x);
                        voxels[x+1][y+1][z][NORTH].vertex(0,ao01T, ao11, ao01, x);

                        voxels[x][y+1][z+1][BOTTOM].vertex(1,ao00, ao11, ao10, x);
                        voxels[x][y+1][z+1][EAST].vertex(0,ao11, ao10T, ao10, x);
                        voxels[x][y+1][z+1][SOUTH].vertex(2,ao00, ao10T, ao10, x);

                        voxels[x+1][y+1][z+1][BOTTOM].vertex(0,ao01, ao10, ao00, x);
                        voxels[x+1][y+1][z+1][WEST].vertex(0,ao01, ao00T, ao00, x);
                        voxels[x+1][y+1][z+1][SOUTH].vertex(0,ao10, ao00T, ao00, x);

                    }}}//}
        } else if (prog < FACE_SUBDIVISIONS+1 ){
//BOTTOMS
            for(int y = -1; y < 0; y++) {
                for(int z = 0; z < CHUNK_WIDTH; z++) {
                    int mask = visibilityMask[(y+1)*my+(z+1)];
                    int maskNorth = visibilityMask[(y+1)*my+(z+2)];
                    int maskTop = visibilityMask[(y+2)*my+(z+1)];
                    int maskEast = mask >> 1;

                    int ao00 = mask;
                    int ao01 = maskNorth;
                    int ao10 = maskEast;
                    int ao11 = maskNorth >> 1;

                    int ao00T = maskTop;
                    int ao01T = visibilityMask[(y+2)*my+(z+2)];
                    int ao10T = maskTop >> 1;
                    int ao11T = ao01T >> 1;

                    for(int x = 0; x < CHUNK_WIDTH; x++) {

                        voxels[x][y+1][z][BOTTOM].vertex(3,ao01, ao10, ao11, x);
                        voxels[x][y+1][z][EAST].vertex(1,ao10, ao11T, ao11, x);
                        voxels[x][y+1][z][NORTH].vertex(2,ao01, ao11T, ao11, x);

                        voxels[x+1][y+1][z][BOTTOM].vertex(2,ao00, ao11, ao01, x);
                        voxels[x+1][y+1][z][WEST].vertex(1,ao00, ao01T, ao01, x);
                        voxels[x+1][y+1][z][NORTH].vertex(0,ao01T, ao11, ao01, x);

                        voxels[x][y+1][z+1][BOTTOM].vertex(1,ao00, ao11, ao10, x);
                        voxels[x][y+1][z+1][EAST].vertex(0,ao11, ao10T, ao10, x);
                        voxels[x][y+1][z+1][SOUTH].vertex(2,ao00, ao10T, ao10, x);

                        voxels[x+1][y+1][z+1][BOTTOM].vertex(0,ao01, ao10, ao00, x);
                        voxels[x+1][y+1][z+1][WEST].vertex(0,ao01, ao00T, ao00, x);
                        voxels[x+1][y+1][z+1][SOUTH].vertex(0,ao10, ao00T, ao00, x);



                    }}}//}
        } else if (prog < FACE_SUBDIVISIONS+2 ){
            for(int y = 0; y < CHUNK_HEIGHT; y++) {
                for(int z = 0; z < CHUNK_WIDTH; z++) {
                    int mask = visibilityMask[(y+1)*my+(z+1)];
                    int maskNorth = visibilityMask[(y+1)*my+(z+2)];
                    int maskTop = visibilityMask[(y+2)*my+(z+1)];
                    int maskEast = mask >> 1;

                    int ao00 = mask;
                    int ao01 = maskNorth;
                    int ao10 = maskEast;
                    int ao11 = maskNorth >> 1;

                    int ao00T = maskTop;
                    int ao01T = visibilityMask[(y+2)*my+(z+2)];
                    int ao10T = maskTop >> 1;
                    int ao11T = ao01T >> 1;

                    for(int x = -1; x < 0; x++) {

                        voxels[x+1][y][z][TOP].vertex(2,ao00T, ao11T, ao01T, x);
                        voxels[x+1][y][z][WEST].vertex(3,ao00T, ao01, ao01T, x);
                        voxels[x+1][y][z][NORTH].vertex(1,ao11T, ao01, ao01T, x);

                        voxels[x+1][y][z+1][TOP].vertex(0,ao01T, ao10T, ao00T, x);
                        voxels[x+1][y][z+1][WEST].vertex(2,ao01T, ao00, ao00T, x);
                        voxels[x+1][y][z+1][SOUTH].vertex(1,ao10T, ao00, ao00T, x);

                        voxels[x+1][y+1][z][BOTTOM].vertex(2,ao00, ao11, ao01, x);
                        voxels[x+1][y+1][z][WEST].vertex(1,ao00, ao01T, ao01, x);
                        voxels[x+1][y+1][z][NORTH].vertex(0,ao01T, ao11, ao01, x);

                        voxels[x+1][y+1][z+1][BOTTOM].vertex(0,ao01, ao10, ao00, x);
                        voxels[x+1][y+1][z+1][WEST].vertex(0,ao01, ao00T, ao00, x);
                        voxels[x+1][y+1][z+1][SOUTH].vertex(0,ao10, ao00T, ao00, x);

                    }}}//}
        } else if (prog < FACE_SUBDIVISIONS+3 ){
//SOUTHS
            for(int y = 0; y < CHUNK_HEIGHT; y++) {
                for(int z = -1; z < 0; z++) {
                    int mask = visibilityMask[(y+1)*my+(z+1)];
                    int maskNorth = visibilityMask[(y+1)*my+(z+2)];
                    int maskTop = visibilityMask[(y+2)*my+(z+1)];
                    int maskEast = mask >> 1;

                    int ao00 = mask;
                    int ao01 = maskNorth;
                    int ao10 = maskEast;
                    int ao11 = maskNorth >> 1;

                    int ao00T = maskTop;
                    int ao01T = visibilityMask[(y+2)*my+(z+2)];
                    int ao10T = maskTop >> 1;
                    int ao11T = ao01T >> 1;

                    for(int x = 0; x < CHUNK_WIDTH; x++) {

                        voxels[x][y][z+1][TOP].vertex(1,ao00T, ao11T, ao10T, x);
                        voxels[x][y][z+1][EAST].vertex(2,ao11T, ao10, ao10T, x);
                        voxels[x][y][z+1][SOUTH].vertex(3,ao00T, ao10, ao10T, x);

                        voxels[x+1][y][z+1][TOP].vertex(0,ao01T, ao10T, ao00T, x);
                        voxels[x+1][y][z+1][WEST].vertex(2,ao01T, ao00, ao00T, x);
                        voxels[x+1][y][z+1][SOUTH].vertex(1,ao10T, ao00, ao00T, x);

                        voxels[x][y+1][z+1][BOTTOM].vertex(1,ao00, ao11, ao10, x);
                        voxels[x][y+1][z+1][EAST].vertex(0,ao11, ao10T, ao10, x);
                        voxels[x][y+1][z+1][SOUTH].vertex(2,ao00, ao10T, ao10, x);

                        voxels[x+1][y+1][z+1][BOTTOM].vertex(0,ao01, ao10, ao00, x);
                        voxels[x+1][y+1][z+1][WEST].vertex(0,ao01, ao00T, ao00, x);
                        voxels[x+1][y+1][z+1][SOUTH].vertex(0,ao10, ao00T, ao00, x);




                    }}}//}

        } else if (prog < FACE_SUBDIVISIONS+4 ){
//0 diagonal x
            for(int y = -1; y < 0; y++) {
                for(int z = -1; z < 0; z++) {
                    int mask = visibilityMask[(y+1)*my+(z+1)];
                    int maskNorth = visibilityMask[(y+1)*my+(z+2)];
                    int maskTop = visibilityMask[(y+2)*my+(z+1)];
                    int maskEast = mask >> 1;

                    int ao00 = mask;
                    int ao01 = maskNorth;
                    int ao10 = maskEast;
                    int ao11 = maskNorth >> 1;

                    int ao00T = maskTop;
                    int ao01T = visibilityMask[(y+2)*my+(z+2)];
                    int ao10T = maskTop >> 1;
                    for(int x = 0; x < CHUNK_WIDTH; x++) {

                        voxels[x][y+1][z+1][BOTTOM].vertex(1,ao00, ao11, ao10, x);
                        voxels[x][y+1][z+1][EAST].vertex(0,ao11, ao10T, ao10, x);
                        voxels[x][y+1][z+1][SOUTH].vertex(2,ao00, ao10T, ao10, x);

                        voxels[x+1][y+1][z+1][BOTTOM].vertex(0,ao01, ao10, ao00, x);
                        voxels[x+1][y+1][z+1][WEST].vertex(0,ao01, ao00T, ao00, x);
                        voxels[x+1][y+1][z+1][SOUTH].vertex(0,ao10, ao00T, ao00, x);

                    }}}//}
        } else if (prog < FACE_SUBDIVISIONS+5 ){
//diag y
            for(int y = 0; y < CHUNK_HEIGHT; y++) {
                for(int z = -1; z < 0; z++) {
                    int mask = visibilityMask[(y+1)*my+(z+1)];
                    int maskNorth = visibilityMask[(y+1)*my+(z+2)];
                    int maskTop = visibilityMask[(y+2)*my+(z+1)];
                    int maskEast = mask >> 1;

                    int ao00 = mask;
                    int ao01 = maskNorth;
                    int ao10 = maskEast;

                    int ao00T = maskTop;
                    int ao01T = visibilityMask[(y+2)*my+(z+2)];
                    int ao10T = maskTop >> 1;

                    for(int x = -1; x < 0; x++) {

                        voxels[x+1][y][z+1][TOP].vertex(0,ao01T, ao10T, ao00T, x);
                        voxels[x+1][y][z+1][WEST].vertex(2,ao01T, ao00, ao00T, x);
                        voxels[x+1][y][z+1][SOUTH].vertex(1,ao10T, ao00, ao00T, x);



                        voxels[x+1][y+1][z+1][BOTTOM].vertex(0,ao01, ao10, ao00, x);
                        voxels[x+1][y+1][z+1][WEST].vertex(0,ao01, ao00T, ao00, x);
                        voxels[x+1][y+1][z+1][SOUTH].vertex(0,ao10, ao00T, ao00, x);


                    }}}//}

        } else if (prog < FACE_SUBDIVISIONS+6){
            //diag z
            for(int y = -1; y < 0; y++) {
                for(int z = 0; z < CHUNK_WIDTH; z++) {
                    int mask = visibilityMask[(y+1)*my+(z+1)];
                    int maskNorth = visibilityMask[(y+1)*my+(z+2)];
                    int maskTop = visibilityMask[(y+2)*my+(z+1)];
                    int maskEast = mask >> 1;

                    int ao00 = mask;
                    int ao01 = maskNorth;
                    int ao10 = maskEast ;
                    int ao11 = maskNorth >> 1;

                    int ao00T = maskTop;
                    int ao01T = visibilityMask[(y+2)*my+(z+2)];

                    for(int x = -1; x < 0; x++) {

                        voxels[x+1][y+1][z][BOTTOM].vertex(2,ao00, ao11, ao01, x);
                        voxels[x+1][y+1][z][WEST].vertex(1,ao00, ao01T, ao01, x);
                        voxels[x+1][y+1][z][NORTH].vertex(0,ao01T, ao11, ao01, x);

                        voxels[x+1][y+1][z+1][BOTTOM].vertex(0,ao01, ao10, ao00, x);
                        voxels[x+1][y+1][z+1][WEST].vertex(0,ao01, ao00T, ao00, x);
                        voxels[x+1][y+1][z+1][SOUTH].vertex(0,ao10, ao00T, ao00, x);


                    }}}//}
        }else if (prog < FACE_SUBDIVISIONS+7){
            for(int y = -1; y < 0; y++) {
                for(int z = -1; z < 0; z++) {
                    int mask = visibilityMask[(y+1)*my+(z+1)];
                    int maskNorth = visibilityMask[(y+1)*my+(z+2)];
                    int maskTop = visibilityMask[(y+2)*my+(z+1)];
                    int maskEast = mask >> 1;

                    int ao00 = mask;
                    int ao01 = maskNorth;
                    int ao10 = maskEast;

                    int ao00T = maskTop;

                    for(int x = -1; x < 0; x++) {



                        voxels[x+1][y+1][z+1][BOTTOM].vertex(0,ao01, ao10, ao00, x);
                        voxels[x+1][y+1][z+1][WEST].vertex(0,ao01, ao00T, ao00, x);
                        voxels[x+1][y+1][z+1][SOUTH].vertex(0,ao10, ao00T, ao00, x);

                    }}}//}
        }

        else {
            return true;
        }
        return false;



    }
}