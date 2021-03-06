package com.niz.factories;

import com.artemis.Component;
import com.artemis.Entity;
import com.artemis.EntityDefinition;
import com.artemis.World;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.assets.AssetManager;
import com.badlogic.gdx.files.FileHandle;
import com.badlogic.gdx.graphics.Camera;
import com.badlogic.gdx.graphics.g2d.TextureAtlas;
import com.badlogic.gdx.scenes.scene2d.InputEvent;
import com.badlogic.gdx.scenes.scene2d.Stage;
import com.badlogic.gdx.scenes.scene2d.ui.Button;
import com.badlogic.gdx.scenes.scene2d.ui.Label;
import com.badlogic.gdx.scenes.scene2d.ui.Skin;
import com.badlogic.gdx.scenes.scene2d.ui.Table;
import com.badlogic.gdx.scenes.scene2d.utils.ClickListener;
import com.badlogic.gdx.tests.g3d.voxel.BlockDefinition;
import com.badlogic.gdx.tests.g3d.voxel.VoxelChunk;
import com.badlogic.gdx.tests.g3d.voxel.VoxelWorld;
import com.badlogic.gdx.utils.Array;
import com.badlogic.gdx.utils.GdxRuntimeException;
import com.badlogic.gdx.utils.Json;
import com.niz.EngineScreen;
import com.niz.actions.ActionList;
import com.niz.component.*;
import com.niz.component.systems.*;
import com.niz.ui.edgeUI.EdgeUI;
import com.niz.ui.edgeUI.TestUI;
import com.artemis.EntityDefArray;

public abstract class GameFactory {
    public static final String path = "data/";
    private static final String TAG = "game factory";
    private SystemDefinition systemDef;
    AssetsSystem assetsSys;// = new AssetsSystem(assets);

    //protected Entity player;
    Json json = new Json();
    public void assets(World world, AssetManager assets, FileHandle file) {

        json.addClassTag("assets", AssetDefinition.class);
        AssetDefinition ass = json.fromJson(AssetDefinition.class, file);//new AssetDefinition();//
        ass.path = file.parent().path()+"/";
        ass.process(world, assets);
        assetsSys.addDef(ass);
    }
    private void systemDefs(float timeStep, World world, AssetManager assets, FileHandle file) {
        //ass.postProcess(assets);

        world.setSystem(assetsSys);

       // if (VoxelSystem.white == null) throw new GdxRuntimeException("error");

        world.setDelta(timeStep);


        //Json json = new Json();

        systemDef = json.fromJson(SystemDefinition.class, file);
        systemDef.setJson(json);
        systemDef.setSystem(FPSInputSystem.class);
        systemDef.setSystem(CameraSystem.class);
        systemDef.setSystem(CameraUpVectorSystem.class);
        systemDef.setSystem(GraphicsSystem.class);
        systemDef.setSystem( PhysicsSystem.class );
        systemDef.setSystem( MovementSystem.class);
        systemDef.setSystem( AABBBodySystem.class);
        systemDef.setSystem( BucketedSystem.class);
        systemDef.setSystem( ActionSystem.class);
        systemDef.setSystem( MovementSystem.class);

        systemDef.setSystem(PositionSystem.class);

        systemDef.setSystem(VoxelSystem.class);
        systemDef.setSystem(VoxelEditingSystem.class);

        systemDef.setDrawSystem(VoxelMeshSystem.class);
        systemDef.setSystem(VoxelSerializingSystem.class);


        systemDef.setSystem( PositionRollingAverageSystem.class);
        //systemDef.setSystem( RotationRollingAverageSystem.class);
        systemDef.setSystem(UpVectorRollingAverageSystem.class);

        systemDef.setSystem( VelocityRollingAverageSystem.class);
        systemDef.setSystem(VelocityPredictionSystem.class);
        systemDef.setDrawSystem(VoxelRenderingSystem.class);
        systemDef.setDrawSystem(BlockHighlightRenderingSystem.class);
        systemDef.setDrawSystem(VoxelGenerateAroundPlayerSystem.class);
        systemDef.setDrawSystem(ModelRenderingSystem.class );
        systemDef.setSystem(BrainSystem.class);
        systemDef.setDrawSystem( DebugVectorSystem.class);
        systemDef.setDrawSystem( DebugPositionSystem.class);


        systemDef.setSystem(CameraControllerSystem.class);
        systemDef.setSystem(CameraFirstPersonSystem.class);
        systemDef.setSystem( CameraPositionInfluenceSystem.class);
        systemDef.setSystem(CameraLookAtSystem.class);
        //systemDef.setSystem( CameraRotationInfluenceSystem.class);
        systemDef.setSystem( PositionLimiterSystem.class);
        //systemDef.preWrite();
        systemDef.procesesSystems(world);


    }




	Array<Component> components = new Array<Component>();
	public void save(World world){

	}

	public void initMenu(final World world, final Skin skin, final Stage stage, final AssetManager assets, final float timestep) {
		//Group group = new WidgetGroup();
        assetsSys = new AssetsSystem(assets);
		final Table table = new Table();
		//final Button newGame = new Button(new Label("New", skin), skin);

        FileHandle dirHandle;
        dirHandle = Gdx.files.internal("data/ini/Play").parent();
        Gdx.app.log(TAG, ""+dirHandle.list().length + "  "  + dirHandle.exists() );
        Array<String> handles = new Array<String>();
        handles.add("Play");
        handles.add("Mods");
        handles.add("Block Editor");
        getHandles(dirHandle, handles, 0, table, skin, assets, world);

        table.setFillParent(true);
        table.layout();
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

        //json.addClassTag("Entity Definition", EntityDefinition.class);
        //json.addClassTag("Entity array", EntityDefArray.class);
        //json.setElementType(EntityDefArray.class, "Entity Def", EntityDefinition.class);



    }


    public void getHandles(FileHandle begin, Array<String> allowedButtons, int recursions, Table table, Skin skin, AssetManager assets, World world)
    {

        FileHandle[] newHandles = begin.list();
        Gdx.app.log("Loop", "running!"+newHandles.length);

        for (String s : allowedButtons)
        {
           
           
                Gdx.app.log("Loop", "isFile!");
                //check if systems
                //check if .ini
                FileHandle f = Gdx.files.internal("data/ini/"+s+"/systems.ini");
                if (f.exists()){
                	 Gdx.app.log("Loop", "button allowed "+s);
                     table.add(createMenuButton(s, f, skin, assets, world, table));
                     table.row();

                }
            
        }
    }
    public static  BlockDefinition[] getBlockDefs(World world, TextureAtlas atlas) {

        BlockDefinition[] defs = new BlockDefinition[256];


        for (int i = 2; i < 256; i++){
            if (defs[i] == null){
                defs[i] = new BlockDefinition(i);
            }
        }

        defs[0] = new BlockDefinition(0);
        defs[1] = new BlockDefinition(1, "stone", atlas);


        defs[0].lightValue = 15;
        //defs[0].dayLightLoss = 0;
        defs[0].isSolid = false;
        defs[0].isEmpty = true;


        return defs;


    }
    private Button createMenuButton(String name, final FileHandle f, final Skin skin, final AssetManager assets, final World world, final Table table) {
        if (skin == null) throw new GdxRuntimeException("null");
        final Button button  = new Button(new Label(name, skin), skin);
        button.addListener(new ClickListener() {
            @Override
            public void clicked(InputEvent event, float x, float y) {

                //assets
                assets(world, assets, f.sibling("assets.ini"));
                while (!assets.update());
                //wait for assets to be loaded
                assetsSys.processDefinitions();

                VoxelChunk.defs = getBlockDefs(world, assetsSys.getTextureAtlas("tiles"));


                systemDefs(1f, world, assets, f);

                world.initialize();
                world.initializeDraw();

                newGame(world);
                world.process();
                saveEntities(world, f);
                //loadEntities(world, f);

                saveDefaultUI(table, skin, world, f, new TestUI());
                //loadUI(table, skin, world, f);



            }


        });
        return button;
    }
    static final Array<EntityDefinition> entArr = new Array<EntityDefinition>();
    private void loadEntities(World world, FileHandle f) {



        EntityDefArray eDefArray = json.fromJson(EntityDefArray.class, f.sibling("entity.dat"));
        for ( EntityDefinition eDef : eDefArray){
            Entity e = world.createEntity();
            eDef.setEntityFromThisOnce(e);
            world.addEntity(e);
        }
    }

    private void saveEntities(World world, FileHandle f) {
        EntityDefArray eDefArray = new EntityDefArray();
        //Json entityJson = new Json();
        for (Entity e : world.getEntityManager().entities){
            Gdx.app.log(TAG, "writing entity"+e);
            if (e == null) continue;
            EntityDefinition eDef = new EntityDefinition();
            if (eDef.setFrom(e))
                eDefArray.add(eDef);
        }

        String entities = json.prettyPrint(eDefArray);
        Gdx.files.external(f.parent().path()+"/entity.dat").writeString(entities, false);
        Gdx.app.log(TAG, "entities  "+entities);
    }

    private void loadUI(Table table, Skin skin, World world, FileHandle f) {
        table.clear();
        EdgeUI ui =  json.fromJson(TestUI.class, f.sibling("ui.ini"));
        Stage stage = table.getStage();
        if (stage == null) throw new GdxRuntimeException("null stage");
        ui.init(skin, stage, world.getSystem(AssetsSystem.class), world);

       // Gdx.app.log("gamefactory", s);
    }

    private void saveDefaultUI(Table table, Skin skin, World world, FileHandle f, EdgeUI ui) {
        table.clear();

        Stage stage = table.getStage();
        if (stage == null) throw new GdxRuntimeException("null stage");
        ui.init(skin, stage, world.getSystem(AssetsSystem.class), world);
        String s = json.prettyPrint(ui);
        Gdx.files.external(f.parent().path()+"/ui.ini").writeString(s, false);
        //Gdx.app.log("gamefactory", s);
    }


    public void newGame(World world) {
       /* Entity e = world.createEntity();
        Position pos = e.add(Position.class);
        pos.pos.set(0f, 45, .5f);
        world.addEntity(e);*/

        Entity player = world.createEntity();
        EngineScreen.player = player;
        //if (true)throw new GdxRuntimeException("sdfjk");
        Position pos = player.add(Position.class);
        pos.pos.set(300,38,800);
        player.add(Player.class);
        player.add(Physics.class);

        player.add(AABBBody.class).ys = .75f;
        player.add(Move.class);
        player.add(CameraPositionInfluencer.class);

        player.add(PositionRollingAverage.class).size = 1;//rolling average of position
        player.add(UpVectorRollingAverage.class).size = 1;
        player.add(UpVector.class).up.set(0,1,0);
        player.add(ActionList.class);
        player.add(Move.class);
        world.addEntity(player);


        Entity camC = world.createEntity();
        camC.add(CameraController.class);
        camC.add(Position.class);
        camC.add(FPSCamera.class);
        world.addEntity(camC);


    }

    public void newEditor(World world){
        //Gdx.app.log(TAG, "NEW GAME");

        VoxelSystem voxel = world.getSystemOrSuperClass(VoxelSystem.class);
        //setDefaultMap(voxel.voxelWorld);
        AssetsSystem as = world.getSystem(AssetsSystem.class);


        //playerModel(as);


        //VoxelChunk.defs = GeneralFactory.getBlockDefs(world);


        Entity e = world.createEntity();
        world.addEntity(e);

        Position pos = e.add(Position.class);
        pos.pos.set(0f, 5, .5f);
       // e.add(Physics.class);
        //e.add(AABBBody.class).ys = .75f;
        //e.add(Brain.class).getShortTarget().set(100000, 0, 0);

        //Move move = e.add(Move.class);
        //move.jumpStrength = 1.5f;
        ActionList actionList = e.add(ActionList.class);
       // actionList.actions.add(AStand.class);


        //ModelInfo mod = e.add(ModelInfo.class);
       // AnimationController animController = new AnimationController(playerModel);
        //mod.set(playerModel, animController );


        e.add(Player.class);
        e.add(CameraPositionInfluencer.class);
        //e.add(CameraRotationInfluencer.class);



        e.add(PositionRollingAverage.class).size = 60;//rolling average of position
        e.add(UpVectorRollingAverage.class).size = 60;
        e.add(UpVector.class).up.set(0,1,0);

        //e.add(VelocityPredictor.class).scale = 240f;
        //e.get(VelocityPredictor.class).y = false;
        //e.add(VelocityRollingAverage.class).size = 100;

        //e.add(DebugVector.class).add(e.get(VelocityRollingAverage.class).result, Color.CYAN);


        Entity camC = world.createEntity();
        camC.add(CameraController.class);
        camC.add(Position.class);
        //camC.add(UpVector.class);
        //camC.add(UpVectorRollingAverage.class);

        world.addEntity(camC);

        Entity tester = world.createEntity();
        tester.add(Position.class).pos.set(0,0,1);
        //tester.add(DebugPosition.class);
        world.addEntity(tester);

        Camera camera = world.getSystem(CameraSystem.class).camera;
        //camera.position.set(0,16,16);
        //camera.rotate(50, -1, 0, 0);
        Entity looker = world.createEntity();
        looker.add(Position.class).pos .set(8,8,8);
        looker.add(CameraLookAt.class);
        world.addEntity(looker);

       /* Entity highlighter = world.createEntity();
        highlighter.add(BlockHighlight.class).dirty = true;
        highlighter.add(ActionList.class).addPre(Pools.obtain(AHighlightBlock.class));
        highlighter.add(Position.class).pos.set(8,8,8);
        world.addEntity(highlighter);*/

    }

    private void setDefaultMap(VoxelWorld voxelWorld) {
        for (int i =0; i < 16; i++)
        for (int j = 0; j < 16; j++)
            voxelWorld.set(i,0,j,0,(byte)1);

        for (int i = 0; i < 10; i++){
            voxelWorld.set(10,i,10,0,(byte)1);
        }
    }


}
