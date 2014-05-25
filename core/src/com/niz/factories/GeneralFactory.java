package com.niz.factories;


import com.artemis.Entity;
import com.artemis.World;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.assets.AssetManager;
import com.badlogic.gdx.graphics.Camera;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.g2d.Sprite;
import com.badlogic.gdx.graphics.g2d.TextureAtlas;
import com.badlogic.gdx.graphics.g3d.Material;
import com.badlogic.gdx.graphics.g3d.Model;
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
import com.badlogic.gdx.scenes.scene2d.ui.*;
import com.badlogic.gdx.scenes.scene2d.utils.ActorGestureListener;
import com.badlogic.gdx.scenes.scene2d.utils.ClickListener;
import com.badlogic.gdx.tests.g3d.voxel.BlockDefinition;
import com.badlogic.gdx.tests.g3d.voxel.VoxelChunk;
import com.badlogic.gdx.tests.g3d.voxel.VoxelWorld;
import com.badlogic.gdx.utils.Pools;
import com.niz.ColorPicker;
import com.niz.ColorPickerButton;
import com.niz.EngineScreen;
import com.niz.RayCaster;
import com.niz.actions.AJump;
import com.niz.actions.AStand;
import com.niz.actions.ActionList;
import com.niz.blocks.EmptyBlockDefinition;
import com.niz.component.*;
import com.niz.component.systems.*;

public class GeneralFactory extends GameFactory{
	public static final float VIEWPORT_SIZE = 20;

	private static final String TAG = "General Factory";


	//VoxelSystem voxelSys;
	//PlatformerInputSystem inputSys;


	Actor dragger, clicker;
	
	Vector3 tmp = new Vector3(), tmp2 = new Vector3();

	private CameraControllerSystem cameraSystem;


	





	@Override
	public void newGame(World world, Stage stage) {
		//Gdx.app.log(TAG, "NEW GAME");
		
		VoxelSystem voxel = world.getSystem(VoxelSystem.class);
		setDefaultMap(voxel.voxelWorld);
        AssetsSystem as = world.getSystem(AssetsSystem.class);


        playerModel(as);


        stage.addActor(dragger);

        VoxelChunk.defs = GeneralFactory.getBlockDefs(world);


        Entity e = player;
		world.addEntity(e);
		
		Position pos = e.add(Position.class);
		pos.pos.set(0f, 5, .5f);
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
		

		e.add(Player.class);
		e.add(CameraInfluencer.class);

		
		
		e.add(PositionRollingAverage.class).size = 2;//rolling average of position
		e.add(VelocityPredictor.class).scale = 240f;
		e.get(VelocityPredictor.class).y = false;
		e.add(VelocityRollingAverage.class).size = 100;

		e.add(DebugVector.class).add(e.get(VelocityRollingAverage.class).result, Color.CYAN);


		Entity camC = world.createEntity();
		camC.add(CameraController.class);
		camC.add(Position.class);

	
		world.addEntity(camC);
		
		Entity tester = world.createEntity();
		tester.add(Position.class).pos.set(0,0,1);
		//tester.add(DebugPosition.class);
		world.addEntity(tester);
		
		
		
		
		
	}


	private boolean getComponentFromName(String string) {
		return false;
	}

	@Override
	public void load(World world) {
		// TODO Auto-generated method stub
		
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
						voxelWorld.set(x, y, z, (byte) 1);
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
					voxelWorld.set(c,i,d, (byte)1);
					//voxelWorld.set(0,i,0, (byte)1);
				}
		//voxelWorld.set(0,0,0, (byte)1);*/

	}

	public static  BlockDefinition[] getBlockDefs(World world) {
        AssetsSystem assets = world.getSystem(AssetsSystem.class);
        TextureAtlas tiles = assets.getTextureAtlas("tiles");


        Gdx.app.log(TAG, "BLOCK DEFS");
		BlockDefinition[] defs = new BlockDefinition[256];
		defs[0] = new BlockDefinition(tiles.findRegion("air"), 0)
		{

			@Override
			public void onUpdate(int x, int y, int z, VoxelWorld world) {
			}
			
		};
		defs[0].lightValue = 15;
		defs[0].dayLightLoss = 0;
		defs[0].isSolid = false;
		defs[0].isEmpty = true;
		//for (int i = 1; i < 32; i++){
		//	defs[i] = new BlockDefinition(tiles, i);
			//BlockDefinition.add(i, defs[i]);
		//}
		
		defs[1] = new BlockDefinition(tiles.findRegion("dirt"), 1){

			@Override
			public void onUpdate(int x, int y, int z, VoxelWorld world) {
			}
			
		};
		
		//defs[10] = new TopBottomBlock(tiles, 8, 1, 10);
		

        for (int i = 0; i < 256; i++){
            if (defs[i] == null){
                defs[i] = new EmptyBlockDefinition(tiles.findRegion("empty"), i);
            }
        }

		return defs;
	}

	@Override
	public void initMenu(final World world, Skin skin, Stage stage, AssetManager assets, float timestep) {
		super.initMenu(world, skin, stage, assets, timestep);
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

    @Override
    protected void editor(final World world, final Stage stage, final Skin skin, Sprite sprite, Sprite spritesel) {
        //color picker
        VoxelChunk.defs = GeneralFactory.getBlockDefs(world);

        final Color[] blockColors = world.getSystem(EditVoxelSystem.class).blockColors;
        final ButtonGroup btnGr = new ButtonGroup();
        //Gdx.app.log(TAG, "editor"+(sprite == null));
        for (int i = 0; i < 8; i++)
        {
            final ColorPickerButton colorA = new ColorPickerButton(skin, sprite, spritesel, i*2);
            final ColorPickerButton colorB = new ColorPickerButton(skin, sprite, spritesel, i*2+1);
            btnGr.add(colorA);
            btnGr.add(colorB);
            colorA.addListener(new ActorGestureListener(){
                public boolean longPress(Actor actor,
                                         float x,
                                         float y){
                    openColorSelectionScreen(colorA, stage, skin, blockColors);
                    return true;
                }
            });
            colorB.addListener(new ActorGestureListener(){
                public boolean longPress(Actor actor,
                                         float x,
                                         float y){
                    openColorSelectionScreen(colorB, stage, skin, blockColors);
                    return true;
                }
            });
            paletteTable.add(colorA).left();
            paletteTable.add(colorB).left();
            paletteTable.row();
        }

        //paletteTable.top();

        Actor touchActor = new Actor();
        touchActor.setSize(Gdx.graphics.getWidth(), Gdx.graphics.getHeight());

        touchActor.addListener(new ClickListener(){
            @Override
            public void clicked(InputEvent event,
                                float x,
                                float y){
                //raycast
                Camera camera = world.getSystem(CameraSystem.class).camera;
                VoxelWorld vw = world.getSystemOrSuperClass(VoxelSystem.class).voxelWorld;
                float sx = event.getStageX(), sy = event.getStageY();
                src.set(sx,sy,0);
                dst.set(sx,sy,1);
                camera.unproject(src);
                camera.unproject(dst);
                Gdx.app.log(TAG, "trace " +sx+","+sy+ " from "+src+" to "+dst);

                ray.trace(src, dst);
                while (ray.hasNext){
                    ray.next();
                    if (vw.get(ray.x, ray.y, ray.z) != 0 ){
                        tmp.set(ray.x, ray.y, ray.z);
                        tmp.add(BlockDefinition.normals[ray.face]);
                        vw.set(tmp, (byte)((ColorPickerButton)(btnGr.getChecked())).colorIndex);
                        Gdx.app.log(TAG, "set"+tmp);

                        break;
                    }
                    Gdx.app.log(TAG, "trace "+ray.x+","+ray.y+","+ray.z);
                }
                //if hits block or floor place block

            }
        });

        stage.addActor(touchActor);
        paletteTable.setFillParent(true);
        paletteTable.left();
        stage.addActor(paletteTable);
        VoxelWorld vw = world.getSystemOrSuperClass(VoxelSystem.class).voxelWorld;

        for (int x = 0; x < 15; x++)
            for(int y = 0; y < 2; y++)
                for (int z = 0; z < 15; z++)
                    vw.set(x,y,z,(byte)1);

        //save/load btns
        //default chunk
        //new btn(selectable size)


        Entity e = player;
        world.addEntity(e);

        Position pos = e.add(Position.class);
        pos.pos.set(0f, 5, .5f);

        e.add(Brain.class).getShortTarget().set(100000, 0, 0);

        Move move = e.add(Move.class);
        move.jumpStrength = 1.5f;
        ActionList actionList = e.add(ActionComponent.class).action;



        //ModelInfo mod = e.add(ModelInfo.class);
        //AnimationController animController = new AnimationController(playerModel);
        //mod.set(playerModel, animController );


        e.add(Player.class);
        e.add(CameraInfluencer.class);

        e.add(PositionRollingAverage.class).size = 2;//rolling average of position
        e.add(VelocityPredictor.class).scale = 240f;
        e.get(VelocityPredictor.class).y = false;
        e.add(VelocityRollingAverage.class).size = 100;

        e.add(DebugVector.class).add(e.get(VelocityRollingAverage.class).result, Color.CYAN);


        Entity camC = world.createEntity();
       // camC.add(CameraController.class);
        //camC.add(Position.class);

        world.addEntity(camC);



    }
    RayCaster ray = new RayCaster();
    Table paletteTable = new Table();
    Vector3 src = new Vector3(), dst = new Vector3();
    public static ColorPicker colorPicker;
    Button okBtn, cancelBtn;
    public static ColorPickerButton currentSelectedColor;
    Table selectTable = new Table();

    private void openColorSelectionScreen(ColorPickerButton actor, final Stage stage, Skin skin, final Color[] blockColors) {
        Gdx.app.log(TAG, "sdfjksdfjksdfjk");
        if (colorPicker == null){
            colorPicker = new ColorPicker(skin);
            okBtn = new Button(skin);
            okBtn.add(new Label("Ok", skin));
            cancelBtn = new Button(skin);
            cancelBtn.add(new Label("Cancel", skin));
            okBtn.addListener(new ClickListener(){
                public void clicked(InputEvent event,
                                    float x,
                                    float y){

                    currentSelectedColor.setColor(colorPicker.getSelectedColor(), blockColors);
                    stage.getActors().removeValue(selectTable, true);


                    stage.addActor(paletteTable);
                }
            });
            cancelBtn.addListener(new ClickListener() {
                public void clicked(InputEvent event,
                                    float x,
                                    float y) {

                    stage.getActors().removeValue(selectTable, true);


                    stage.addActor(paletteTable);

                }
            });

            selectTable.setFillParent(true);
            selectTable.add(colorPicker);
            selectTable.row();
            selectTable.add(okBtn);
            selectTable.add(cancelBtn);


        }

        currentSelectedColor = actor;




        stage.addActor(selectTable);
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

				if (body.onWall && body.onGround){
					//move.jumping = true;
					move.jumpEndTick = (int) (EngineScreen.tick + move.jumpTime / EngineScreen.timeStep);	
					Brain brain = player.get(Brain.class);;
					//Gdx.app.log(TAG, "jump"+(move.jumpTime / EngineScreen.timeStep));
					//move.rotation += 180;
					//move.rotation %= 360;
					//brain.getShortTarget().scl(-1f, 0, 0);
					//player.get(Target.class).v.x *= -1;
					ActionComponent actionC = player.get(ActionComponent.class);
					actionC.action.actions.clear();
					Gdx.app.log(TAG, "wjump "+actionC.action.actions.size());
					//body.wasOnWall = false;
					body.onWall = false;
					body.offWall = true;

					actionC.action.actions.getRoot().insertAfterMe(Pools.obtain(AJump.class));
                    return true;
				}
                if (body.onGround){
                    //Move move = player.get(Move.class);
                    //move.jumping = true;
                    move.jumpEndTick = (int) (EngineScreen.tick + move.jumpTime / EngineScreen.timeStep);
                    //Gdx.app.log(TAG, "jump"+(move.jumpTime / EngineScreen.timeStep));

                    ActionComponent actionC = player.get(ActionComponent.class);
                    actionC.action.actions.clear();
                    actionC.action.actions.getRoot().insertAfterMe(Pools.obtain(AJump.class));
                    return true;
                }
                return false;
	        }
	 
	        public void touchUp (InputEvent event, float x, float y, int pointer, int button) {
	        	//player.get(Move.class).jumping = false;
	        	player.get(Move.class).jumpEndTick = EngineScreen.tick;
				//Gdx.app.log(TAG, "NOT JUMPSING ANY MORE");	       
	        	}

			
			
		});
		
		
	}
	
	
}
