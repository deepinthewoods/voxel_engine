package com.niz.component.systems;

import com.artemis.Aspect;
import com.artemis.Component;
import com.artemis.ComponentMapper;
import com.artemis.Entity;
import com.artemis.systems.EntityProcessingSystem;
import com.badlogic.gdx.graphics.Camera;
import com.niz.component.CameraLookAt;
import com.niz.component.Position;
import com.niz.observer.Observer;
import com.niz.observer.Subject;
import com.niz.observer.Subjects;

/**
 * Created by niz on 03/06/2014.
 */
public class CameraLookAtSystem extends EntityProcessingSystem implements Observer {
    private static final String TAG = "cam look at system";
    private ComponentMapper<Position> posM;
    private Camera cam;
    private Position newPos;

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
        if (newPos != null){
            pos.pos.set(newPos.pos);
            newPos = null;
        }

        cam.lookAt(pos.pos);
        //Gdx.app.log(TAG, "look at");
    }

    public void initialize(){
        posM = world.getMapper(Position.class);
        cam = world.getSystem(CameraSystem.class).camera;
        Subjects.get("setCameraLookAt").add(this);
    }

    @Override
    public void onNotify(Entity e, Subject.Event event, Component c) {
        Position pos = (Position) c;
        newPos = pos;
    }
}
