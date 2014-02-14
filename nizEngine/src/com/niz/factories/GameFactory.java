package com.niz.factories;

import com.artemis.World;
import com.badlogic.gdx.assets.AssetManager;
import com.badlogic.gdx.assets.loaders.AssetLoader;
import com.badlogic.gdx.graphics.OrthographicCamera;
import com.badlogic.gdx.graphics.g3d.ModelBatch;

public abstract class GameFactory {

public abstract void init(World world, float timeStep, AssetManager assets, OrthographicCamera camera, ModelBatch modelBatch) ;

public abstract void doneLoading(AssetManager assets);

}
