package com.niz;

import com.artemis.Component;
import com.artemis.Entity;
import com.artemis.World;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Screen;
import com.badlogic.gdx.assets.AssetManager;
import com.badlogic.gdx.graphics.Camera;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.graphics.OrthographicCamera;
import com.badlogic.gdx.graphics.PerspectiveCamera;
import com.badlogic.gdx.graphics.g2d.Batch;
import com.badlogic.gdx.graphics.g2d.BitmapFont;
import com.badlogic.gdx.graphics.g3d.Environment;
import com.badlogic.gdx.graphics.g3d.ModelBatch;
import com.badlogic.gdx.graphics.g3d.attributes.ColorAttribute;
import com.badlogic.gdx.graphics.g3d.environment.DirectionalLight;
import com.badlogic.gdx.scenes.scene2d.Stage;
import com.badlogic.gdx.scenes.scene2d.ui.Skin;
import com.badlogic.gdx.utils.Array;
import com.niz.component.AABBBody;
import com.niz.component.ActionComponent;
import com.niz.component.Move;
import com.niz.factories.GameFactory;
import com.niz.factories.PlatformerFactory;

public class EngineScreen implements Screen{

	
	

	

	@Override
	public void show() {
	}

	@Override
	public void hide() {
	}

	@Override
	public void pause() {
	}

	@Override
	public void resume() {
	}

	@Override
	public void dispose() {
		world.dispose();
		shapeBatch.dispose();
		assets.dispose();
	}
	
	private static final String TAG = "main engine";
	//private OrthographicCamera uiCamera;
	//private Camera  worldCamera;
	//private PerspectiveCamer
	private Batch spriteBatch;
	private World world;
	private ShapeBatch shapeBatch;
	//private ModelBatch modelBatch;
	public static float accumulator, timeStep = 1f/128f, minTimeStep = 1f/15f;
	protected GameFactory factory;
	protected AssetManager assets;
	private boolean assetsLoaded;
	private ModelBatch modelBatch;
	
	private Camera camera, shapeCamera;
	private Skin skin;
	private Stage stage;
	private BitmapFont font;
	private Environment env;
	public static Entity player;
	public static int tick;
	public NizEngine game;
	
	public EngineScreen(NizEngine nizEngine) {	
		game = nizEngine;
		float w = Gdx.graphics.getWidth();
		float h = Gdx.graphics.getHeight();
		
		skin = new Skin(Gdx.files.internal("data/skin/Holo-dark-mdpi.json"));
		stage = new Stage();
		spriteBatch = stage.getSpriteBatch();
		//spriteBatch = new SpriteBatch();
		shapeBatch = new ShapeBatch();
		world = new World();
		world.getInputMux().addProcessor(stage);
		assets = new AssetManager();
		assetsLoaded = false;
		
		factory = new PlatformerFactory();
		modelBatch = new ModelBatch();
		shapeCamera = new OrthographicCamera(Gdx.graphics.getWidth()/ Gdx.graphics.getHeight(), 1);
		//shapeCamera.update();
		camera = new PerspectiveCamera(67, Gdx.graphics.getWidth(), Gdx.graphics.getHeight());
		//camera = new OrthographicCamera(20f,15f);
		camera.near = 0.5f;
		camera.far = 1000f;
		camera.update();
		
		env = new Environment();
		env.set(new ColorAttribute(ColorAttribute.AmbientLight, 1f, 1f, 1f, 1f));
		//env.add(new DirectionalLight().set(1, 1, 1, 0, -1, .5f));
		
		//orthoCamera = new OrthographicCamera();
		
		factory.register(world, assets, camera);
		
		factory.initMenu(world, skin, stage, camera);
		//worldTest.create(camera);
		font = new BitmapFont();
	}

	

	@Override
	public void render(float delta) {		
		if(!assetsLoaded && assets.update()) {
			factory.init(timeStep, world, env, assets, camera, modelBatch, shapeBatch);
			assetsLoaded = true;
		}
		if (assets.getProgress() < 1f){
			//Gdx.app.log(TAG, "progress"+assets.getProgress());
			return;
		}
		//float delta = Gdx.graphics.getDeltaTime();
		delta = Math.min(delta, minTimeStep);
		accumulator += delta;
		while (accumulator > timeStep){
			accumulator -= timeStep;
			world.process();
			tick++;
		}
		stage.act(delta);
		//DRAW

		Gdx.gl.glClearColor(0.1f, 0.1f, .5f, 1f);
		Gdx.gl.glClear(GL20.GL_COLOR_BUFFER_BIT | GL20.GL_DEPTH_BUFFER_BIT);
		
		stage.draw();
		
		modelBatch.begin(camera);
		//worldTest.render(modelBatch);
		world.draw(delta);
		modelBatch.end();
		
		spriteBatch.begin();
		Array<Component> array = new Array<Component>();
		player.getComponents(array );
		for (int i = 0; i < array.size; i++)
		font.draw(spriteBatch, array.get(i).getClass().getSimpleName()
				, 10, 40+i*20);
		
		if (player.getComponent(AABBBody.class) != null)
		font.draw(spriteBatch, "fps: " + Gdx.graphics.getFramesPerSecond() + ",   "+
		//player.getComponentBits()
		//.get(ActionComponent.class)
		//.action.actions.getRoot().getNext()
		(player.get(AABBBody.class).onGround?"onGround  ":"")+
		//+(player.get(AABBBody.class).wasOnGround?"wasOnGround  ":"")
		(player.get(AABBBody.class).onWall?"onWall  ":"")
		+(player.get(Move.class).moving?"moving":"")
	//			+(player.get(AABBBody.class).wasOnWall?"wasOnWall":"")

		, 0, 20);
		
		if (player.getComponent(AABBBody.class) != null)
			font.draw(spriteBatch, " "+
			player//.getComponentBits()
			.get(ActionComponent.class)
			.action.actions.getRoot().getNext().getClass().getSimpleName()
			//player.get(AABBBody.class).onWall
			, 280, 20);
	
		spriteBatch.end();
		shapeBatch.draw(shapeCamera, camera);
		
	}

	@Override
	public void resize(int width, int height) {
		spriteBatch.getProjectionMatrix().setToOrtho2D(0, 0, width, height);
		camera.viewportWidth = width;
		camera.viewportHeight = height;
		camera.update();
	
	}

}
