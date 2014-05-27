package com.niz.component.systems;

import com.artemis.Aspect;
import com.artemis.Entity;
import com.artemis.systems.EntitySystem;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.Camera;
import com.badlogic.gdx.graphics.PerspectiveCamera;
import com.badlogic.gdx.utils.Array;

public class CameraSystem extends EntitySystem{
private static final String TAG = "camera system";

public CameraSystem() {
		super(Aspect.getEmpty());
		
	}

public transient Camera camera;

@Override
protected void processEntities(Array<Entity> entities) {

    camera.update();
    //Gdx.app.log(TAG, "camera"+camera.position);
}
@Override
public void initialize(){
	camera = new PerspectiveCamera(67, Gdx.graphics.getWidth(), Gdx.graphics.getHeight());
	//camera = new OrthographicCamera(20f,15f);
	camera.near = 0.5f;
	camera.far = 1000f;

	camera.update();
}
}
