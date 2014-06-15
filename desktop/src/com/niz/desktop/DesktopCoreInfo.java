package com.niz.desktop;

import com.niz.CoreInfo;

/**
 * Created by niz on 15/06/2014.
 */
public class DesktopCoreInfo implements CoreInfo {
    @Override
    public boolean shouldUseThreads() {
        return true;
    }

    @Override
    public int getNumberOfCores() {
        return Runtime.getRuntime().availableProcessors();
    }
}
