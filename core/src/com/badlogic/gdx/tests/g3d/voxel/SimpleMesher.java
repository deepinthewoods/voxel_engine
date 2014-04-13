package com.badlogic.gdx.tests.g3d.voxel;


import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.Mesh;
import com.badlogic.gdx.graphics.VertexAttribute;
import com.badlogic.gdx.math.Vector3;

public class SimpleMesher implements Mesher{
	private static final int VERTEX_SIZE = 4;
	private short[] indices;
	private byte[] voxels;
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
	private float[] vertices;

	public SimpleMesher(int width, int height, int depth) {
		this.vertices = new float[VoxelChunk.VERTEX_SIZE * 6 * VoxelWorld.CHUNK_SIZE_X * VoxelWorld.CHUNK_SIZE_Y * VoxelWorld.CHUNK_SIZE_Z*2];

		int len = VoxelWorld.CHUNK_SIZE_X * VoxelWorld.CHUNK_SIZE_Y * VoxelWorld.CHUNK_SIZE_Z * 6 * 6 / 3;
		indices = new short[len];
		short j = 0;
		int i = 0;
		for (i = 0; i < len; i += 6, j += 4) {
			indices[i + 0] = (short)(j + 0);
			indices[i + 1] = (short)(j + 1);
			indices[i + 2] = (short)(j + 2);
			indices[i + 3] = (short)(j + 2);
			indices[i + 4] = (short)(j + 3);
			indices[i + 5] = (short)(j + 0);
		}
		
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
		
	}
	@Override
	public int calculateVertices(VoxelChunk chunk,
			VoxelWorld voxelWorld, MeshBatcher batch) {
		
		int count = calculateVertices(vertices, chunk);;
		batch.flush(chunk, vertices, count, indices, this);
		

		return count ;
	}

	@Override
	public Mesh newMesh(int vertexCount) {
		Mesh mesh = new Mesh(true, 
				vertexCount, 
				vertexCount*6/VERTEX_SIZE,
				VertexAttribute.Position()//, VertexAttribute.Normal()
				, VertexAttribute.Color()
				//VertexAttribute.TexCoords(0)
				);
		return mesh;
	}

	/**
	 * Creates a mesh out of the chunk, returning the number of
	 * indices produced
	 * @return the number of vertices produced
	 */
	public int calculateVertices(float[] vertices, VoxelChunk c) {
		int i = 0;
		Vector3 offset = c.offset;

		int vertexOffset = 0;
		for(int y = 0; y < c.height; y++) {
			for(int z = 0; z < c.depth; z++) {
				for(int x = 0; x < c.width; x++, i++) {
					BlockDefinition def = VoxelChunk.blockDef(c.getFast(x, y, z));//getDefFromIndex(i);
					if(def.isEmpty) continue;
					
					
					//Gdx.app.log("voxelChunk", "def"+voxel);
					if(y < height - 1) {
						if(voxels[i+topOffset] <= 0) vertexOffset = createTop(offset, x, y, z, def, vertices, vertexOffset);
					} else {
						vertexOffset = createTop(offset, x, y, z, def, vertices, vertexOffset);
					}
					if(y > 0) {
						
						if(voxels[i+bottomOffset] <= 0) vertexOffset = createBottom(offset, x, y, z, def, vertices, vertexOffset);
					} else {
						vertexOffset = createBottom(offset, x, y, z, def, vertices, vertexOffset);
					}
					if(x > 0) {
						
						//Gdx.app.log("voxelChunk", "def"+voxel);
						if(voxels[i+leftOffset] <= 0) vertexOffset = createLeft(offset, x, y, z, def, vertices, vertexOffset);
					} else {
						vertexOffset = createLeft(offset, x, y, z, def, vertices, vertexOffset);
					}
					if(x < width - 1) {
						//Gdx.app.log("voxelChunk", "def"+voxel);
						if(voxels[i+rightOffset] <= 0) vertexOffset = createRight(offset, x, y, z, def, vertices, vertexOffset);
					} else {
						vertexOffset = createRight(offset, x, y, z, def, vertices, vertexOffset);
					}
					if(z > 0) {
						//Gdx.app.log("voxelChunk", "def"+voxel);
						if(voxels[i+frontOffset] <= 0) vertexOffset = createFront(offset, x, y, z, def, vertices, vertexOffset);
					} else {
						vertexOffset = createFront(offset, x, y, z, def, vertices, vertexOffset);
					}
					if(z < depth - 1) {
						//Gdx.app.log("voxelChunk", "def"+voxel);
						if(voxels[i+backOffset] <= 0) vertexOffset = createBack(offset, x, y, z, def, vertices, vertexOffset);
					} else {
						vertexOffset = createBack(offset, x, y, z, def, vertices, vertexOffset);
					}
				}
			}
		}
		return vertexOffset;
	}

	public static int createTop(Vector3 offset, int x, int y, int z, BlockDefinition def, float[] vertices, int vertexOffset) {
		int  side =  BlockDefinition.TOP;
		float[] uvs = def.getUVs(side);
		float u = uvs[0], v = uvs[1], 
				u2 = uvs[2], v2 = uvs[3];
		float c = Color.WHITE.toFloatBits();
		vertices[vertexOffset++] = offset.x + x;
		vertices[vertexOffset++] = offset.y + y + 1;
		vertices[vertexOffset++] = offset.z + z;
		//vertices[vertexOffset++] = 0;
		//vertices[vertexOffset++] = 1;
		//vertices[vertexOffset++] = 0;
		vertices[vertexOffset++] = c;//lightColors[lightAverages[yp]+TOP_FACE_BRIGHTNESS_BONUS];//(light(c[BlockDefinition.TOP])+light(c[BlockDefinition.LEFT])+light(c[BlockDefinition.BACK]))/3];//lightColors[(light(c[BlockDefinition.TOP]))];//
		//vertices[vertexOffset++] = u;
		//vertices[vertexOffset++] = v;
		
		vertices[vertexOffset++] = offset.x + x + 1;
		vertices[vertexOffset++] = offset.y + y + 1;
		vertices[vertexOffset++] = offset.z + z;
		//vertices[vertexOffset++] = 0;
		//vertices[vertexOffset++] = 1;
		//vertices[vertexOffset++] = 0;
		vertices[vertexOffset++] = c;//lightColors[lightAverages[xp+yp]+TOP_FACE_BRIGHTNESS_BONUS];//(light(c[BlockDefinition.TOP])+light(block)/2];//lightColors[(light(c[BlockDefinition.TOP]))];//
		//vertices[vertexOffset++] = u2;
		//vertices[vertexOffset++] = v;
		//Gdx.app.log("chunk", ""+(c[BlockDefinition.TOP]+c[BlockDefinition.RIGHT]+c[BlockDefinition.BACK])/3);
		vertices[vertexOffset++] = offset.x + x + 1;
		vertices[vertexOffset++] = offset.y + y + 1;
		vertices[vertexOffset++] = offset.z + z + 1;
		//vertices[vertexOffset++] = 0;
		//vertices[vertexOffset++] = 1;
		//vertices[vertexOffset++] = 0;
		vertices[vertexOffset++] = c;
		//vertices[vertexOffset++] = u2;
		//vertices[vertexOffset++] = v2;
		//Gdx.app.log("chunk", ""+(c[BlockDefinition.TOP]+c[BlockDefinition.RIGHT]+c[BlockDefinition.FRONT])/3);
		vertices[vertexOffset++] = offset.x + x;
		vertices[vertexOffset++] = offset.y + y + 1;
		vertices[vertexOffset++] = offset.z + z + 1;
//		vertices[vertexOffset++] = 0;
//		vertices[vertexOffset++] = 1;
//		vertices[vertexOffset++] = 0;
		vertices[vertexOffset++] = c;
//		vertices[vertexOffset++] = u;
//		vertices[vertexOffset++] = v2;
		//Gdx.app.log("chunk", ""+(c[BlockDefinition.TOP]+c[BlockDefinition.LEFT]+c[BlockDefinition.FRONT])/3);
		//flipVertices(yp, xp+yp, xp+yp+zp, yp+zp, vertices, vertexOffset, lightAverages);
		return vertexOffset;
	}
	
	public static int createBottom(Vector3 offset, int x, int y, int z, BlockDefinition def, float[] vertices, int vertexOffset) {
		int  side =  BlockDefinition.BOTTOM;
		float[] uvs = def.getUVs(side);
		float u = uvs[0], v = uvs[1], 
				u2 = uvs[2], v2 = uvs[3];
		float c = Color.WHITE.toFloatBits();

		vertices[vertexOffset++] = offset.x + x;
		vertices[vertexOffset++] = offset.y + y;//d
		vertices[vertexOffset++] = offset.z + z;
//		vertices[vertexOffset++] = 0;
//		vertices[vertexOffset++] = -1;
//		vertices[vertexOffset++] = 0;
		vertices[vertexOffset++] = c;
//		vertices[vertexOffset++] = u;
//		vertices[vertexOffset++] = v;
		
		vertices[vertexOffset++] = offset.x + x;
		vertices[vertexOffset++] = offset.y + y;
		vertices[vertexOffset++] = offset.z + z + 1;
//		vertices[vertexOffset++] = 0;
//		vertices[vertexOffset++] = -1;
//		vertices[vertexOffset++] = 0;
		vertices[vertexOffset++] = c;
//		vertices[vertexOffset++] = u2;
//		vertices[vertexOffset++] = v;
		
		vertices[vertexOffset++] = offset.x + x + 1;
		vertices[vertexOffset++] = offset.y + y;
		vertices[vertexOffset++] = offset.z + z + 1;
//		vertices[vertexOffset++] = 0;
//		vertices[vertexOffset++] = -1;
//		vertices[vertexOffset++] = 0;
		vertices[vertexOffset++] = c;
//		vertices[vertexOffset++] = u2;
//		vertices[vertexOffset++] = v2;
		
		vertices[vertexOffset++] = offset.x + x + 1;
		vertices[vertexOffset++] = offset.y + y;
		vertices[vertexOffset++] = offset.z + z;
//		vertices[vertexOffset++] = 0;
//		vertices[vertexOffset++] = -1;
//		vertices[vertexOffset++] = 0;
		vertices[vertexOffset++] = c;
//		vertices[vertexOffset++] = u;
//		vertices[vertexOffset++] = v2;
		//flipVertices(0, zp, xp+zp, xp, vertices, vertexOffset, lightAverages);
		return vertexOffset;
	}
	
	public static int createLeft(Vector3 offset, int x, int y, int z, BlockDefinition def, float[] vertices, int vertexOffset) {
		int  side =  BlockDefinition.LEFT;
		float[] uvs = def.getUVs(side);
		float u = uvs[0], v = uvs[1], 
				u2 = uvs[2], v2 = uvs[3];
		float c = Color.WHITE.toFloatBits();

		vertices[vertexOffset++] = offset.x + x;
		vertices[vertexOffset++] = offset.y + y;
		vertices[vertexOffset++] = offset.z + z;
//		vertices[vertexOffset++] = -1;
//		vertices[vertexOffset++] = 0;
//		vertices[vertexOffset++] = 0;
		vertices[vertexOffset++] = c;
//		vertices[vertexOffset++] = u;
//		vertices[vertexOffset++] = v2;
		
		
		vertices[vertexOffset++] = offset.x + x;
		vertices[vertexOffset++] = offset.y + y + 1;
		vertices[vertexOffset++] = offset.z + z;
//		vertices[vertexOffset++] = -1;
//		vertices[vertexOffset++] = 0;
//		vertices[vertexOffset++] = 0;
		vertices[vertexOffset++] = c;
//		vertices[vertexOffset++] = u;
//		vertices[vertexOffset++] = v;
		
		vertices[vertexOffset++] = offset.x + x;
		vertices[vertexOffset++] = offset.y + y + 1;
		vertices[vertexOffset++] = offset.z + z + 1;
//		vertices[vertexOffset++] = -1;
//		vertices[vertexOffset++] = 0;
//		vertices[vertexOffset++] = 0;
		vertices[vertexOffset++] = c;
//		vertices[vertexOffset++] = u2;
//		vertices[vertexOffset++] = v;
		
		vertices[vertexOffset++] = offset.x + x;
		vertices[vertexOffset++] = offset.y + y;
		vertices[vertexOffset++] = offset.z + z + 1;
//		vertices[vertexOffset++] = -1;
//		vertices[vertexOffset++] = 0;
//		vertices[vertexOffset++] = 0;
		vertices[vertexOffset++] = c;
//		vertices[vertexOffset++] = u2;
//		vertices[vertexOffset++] = v2;
		//flipVertices(0, yp, yp+zp, zp, vertices, vertexOffset, lightAverages);
		return vertexOffset;
	}
	
	public static int createRight(Vector3 offset, int x, int y, int z, BlockDefinition def, float[] vertices, int vertexOffset) {
		int  side =  BlockDefinition.RIGHT;
		float[] uvs = def.getUVs(side);
		float u = uvs[0], v = uvs[1], 
				u2 = uvs[2], v2 = uvs[3];
		float c = Color.WHITE.toFloatBits();

		vertices[vertexOffset++] = offset.x + x + 1;
		vertices[vertexOffset++] = offset.y + y;
		vertices[vertexOffset++] = offset.z + z;
//		vertices[vertexOffset++] = 0;
//		vertices[vertexOffset++] = 1;
//		vertices[vertexOffset++] = 0;
		vertices[vertexOffset++] = c;
//		vertices[vertexOffset++] = u2;
//		vertices[vertexOffset++] = v2;
		
		vertices[vertexOffset++] = offset.x + x + 1;
		vertices[vertexOffset++] = offset.y + y;
		vertices[vertexOffset++] = offset.z + z + 1;
//		vertices[vertexOffset++] = 0;
//		vertices[vertexOffset++] = 1;
//		vertices[vertexOffset++] = 0;
		vertices[vertexOffset++] = c;
//		vertices[vertexOffset++] = u;
//		vertices[vertexOffset++] = v2;
		
		vertices[vertexOffset++] = offset.x + x + 1;
		vertices[vertexOffset++] = offset.y + y + 1;
		vertices[vertexOffset++] = offset.z + z + 1;
//		vertices[vertexOffset++] = 0;
//		vertices[vertexOffset++] = 1;
//		vertices[vertexOffset++] = 0;
		vertices[vertexOffset++] = c;
//		vertices[vertexOffset++] = u;
//		vertices[vertexOffset++] = v;
		
		vertices[vertexOffset++] = offset.x + x + 1;
		vertices[vertexOffset++] = offset.y + y + 1;
		vertices[vertexOffset++] = offset.z + z;
//		vertices[vertexOffset++] = 0;
//		vertices[vertexOffset++] = 1;
//		vertices[vertexOffset++] = 0;
		vertices[vertexOffset++] = c;
//		vertices[vertexOffset++] = u2;
//		vertices[vertexOffset++] = v;
		//flipVertices(xp, xp+zp, xp+yp+zp, xp+yp, vertices, vertexOffset, lightAverages);
		return vertexOffset;
		
	}
	
	//n
	public static int createFront(Vector3 offset, int x, int y, int z, BlockDefinition def, float[] vertices, int vertexOffset) {
		int  side =  BlockDefinition.FRONT;
		float[] uvs = def.getUVs(side);
		float u = uvs[0], v = uvs[1], 
				u2 = uvs[2], v2 = uvs[3];
		float c = Color.WHITE.toFloatBits();

		vertices[vertexOffset++] = offset.x + x;
		vertices[vertexOffset++] = offset.y + y;
		vertices[vertexOffset++] = offset.z + z;
//		vertices[vertexOffset++] = 0;
//		vertices[vertexOffset++] = 0;
//		vertices[vertexOffset++] = 1;
		vertices[vertexOffset++] = c;
//		vertices[vertexOffset++] = u;
//		vertices[vertexOffset++] = v2;
		//wus
		vertices[vertexOffset++] = offset.x + x + 1;
		vertices[vertexOffset++] = offset.y + y;
		vertices[vertexOffset++] = offset.z + z;
//		vertices[vertexOffset++] = 0;
//		vertices[vertexOffset++] = 0;
//		vertices[vertexOffset++] = 1;
		vertices[vertexOffset++] = c;
//		vertices[vertexOffset++] = u2;
//		vertices[vertexOffset++] = v2;
		
		vertices[vertexOffset++] = offset.x + x + 1;
		vertices[vertexOffset++] = offset.y + y + 1;
		vertices[vertexOffset++] = offset.z + z;
//		vertices[vertexOffset++] = 0;
//		vertices[vertexOffset++] = 0;
//		vertices[vertexOffset++] = 1;
		vertices[vertexOffset++] = c;
//		vertices[vertexOffset++] = u2;
//		vertices[vertexOffset++] = v;
		
		vertices[vertexOffset++] = offset.x + x;
		vertices[vertexOffset++] = offset.y + y + 1;
		vertices[vertexOffset++] = offset.z + z;
//		vertices[vertexOffset++] = 0;
//		vertices[vertexOffset++] = 0;
//		vertices[vertexOffset++] = 1;
		vertices[vertexOffset++] = c;
//		vertices[vertexOffset++] = u;
//		vertices[vertexOffset++] = v;
		//flipVertices(0, xp, xp+yp, yp, vertices, vertexOffset, lightAverages);
		return vertexOffset;
	}
	
	public static int createBack(Vector3 offset, int x, int y, int z, BlockDefinition def, float[] vertices, int vertexOffset) {
		int  side =  BlockDefinition.BACK;
		float[] uvs = def.getUVs(side);
		float u = uvs[0], v = uvs[1], 
				u2 = uvs[2], v2 = uvs[3];
		float c = Color.WHITE.toFloatBits();

		vertices[vertexOffset++] = offset.x + x;
		vertices[vertexOffset++] = offset.y + y;
		vertices[vertexOffset++] = offset.z + z + 1;
//		vertices[vertexOffset++] = 0;
//		vertices[vertexOffset++] = 0;
//		vertices[vertexOffset++] = -1;
		vertices[vertexOffset++] = c;
//		vertices[vertexOffset++] = u;
//		vertices[vertexOffset++] = v2;
		
		vertices[vertexOffset++] = offset.x + x;
		vertices[vertexOffset++] = offset.y + y + 1;
		vertices[vertexOffset++] = offset.z + z + 1;
//		vertices[vertexOffset++] = 0;
//		vertices[vertexOffset++] = 0;
//		vertices[vertexOffset++] = -1;
		vertices[vertexOffset++] = c;
//		vertices[vertexOffset++] = u;
//		vertices[vertexOffset++] = v;
		
		vertices[vertexOffset++] = offset.x + x + 1;
		vertices[vertexOffset++] = offset.y + y + 1;
		vertices[vertexOffset++] = offset.z + z + 1;
//		vertices[vertexOffset++] = 0;
//		vertices[vertexOffset++] = 0;
//		vertices[vertexOffset++] = -1;
		vertices[vertexOffset++] = c;
//		vertices[vertexOffset++] = u2;
//		vertices[vertexOffset++] = v;
		
		vertices[vertexOffset++] = offset.x + x + 1;
		vertices[vertexOffset++] = offset.y + y;
		vertices[vertexOffset++] = offset.z + z + 1;
//		vertices[vertexOffset++] = 0;
//		vertices[vertexOffset++] = 0;
//		vertices[vertexOffset++] = -1;
		vertices[vertexOffset++] = c;
//		vertices[vertexOffset++] = u2;
//		vertices[vertexOffset++] = v2;
		//flipVertices(zp, yp+zp, xp+yp+zp, xp+zp, vertices, vertexOffset, lightAverages);
		return vertexOffset;
	}

	

}
