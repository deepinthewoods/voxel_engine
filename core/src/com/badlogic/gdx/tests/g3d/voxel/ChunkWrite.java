package com.badlogic.gdx.tests.g3d.voxel;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.utils.Array;
import com.badlogic.gdx.utils.GdxRuntimeException;
import com.badlogic.gdx.utils.Pools;

/**
 * Created by niz on 15/06/2014.
 */
public class ChunkWrite implements Runnable {
    private static final int COMPRESS_ITERATIONS = 16;
    private static final String TAG = "Voxel Write Runnable";
    WriteThread thread;
    VoxelBlobReader reader;
    private boolean done;
    public boolean idle;
    private VoxelChunk chunk;
    private VoxelWorld vw;
    private int progress, progressCoarse, arrayProgress;
    private Array<BlobByteArray> byteArrays = new Array<BlobByteArray>();
    byte[] currentArray;

    public void begin(VoxelChunk chunk, VoxelWorld world, VoxelBlobReader reader){
        if (thread != null)
            thread.onResume();
        done = false;
        idle = false;
        this.chunk = chunk;
        progress = 0;
        progressCoarse = 0;
        arrayProgress = 0;
        this.vw = world;
        this.reader = reader;
    }
    byte value, newVal;
    int run;
    @Override
    public void run() {
        //Gdx.app.log(TAG, "run");
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
            progressCoarse++;
        }
        else if (progressCoarse == 1){//RLE
            //progress is current voxel index
            if (progress == 0){
                BlobByteArray blobArr = Pools.obtain(BlobByteArray.class);
                currentArray = blobArr.bytes;
                byteArrays.add(blobArr);
                run = 0;
                value = chunk.getByIndex(0);
            }
            for (int i = 0; i < COMPRESS_ITERATIONS; i++){
                newVal = chunk.getByIndex(progress+i);
                if (newVal != value){
                    currentArray[arrayProgress++] = (byte) run;
                    currentArray[arrayProgress++] = value;
                    run = 1;
                    value = newVal;
                    checkForEndOfArray();
                } else {
                    run++;
                    if (run == 256){
                        currentArray[arrayProgress++] = (byte) 255;
                        currentArray[arrayProgress++] = value;
                        checkForEndOfArray();
                        run = 1;
                    }
                }
            }

        } else if (progressCoarse == 2){
            if (progress == 0){
                if (!reader.beginWrite(chunk, byteArrays)){
                    //unsuccessful, need to wait and try again
                    idle = true;
                    if (thread != null)
                        thread.onPause();
                } else progress++;
                return;
            }
            if (reader.processWrite(byteArrays)){
                done = true;
                thread.ser.finishedWrite(chunk);
                if (thread != null)
                    thread.onPause();
            }
        }

    }

    private void checkForEndOfArray() {
        if (arrayProgress >= currentArray.length){
            arrayProgress = 0;
            BlobByteArray blobArr = Pools.obtain(BlobByteArray.class);
            currentArray = blobArr.bytes;
            byteArrays.add(blobArr);
        }
    }


}
