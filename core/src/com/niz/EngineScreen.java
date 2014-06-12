package com.niz;

import com.artemis.World;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.InputMultiplexer;
import com.badlogic.gdx.InputProcessor;
import com.badlogic.gdx.Screen;
import com.badlogic.gdx.assets.AssetManager;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.graphics.g2d.*;
import com.badlogic.gdx.scenes.scene2d.Actor;
import com.badlogic.gdx.scenes.scene2d.Stage;
import com.badlogic.gdx.scenes.scene2d.ui.*;
import com.badlogic.gdx.scenes.scene2d.utils.BaseDrawable;
import com.badlogic.gdx.scenes.scene2d.utils.NinePatchDrawable;
import com.badlogic.gdx.utils.Array;
import com.badlogic.gdx.utils.viewport.ExtendViewport;
import com.badlogic.gdx.utils.viewport.ScreenViewport;
import com.niz.factories.GameFactory;
import com.niz.factories.GeneralFactory;
import com.niz.ui.SlideColorPicker;

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
		assets.dispose();
	}
	
	private static final String TAG = "main engine";
	//private OrthographicCamera uiCamera;
	//private Camera  worldCamera;
	//private PerspectiveCamer
	private Batch spriteBatch;
	private World world;
	//private ShapeBatch shapeBatch;
	public static float accumulator, timeStep = 1f/128f, minTimeStep = 1f/15f;
	protected GameFactory factory;
	protected AssetManager assets;
	//private boolean assetsLoaded;
	//private ModelBatch modelBatch;

	private Skin skin;
	private Stage stage;
	private BitmapFont font;
	//private Environment env;
	//public static Entity player;
	public static int tick;
	public NizMain game;
	
	public EngineScreen(NizMain nizEngine) {
		game = nizEngine;
		float w = Gdx.graphics.getWidth();
		float h = Gdx.graphics.getHeight();
		skin = new Skin();
		stage = new Stage(new ScreenViewport());
		spriteBatch = new SpriteBatch();
        //stage.getSpriteBatch().getProjectionMatrix().setToOrtho2D(0,0,w,h);
		//spriteBatch = new SpriteBatch();
		world = new World();

        InputMultiplexer inputMux = new InputMultiplexer();
        inputMux.addProcessor(new ScrollAdapter());
        inputMux.addProcessor(stage);
        Gdx.input.setInputProcessor(inputMux);

        assets = new AssetManager();
        assets.load("data/tiles.pack", TextureAtlas.class);
        while (!assets.update());

        makeSkin(skin, assets.get("data/tiles.pack", TextureAtlas.class));

        //assetsLoaded = false;
		
		factory = new GeneralFactory();

		//factory.assets(world, assets);
        factory.initMenu(world, skin, stage, assets, 1f);

		//worldTest.create(camera);
		font = new BitmapFont();
	}

    private void makeSkin(Skin skin, TextureAtlas atlas) {
        BitmapFont font = new BitmapFont(Gdx.files.internal("data/font/dpcomic-16.fnt"), atlas.findRegion("fonts"));//, Gdx.files.internal("data/font/fonts.png"));
        font.setScale(2f);
        TextureRegion reg = atlas.findRegion("button");
        NinePatchDrawable patch = new NinePatchDrawable(new NinePatch(reg));
        NinePatchDrawable up = new NinePatchDrawable(new NinePatch(reg));
        float border = Gdx.graphics.getHeight()/50f;
        up.getPatch().setColor(Colors.DARK_BLUE);

        setBorder(up, border);
        NinePatchDrawable down = new NinePatchDrawable(new NinePatch(reg)); down.getPatch().setColor(Colors.LIGHT_BLUE);
        NinePatchDrawable checked = new NinePatchDrawable(new NinePatch(reg)); checked.getPatch().setColor(Colors.DARK_GREEN);
        NinePatchDrawable back = new NinePatchDrawable(new NinePatch(reg)); back.getPatch().setColor(Color.DARK_GRAY);
        NinePatchDrawable knob = new NinePatchDrawable(new NinePatch(reg));
        NinePatchDrawable cursor = new NinePatchDrawable(new NinePatch(reg));
        NinePatchDrawable selection = new NinePatchDrawable(new NinePatch(reg));
        NinePatchDrawable scroll = new NinePatchDrawable(new NinePatch(reg));
        NinePatchDrawable sliderBack = new NinePatchDrawable(new NinePatch(atlas.findRegion("slider")));
        NinePatchDrawable sliderKnob = new NinePatchDrawable(new NinePatch(atlas.findRegion("sliderknob")));


        TextureRegion blockSel = atlas.findRegion("buttonselected");
        NinePatch blockSel9 = new NinePatch(blockSel);
        blockSel9.setMiddleWidth(2);
        blockSel9.setMiddleHeight(2);

        NinePatchDrawable checkBlock = new NinePatchDrawable(blockSel9);
        checkBlock.setBottomHeight(border); checkBlock.setTopHeight(border); checkBlock.setLeftWidth(border); checkBlock.setRightWidth(border);
        //checkBlock.getPatch().setColor(Colors.DARK_BLUE);

        NinePatchDrawable upBlock = new NinePatchDrawable(new NinePatch(reg));
        NinePatchDrawable downBlock = new NinePatchDrawable(new NinePatch(reg)); downBlock.getPatch().setColor(Color.LIGHT_GRAY);
        setBorder(upBlock, border);
        setBorder(downBlock, border);


        Color color = new Color(Color.WHITE);
        TextButton.TextButtonStyle tbStyle = new TextButton.TextButtonStyle(up, down, null, font);
        TextButton.TextButtonStyle tbStyleToggle = new TextButton.TextButtonStyle(up, down, checked, font);

        Button.ButtonStyle butStyle = new Button.ButtonStyle(up, down, null);
        Button.ButtonStyle butStyleToggle = new Button.ButtonStyle(up, down, checked);

        BitmapFont blockFont = new BitmapFont(Gdx.files.internal("data/font/dpcomic-16.fnt"), atlas.findRegion("fonts"));
        blockFont.setColor(Color.DARK_GRAY);
        TextButton.TextButtonStyle butStyleBlock = new TextButton.TextButtonStyle(upBlock, downBlock, checkBlock, blockFont);
        butStyleBlock.fontColor = new Color(Color.DARK_GRAY);

        Touchpad.TouchpadStyle tpStyle = new Touchpad.TouchpadStyle(back, knob);
        TextField.TextFieldStyle tfStyle = new TextField.TextFieldStyle(font, color, cursor, selection, back);

        setBorder(sliderBack, border);
        sliderBack.setLeftWidth(4);sliderBack.setRightWidth(4);
        Slider.SliderStyle sliderStyle = new Slider.SliderStyle(sliderBack, sliderKnob);
        SlideColorPicker.SlideColorPickerStyle colorSliderStyle = new SlideColorPicker.SlideColorPickerStyle();
        colorSliderStyle.knob = sliderKnob;
        colorSliderStyle.background = sliderBack;
        Label.LabelStyle labeStyle = new Label.LabelStyle(font, color);
        ScrollPane.ScrollPaneStyle scrollStyle = new ScrollPane.ScrollPaneStyle(back, scroll, knob, scroll, knob);


        skin.add("default", tbStyle);
        skin.add("toggle", tbStyleToggle);

        skin.add("default", butStyle);
        skin.add("toggle", butStyleToggle);
        skin.add("block", butStyleBlock);

        skin.add("default", tpStyle);
        skin.add("default", tfStyle);
        skin.add("default-horizontal", sliderStyle);
        skin.add("default-horizontal", colorSliderStyle);
        skin.add("default", labeStyle);
        skin.add("default", scrollStyle);

    }



    private void setBorder(BaseDrawable draw, float border){
        draw.setBottomHeight(border); draw.setTopHeight(border);
        draw.setLeftWidth(border); draw.setRightWidth(border);
    }

    @Override
	public void render(float delta) {		
		if(!assets.update()) {
			//factory.init(timeStep, world, assets);

			return;
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

		Gdx.gl.glClearColor(0.1f, 0.1f, .1f, 1f);
		Gdx.gl.glClear(GL20.GL_COLOR_BUFFER_BIT | GL20.GL_DEPTH_BUFFER_BIT);
		

		//Camera camera = world.getSystem(CameraSystem.class).camera;

		world.draw(delta);

        stage.draw();

		spriteBatch.begin();
		/*Array<Component> array = new Array<Component>();
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
	*/
		spriteBatch.end();

	}

	@Override
	public void resize(int width, int height) {
		spriteBatch.getProjectionMatrix().setToOrtho2D(0, 0, width, height);
        stage.getViewport().update(width, height, true);
        Array<Actor> actors = stage.getActors();
        for (int i = 0; i < actors.size; i++){
            Actor a = actors.get(i);
            Table t = (Table) a;
            t.invalidate();
            Gdx.app.log(TAG, "resize)"+i);
        }
        //tab.validate();
		//camera.viewportWidth = width;
		//camera.viewportHeight = height;
		//camera.update();
	
	}

}
