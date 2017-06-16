package com.niz.component.systems;


import com.artemis.Aspect;
import com.artemis.ComponentMapper;
import com.artemis.Entity;
import com.artemis.systems.EntitySystem;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.TextureAtlas;
import com.badlogic.gdx.graphics.g3d.Renderable;
import com.badlogic.gdx.math.Vector3;
import com.badlogic.gdx.tests.g3d.voxel.*;
import com.badlogic.gdx.utils.Array;
import com.badlogic.gdx.utils.PauseableThread;
import com.niz.NizMain;
import com.niz.component.Player;
import com.niz.component.Position;

public class VoxelSystem extends EntitySystem {

    private static final String TAG = "voxel system";
    //public GreedyMesher mesher;
   // private MeshBatcher batch;
    public transient VoxelWorld voxelWorld;
    private Renderable baseRenderable;
    private Texture voxelTexture;
    protected ComponentMapper<Position> posM;

    //private BlockDefinition[] defs;
	//private TextureRegion[] tiles;
    //public static TextureRegion white;
	public VoxelSystem() {
		super(Aspect.getAspectForAll(Player.class, Position.class));

		int x = 12, y = 4, z = 4;
        x = 8; y = 8; z = 8;
		//batch = new MeshBatcher(10000000, 10000000, 13);
        //mesher = new GreedyMesher(batch);

        voxelWorld = new VoxelWorld(1000
                , 16, 16, 1
				);
       // Gdx.app.log(TAG, "material"+voxelWorld.m);

	}

    public VoxelSystem(Aspect aspect){
        super(aspect);
    }

    transient Vector3 tmp = new Vector3();
	
	

	@Override
	protected void processEntities(Array<Entity> entities) {
        if (entities.size == 0) return;
        //voxelWorld.makeMesh(), mesher, batch);
        VoxelChunk c = voxelWorld.getChunkFromVoxel(1, 1, 1, 0);
        for (int i = 0; i < 100; i++) {
            //Gdx.app.log(TAG, "voxelH"+voxelWorld.get(0, i, 0, 0));
        }

        //if (c != null)
            //for (int p = -20; p < 20; p++)
            //Gdx.app.log(TAG, "v" + VoxelChunk.blockDef(voxelWorld.get(p,2,2,0)).isSolid+p);

	}

    @Override
    public void initialize(){
        posM = world.getMapper(Position.class);
        AssetsSystem assetsSys = world.getSystem(AssetsSystem.class);
        TextureAtlas tiles = assetsSys.getTextureAtlas("tiles");
        TextureAtlas.AtlasRegion white = tiles.findRegion("air");
        MeshBatcher.whiteTextureU = white.getU();
        MeshBatcher.whiteTextureV = white.getV();



    }


}
