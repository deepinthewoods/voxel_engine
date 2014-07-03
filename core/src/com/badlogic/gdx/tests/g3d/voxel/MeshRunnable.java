package com.badlogic.gdx.tests.g3d.voxel;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.utils.GdxRuntimeException;
import com.badlogic.gdx.utils.PauseableThread;

/**
 * Created by niz on 15/06/2014.
 */
public class MeshRunnable implements Runnable {
    private static final String TAG = "mehs runnable";
    public GreedyMesher mesher;
    public MeshThread thread;
    public MeshBatcher batch;
    public boolean done, idle = true;
    public VoxelChunk chunk;
    private VoxelWorld world;

    public MeshRunnable(GreedyMesher mesher){
        this.mesher = mesher;

    }

    public void begin(VoxelChunk chunk, VoxelWorld world){
        mesher.begin(chunk, world);
        chunk.setDirty(false);



        done = false;
        idle = false;
        this.chunk = chunk;
        this.world = world;
        thread.onResume();
    }

    @Override
    public void run() {
            if (done){
               thread.onPause();
                //Gdx.app.log(TAG, "PAUSE");
                return;
            }
            if (mesher.process()){
                done = true;
                //Gdx.app.log(TAG, "DONEDONEDONEDONEDONEDONEDONEDONEDONEDONEDONEDONEDONEDONEDONEDONEDONEDONEDONE" +thread.index);
                thread.onPause();



                //else throw new GdxRuntimeException("hjksfhadkl");
            }
    }



    public void restart() {
        mesher.begin(chunk, world);
        done = false;
        idle = false;
    }
}
