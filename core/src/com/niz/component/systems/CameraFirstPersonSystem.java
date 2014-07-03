package com.niz.component.systems;

import com.artemis.Aspect;
import com.artemis.Component;
import com.artemis.ComponentMapper;
import com.artemis.Entity;
import com.artemis.systems.EntitySystem;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.Camera;
import com.badlogic.gdx.math.Quaternion;
import com.badlogic.gdx.math.Vector3;
import com.badlogic.gdx.utils.Array;
import com.niz.component.CameraController;
import com.niz.component.FPSCamera;
import com.niz.observer.Observer;
import com.niz.observer.Subject;
import com.niz.observer.Subjects;

/**
 * Created by niz on 28/06/2014.
 */
public class CameraFirstPersonSystem extends EntitySystem implements Observer{

    private static final String TAG = "Camera firstperson system";
    private Camera camera;

    public CameraFirstPersonSystem(){
        super(Aspect.getAspectForAll(CameraController.class, FPSCamera.class));
    }

    @Override
    protected void processEntities(Array<Entity> entities) {

    }

    @Override
    public void initialize() {
        camera = world.getSystem(CameraSystem.class).camera;
        Subjects.get("screenDragged").add(this);
    }
    float degreesPerPixel = .5f;
    Vector3 tmp = new Vector3();
    @Override
    public void onNotify(Entity e, Subject.Event event, Component c) {
        //Gdx.app.log(TAG, "drag");
        float deltaX = -Gdx.input.getDeltaX() * degreesPerPixel;
        float deltaY = -Gdx.input.getDeltaY() * degreesPerPixel;
        camera.direction.rotate(camera.up, deltaX);
        tmp.set(camera.direction).crs(camera.up).nor();
        camera.direction.rotate(tmp, deltaY);
    }
}
