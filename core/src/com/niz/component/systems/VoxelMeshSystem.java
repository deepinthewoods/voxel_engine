package com.niz.component.systems;

import com.artemis.Aspect;
import com.artemis.ComponentMapper;
import com.artemis.Entity;
import com.artemis.systems.EntitySystem;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.tests.g3d.voxel.*;
import com.badlogic.gdx.utils.Array;
import com.badlogic.gdx.utils.IntMap;
import com.niz.NizMain;
import com.niz.component.Player;
import com.niz.component.Position;

/**
 * Created by niz on 16/06/2014.
 */
public class VoxelMeshSystem extends EntitySystem
{

    private static final String TAG = "voxel mesh system";
    private static final int MAX_VERTICES = 100000;
    private static final int MAX_INDICES = 100000;
    private static final int BATCHER_LEVELS = 23;
    Array<MeshThread> threads = new Array<MeshThread>();
    MeshRunnable runnable = null;
    private boolean hasThreads;
    private VoxelWorld voxelWorld;
    private ComponentMapper<Position> posM;
    private boolean coloredBatchQueued;
    private boolean preQueued;
    private FacesPreprocessor pre;
    private IntMap<MeshThread> inProgress = new IntMap<MeshThread>();

    /**
     * Creates an entity system that uses the specified filter
     * as a matcher against entities.
     *
     */
    public VoxelMeshSystem() {
        super(Aspect.getAspectForAll(Player.class, Position.class));
    }

    @Override
    protected void processEntities(Array<Entity> entities) {
        if (entities.size == 0) return;
        if (hasThreads){
            for (int i = 0; i < threads.size; i++){
                MeshThread thread = threads.get(i);
                if (thread.isPaused()){
                    //Gdx.app.log(TAG, "paused thread"+thread.index);
                    if (thread.runn.done){
                        //Gdx.app.log(TAG, "MESH END "+thread.runn.chunk.offset.cpy().scl(1f/16f) + "  "+thread.index);

                        inProgress.remove(thread.runn.chunk.index);
                        thread.runn.mesher.end();
                        thread.runn.done = false;
                        thread.runn.idle = true;
                        //thread.onPause();
                        continue;
                    } else

                    if (thread.runn.idle){
                        //try to start on a new mesh
                        VoxelChunk chunk = voxelWorld.getClosestDirtyChunk(posM.get(entities.get(0)));
                        if (chunk == null){
                            //Gdx.app.log(TAG, "MNO MeshT");

                            continue;
                        }
                        //Gdx.app.log(TAG, "MESH START MESH START MESH START "+chunk.offset + "  "+thread.index);


                        if (inProgress.containsKey(chunk.index)){
                            inProgress.get(chunk.index).runn.restart();
                            //Gdx.app.log(TAG, "MESH RESTART!!!!!!!!!!!!!!");
                            continue;
                        }
                        thread.runn.begin(chunk, voxelWorld);
                        inProgress.put(chunk.index, thread);

                        continue;
                    } else  {
                       // Gdx.app.log(TAG, "NONE NONE");

                    }
                } else {

                }
            }
        } else {
            if (runnable.idle){
                //try to start on a new mesh
                VoxelChunk chunk = voxelWorld.getClosestDirtyChunk(posM.get(entities.get(0)));
                chunk.setDirty(false);                runnable.begin(chunk, voxelWorld);
            } else if (runnable.done){
                runnable.mesher. chunk.setDirty(false);

            }
            runnable.run();
        }
    }

    @Override
    public void initialize() {
        voxelWorld = world.getSystem(VoxelSystem.class).voxelWorld;
        posM = world.getMapper(Position.class);


        hasThreads = NizMain.coreInfo.shouldUseThreads();

        if (coloredBatchQueued) {
            makeColoredBatchers();
            coloredBatchQueued = false;

        } else {


            if (hasThreads) {
                int num = NizMain.coreInfo.getNumberOfCores();
                //Gdx.app.log(TAG, "cores : "+num);
                for (int i = 0; i < num; i++) {
                    MeshBatcher batch = new MeshBatcher(MAX_VERTICES, MAX_INDICES, BATCHER_LEVELS);
                    GreedyMesher mesher = new GreedyMesher(batch);
                    MeshThread thr = new MeshThread(new MeshRunnable( mesher ), i);
                    thr.init();
                    threads.add(thr);
                    thr.start();
                    thr.onPause();
                }
            } else {
                MeshBatcher batch = new MeshBatcher(MAX_VERTICES, MAX_INDICES, BATCHER_LEVELS);
                GreedyMesher mesher = new GreedyMesher(batch);
                runnable = new MeshRunnable( mesher );
            }
        }

        if (preQueued){
            doPreProcessor();
            preQueued = false;
        }


    }

    public void setPreprocessor(FacesPreprocessor pre) {
        preQueued = true;
        this.pre = pre;
    }

    public void doPreProcessor(){
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
        coloredBatchQueued = true;
    }

    public void makeColoredBatchers(){
        if (hasThreads) {
            int num = NizMain.coreInfo.getNumberOfCores();
            //Gdx.app.log(TAG, "colored batcher : "+num);
            for (int i = 0; i < num; i++) {
                ColoredMeshBatcher batch = new ColoredMeshBatcher(MAX_VERTICES, MAX_INDICES, BATCHER_LEVELS);
                GreedyMesher mesher = new GreedyMesher(batch);
                MeshThread thr = new MeshThread(new MeshRunnable( mesher ), i);
                thr.init();
                threads.add(thr);
                thr.start();
                thr.onPause();
            }
        } else {
            ColoredMeshBatcher batch = new ColoredMeshBatcher(MAX_VERTICES, MAX_INDICES, BATCHER_LEVELS);
            GreedyMesher mesher = new GreedyMesher(batch);
            runnable = new MeshRunnable( mesher );
        }
    }


}
