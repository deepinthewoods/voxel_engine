package com.niz.component.systems;

import com.artemis.Aspect;
import com.artemis.Entity;
import com.artemis.systems.EntitySystem;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.Camera;
import com.badlogic.gdx.graphics.PerspectiveCamera;
import com.badlogic.gdx.utils.Array;

public class CameraSystem extends EntitySystem{
public CameraSystem() {
		super(Aspect.getEmpty());
		
	}

public Camera camera;

@Override
protected void processEntities(Array<Entity> entities) {
	camera.update();
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
