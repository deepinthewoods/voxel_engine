package com.niz;

import com.artemis.World;
import com.badlogic.gdx.ApplicationListener;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.assets.AssetManager;
import com.badlogic.gdx.graphics.Camera;
import com.badlogic.gdx.graphics.GL10;
import com.badlogic.gdx.graphics.OrthographicCamera;
import com.badlogic.gdx.graphics.PerspectiveCamera;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.g3d.ModelBatch;
import com.badlogic.gdx.tests.g3d.voxel.VoxelTest;
import com.niz.factories.GameFactory;
import com.niz.factories.PlatformerFactory;

public class nizEngine implements ApplicationListener {
	private static final String TAG = "main engine";
	//private OrthographicCamera uiCamera;
	//private Camera  worldCamera;
	//private PerspectiveCamer
	//private SpriteBatch spriteBatch;
	private World world;
	private ShapeBatch shapeBatch;
	//private ModelBatch modelBatch;
	public float accumulator, timeStep = 1f/128f, minTimeStep = 1f/15f;
	protected GameFactory factory;
	protected AssetManager assets;
	private boolean assetsLoaded;
	private VoxelTest worldTest;
	@Override
	public void create() {		
		float w = Gdx.graphics.getWidth();
		float h = Gdx.graphics.getHeight();
		
		//uiCamera = new OrthographicCamera(w/h, 1);
		//worldCamera = new OrthographicCamera(w/h, 1);//= new PerspectiveCamera(60, w/h, 1);
		float widthOverHeight = (float)Gdx.graphics.getWidth()/Gdx.graphics.getHeight();
		float width = 10, height = width*widthOverHeight;
		//worldCamera = new OrthographicCamera(width, height);
		//worldCamera.update();
		
		
		//spriteBatch = new SpriteBatch();
		shapeBatch = new ShapeBatch();
		//modelBatch = new ModelBatch();
		world = new World();
		assets = new AssetManager();
		assetsLoaded = false;
		
		factory = new PlatformerFactory();
		worldTest = new VoxelTest();
		worldTest.create();
		//factory.init(world, timeStep, assets, worldTest.camera, worldTest.modelBatch, worldTest.voxelWorld);
		
		
	}

	@Override
	public void dispose() {
		//spriteBatch.dispose();
		world.dispose();
		shapeBatch.dispose();
		//worldTest.dispose();
	}

	@Override
	public void render() {		
		if(!assetsLoaded && assets.update()) {
			//factory.doneLoading(assets, world, worldTest.camera);
			assetsLoaded = true;
		}
		if (assets.getProgress() < 1f){
			Gdx.app.log(TAG, "progress"+assets.getProgress());
			return;
		}
		
		
		float delta = Gdx.graphics.getDeltaTime();
		delta = Math.min(delta, minTimeStep);
		accumulator += delta;
		while (accumulator > timeStep){
			accumulator -= timeStep;
			world.process();
		}
		//DRAW
		worldTest.render();
		
	}

	@Override
	public void resize(int width, int height) {
	}

	@Override
	public void pause() {
	}

	@Override
	public void resume() {
	}
}
