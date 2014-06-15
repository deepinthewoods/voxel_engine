package com.badlogic.gdx.tests.g3d.voxel;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.math.Vector3;
import com.badlogic.gdx.utils.IntIntMap;

/**
 * Created by niz on 14/06/2014.
 */
public class FacesPreprocessor implements IVoxelPreprocessor {

    private static final String TAG = "faceprocessor";
    public IntIntMap[] faces = new IntIntMap[6];

    public FacesPreprocessor(){
        for (int f = 0; f < 6; f++)
            faces[f] = new IntIntMap();
    }

    public int hash(int x, int y, int z){
        return (x << 16) + (y << 8) + z;
    }

    @Override
    public void process(GreedyMesher.VoxelFace[][][][] voxels, int w, int h, Vector3 offset) {
        for (int x = 0; x < w; x++)
            for (int y = 0; y < h; y++)
                for (int z = 0; z < w; z++){
                    int hash = hash(x+offset.x,y+offset.y,z+offset.z);

                    for (int f = 0; f < 6; f++){
                        if (faces[f].containsKey(hash)){
                            Gdx.app.log(TAG, "face"+f+"   "+x+","+y+","+z);
                            int face = faces[f].get(hash, 0);
                            if (voxels[x][y][z][f].transparent == false)
                                voxels[x][y][z][f].set(VoxelChunk.blockDef(face), f, face);
                        }
                    }

                }
    }

    public int hash(float x, float y, float z) {
        return hash((int)x, (int)y, (int)z);
    }

    public void setFace(float x, float y, float z, int face, int id) {
        Gdx.app.log(TAG, "fsetface"+(int)x);

        faces[face].put(hash(x, y, z), id);
    }
}
