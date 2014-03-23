package com.niz.factories;

import voxel.BlockDefinition;

import com.artemis.Entity;
import com.artemis.World;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.assets.AssetManager;
import com.badlogic.gdx.graphics.Camera;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.PerspectiveCamera;
import com.badlogic.gdx.graphics.Pixmap;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.graphics.g3d.Material;
import com.badlogic.gdx.graphics.g3d.Model;
import com.badlogic.gdx.graphics.g3d.ModelBatch;
import com.badlogic.gdx.graphics.g3d.ModelInstance;
import com.badlogic.gdx.graphics.g3d.attributes.ColorAttribute;
import com.badlogic.gdx.graphics.g3d.model.Node;
import com.badlogic.gdx.graphics.g3d.model.NodePart;
import com.badlogic.gdx.graphics.g3d.utils.AnimationController;
import com.badlogic.gdx.math.Vector3;
import com.badlogic.gdx.scenes.scene2d.Actor;
import com.badlogic.gdx.scenes.scene2d.InputEvent;
import com.badlogic.gdx.scenes.scene2d.InputListener;
import com.badlogic.gdx.scenes.scene2d.Stage;
import com.badlogic.gdx.scenes.scene2d.ui.Skin;
import com.badlogic.gdx.tests.g3d.voxel.VoxelWorld;
import com.badlogic.gdx.utils.Pools;
import com.niz.EngineScreen;
import com.niz.ShapeBatch;
import com.niz.actions.AJump;
import com.niz.actions.AStand;
import com.niz.actions.ActionList;
import com.niz.blocks.TopBottomBlock;
import com.niz.component.AABBBody;
import com.niz.component.ActionComponent;
import com.niz.component.Brain;
import com.niz.component.CameraController;
import com.niz.component.CameraInfluences;
import com.niz.component.ModelInfo;
import com.niz.component.Move;
import com.niz.component.Physics;
import com.niz.component.Player;
import com.niz.component.Position;
import com.niz.component.PositionLimiter;
import com.niz.component.RollingAverage;
import com.niz.component.VelocityPredictor;
import com.niz.component.systems.AABBBodySystem;
import com.niz.component.systems.ActionSystem;
import com.niz.component.systems.BrainSystem;
import com.niz.component.systems.BucketedSystem;
import com.niz.component.systems.CameraBehaviourSystem;
import com.niz.component.systems.CameraFollowerSystem;
import com.niz.component.systems.DebugPositionSystem;
import com.niz.component.systems.DebugVectorSystem;
import com.niz.component.systems.ModelRenderingSystem;
import com.niz.component.systems.MovementSystem;
import com.niz.component.systems.PhysicsSystem;
import com.niz.component.systems.PositionLimiterSystem;
import com.niz.component.systems.RollingAverageSystem;
import com.niz.component.systems.VelocityPredictionSystem;
import com.niz.component.systems.VoxelMeshingSystem;
import com.niz.component.systems.VoxelRenderingSystem;
import com.niz.component.systems.VoxelSystem;

public class PlatformerFactory extends GameFactory{
	public static final float VIEWPORT_SIZE = 20;

	private static final String TAG = "Platformer Factory";
	
	//VoxelSystem voxelSys;
	//PlatformerInputSystem inputSys;

	private Entity player;
	
	Actor dragger, clicker;
	
	Vector3 tmp = new Vector3(), tmp2 = new Vector3();

	private CameraFollowerSystem cameraSystem;
	
	@Override
	public void register(World world, AssetManager assets, Camera camera) {
		
		assets.load("data/tiles.png", Texture.class);
		assets.load("data/fades.png", Pixmap.class);
		assets.load("data/humanmodel.g3db", Model.class);
		player = world.createEntity();
		EngineScreen.player = player;
	}

	@Override
	public void doneLoading(float timeStep, World world, AssetManager assets, Camera camera, ModelBatch modelBatch, ShapeBatch shapeBatch) {
		TextureRegion[][] tiles = new TextureRegion(assets.get("data/tiles.png", Texture.class)).split(16, 16);
		BlockDefinition[] defs = getBlockDefs(tiles);
		Pixmap fades = assets.get("data/fades.png", Pixmap.class);
		playerModel(assets);
		PerspectiveCamera cam = (PerspectiveCamera) camera;
		cam.fieldOfView = 67;
		camera.position.set(10, 10f, 22);
		camera.far =52;
		camera.near = 8;
		camera.rotate(0, -1, 0, 0);
		camera.update();
		
		world.setDelta(timeStep);
		PhysicsSystem physics = new PhysicsSystem(1, 100, timeStep);
		world.setSystem(physics );
		Physics.physics = physics.engine;
		world.setSystem(new MovementSystem());
		world.setSystem(new AABBBodySystem());
		world.setSystem(new BucketedSystem());
		world.setSystem(new ActionSystem());
		world.setSystem(new MovementSystem());
		VoxelSystem voxelSystem = new VoxelSystem(10,10,1, false, false, false, false, false, false, defs, tiles[0]);
		
		world.setSystem(voxelSystem);
		
		world.setSystem(new VoxelMeshingSystem());
		
		world.setSystem(new RollingAverageSystem());
		world.setSystem(new VelocityPredictionSystem());
		
		VoxelRenderingSystem voxelR = new VoxelRenderingSystem();	
		//voxelR.set(modelBatch, camera, tiles[0]);
		world.setDrawSystem(voxelR);
		
		ModelRenderingSystem modelR = new ModelRenderingSystem();
		//modelR.set(modelBatch, camera, voxelR.lights);
		world.setDrawSystem(modelR );
		world.setSystem(new BrainSystem());
		world.setDrawSystem(new DebugVectorSystem());
		world.setDrawSystem(new DebugPositionSystem());
		//world.setDrawSystem(new TargetLineRenderingSystem());
		
		cameraSystem = new CameraFollowerSystem(camera);
		world.setSystem(cameraSystem);
		world.setSystem(new CameraBehaviourSystem());
		world.setSystem(new PositionLimiterSystem());
		//inputSys = new PlatformerInputSystem(camera, voxelR.voxelWorld);
		//inputSys.setPlayer(e);
		//world.setInputSystem(inputSys);
		
		
	}
	@Override
	public void newGame(World world, Stage stage) {
		//Gdx.app.log(TAG, "NEW GAME");
		
		VoxelSystem voxel = world.getSystem(VoxelSystem.class);
		setDefaultMap(voxel.voxelWorld);
		
		
		
		Entity e = player;
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
		
		e.add(Move.class);
		e.add(Player.class);
		//e.add(DebugPosition.class);
		//inputSys.setPlayer(e);
		cameraSystem.prevPosition.set(e.get(Position.class).pos);
		stage.addActor(dragger);
		
		
		Entity posAcc = world.createEntity();
		posAcc.add(Position.class);
		posAcc.add(RollingAverage.class).set(e.get(Position.class).pos, 10);
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
		camC.add(PositionLimiter.class).set(e.get(Position.class).pos, 10f);
		world.addEntity(camC);
		
		Entity tester = world.createEntity();
		tester.add(Position.class).pos.set(0,0,1);
		//tester.add(DebugPosition.class);
		world.addEntity(tester);
		
		
		
		
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
		for (int x = 0; x < 200; x++)
			for (int y = 0; y < 1; y++)
				for (int z = 0; z < 1; z++){
					//if (MathUtils.randomBoolean()) {
						//Gdx.app.log(TAG, "ww"+x+","+y+","+z+(voxelWorld == null));
						voxelWorld.set(x, y, z, (byte) 10);
					}
		for (int i = 0; i < 30; i++){
			voxelWorld.set(60,i,0, (byte)1);
		}
		for (int i = 5; i < 50; i++){
			voxelWorld.set(56,i,0, (byte)1);
		}
		
		for (int i = 0; i < 30; i++){
			voxelWorld.set(i+61,30,0, (byte)1);
			voxelWorld.set(0,i,0, (byte)1);
		}
	}

	private BlockDefinition[] getBlockDefs(TextureRegion[][] tiles) {
		BlockDefinition[] defs = new BlockDefinition[32];
		defs[0] = new BlockDefinition(tiles, 0)
		{

			@Override
			public void onUpdate(int x, int y, int z, VoxelWorld world) {
			}
			
		};
		defs[0].dayLightLoss = 0;
		defs[0].isSolid = false;
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
	public void initMenu(final World world, Skin skin, Stage stage, Camera camera) {
		super.initMenu(world, skin, stage, camera);
		dragger = new Actor();
		dragger.setSize(100000, 100000);
		clicker = new Actor();
		clicker.setSize(100000, 100000);
		//table.setFillParent(true);
		//dragTable.setSize(Gdx.graphics.getWidth(), Gdx.graphics.getHeight());
		
		setInput(dragger, clicker, player, camera);
		//Group group = (Group) stage.getActors().get(0);
		//stage.addActor(dragger);
	}

	public void setInput(Actor dragger, Actor clicker, final Entity player, final Camera camera){
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
					//Gdx.app.log(TAG, "wjump "+actionC.action.actions.size());
					//body.wasOnWall = false;
					body.onWall = false;
					
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
