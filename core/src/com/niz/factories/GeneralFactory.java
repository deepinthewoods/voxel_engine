package com.niz.factories;


import com.artemis.World;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.g2d.TextureAtlas;
import com.badlogic.gdx.graphics.g3d.Material;
import com.badlogic.gdx.graphics.g3d.ModelInstance;
import com.badlogic.gdx.graphics.g3d.attributes.ColorAttribute;
import com.badlogic.gdx.graphics.g3d.model.Node;
import com.badlogic.gdx.graphics.g3d.model.NodePart;
import com.badlogic.gdx.graphics.g3d.utils.AnimationController;
import com.badlogic.gdx.math.MathUtils;
import com.badlogic.gdx.math.Vector3;
import com.badlogic.gdx.scenes.scene2d.Actor;
import com.badlogic.gdx.tests.g3d.voxel.BlockDefinition;
import com.badlogic.gdx.tests.g3d.voxel.VoxelWorld;
import com.niz.component.systems.AssetsSystem;
import com.niz.component.systems.CameraControllerSystem;

public class GeneralFactory extends GameFactory{
	public static final float VIEWPORT_SIZE = 20;

	private static final String TAG = "General Factory";


	//VoxelSystem voxelSys;
	//PlatformerInputSystem inputSys;


	Actor dragger, clicker;
	
	Vector3 tmp = new Vector3(), tmp2 = new Vector3();

	private CameraControllerSystem cameraSystem;


	









	private boolean getComponentFromName(String string) {
		return false;
	}



	
	
	ModelInstance playerModel;
	private void playerModel(AssetsSystem assets){
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
		playerModel = new ModelInstance(assets.getModel("humanmodel")
				, all
				);
		
		setColors(playerModel, skin, colors);
		AnimationController playerAnimController = new AnimationController(playerModel);
		
		
		ModelInstance zombieModel = new ModelInstance(assets.getModel("humanmodel")
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
			for (int y = 1; y < 2; y++)
				for (int z = 0; z < 12; z++){
					//if (MathUtils.randomBoolean()) {
						//Gdx.app.log(TAG, "ww"+x+","+y+","+z+(voxelWorld == null));
						voxelWorld.set(x, y, z, 0, (byte) 1);
					}
		/*for (int i = 0; i < 30; i++){
			voxelWorld.set(60,i,0, (byte)1);
		}
		for (int i = 5; i < 50; i++){
			voxelWorld.set(56,i,0, (byte)1);
		}*/
		
		
		for (int c = 0; c < 1000; c+= MathUtils.random(12, 20))
			for (int d = 0, max = MathUtils.random(10); d < max; d++)
				for (int i = 0; i < 4; i++){
					voxelWorld.set(c,i,d, 0, (byte)1);
					//voxelWorld.set(0,i,0, (byte)1);
				}
		//voxelWorld.set(0,0,0, (byte)1);*/

	}

	public static  BlockDefinition[] getBlockDefs(World world) {
        AssetsSystem assets = world.getSystem(AssetsSystem.class);
        TextureAtlas tiles = assets.getTextureAtlas("tiles");


        Gdx.app.log(TAG, "BLOCK DEFS");
		BlockDefinition[] defs = new BlockDefinition[256];
		defs[0] = new BlockDefinition( 0)
		{

			@Override
			public void onUpdate(int x, int y, int z, VoxelWorld world) {
			}
			
		};
		defs[0].lightValue = 15;
		//defs[0].dayLightLoss = 0;
		defs[0].isSolid = false;
		defs[0].isEmpty = true;
		//for (int i = 1; i < 32; i++){
		//	defs[i] = new BlockDefinition(tiles, i);
			//BlockDefinition.add(i, defs[i]);
		//}
		
		defs[1] = new BlockDefinition( 1){

			@Override
			public void onUpdate(int x, int y, int z, VoxelWorld world) {
			}
			
		};
		
		//defs[10] = new TopBottomBlock(tiles, 8, 1, 10);
		

        for (int i = 0; i < 256; i++){
            if (defs[i] == null){
                defs[i] = new BlockDefinition(i);
            }
        }

		return defs;
	}









	
}
