package com.badlogic.gdx.tests.g3d.voxel;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.files.FileHandle;
import com.badlogic.gdx.graphics.*;
import com.badlogic.gdx.math.Vector3;
import com.badlogic.gdx.utils.Array;
import com.badlogic.gdx.utils.IntIntMap;

/**
 * Created by niz on 14/06/2014.
 */
public class TextureCreationBatcher implements IVoxelPreprocessor, MeshBatch {
    private static final String TAG = "texture creation preprocessor/batcher";
    public FacesPreprocessor facesPre = new FacesPreprocessor();
    public String path = "data/blocks/test";
    public IntIntMap[] faces = new IntIntMap[6];

    public TextureCreationBatcher() {
        for (int i = 0; i < 6; i++)
            faces[i] = new IntIntMap();
    }

    @Override
    public void process(GreedyMesher.VoxelFace[][][][] voxels, int w, int h, Vector3 offset) {
//        facesPre.process(voxels, w, h, offset);
        for (int f = 0; f < 6; f++){
            faces[f].clear();
        }
        BlockDefinition def = VoxelChunk.blockDef(1);
        for (int x = 0; x < w; x++)
            for (int y = 0; y < h; y++)
                for (int z = 0; z < w; z++){
                    int hash = facesPre.hash(x + offset.x, y + offset.y, z + offset.z);

                    for (int f = 0; f < 6; f++){

                        if (facesPre.faces[f].containsKey(hash)){
                            Gdx.app.log(TAG, "face"+f+"   "+x+","+y+","+z);
                            int face = facesPre.faces[f].get(hash, 0);
                            faces[f].put(facesPre.hash(x,y,z), face);
                        } else {
                            int face = voxels[x][y][z][f].type;
                            faces[f].put(facesPre.hash(x,y,z), face);
                        }
                        GreedyMesher.VoxelFace aFace = voxels[x][y][z][f];
                        //if (!aFace.transparent)
                            aFace.set(def, f, 1);
                    }

                }
    }

    @Override
    public int flushCache(VoxelChunk chunk, GreedyMesher greedyMesher) {
        for (int i = 0; i < pixmaps.size; i++){
            FileHandle file = Gdx.files.external(path+i+".png");
            PixmapIO.writePNG(file, pixmaps.get(i));
        }
        pixmaps.clear();

        return 0;
    }
    @Override
    public Mesh newMesh(int size) {
        Mesh mesh = new Mesh(true,
                size,
                size / 4 * 6,
                VertexAttribute.Position(),
                VertexAttribute.Color(),
                VertexAttribute.TexCoords(0),
                new VertexAttribute(VertexAttributes.Usage.Generic, 2, "a_texStart")
        );


        return mesh;
    }

    @Override
    public void quad(Vector3 bottomLeft, Vector3 topLeft, Vector3 topRight, Vector3 bottomRight, int width, int height, GreedyMesher.VoxelFace voxel, boolean backFace) {
        bottomLeft.sub(BlockDefinition.reflectedNormals[voxel.side]);
        topRight.sub(BlockDefinition.reflectedNormals[voxel.side]);
        float sx =  Math.min(bottomLeft.x, topRight.x);
        float sy =  Math.min(bottomLeft.y, topRight.y);
        float sz =  Math.min(bottomLeft.z, topRight.z);
        float ex =  Math.max(bottomLeft.x, topRight.x);
        float ey =  Math.max(bottomLeft.y, topRight.y);
        float ez =  Math.max(bottomLeft.z, topRight.z);
        Gdx.app.log(TAG, "quad "+bottomLeft+"    "+topRight);

        startTexture(width, height);
        boolean bx = true;
        for (float x = sx; x < ex || bx; x++, bx = false){
            boolean by = true;
            for (float y =  sy; y < ey || by; y++, by = false){
                boolean bz = true;
                for (float z =  sz; z < ez || bz; z++, bz = false){
                    writePixel((int)x, (int)y, (int)z, voxel.side, faces[voxel.side].get(facesPre.hash(x, y, z), 0));
                   // Gdx.app.log(TAG, "pixel "+x+","+y+","+z);

                }
            }
        }
        endTexture();
    }

    private int textureHeight, textureWidth;
    private Pixmap pixmap;
    private Array<Pixmap> pixmaps = new Array<Pixmap>();
    private void startTexture(int w, int h){
        pixmap = new Pixmap(w, h, Pixmap.Format.RGBA8888);
        textureWidth = w;
        textureHeight = h;
    }

    private void endTexture(){
        pixmaps.add(pixmap);
        pixmap = null;
    }

    private void writePixel(int x, int y, int z, int side, int id) {
        int color = ColoredMeshBatcher.blockColors[id].toIntBits();
        switch (side){
            case BlockDefinition.LEFT:
                pixmap.drawPixel(y,z,color);
                break;
            case BlockDefinition.RIGHT:
                pixmap.drawPixel(y,z,color);
                break;
            case BlockDefinition.TOP:
                pixmap.drawPixel(x,z,color);
                break;
            case BlockDefinition.BOTTOM:
                pixmap.drawPixel(textureWidth-1-x,z,color);
                break;
            case BlockDefinition.FRONT:
                pixmap.drawPixel(x,y,color);
                break;
            case BlockDefinition.BACK:
                pixmap.drawPixel(x,y,color);
                break;
        }
    }
}
