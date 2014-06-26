package com.niz.component.systems;

import com.artemis.Aspect;
import com.artemis.ComponentMapper;
import com.artemis.Entity;
import com.artemis.systems.EntitySystem;
import com.badlogic.gdx.math.Vector3;
import com.badlogic.gdx.tests.g3d.voxel.*;
import com.badlogic.gdx.utils.Array;
import com.badlogic.gdx.utils.IntMap;
import com.badlogic.gdx.utils.LongMap;
import com.badlogic.gdx.utils.Pools;
import com.niz.NizMain;
import com.niz.component.Player;
import com.niz.component.Position;

import java.util.Iterator;

/**
 * Created by niz on 15/06/2014.
 */
public class VoxelSerializingSystem extends EntitySystem {
    private static final float READ_INTERVAL = .5f;
    private static final float WRITE_INTERVAL = .5f;
    /**
     * Manages blobs and save/load
     *
     * idle bool on the threads means they need to wait
     */



    private LongMap<VoxelBlobReader> blobs = new LongMap<VoxelBlobReader>();
    private Array<ReadThread> readThreads = new Array<ReadThread>();
    private Array<WriteThread> writeThreads = new Array<WriteThread>();

    private boolean hasThreads;
    private WriteThread writeRunn;
    private ReadThread readRunn;
    private ComponentMapper<Position> posM;

    private Object lock = new Object();
    private LongMap<VoxelChunk> reads = new LongMap<VoxelChunk>();
    private LongMap<VoxelChunk> writes = new LongMap<VoxelChunk>();
    private LongMap<VoxelChunk> inProgress = new LongMap<VoxelChunk>();
    private LongMap.Entries<VoxelChunk> readV = new LongMap.Entries<VoxelChunk>(reads);;
    private LongMap.Entries<VoxelChunk> writeV = new LongMap.Entries<VoxelChunk>(writes);;
    private VoxelWorld vw;
    private Array<VoxelChunk> finishedRead = new Array<VoxelChunk>();
    private Array<VoxelChunk> finishedWrite = new Array<VoxelChunk>();

    public VoxelSerializingSystem() {
        super(Aspect.getAspectForAll(Player.class, Position.class));
    }

    @Override
    protected void processEntities(Array<Entity> entities) {
        for (int i = 0; i < finishedRead.size; i++){
            VoxelChunk c = finishedRead.get(i);
            vw.setValid(c);
            inProgress.remove(hash((int)c.offset.x, (int)c.offset.y, (int)c.offset.z, c.plane));
        }
        finishedRead.clear();

        for (int i = 0; i < finishedWrite.size; i++){
            VoxelChunk c = finishedWrite.get(i);
            //vw.setValid(c);

            inProgress.remove(hash((int)c.offset.x, (int)c.offset.y, (int)c.offset.z, c.plane));
        }
        finishedWrite.clear();




        if (entities.size == 0) return;
        Position pos = posM.get(entities.get(0));
        float dt = world.getDelta();

        for (int i = 0; i < writeThreads.size; i++){
            WriteThread thread = writeThreads.get(i);
            if (!hasThreads)
                thread.runn.run();
            if (thread.isPaused()){
                thread.time += dt;
                if (thread.runn.idle){//waiting for its Blob to become free. should be unpaused at intervals
                    if (thread.time > WRITE_INTERVAL)
                        thread.onResume();
                } else { //finished, should look for new chunk to read from
                    if (thread.time > WRITE_INTERVAL)
                        findWriteTarget(thread, pos);
                }
            }
        }

        for (int i = 0; i < readThreads.size; i++){
            ReadThread thread = readThreads.get(i);
            if (!hasThreads)
                thread.runn.run();
            if (thread.isPaused()){
                thread.time += dt;
                if (thread.runn.idle){//waiting for blob
                    if (thread.time > READ_INTERVAL)
                        thread.onResume();
                } else {//done reading
                    if (thread.time > READ_INTERVAL)
                        findReadTarget(thread, pos);
                }
            }
        }

        //TODO clean up unused blobs from time to time

    }

    private void findReadTarget(ReadThread thread, Position pos) {
        //look in queues
        //synchronized (lock) //only run by the main thread
        {
            Iterator<LongMap.Entry<VoxelChunk>> i = readV.iterator();
            float dist = 0;
            LongMap.Entry<VoxelChunk> select = null;
            while (i.hasNext()) {
                LongMap.Entry<VoxelChunk> c = i.next();
                if ((select == null || dist > pos.pos.dst2(c.value.offset)) && !inProgress.containsKey(c.key)) {
                    select = c;
                    dist = pos.pos.dst2(c.value.offset);
                }
            }
            if (select != null) {
                VoxelBlobReader blob = getBlob(select.value);
                thread.runn.begin(select.value, vw, blob);
                inProgress.put(select.key, select.value);
                reads.remove(select.key);
                thread.onResume();
            }

        }

    }

    private VoxelBlobReader getBlob(VoxelChunk chunk) {
        Vector3 offset = chunk.offset;
        int x = (int) offset.x;
        int y = (int) offset.y;
        int z = (int) offset.z;
        x /= chunk.width;
        y /= chunk.height;
        z /= chunk.depth;
        int p = chunk.plane;

        long hash = hash(x,y,z,p);

        if (blobs.containsKey(hash)){
            return blobs.get(hash);
        }
        //create blob
        VoxelBlobReader blob = Pools.obtain(VoxelBlobReader.class);

        blobs.put(hash, blob);
        return blob;
    }

    private void findWriteTarget(WriteThread thread, Position pos) {
        {
            Iterator<LongMap.Entry<VoxelChunk>> i = writeV.iterator();
            float dist = 0;
            LongMap.Entry<VoxelChunk> select = null;
            while (i.hasNext()) {
                LongMap.Entry<VoxelChunk> c = i.next();
                if ((select == null || dist > pos.pos.dst2(c.value.offset)) && !inProgress.containsKey(c.key)) {
                    select = c;
                    dist = pos.pos.dst2(c.value.offset);
                }
            }
            if (select != null) {
                VoxelBlobReader blob = getBlob(select.value);
                thread.runn.begin(select.value, vw, blob);
                inProgress.put(select.key, select.value);
                reads.remove(select.key);
                thread.onResume();
            }

        }
    }


    public void addSave(VoxelChunk chunk){
        int x = (int) chunk.offset.x;
        int y = (int) chunk.offset.y;
        int z = (int) chunk.offset.z;
        int p = (int) chunk.plane;
       // synchronized (lock){
            writes.put(hash(x,y,z,p), chunk);

        //}
    }

    public void addRead(VoxelChunk chunk){
        int x = (int) chunk.offset.x;
        int y = (int) chunk.offset.y;
        int z = (int) chunk.offset.z;
        int p = (int) chunk.plane;
        //synchronized (lock){
            reads.put(hash(x,y,z,p), chunk);

        //}
    }



    public long hash(int x, int y, int z, int p){
        return x | (y<<16) | (z << 32) | (p<<48);

    }


    @Override
    public void initialize() {
        hasThreads = NizMain.coreInfo.shouldUseThreads();
        posM = world.getMapper(Position.class);
        vw = world.getSystem(VoxelSystem.class).voxelWorld;
        int num = NizMain.coreInfo.getNumberOfCores();
        //Gdx.app.log(TAG, "cores : "+num);
        for (int i = 0; i < num; i++){
            ChunkReadOrGenerate rRun = new ChunkReadOrGenerate();
            ReadThread r;
            if (hasThreads){
                r = new ReadThread(rRun){
                    boolean p;
                    @Override
                    public void onPause() {
                        p = true;
                        time = READ_INTERVAL;
                    }

                    @Override
                    public void onResume() {
                        p = false;
                    }

                    @Override
                    public boolean isPaused() {
                        return p;
                    }

                    @Override
                    public void stopThread() {
                        p = true;
                    }
                    @Override
                    public synchronized void start() {
                    }
                };
            } else {
                r =new ReadThread(rRun);
            }
            r.init(this);
            readThreads.add(r);
            r.start();
            r.onPause();

            ChunkWrite wRun = new ChunkWrite();
            WriteThread w;
            if (hasThreads){
                w = new WriteThread(wRun){
                    boolean p;
                    @Override
                    public void onPause() {
                        p = true;
                        time = WRITE_INTERVAL;
                    }

                    @Override
                    public void onResume() {
                        p = false;
                    }

                    @Override
                    public boolean isPaused() {
                        return p;
                    }

                    @Override
                    public void stopThread() {
                        p = true;
                    }
                    @Override
                    public synchronized void start() {
                    }
                };
            } else {
                w = new WriteThread(wRun);
            }
            w.init(this);
            writeThreads.add(w);
            w.start();
            w.onPause();
        }


    }

    public void finishedWrite(VoxelChunk chunk) {
        synchronized (lock){
            finishedWrite.add(chunk);
        }
    }

    public void finishedRead(VoxelChunk chunk) {
        synchronized (lock){
            finishedRead.add(chunk);
        }
    }
}
