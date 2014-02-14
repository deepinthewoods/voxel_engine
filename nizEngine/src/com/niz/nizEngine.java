package com.niz;

import com.artemis.World;
import com.badlogic.gdx.ApplicationListener;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.assets.AssetManager;
import com.badlogic.gdx.graphics.GL10;
import com.badlogic.gdx.graphics.OrthographicCamera;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.niz.factories.GameFactory;
import com.niz.factories.PlatformerFactory;

public class nizEngine implements ApplicationListener {
	private static final String TAG = "main engine";
	private OrthographicCamera uiCamera, gameCamera;
	private SpriteBatch spriteBatch;
	private World world;
	private ShapeBatch shapeBatch;
	public float accumulator, timeStep = 1f/128f, minTimeStep = 1f/15f;
	protected GameFactory factory;
	protected AssetManager assets;
	private boolean assetsLoaded;
	@Override
	public void create() {		
		float w = Gdx.graphics.getWidth();
		float h = Gdx.graphics.getHeight();
		
		uiCamera = new OrthographicCamera(w/h, 1);
		gameCamera = new OrthographicCamera(w/h, 1);
		spriteBatch = new SpriteBatch();
		shapeBatch = new ShapeBatch();
		world = new World();
		assets = new AssetManager();
		assetsLoaded = false;
		
		factory = new PlatformerFactory();
		
		factory.init(world, timeStep, assets, null, null);
	}

	@Override
	public void dispose() {
		spriteBatch.dispose();
		world.dispose();
		shapeBatch.dispose();
	}

	@Override
	public void render() {		
		if(!assetsLoaded && assets.update()) {
			factory.doneLoading(assets);
			assetsLoaded = true;
		}
		if (assets.getProgress() < 1f){
			Gdx.app.log(TAG, "progress"+assets.getProgress());
			return;
		}
		Gdx.gl.glClearColor(1, 1, 1, 1);
		Gdx.gl.glClear(GL10.GL_COLOR_BUFFER_BIT);
		
		float delta = Gdx.graphics.getDeltaTime();
		delta = Math.min(delta, minTimeStep);
		accumulator += delta;
		while (accumulator > timeStep){
			accumulator -= timeStep;
			world.process();
		}
		//DRAW
		world.draw(delta);
		
		
		spriteBatch.setProjectionMatrix(uiCamera.combined);
		spriteBatch.begin();
		//sprite.draw(batch);
		spriteBatch.end();
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
