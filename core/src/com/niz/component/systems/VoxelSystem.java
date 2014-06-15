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
    private boolean hasThreads;

    //private BlockDefinition[] defs;
	//private TextureRegion[] tiles;
    //public static TextureRegion white;
	public VoxelSystem() {
		super(Aspect.getAspectForAll(Player.class, Position.class));

		int x = 12, y = 4, z = 4;
		//batch = new MeshBatcher(10000000, 10000000, 13);
        //mesher = new GreedyMesher(batch);

        voxelWorld = new VoxelWorld(x, y, z
                , 16, 16, 4
				);
       // Gdx.app.log(TAG, "material"+voxelWorld.m);

	}

    public VoxelSystem(Aspect aspect){
        super(aspect);
    }

    transient Vector3 tmp = new Vector3();
	
	
	Array<MeshThread> threads = new Array<MeshThread>();
    MeshRunnable runnable = null;
	@Override
	protected void processEntities(Array<Entity> entities) {
        if (entities.size == 0) return;
        //voxelWorld.makeMesh(), mesher, batch);

        if (hasThreads){
            for (int i = 0; i < threads.size; i++){
                MeshThread thread = threads.get(i);
                if (thread.isPaused()){
                    //Gdx.app.log(TAG, "paused thread");
                    if (thread.runn.idle){
                        //try to start on a new mesh
                        VoxelChunk chunk = voxelWorld.getClosestDirtyChunk(posM.get(entities.get(0)));
                        if (chunk == null) return;
                        voxelWorld.setDirty(chunk.index, false);
                        thread.runn.start(chunk, voxelWorld);
                        return;
                    } else if (thread.runn.done){

                        thread.runn.end();
                        return;
                    }
                }
            }
        } else {
            if (runnable.idle){
                //try to start on a new mesh
                VoxelChunk chunk = voxelWorld.getClosestDirtyChunk(posM.get(entities.get(0)));
                voxelWorld.setDirty(chunk.index, false);
                runnable.start(chunk, voxelWorld);
            } else if (runnable.done){

                runnable.end();
            }
            runnable.run();
        }
	}

    @Override
    public void initialize(){
        posM = world.getMapper(Position.class);
        AssetsSystem assetsSys = world.getSystem(AssetsSystem.class);
        TextureAtlas tiles = assetsSys.getTextureAtlas("tiles");
        TextureAtlas.AtlasRegion white = tiles.findRegion("air");
        MeshBatcher.whiteTextureU = white.getU();
        MeshBatcher.whiteTextureV = white.getV();

        hasThreads = NizMain.coreInfo.shouldUseThreads();

        if (hasThreads){
            int num = NizMain.coreInfo.getNumberOfCores();
            //Gdx.app.log(TAG, "cores : "+num);
            for (int i = 0; i < num; i++){
                MeshBatcher batch = new MeshBatcher(10000000, 10000000, 13);
                GreedyMesher mesher = new GreedyMesher(batch);
                MeshThread thr = new MeshThread(new MeshRunnable(
                        mesher
                ));
                thr.init();
                threads.add(thr);
                thr.start();
                thr.onPause();
            }
        } else {
            MeshBatcher batch = new MeshBatcher(10000000, 10000000, 13);
            GreedyMesher mesher = new GreedyMesher(batch);
            runnable = new MeshRunnable(
                    mesher
            );
        }

    }

    public void setPreprocessor(FacesPreprocessor pre) {
        if (hasThreads){
            for (int i = 0; i < threads.size; i++){
                MeshThread thr = threads.get(i);
                thr.runn.mesher.preprocessor = pre;

            }
        } else {
            runnable.mesher.preprocessor = pre;
        }
    }

    public void setColoredBatcher() {
        if (hasThreads){
            for (int i = 0; i < threads.size; i++) {
                MeshThread thread = threads.get(i);
                thread.stopThread();
            }
            threads.clear();
            int num = NizMain.coreInfo.getNumberOfCores();
            for (int i = 0; i < num; i++){

                MeshBatcher batch = new ColoredMeshBatcher(10000000, 10000000, 13);
                GreedyMesher mesher = new GreedyMesher(batch);
                MeshThread thr = new MeshThread(new MeshRunnable(
                        mesher
                ));
                thr.init();
                threads.add(thr);
                thr.start();
                thr.onPause();
            }
        } else {
            MeshBatcher batch = new ColoredMeshBatcher(10000000, 10000000, 13);
            GreedyMesher mesher = new GreedyMesher(batch);
            runnable = new MeshRunnable(
                    mesher
            );
        }
    }
}
