package com.niz.client;

import com.niz.CoreInfo;

/**
 * Created by niz on 15/06/2014.
 */
public class DummycoreInfo implements CoreInfo {
    @Override
    public boolean shouldUseThreads() {
        return false;
    }

    @Override
    public int getNumberOfCores() {
        return 1;
    }
}
