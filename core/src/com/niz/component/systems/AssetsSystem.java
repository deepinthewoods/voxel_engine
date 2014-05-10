package com.niz.component.systems;

import com.artemis.systems.VoidEntitySystem;
import com.badlogic.gdx.assets.AssetManager;

/**
 * Created by niz on 09/05/2014.
 */
public class AssetsSystem extends VoidEntitySystem {

    public final AssetManager assets;

    public AssetsSystem(AssetManager assets) {
        this.assets = assets;
    }

    @Override
    protected void processSystem() {

    }
}
