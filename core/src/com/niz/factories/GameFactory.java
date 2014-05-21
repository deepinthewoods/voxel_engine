package com.niz.factories;

import com.artemis.Component;
import com.artemis.Entity;
import com.artemis.World;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.assets.AssetManager;
import com.badlogic.gdx.files.FileHandle;
import com.badlogic.gdx.graphics.g2d.Sprite;
import com.badlogic.gdx.graphics.g2d.TextureAtlas;
import com.badlogic.gdx.scenes.scene2d.Action;
import com.badlogic.gdx.scenes.scene2d.InputEvent;
import com.badlogic.gdx.scenes.scene2d.Stage;
import com.badlogic.gdx.scenes.scene2d.actions.Actions;
import com.badlogic.gdx.scenes.scene2d.ui.Button;
import com.badlogic.gdx.scenes.scene2d.ui.Label;
import com.badlogic.gdx.scenes.scene2d.ui.Skin;
import com.badlogic.gdx.scenes.scene2d.ui.Table;
import com.badlogic.gdx.scenes.scene2d.utils.ClickListener;
import com.badlogic.gdx.utils.Array;
import com.badlogic.gdx.utils.Json;
import com.badlogic.gdx.utils.JsonValue;
import com.niz.EngineScreen;
import com.niz.component.systems.*;

public abstract class GameFactory {
    private static final String path = "data/";
    private AssetDefinition ass;
    private SystemDefinition systemDef;
    protected Entity player;
    public void assets(World world, AssetManager assets) {

        FileHandle file = Gdx.files.internal(path+"assets.ini");

        Json json = new Json();

        JsonValue value;

        json.addClassTag("assets", AssetDefinition.class);
        ass = json.fromJson(AssetDefinition.class, file);//new AssetDefinition();//
        assets.load("data/tiles.atlas", TextureAtlas.class);

        ass.process(world, assets);


        player = world.createEntity();
        EngineScreen.player = player;
    }
    public void systems(float timeStep, World world, AssetManager assets, FileHandle file) {
        ass.postProcess(assets);




        world.setDelta(timeStep);


        Json json = new Json();

        //String s = json.toJson(ass);


        //funct.execute(world);

        //systemDef = new SystemDefinition();
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
        //systemDef.setSystem(EditVoxelSystem.class);


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
        world.setSystem(new AssetsSystem(assets));


    }
public abstract void newGame(World world, Stage stage);

public abstract void load(World world);

public void init(float timeStep, World world, AssetManager assets, FileHandle file){
	
	
	systems(timeStep, world, assets, file);
	
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
		final Button newGame = new Button(new Label("New", skin), skin);
		
		newGame.addListener(new ClickListener(){
            @Override
            public void clicked(InputEvent event, float x, float y) {

                table.addAction(
                        Actions.sequence(
                                Actions.fadeOut(.4f)
                                ,
                                Actions.parallel(
                                        Actions.removeActor(table)
                                        ,
                                        new Action(){

                                            @Override
                                            public boolean act(float delta) {
                                                FileHandle file = Gdx.files.internal("data/game.ini");
                                                init(timestep, world, assets, file);
                                                newGame(world, stage);
                                                return true;
                                            }



                                        }
                                )
                        )
                );

                newGame.removeListener(this);
            }
        });
        final Sprite btnSprite = assets.get("data/tiles.atlas", TextureAtlas.class).createSprite("button");
        final Sprite btnSpriteSelected = assets.get("data/tiles.atlas", TextureAtlas.class).createSprite("buttonselected");

        //if (btnSprite == null) throw new GdxRuntimeException("nulll");

        final Button editorBtn = new Button(new Label("Editor", skin), skin);
        editorBtn.addListener(new ClickListener(){
            @Override
            public void clicked(InputEvent event, float x, float y) {

                table.addAction(
                        Actions.sequence(
                                Actions.fadeOut(.4f)
                                ,
                                Actions.parallel(
                                        Actions.removeActor(table)
                                        ,
                                        new Action(){

                                            @Override
                                            public boolean act(float delta) {
                                                FileHandle file = Gdx.files.internal("data/editor.ini");
                                                init(timestep, world, assets, file);
                                                return true;
                                            }

                                        },
                                        new Action(){

                                            @Override
                                            public boolean act(float delta) {
                                                editor(world, stage, skin, btnSprite, btnSpriteSelected);
                                                return true;
                                            }

                                        }
                                )
                        )
                );

                editorBtn.removeListener(this);
            }
        });


		table.add(newGame);
        table.row();
		table.add(editorBtn);
		table.setFillParent(true);
		table.layout();
		
		stage.addActor(table);



		//stage.addActor(group);
		
		
		
	}



    protected abstract void editor(World world, Stage stage, Skin skin, Sprite sprite, Sprite spritesel);
}
