package com.niz.factories;

import voxel.BlockDefinition;

import com.artemis.Entity;
import com.artemis.World;
import com.badlogic.gdx.assets.AssetManager;
import com.badlogic.gdx.graphics.Camera;
import com.badlogic.gdx.graphics.Color;
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
import com.badlogic.gdx.tests.g3d.voxel.VoxelWorld;
import com.niz.actions.AStand;
import com.niz.actions.ActionList;
import com.niz.blocks.TopBottomBlock;
import com.niz.component.AABBBody;
import com.niz.component.ActionComponent;
import com.niz.component.ModelInfo;
import com.niz.component.Move;
import com.niz.component.Physics;
import com.niz.component.Player;
import com.niz.component.Position;
import com.niz.component.Target;
import com.niz.component.input.PlatformerInputSystem;
import com.niz.component.systems.AABBBodySystem;
import com.niz.component.systems.ActionSystem;
import com.niz.component.systems.BucketedSystem;
import com.niz.component.systems.ModelRenderingSystem;
import com.niz.component.systems.MovementSystem;
import com.niz.component.systems.PhysicsSystem;
import com.niz.component.systems.VoxelRenderingSystem;

public class PlatformerFactory extends GameFactory{
	public static final float VIEWPORT_SIZE = 20;

	private static final String TAG = "Platformer Factory";
	
	//VoxelSystem voxelSys;
	PlatformerInputSystem inputSys;
	
	@Override
	public void init(World world, AssetManager assets, Camera camera) {
		
		assets.load("data/tiles.png", Texture.class);
		assets.load("data/fades.png", Pixmap.class);
		assets.load("data/humanmodel.g3db", Model.class);
	}

	@Override
	public void doneLoading(float timeStep, World world, AssetManager assets, Camera camera, ModelBatch modelBatch) {
		TextureRegion[][] tiles = new TextureRegion(assets.get("data/tiles.png", Texture.class)).split(16, 16);
		BlockDefinition[] defs = getBlockDefs(tiles);
		Pixmap fades = assets.get("data/fades.png", Pixmap.class);
		playerModel(assets);
		
		camera.position.set(8, 14, 22);
		camera.far =52;
		camera.near = 8;
		camera.rotate(25, -1, 0, 0);
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
		VoxelRenderingSystem voxelR = new VoxelRenderingSystem(defs);	
		voxelR.set(modelBatch, camera, tiles[0]);
		world.setDrawSystem(voxelR);
		
		ModelRenderingSystem modelR = new ModelRenderingSystem();
		modelR.set(modelBatch, camera, voxelR.lights);
		world.setDrawSystem(modelR );

		
		
		
		inputSys = new PlatformerInputSystem(camera, voxelR.voxelWorld);
		//inputSys.setPlayer(e);
		world.setInputSystem(inputSys);
		
		world.initialize();
		
	}

	
	private void setDefaultMap(VoxelWorld voxelWorld) {
		for (int x = 0; x < 200; x++)
			for (int y = 0; y < 2; y++)
				for (int z = 0; z < 1; z++){
					//if (MathUtils.randomBoolean()) {
						//Gdx.app.log(TAG, "ww"+x+","+y+","+z+(voxelWorld == null));
						voxelWorld.set(x, y, z, (byte) 10);
					}
	}

	@Override
	public void newGame(World world) {
		//Gdx.app.log(TAG, "NEW GAME");
		
		VoxelRenderingSystem voxelRR = world.getSystem(VoxelRenderingSystem.class);
		setDefaultMap(voxelRR.voxelWorld );
		
		Entity e;
		e = world.createEntity();
		world.addEntity(e);
		
		Position pos = e.add(Position.class);
		pos.pos.set(1f, 5, .5f);
		e.add(Physics.class);
		e.add(AABBBody.class).ys0 = -.15f;
		e.add(Target.class);
		e.add(Move.class);
		ActionList actionList = e.add(ActionComponent.class).action;
		actionList.actions.add(AStand.class);
		
		ModelInfo mod = e.add(ModelInfo.class);
		AnimationController animController = new AnimationController(playerModel);
		mod.set(playerModel, animController );
		
		e.add(Move.class);
		e.add(Player.class);
		//inputSys.setPlayer(e);
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
				, Color.BLUE
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
	
	
}
