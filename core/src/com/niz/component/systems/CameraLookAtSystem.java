package com.niz.component.systems;

import com.artemis.Aspect;
import com.artemis.ComponentMapper;
import com.artemis.Entity;
import com.artemis.Filter;
import com.artemis.systems.EntityProcessingSystem;
import com.artemis.systems.EntitySystem;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.Camera;
import com.badlogic.gdx.utils.Array;
import com.niz.component.CameraLookAt;
import com.niz.component.Position;

/**
 * Created by niz on 03/06/2014.
 */
public class CameraLookAtSystem extends EntityProcessingSystem {
    private static final String TAG = "cam look at system";
    private ComponentMapper<Position> posM;
    private Camera cam;

    /**
     * Creates an entity system that uses the specified filter
     * as a matcher against entities.
     *

     */
    public CameraLookAtSystem() {
        super(Aspect.getAspectForAll(Position.class, CameraLookAt.class));
    }


    @Override
    protected void process(Entity e) {
        Position pos = posM.get(e);
        cam.lookAt(pos.pos);
        //Gdx.app.log(TAG, "look at");
    }

    public void initialize(){
        posM = world.getMapper(Position.class);
        cam = world.getSystem(CameraSystem.class).camera;
    }
}
