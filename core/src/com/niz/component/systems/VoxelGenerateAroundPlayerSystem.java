package com.niz.component.systems;

import com.artemis.Aspect;
import com.artemis.Component;
import com.artemis.ComponentMapper;
import com.artemis.Entity;
import com.artemis.systems.EntitySystem;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.tests.g3d.voxel.VoxelChunk;
import com.badlogic.gdx.tests.g3d.voxel.VoxelWorld;
import com.badlogic.gdx.utils.Array;
import com.badlogic.gdx.utils.LongMap;
import com.niz.component.Player;
import com.niz.component.Position;
import com.niz.observer.Observer;
import com.niz.observer.Subject.Event;
import com.niz.observer.Subjects;

/**
 * Created by niz on 26/06/2014.
 */
public class VoxelGenerateAroundPlayerSystem extends EntitySystem implements Observer {
    private static final int SPAWN_SIZE = 3;
	private static final String TAG = "voxel generate around playere system";
    private ComponentMapper<Position> posM;
    private VoxelSystem voxelSys;
    private VoxelWorld vw;
    private VoxelSerializingSystem ser;
	private boolean queueFreeChunks;

    /**
     * Creates an entity system that uses the specified filter
     * as a matcher against entities.
     *
     */
    public VoxelGenerateAroundPlayerSystem() {
        super(Aspect.getAspectForAll(Player.class, Position.class));;
    }

    @Override
    protected void processEntities(Array<Entity> entities) {
        if (entities.size == 0)return;
        Position pos = posM.get(entities.get(0));
        for (int x = (int) (pos.pos.x / vw.CHUNK_SIZE_X )-SPAWN_SIZE ; x < (int) pos.pos.x / vw.CHUNK_SIZE_X  + 1 + SPAWN_SIZE; x++)
            for (int y = (int) (pos.pos.y / vw.CHUNK_SIZE_Y ) -SPAWN_SIZE; y < (int)pos.pos.y / vw.CHUNK_SIZE_Y + 1 + SPAWN_SIZE; y++)
                for (int z = (int) (pos.pos.z / vw.CHUNK_SIZE_Z)-SPAWN_SIZE ; z < (int)pos.pos.z / vw.CHUNK_SIZE_Z + 1 + SPAWN_SIZE; z++){
                    VoxelChunk chunk = vw.getChunk(x, y, z, pos.plane);
                    if (chunk == null){
                        ser.addRead(x,y,z,pos.plane);
                    }


            }
        if (queueFreeChunks){
        	Gdx.app.log(TAG, "queue free chunks");
        	vw.removeChunksBasedOnDistanceTo(pos.pos, SPAWN_SIZE+1);
        	queueFreeChunks = false;
        }
        ser.removeReadsBasedOnDistanceTo(pos.pos, (SPAWN_SIZE+1) * vw.CHUNK_SIZE_X);
        
    }

    @Override
    public void initialize() {
        voxelSys = world.getSystem(VoxelSystem.class);
        vw = voxelSys.voxelWorld;
        ser = world.getSystem(VoxelSerializingSystem.class);
        posM = world.getMapper(Position.class);
        Subjects.get("need chunk").add(this);;

    }

	@Override
	public void onNotify(Entity e, Event event, Component c) {
		queueFreeChunks = true;
		
	}
}
