package com.badlogic.gdx.tests.g3d.voxel;

import com.badlogic.gdx.utils.PauseableThread;
import com.niz.component.systems.VoxelSerializingSystem;

/**
 * Created by niz on 15/06/2014.
 */
public class ReadThread extends PauseableThread {
    public ChunkReadOrGenerate runn;
    public float time;
    protected VoxelSerializingSystem ser;

    /**
     * Constructs a new thread setting the runnable which will be called repeatedly in a loop.
     *
     * @param runnable the runnable.
     */
    public ReadThread(ChunkReadOrGenerate runnable) {
        super(runnable);
        runn = runnable;

    }

    @Override
    public void onPause() {
        time = 0;
        super.onPause();
    }

    public void init(VoxelSerializingSystem voxelSerializingSystem) {

        this.ser = voxelSerializingSystem;
        runn.thread = this;
    }
}
