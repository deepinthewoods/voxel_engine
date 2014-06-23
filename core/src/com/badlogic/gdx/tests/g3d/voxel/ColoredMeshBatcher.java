package com.badlogic.gdx.tests.g3d.voxel;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.Mesh;
import com.badlogic.gdx.math.Vector3;
import com.badlogic.gdx.utils.GdxRuntimeException;

/**
 * Created by niz on 14/06/2014.
 */
public class ColoredMeshBatcher extends MeshBatcher {
    private static final String TAG = "colored batcher";
    public static Color[] blockColors;//for editing
    static {
        blockColors = new Color[256];
        for (int i = 0; i < 256; i++)
            blockColors[i] = new Color(Color.WHITE);
    }

    public ColoredMeshBatcher(int vertexSize, int indexSize, int levels) {
        super(vertexSize, indexSize, levels);
    }

    Color tmpC = new Color();



    @Override
    public void addVertices(Vector3[] vertices, int[] colorArray, short[] indexes, boolean flip, GreedyMesher.VoxelFace voxel, int width, int height) {
        //Gdx.app.log(TAG, "add verts");
        for (int i = 0; i < 4; i++){
            Vector3 v = vertices[i];
            if (colorArray[i] > 15) throw new GdxRuntimeException("light error "+colorArray[i]);
            float c;// = GreedyMesher.lightValues[colorArray[i]];//highlightColors[i];//
            float delta = colorArray[i]/15f;
            if (delta > 1f) delta = 1f;
            tmpC.set(blockColors[voxel.type]).mul(GreedyMesher.lightColors[colorArray[i]]);
            c = tmpC.toFloatBits();
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


            cachedVerts[cacheProgress++] = whiteTextureU;
            cachedVerts[cacheProgress++] = whiteTextureV;

        }

        for (int i = 0; i < 6; i++){
            cachedIndexes[indexProgress++] = (short) (indexes[i]+vertexTotal);
            //Gdx.app.log(TAG, "index  "+cachedIndexes[indexProgress-1]);

        }
        //Gdx.app.log(TAG, "index length "+vertexTotal);
        vertexTotal += 4;
    }

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
}
