package com.niz.factories;

import voxel.BlockDefinition;

import com.artemis.World;
import com.artemis.systems.DrawSystem;
import com.badlogic.gdx.assets.AssetManager;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.OrthographicCamera;
import com.badlogic.gdx.graphics.Pixmap;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.graphics.g3d.Environment;
import com.badlogic.gdx.graphics.g3d.Material;
import com.badlogic.gdx.graphics.g3d.Model;
import com.badlogic.gdx.graphics.g3d.ModelBatch;
import com.badlogic.gdx.graphics.g3d.ModelInstance;
import com.badlogic.gdx.graphics.g3d.attributes.ColorAttribute;
import com.badlogic.gdx.graphics.g3d.model.Node;
import com.badlogic.gdx.graphics.g3d.model.NodePart;
import com.badlogic.gdx.graphics.g3d.utils.AnimationController;
import com.niz.blocks.TopBottomBlock;
import com.niz.component.systems.AABBBodySystem;
import com.niz.component.systems.BucketedSystem;
import com.niz.component.systems.ModelRenderingSystem;
import com.niz.component.systems.MovementSystem;
import com.niz.component.systems.PhysicsSystem;
import com.niz.component.systems.VoxelRenderingSystem;
import com.niz.component.systems.VoxelSystem;

public class PlatformerFactory extends GameFactory{
	VoxelSystem voxelSys;
	@Override
	public void init(World world, float timeStep, AssetManager assets, OrthographicCamera worldCamera, ModelBatch modelBatch) {
		world.setDelta(timeStep);
		
		world.setSystem(new PhysicsSystem(1, 100, timeStep));
		voxelSys = new VoxelSystem(4,4,1);
		world.setSystem(voxelSys);
		world.setSystem(new MovementSystem());
		world.setSystem(new AABBBodySystem());
		world.setSystem(new BucketedSystem());
		
		Environment env = new Environment();
		env.set( new ColorAttribute(ColorAttribute.AmbientLight, 1f, 1f, 1f, 1f) );	
		
		
		VoxelRenderingSystem voxelR = new VoxelRenderingSystem(voxelSys.voxelWorld);	
		voxelR.set(modelBatch, worldCamera, env);
		world.setDrawSystem(voxelR);
		
		ModelRenderingSystem modelR = new ModelRenderingSystem();
		modelR.set(modelBatch, worldCamera, env);
		world.setDrawSystem(modelR );
		
		world.setInputSystem(new PlatformerInputSystem(worldCamera));
	}

	@Override
	public void doneLoading(AssetManager assets) {
		TextureRegion[][] tiles = assets.get("tiles.png", TextureRegion.class).split(16, 16);
		
		BlockDefinition[] blockDefs = getBlockDefs(tiles);
		Pixmap fades = assets.get("data/fades.png", Pixmap.class);
		
		voxelSys.set(tiles, blockDefs, fades);
		
		
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
		playerModel = new ModelInstance(assets.get("data/_voxel5.g3db", Model.class)
				, all
				);

		setColors(playerModel, skin, colors);
		AnimationController playerAnimController = new AnimationController(playerModel);
		
		
		ModelInstance zombieModel = new ModelInstance(assets.get("data/_voxel5.g3db", Model.class)
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
		defs[0] = new BlockDefinition(tiles, 0);
		defs[0].dayLightLoss = 0;
		defs[0].isSolid = false;
		//for (int i = 1; i < 32; i++){
		//	defs[i] = new BlockDefinition(tiles, i);
			//BlockDefinition.add(i, defs[i]);
		//}
		
		defs[1] = new BlockDefinition(tiles, 1);
		
		defs[10] = new TopBottomBlock(tiles, 8, 1, 10);
		
		
		return defs;
	}
	
	
}
