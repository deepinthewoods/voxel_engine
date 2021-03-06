package com.badlogic.gdx.tests.g3d.voxel;

import com.badlogic.gdx.utils.PauseableThread;

/**
 * Created by niz on 15/06/2014.
 */
public class MeshThread extends PauseableThread {
    public final int index;
    public MeshRunnable runn;
    /**
     * Constructs a new thread setting the runnable which will be called repeatedly in a loop.
     *
     * @param runnable the runnable.
     */
    public MeshThread(MeshRunnable runnable, int index) {
        super(runnable);
        runn = runnable;
        this.index = index;
    }

    public void init() {
        runn.thread = this;
    }


}
