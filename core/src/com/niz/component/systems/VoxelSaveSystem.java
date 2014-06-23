package com.niz.component.systems;

import com.artemis.Aspect;
import com.artemis.Entity;
import com.artemis.Filter;
import com.artemis.systems.EntitySystem;
import com.badlogic.gdx.tests.g3d.voxel.VoxelWorld;
import com.badlogic.gdx.utils.Array;

/**
 * Created by niz on 18/06/2014.
 */
public class VoxelSaveSystem extends EntitySystem {


    private float time;
    private VoxelSerializingSystem ser;
    private VoxelWorld vw;

    /**
     *
     */
    public VoxelSaveSystem() {
        super(Aspect.getEmpty());
    }

    @Override
    protected void processEntities(Array<Entity> entities) {
        //loop through chunks in whole world

        //set flag if needed




    }

    @Override
    public void initialize() {
        vw = world.getSystem(VoxelSystem.class).voxelWorld;
        ser = world.getSystem(VoxelSerializingSystem.class);
    }
}
