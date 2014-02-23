package com.niz.component.input;

import com.artemis.Entity;
import com.artemis.systems.InputSystem;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.Camera;
import com.badlogic.gdx.graphics.OrthographicCamera;
import com.badlogic.gdx.math.Vector3;
import com.badlogic.gdx.tests.g3d.voxel.VoxelWorld;

public class PlatformerInputSystem extends InputSystem{

	private static final long TAP_TIME_LIMIT = 200;
	OrthographicCamera camera;
	Entity player;
	long touchTime;
	Vector3 tmp = new Vector3(), tmp2 = new Vector3();
	public PlatformerInputSystem(Camera cam, VoxelWorld vw) {
		float aspectRatio = Gdx.graphics.getWidth()/(float)Gdx.graphics.getHeight();
		float height = 40;
		//camera = (OrthographicCamera) cam;
		//camera.setToOrtho(false, height*aspectRatio, height);
		//camera.rotate(-70, 1, 0,0);
		//camera.position.set(50, 100, 50);
		
		//centreOnPlayer();
		//camera.far = 1750;
		//camera.near = 0.5f;
		//camera.fieldOfView = 4.5f;
		//camera.setToOrtho(false, 20, 30);
		//camera.zoom = .0752f;
		//camera.update();
	}
	
	@Override
	public boolean keyDown(int keycode) {
		
		return false;
	}

	@Override
	public boolean keyUp(int keycode) {
		return false;
	}

	@Override
	public boolean keyTyped(char character) {
		return false;
	}

	@Override
	public boolean touchDown(int screenX, int screenY, int pointer, int button) {
		touchTime = System.currentTimeMillis();
		return false;
	}

	@Override
	public boolean touchUp(int screenX, int screenY, int pointer, int button) {
		if (System.currentTimeMillis() - touchTime < TAP_TIME_LIMIT){
			//TODO jump
		}
		return false;
	}

	@Override
	public boolean touchDragged(int screenX, int screenY, int pointer) {
		//if time < limit or drag amount small maybe cancel
		//move
		tmp.set(0,0,0);
		tmp2.set(Gdx.input.getDeltaX(), Gdx.input.getDeltaY(), 0);
		camera.unproject(tmp);
		camera.unproject(tmp2);
		tmp2.sub(tmp);
		//move by tmp2
		camera.position.add(tmp2);
		return false;
	}

	@Override
	public boolean mouseMoved(int screenX, int screenY) {
		return false;
	}

	@Override
	public boolean scrolled(int amount) {
		return false;
	}

	public void setPlayer(Entity playerE) {
		player = playerE;
		
	}

	@Override
	protected void onTick() {
	}

}
