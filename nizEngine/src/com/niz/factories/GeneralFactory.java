package com.niz.factories;


import com.artemis.Entity;
import com.artemis.World;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.assets.AssetManager;
import com.badlogic.gdx.files.FileHandle;
import com.badlogic.gdx.graphics.Camera;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.PerspectiveCamera;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.graphics.g3d.Material;
import com.badlogic.gdx.graphics.g3d.Model;
import com.badlogic.gdx.graphics.g3d.ModelBatch;
import com.badlogic.gdx.graphics.g3d.ModelInstance;
import com.badlogic.gdx.graphics.g3d.attributes.ColorAttribute;
import com.badlogic.gdx.graphics.g3d.model.Node;
import com.badlogic.gdx.graphics.g3d.model.NodePart;
import com.badlogic.gdx.graphics.g3d.utils.AnimationController;
import com.badlogic.gdx.math.MathUtils;
import com.badlogic.gdx.math.Vector3;
import com.badlogic.gdx.scenes.scene2d.Actor;
import com.badlogic.gdx.scenes.scene2d.InputEvent;
import com.badlogic.gdx.scenes.scene2d.InputListener;
import com.badlogic.gdx.scenes.scene2d.Stage;
import com.badlogic.gdx.scenes.scene2d.ui.Skin;
import com.badlogic.gdx.tests.g3d.voxel.BlockDefinition;
import com.badlogic.gdx.tests.g3d.voxel.VoxelWorld;
import com.badlogic.gdx.utils.Json;
import com.badlogic.gdx.utils.JsonValue;
import com.badlogic.gdx.utils.Pools;
import com.niz.EngineScreen;
import com.niz.ShapeBatch;
import com.niz.actions.AJump;
import com.niz.blocks.TopBottomBlock;
import com.niz.commands.InvokeSpecialFunction;
import com.niz.component.AABBBody;
import com.niz.component.ActionComponent;
import com.niz.component.Brain;
import com.niz.component.Move;
import com.niz.component.systems.AABBBodySystem;
import com.niz.component.systems.ActionSystem;
import com.niz.component.systems.BrainSystem;
import com.niz.component.systems.BucketedSystem;
import com.niz.component.systems.CameraBehaviourSystem;
import com.niz.component.systems.CameraFollowerSystem;
import com.niz.component.systems.CameraSystem;
import com.niz.component.systems.DebugPositionSystem;
import com.niz.component.systems.DebugVectorSystem;
import com.niz.component.systems.ModelRenderingSystem;
import com.niz.component.systems.MovementSystem;
import com.niz.component.systems.PhysicsSystem;
import com.niz.component.systems.PositionLimiterSystem;
import com.niz.component.systems.RollingAverageSystem;
import com.niz.component.systems.VelocityPredictionSystem;
import com.niz.component.systems.VoxelRenderingSystem;
import com.niz.component.systems.VoxelSystem;

public class GeneralFactory extends GameFactory{
	public static final float VIEWPORT_SIZE = 20;

	private static final String TAG = "General Factory";

	private static final String path = "/data/";
	
	//VoxelSystem voxelSys;
	//PlatformerInputSystem inputSys;

	private Entity player;
	
	Actor dragger, clicker;
	
	Vector3 tmp = new Vector3(), tmp2 = new Vector3();

	private CameraFollowerSystem cameraSystem;

	private AssetDefinition ass;
	private SystemDefinition systemDef;
	
	@Override
	public void assets(World world, AssetManager assets) {
		
		FileHandle file = Gdx.files.external(path+"assets.ini");
		
		//String[] s = file.readString().split("/n");
		//Gdx.app.log(TAG, "strings"+s[0]);
		
		//Writer wrietr = new FileWriter()
		
		Json json = new Json();
		
		JsonValue value;
		
		
		
		
		
		json.addClassTag("assets", AssetDefinition.class);
		ass = json.fromJson(AssetDefinition.class, file);//new AssetDefinition();//
		ass.process(world, assets);
		
		
		//String jtext = json.toJson(ass);
		
		//file.writeString(json.prettyPrint(jtext), false);
		//Gdx.app.log(TAG, "strings"+jtext);
		
		player = world.createEntity();
		EngineScreen.player = player;
	}

	@Override
	public void systems(float timeStep, World world, AssetManager assets, ModelBatch modelBatch, ShapeBatch shapeBatch) {
		ass.postProcess(assets);
		//TextureRegion[][] tiles = new TextureRegion(assets.get("data/tiles.png", Texture.class)).split(16, 16);
		//BlockDefinition[] defs = getBlockDefs(tiles);
		//Pixmap fades = assets.get("data/fades.png", Pixmap.class);
		playerModel(assets);
		
		
		
		world.setDelta(timeStep);
		

		Json json = new Json();
		FileHandle file = Gdx.files.external(path+"systems.ini");
		
		//String s = json.toJson(ass);
				
		InvokeSpecialFunction funct = new InvokeSpecialFunction();
		
		//funct.execute(world);
				
		//systemDef = new SystemDefinition();
		systemDef = json.fromJson(SystemDefinition.class, file);	
				
		systemDef.setSystem(CameraSystem.class);
		
				
		systemDef.setSystem(PhysicsSystem.class );
		systemDef.setSystem( MovementSystem.class);
		systemDef.setSystem( AABBBodySystem.class);
		systemDef.setSystem( BucketedSystem.class);
		systemDef.setSystem( ActionSystem.class);
		systemDef.setSystem( MovementSystem.class);
		
		
		systemDef.setSystem(VoxelSystem.class);
		
		systemDef.setSystem( RollingAverageSystem.class);
		systemDef.setSystem( VelocityPredictionSystem.class);
		
		systemDef.setDrawSystem(VoxelRenderingSystem.class);
		

		systemDef.setDrawSystem(ModelRenderingSystem.class );
		systemDef.setSystem(BrainSystem.class);
		systemDef.setDrawSystem( DebugVectorSystem.class);
		systemDef.setDrawSystem( DebugPositionSystem.class);

		
		systemDef.setSystem(CameraFollowerSystem.class);
		systemDef.setSystem( CameraBehaviourSystem.class);
		systemDef.setSystem( PositionLimiterSystem.class);

		//systemDef.preWrite();
		
		systemDef.procesesSystems(world);
		//world.getSystem(CameraSystem.class).camera = cam;
		
		String s = json.toJson(systemDef);
		//file.writeString(json.prettyPrint(s), false);
		Gdx.app.log(TAG, json.prettyPrint(s));
		
	}
	@Override
	public void newGame(World world, Stage stage) {
		//Gdx.app.log(TAG, "NEW GAME");
		
		VoxelSystem voxel = world.getSystem(VoxelSystem.class);
		setDefaultMap(voxel.voxelWorld);
		
		
		/*
		 
		 create : player
		 player.add : Position
		 player.position : x,y,z
		 player.add: Physics
		 player.add : AABBBody
		 player.add : Brain
		 player.brain.shortTarget : 
		 player.add : Move
		 player.move.jumpStringth : 1.5f
		 player.add : ActionList
		 player.actions.add : AStand
		 player.add : ModelInfo
		 player.modelInfo : !ANIMCONTROLLER
		 player.add : Player
		 
		 parsers/commands
		 
		 
		 subject.( e.add / comp name).( c.add / field )	:	field=value(float or int?) / add = name of C
		 
		 add : c.name
		 c.add special case
		 c.field : value
		 c.v.set : 3 floats
		 
		Entity e = null;
		switch (words[0]){
			create:
			entityname: 
					e = world.getEntityByName(words[0]):
					switch (words[1]){
						add: name (params)
						componentname:
								Component c = get(name);
								switch (words[2])
									field: float or int
									vector: 3 floats
									set:various e,float,int
									add:various e, float, int
					}
		}
		
		commands needed 
		e.addComponent(name)
		e.getComponent().setField(name, f); -> e.getComponent().setVector(name, xyz);
		
		e.getComponent().customCommand(String command, String params);
		
		lists needed for ui
			allComponents for add
			fieldsOnComponent for field set
			custom commands on this Component
		
		--first list-- 
			add E
			entitynames
			
		--add E--
			input name
		
		--entity--
			add C
			e.current component names
			
		--add C--
			all possible components (mark e.current components )
			
		--clicked on add C name--
			command
			
		--e current component name--
			fields
			custom commands
			
		--fields (depends on type)--
			input value
			command
			
		--c.custom commands
			verb and field inputs

		LIST
		E/C/custom
	
	EntityCommand -> add C   , select C
	ComponentCommand -> set field    , custom commands
	CustomCommand -> create E

		 */
		
		
		/*Entity e = player;
		world.addEntity(e);
		
		Position pos = e.add(Position.class);
		pos.pos.set(10f, 5, .5f);
		e.add(Physics.class);
		e.add(AABBBody.class).ys = .75f;
		e.add(Brain.class).getShortTarget().set(100000, 0, 0);
		
		Move move = e.add(Move.class);
		move.jumpStrength = 1.5f;
		ActionList actionList = e.add(ActionComponent.class).action;
		actionList.actions.add(AStand.class);
		
		
		ModelInfo mod = e.add(ModelInfo.class);
		AnimationController animController = new AnimationController(playerModel);
		mod.set(playerModel, animController );
		

		e.add(Player.class);*/
	
		stage.addActor(dragger);
		
		
		/*Entity posAcc = world.createEntity();
		posAcc.add(Position.class);
		posAcc.add(RollingAverage.class).set(e, 10);
		//posAcc.add(DebugVector.class).add(posAcc.get(Position.class).pos, Color.CYAN);
		//posAcc.add(DebugPosition.class);
		world.addEntity(posAcc);
		
		Entity velocityAcc = world.createEntity();
		velocityAcc.add(Position.class);
		velocityAcc.add(VelocityPredictor.class).set(e, 1220);
		velocityAcc.get(VelocityPredictor.class).y = false;
		velocityAcc.add(RollingAverage.class).set(velocityAcc.get(VelocityPredictor.class).vel, 500);
		//velocityAcc.add(DebugPosition.class);
		//velocityAcc.add(DebugVector.class)
		//.add(velocityAcc.get(VelocityPredictor.class).vel, Color.RED)
		//.add(velocityAcc.get(Position.class).pos, Color.ORANGE);
		
		world.addEntity(velocityAcc);
		
		Entity camC = world.createEntity();
		camC.add(CameraController.class);
		camC.add(Position.class);
		CameraInfluences camInf = camC.add(CameraInfluences.class);
		camInf.add(posAcc, 12f);
		camInf.add(velocityAcc, 1f);
		camC.add(PositionLimiter.class).set(e, 10f);
		world.addEntity(camC);
		
		Entity tester = world.createEntity();
		tester.add(Position.class).pos.set(0,0,1);
		//tester.add(DebugPosition.class);
		world.addEntity(tester);
		*/
		
		
		
		
	}


	private boolean getComponentFromName(String string) {
		return false;
	}

	@Override
	public void load(World world) {
		// TODO Auto-generated method stub
		
	}

	
	
	ModelInstance playerModel;
	private void playerModel(AssetManager assets){
	String[][] skin = {
				
				{
				"humanhands", "humanhead","humanupperArmL", "humanLowerArmL", "humanupperArmR", "humanLowerArmR"
		},  {
				"humantorso", "humanfeet"
		},  {
				"humanlegs"//, "wizardhat"
		},  {
				"armArmature", "legArmature", "bodyArmature"	
		}
		};
		Color[] colors = {
				Color.PINK
				, Color.RED
				, Color.CYAN
				, Color.BLACK
		};
		int tot = 0;
		for (int i = 0; i < skin.length; i++)tot += skin[i].length;
		String[] all = new String[tot];
		
		for (int i = 0,  prog = 0; i < skin.length; i++)
			prog = addTo(prog, all, skin[i]);
		playerModel = new ModelInstance(assets.get("data/humanmodel.g3db", Model.class)
				, all
				);
		
		setColors(playerModel, skin, colors);
		AnimationController playerAnimController = new AnimationController(playerModel);
		
		
		ModelInstance zombieModel = new ModelInstance(assets.get("data/humanmodel.g3db", Model.class)
				, all
				);
		colors = new Color[]{
				Color.GREEN
				, Color.DARK_GRAY
				, Color.GRAY
				, Color.BLACK
		};
		setColors(zombieModel, skin, colors);
	}
	
	
	private void setColors(ModelInstance playerModel, String[][]skin, Color[] colors) {
		int j = 0;;
		for (int i = 0; i < playerModel.nodes.size; i++) {
			//m.id
			Node n = playerModel.nodes.get(i);
			String s = n.id;
			//Gdx.app.log(TAG,  "node "+s);
			Color c = getColorFromStrings(s, skin, colors);
			for (NodePart np : n.parts){
				np.material = new Material();
				np.material.set(new ColorAttribute(ColorAttribute.Diffuse, c));
			}
			
		}
		
	}

	private Color getColorFromStrings(String compstring, String[][] skin, Color[] colors) {
		for (int i = 0; i < skin.length; i++){
			for (int t = 0; t < skin[i].length; t++){
				String s = skin[i][t];
				//Gdx.app.log(TAG, "mesjhdsjklj "+m.id + "   <-->   "+playerModel.nodes.get(0).parts.get(0).meshPart.id);
				if (s.equalsIgnoreCase(compstring)){
					return colors[i];
					
					//continue;
				}
			}
		}
		return Color.MAGENTA;
	}

	private int addTo(int prog, String[] all, String[] from) {
		int i = 0;
		for (; i < from.length; i++){
			all[i+prog] = from[i];
		}
		return prog+i;
	}
	
	private void setDefaultMap(VoxelWorld voxelWorld) {
		for (int x = 0; x < 880; x++)
			for (int y = 0; y < 2; y++)
				for (int z = 0; z < 2; z++){
					//if (MathUtils.randomBoolean()) {
						//Gdx.app.log(TAG, "ww"+x+","+y+","+z+(voxelWorld == null));
						voxelWorld.set(x, y, z, (byte) 1);
					}
		/*for (int i = 0; i < 30; i++){
			voxelWorld.set(60,i,0, (byte)1);
		}
		for (int i = 5; i < 50; i++){
			voxelWorld.set(56,i,0, (byte)1);
		}*/
		
		
		for (int c = 0; c < 1000; c+= MathUtils.random(14, 15))
			for (int d = 0; d < 10; d++)
				for (int i = 0; i < 4; i++){
					voxelWorld.set(c,i,d, (byte)1);
					//voxelWorld.set(0,i,0, (byte)1);
				}
		//voxelWorld.set(0,0,0, (byte)1);*/

	}

	public static  BlockDefinition[] getBlockDefs(TextureRegion[][] tiles) {
		BlockDefinition[] defs = new BlockDefinition[32];
		defs[0] = new BlockDefinition(tiles, 0)
		{

			@Override
			public void onUpdate(int x, int y, int z, VoxelWorld world) {
			}
			
		};
		defs[0].dayLightLoss = 0;
		defs[0].isSolid = false;
		defs[0].isEmpty = true;
		//for (int i = 1; i < 32; i++){
		//	defs[i] = new BlockDefinition(tiles, i);
			//BlockDefinition.add(i, defs[i]);
		//}
		
		defs[1] = new BlockDefinition(tiles, 1){

			@Override
			public void onUpdate(int x, int y, int z, VoxelWorld world) {
			}
			
		};
		
		defs[10] = new TopBottomBlock(tiles, 8, 1, 10);
		
		
		return defs;
	}

	@Override
	public void initMenu(final World world, Skin skin, Stage stage) {
		super.initMenu(world, skin, stage);
		dragger = new Actor();
		dragger.setSize(100000, 100000);
		clicker = new Actor();
		clicker.setSize(100000, 100000);
		//table.setFillParent(true);
		//dragTable.setSize(Gdx.graphics.getWidth(), Gdx.graphics.getHeight());
		
		setInput(dragger, clicker, player);
		//Group group = (Group) stage.getActors().get(0);
		//stage.addActor(dragger);
	}

	public void setInput(Actor dragger, Actor clicker, final Entity player){
		dragger.addListener(new InputListener(){
			/*@Override
			public void drag (InputEvent event, float x, float y, int pointer) {

				tmp.set(0,0,0);
				camera.project(tmp);
	
				tmp.set(0,0,.5f);
				tmp2.set(Gdx.input.getDeltaX(), Gdx.input.getDeltaY(), .5f);
				camera.unproject(tmp);
				camera.unproject(tmp2);
				tmp2.sub(tmp);
				tmp2.z = 0;
			
				camera.position.sub(tmp2.x, 0, 0);
				camera.update(true);
				//Gdx.app.log(TAG,  "dragged"+tmp2);
				player.get(Target.class).v.x = camera.position.x;;
			}*/
			@Override
			public boolean keyDown(InputEvent event, int keycode) {
				//player.get(Target.class).v.x *= -1;
				Gdx.app.log(TAG, "df"+keycode);

				return true;
			};
			
			public boolean touchDown (InputEvent event, float x, float y, int pointer, int button) {
				Move move = player.get(Move.class);
				AABBBody body = player.get(AABBBody.class);
				if (body.onGround){
					//Move move = player.get(Move.class);
					//move.jumping = true;
					move.jumpEndTick = (int) (EngineScreen.tick + move.jumpTime / EngineScreen.timeStep);
					//Gdx.app.log(TAG, "jump"+(move.jumpTime / EngineScreen.timeStep));

					ActionComponent actionC = player.get(ActionComponent.class);
					actionC.action.actions.clear();
					actionC.action.actions.getRoot().insertAfterMe(Pools.obtain(AJump.class));
				}
				if (body.onWall){
					//move.jumping = true;
					move.jumpEndTick = (int) (EngineScreen.tick + move.jumpTime / EngineScreen.timeStep);	
					Brain brain = player.get(Brain.class);;
					//Gdx.app.log(TAG, "jump"+(move.jumpTime / EngineScreen.timeStep));
					//move.rotation += 180;
					//move.rotation %= 360;
					brain.getShortTarget().scl(-1f, 0, 0);
					//player.get(Target.class).v.x *= -1;
					ActionComponent actionC = player.get(ActionComponent.class);
					actionC.action.actions.clear();
					Gdx.app.log(TAG, "wjump "+actionC.action.actions.size());
					//body.wasOnWall = false;
					body.onWall = false;
					body.offWall = true;
					actionC.action.actions.getRoot().insertAfterMe(Pools.obtain(AJump.class));
				}	                return true;
	        }
	 
	        public void touchUp (InputEvent event, float x, float y, int pointer, int button) {
	        	//player.get(Move.class).jumping = false;
	        	player.get(Move.class).jumpEndTick = EngineScreen.tick;
				//Gdx.app.log(TAG, "NOT JUMPSING ANY MORE");	       
	        	}

			
			
		});
		
		
	}
	
	
}
