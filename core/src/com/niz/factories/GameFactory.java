package com.niz.factories;

import com.artemis.Component;
import com.artemis.World;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.assets.AssetManager;
import com.badlogic.gdx.files.FileHandle;
import com.badlogic.gdx.graphics.g2d.TextureAtlas;
import com.badlogic.gdx.scenes.scene2d.Action;
import com.badlogic.gdx.scenes.scene2d.Actor;
import com.badlogic.gdx.scenes.scene2d.InputEvent;
import com.badlogic.gdx.scenes.scene2d.Stage;
import com.badlogic.gdx.scenes.scene2d.ui.Button;
import com.badlogic.gdx.scenes.scene2d.ui.Label;
import com.badlogic.gdx.scenes.scene2d.ui.Skin;
import com.badlogic.gdx.scenes.scene2d.ui.Table;
import com.badlogic.gdx.scenes.scene2d.utils.ClickListener;
import com.badlogic.gdx.utils.Array;
import com.badlogic.gdx.utils.GdxRuntimeException;
import com.badlogic.gdx.utils.Json;
import com.badlogic.gdx.utils.JsonValue;
import com.niz.component.systems.*;

public abstract class GameFactory {
    public static final String path = "data/";
    private AssetDefinition ass;
    private SystemDefinition systemDef;
    //protected Entity player;
    public void assets(World world, AssetManager assets, FileHandle file) {
        Json json = new Json();
        json.addClassTag("assets", AssetDefinition.class);
        ass = json.fromJson(AssetDefinition.class, file);//new AssetDefinition();//

        ass.process(world, assets, file.path());

    }
    public void systems(float timeStep, World world, AssetManager assets, FileHandle file) {
        //ass.postProcess(assets);
        AssetsSystem assetsSys = new AssetsSystem(assets);
        world.setSystem(assetsSys);
        TextureAtlas tiles = assetsSys.getTextureAtlas("tiles");
        VoxelSystem.white = tiles.findRegion("air");
        if (VoxelSystem.white == null) throw new GdxRuntimeException("error");

        world.setDelta(timeStep);


        Json json = new Json();

        systemDef = json.fromJson(SystemDefinition.class, file);
        systemDef.setJson(json);
        systemDef.setSystem(CameraSystem.class);
        systemDef.setSystem(GraphicsSystem.class);

        systemDef.setSystem( PhysicsSystem.class );
        systemDef.setSystem( MovementSystem.class);
        systemDef.setSystem( AABBBodySystem.class);
        systemDef.setSystem( BucketedSystem.class);
        systemDef.setSystem( ActionSystem.class);
        systemDef.setSystem( MovementSystem.class);


        systemDef.setSystem(VoxelSystem.class);
        systemDef.setSystem(EditVoxelSystem.class);


        systemDef.setSystem( PositionRollingAverageSystem.class);
        systemDef.setSystem( VelocityRollingAverageSystem.class);
        systemDef.setSystem(VelocityPredictionSystem.class);
        systemDef.setDrawSystem(VoxelRenderingSystem.class);


        systemDef.setDrawSystem(ModelRenderingSystem.class );
        systemDef.setSystem(BrainSystem.class);
        systemDef.setDrawSystem( DebugVectorSystem.class);
        systemDef.setDrawSystem( DebugPositionSystem.class);


        systemDef.setSystem(CameraControllerSystem.class);
        systemDef.setSystem( CameraInfluenceSystem.class);
        systemDef.setSystem( PositionLimiterSystem.class);

        //systemDef.preWrite();
        systemDef.procesesSystems(world);


    }


public void init(World world, AssetManager assets, FileHandle file){
	
	
	systems(1f, world, assets, file);
	
	world.initialize();
	world.initializeDraw();
}

	Array<Component> components = new Array<Component>();
	public void save(World world){

	}

	public void initMenu(final World world, final Skin skin, final Stage stage, final AssetManager assets, final float timestep) {
		//Group group = new WidgetGroup();
		Gdx.input.setInputProcessor(stage);
		final Table table = new Table();
		//final Button newGame = new Button(new Label("New", skin), skin);

        FileHandle dirHandle;
        dirHandle = Gdx.files.internal("data/ini");
        Array<String> handles = new Array<String>();
        handles.add("Play");
        handles.add("Mods");
        handles.add("Block Editor");
        getHandles(dirHandle, handles, 0, table, skin, assets, world);

        table.setFillParent(true);
        stage.addActor(table);

		/*
        TextureAtlas atlas = assets.get(path+"tiles.pack", TextureAtlas.class);
        final Sprite btnSprite = atlas.createSprite("button");
        final Sprite btnSpriteSelected = atlas.createSprite("buttonselected");

        //if (btnSprite == null) throw new GdxRuntimeException("nulll");

        final Button editorBtn = new Button(new Label("Editor", skin), skin);


		table.add(newGame);
        table.row();
		table.add(editorBtn);
		table.setFillParent(true);
		table.layout();
		




		//stage.addActor(group);

    */

	}


    public void getHandles(FileHandle begin, Array<String> allowedButtons, int recursions, Table table, Skin skin, AssetManager assets, World world)
    {

        FileHandle[] newHandles = begin.list();
        Gdx.app.log("Loop", "running!"+newHandles.length);

        for (FileHandle f : newHandles)
        {
            Gdx.app.log("Loop", "f!");

            if (f.isDirectory())
            {
                Gdx.app.log("Loop", "isFolder!");



                getHandles(f, allowedButtons, recursions+1, table, skin, assets, world);






            }
            else
            {
                Gdx.app.log("Loop", "isFile!");
                //check if systems
                //check if .ini

                if (f.name().equals("systems.ini")){
                    Gdx.app.log("Loop", "is systems.ini!");
                    if (recursions > 1){
                        Gdx.app.log("Loop", "is mod!");

                    } else {
                        Gdx.app.log("Loop", "is button!"+f.name());
                        if (allowedButtons.contains(f.parent().name(), false)){
                            Gdx.app.log("Loop", "button allowed "+f.parent().name());
                            table.add(createMenuButton(f.parent().name(), f, skin, assets, world));
                        } else {
                            Gdx.app.log("Loop", "button not allowed " + f.parent().name());

                        }

                    }

                }
            }
        }
    }

    private Button createMenuButton(String name, final FileHandle f, Skin skin, final AssetManager assets, final World world) {
        if (skin == null) throw new GdxRuntimeException("null");
        final Button button  = new Button(new Label(name, skin), skin);
        button.addListener(new ClickListener() {
            @Override
            public void clicked(InputEvent event, float x, float y) {

                button.addAction(

                                new Action() {

                                    @Override
                                    public boolean act(float delta) {

                                        init(world, assets, f);
                                        //init(timestep, world, assets, file);
                                        return true;
                                    }

                                }

                );

            }
        });




        return button;

    }





}
