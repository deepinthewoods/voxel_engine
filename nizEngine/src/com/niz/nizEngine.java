package com.niz;

import com.artemis.World;
import com.badlogic.gdx.ApplicationListener;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.assets.AssetManager;
import com.badlogic.gdx.graphics.Camera;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.graphics.PerspectiveCamera;
import com.badlogic.gdx.graphics.g2d.TextureAtlas;
import com.badlogic.gdx.graphics.g3d.ModelBatch;
import com.badlogic.gdx.scenes.scene2d.Stage;
import com.badlogic.gdx.scenes.scene2d.ui.Skin;
import com.niz.factories.GameFactory;
import com.niz.factories.PlatformerFactory;

public class NizEngine implements ApplicationListener {
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
	private ModelBatch modelBatch;
	private Camera camera;
	private Skin skin;
	private Stage stage;
	@Override
	public void create() {		
		float w = Gdx.graphics.getWidth();
		float h = Gdx.graphics.getHeight();
		
		skin = new Skin(Gdx.files.internal("data/skin/Holo-dark-mdpi.json"));
		stage = new Stage();
		//spriteBatch = new SpriteBatch();
		shapeBatch = new ShapeBatch();
		world = new World();
		world.getInputMux().addProcessor(stage);
		assets = new AssetManager();
		assetsLoaded = false;
		
		factory = new PlatformerFactory();
		modelBatch = new ModelBatch();
		camera = new PerspectiveCamera(67, Gdx.graphics.getWidth(), Gdx.graphics.getHeight());
		//camera = new OrthographicCamera(20f,15f);
		camera.near = 0.5f;
		camera.far = 1000f;
		camera.update();
		
		//orthoCamera = new OrthographicCamera();
		
		factory.init(world, assets, camera);
		
		factory.initMenu(world, skin, stage);
		//worldTest.create(camera);
		
	}

	@Override
	public void dispose() {
		//spriteBatch.dispose();
		world.dispose();
		shapeBatch.dispose();
		assets.dispose();
		//worldTest.dispose();
	}

	@Override
	public void render() {		
		if(!assetsLoaded && assets.update()) {
			factory.doneLoading(timeStep, world, assets, camera, modelBatch);
			assetsLoaded = true;
			//factory.newGame(world);
			//assets.dispose();
			//assets = null;
		}
		if (assets.getProgress() < 1f){
			//Gdx.app.log(TAG, "progress"+assets.getProgress());
			return;
		}
		
		//camera.update();
		//Gdx.app.log(TAG,  "camera"+camera.position);
		float delta = Gdx.graphics.getDeltaTime();
		delta = Math.min(delta, minTimeStep);
		accumulator += delta;
		while (accumulator > timeStep){
			accumulator -= timeStep;
			world.process();
		}
		stage.act(delta);
		//DRAW
		//camera.
		Gdx.gl.glClearColor(0f, 0f, 0f, 1f);
		Gdx.gl.glClear(GL20.GL_COLOR_BUFFER_BIT | GL20.GL_DEPTH_BUFFER_BIT);
		
		stage.draw();
		
		modelBatch.begin(camera);
		//worldTest.render(modelBatch);
		world.draw(delta);
		modelBatch.end();
		
	}

	@Override
	public void resize(int width, int height) {
		camera.viewportWidth = width;
		camera.viewportHeight = height;
		camera.update();
	
	}

	@Override
	public void pause() {
	}

	@Override
	public void resume() {
	}
}
