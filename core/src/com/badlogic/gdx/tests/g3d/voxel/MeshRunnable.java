package com.badlogic.gdx.tests.g3d.voxel;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.utils.PauseableThread;

/**
 * Created by niz on 15/06/2014.
 */
public class MeshRunnable implements Runnable {
    private static final String TAG = "mehs runnable";
    public GreedyMesher mesher;
    public PauseableThread thread;
    public MeshBatcher batch;
    public boolean done, idle = true;
    public VoxelChunk chunk;
    private VoxelWorld world;

    public MeshRunnable(GreedyMesher mesher){
        this.mesher = mesher;

    }

    public void begin(VoxelChunk chunk, VoxelWorld world){
        mesher.begin(chunk, world);
        if (thread != null)
            thread.onResume();
        done = false;
        idle = false;
        this.chunk = chunk;
        this.world = world;
    }

    @Override
    public void run() {
        //Gdx.app.log(TAG, "run");
            if (mesher.process()){
                done = true;
                if (thread != null)
                    thread.onPause();
            }
    }

    public void end(){
        mesher.end();
        idle = true;
    }

    public void restart() {
        mesher.begin(chunk, world);
        done = false;
        idle = false;
    }
}
