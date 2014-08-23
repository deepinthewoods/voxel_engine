package com.badlogic.gdx.tests.g3d.voxel;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.utils.Array;
import com.badlogic.gdx.utils.GdxRuntimeException;
import com.badlogic.gdx.utils.Pools;

/**
 * Created by niz on 15/06/2014.
 */
public class ChunkReadOrGenerate
        implements Runnable {
    private static final String TAG = "voxelreadorgenerate runnable ";
    ReadThread thread;

    private static final int DECOMPRESS_ITERATIONS = 8;
    VoxelBlobReader reader;

    int progressCoarse;

    Array<BlobByteArray> byteArrays = new Array<BlobByteArray>();
    private VoxelChunk chunk;
    private int progress, chunkIndexDecompressProgress, byteArrayIndexProgress;
    private VoxelWorld vw;
    byte[] currentByteArray;
    public boolean idle;

    public void begin(VoxelChunk chunk, VoxelWorld world, VoxelBlobReader reader){
        while (byteArrays.size > 0)
            Pools.free(byteArrays.pop());
        this.chunk = chunk;
        progress = 0;
        this.vw = world;
        this.reader = reader;

        boolean generate = !reader.containsChunk(chunk);
        progressCoarse = generate?3:0;

    }
    byte value;
    int run;
    @Override
    public void run() {
        //Gdx.app.log(TAG, "run "+progressCoarse);

        if (progressCoarse == -1){
            try {
                wait(10);
            } catch (InterruptedException e) {
                throw new GdxRuntimeException("error" +e.getCause());
                //e.printStackTrace();
            }
            Gdx.app.log(TAG, "waiting");
        }
        else if (progressCoarse == 0){
            if (!reader.beginRead(chunk)){
                idle = true;
                if (thread != null)
                    thread.onPause();
            }
        }
        else if (progressCoarse == 1){//read
            if (reader.processRead(byteArrays)){
                progress = 0;
                chunkIndexDecompressProgress = 0;
                byteArrayIndexProgress = 0;
                progressCoarse++;
                if (byteArrays.size == 0)
                    progressCoarse++;

            }
        } else if (progressCoarse == 2){//decompress into chunk
            //progress is how far into the bytes we are
            //chunkIndexDecompressProgress is current chunk index
            //byteArrayIndexProgress is which byte array we are one currently
            if (progress == 0){
                currentByteArray = byteArrays.get(byteArrayIndexProgress).bytes;
            } else {
                for (int i = 0; i < DECOMPRESS_ITERATIONS; i++){
                    //read value-length pair
                    value = currentByteArray[byteArrayIndexProgress++];
                    run = currentByteArray[byteArrayIndexProgress++] & 0xff;
                    for (;run > 0; run--){
                        chunk.setByIndex(chunkIndexDecompressProgress++, value);
                    }

                    if (chunkIndexDecompressProgress == chunk.voxels.length) {
                        progressCoarse = 3;
                        currentByteArray = null;
                        return;
                    }

                    progress++;
                    if (progress == currentByteArray.length){
                        progress = 0;
                        byteArrayIndexProgress++;
                        return;
                    }

                }

            }




        } else if (progressCoarse == 3){//generate chunk
            //Gdx.app.log(TAG, "generate"+chunk.offset);
            for (int x = 0; x < chunk.width; x++)
                for (int y = 0; y < chunk.height; y++)
                    for (int z = 0; z < chunk.depth; z++){
                        if (chunk.offset.y + y == 2) {
                            chunk.set(x, y, z, (byte) 1);
                            //Gdx.app.log(TAG, "solid block "+x+","+y+","+z);
                        }
                        else chunk.set(x,y,z, (byte) 0);
                    }
            chunk.setDirty(true);
            progressCoarse++;
        } else {//finished
            thread.ser.finishedRead(chunk);
            if (thread != null)
                thread.onPause();
        }
    }

}
