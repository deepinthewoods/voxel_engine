package com.badlogic.gdx.tests.g3d.voxel;

import com.badlogic.gdx.graphics.Mesh;
import com.badlogic.gdx.graphics.VertexAttributes;

public class UberMesh{

	int size;
	
	public UberMesh(int size) {
		cachedVerts = new float[size];
		this.size = size;
	}
	float[] cachedVerts;
	int cacheProgress = 0;
	public void addVertices(float[] vertices, int length) {
		
		for (int i = 0; i < length; i++)
			cachedVerts[cacheProgress++] = vertices[i];
	}
	
	public int flushCache(Mesh mesh){
		mesh.setVertices(cachedVerts, 0, cacheProgress);
		int size = cacheProgress;
		cacheProgress = 0;
		return size;
	}

}
