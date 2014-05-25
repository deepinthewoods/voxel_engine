package com.niz.component.systems;

import com.artemis.Aspect;
import com.artemis.Component;
import com.artemis.ComponentMapper;
import com.artemis.Entity;
import com.artemis.systems.EntitySystem;
import com.badlogic.gdx.graphics.Camera;
import com.badlogic.gdx.math.Vector3;
import com.badlogic.gdx.utils.Array;
import com.niz.component.AABBBody;
import com.niz.component.CameraController;
import com.niz.component.Position;
import com.niz.observer.Observer;
import com.niz.observer.Subject.Event;

/**
 * @author Niall Quinlan
 * sets camera to Position of this Entity
 *
 */
public class CameraControllerSystem extends EntitySystem implements Observer{

	private static final String TAG = "camera controller system";
	private transient Camera camera;
	private ComponentMapper<Position> positionM;
	public transient Vector3 prevPosition;
	//private ComponentMapper<AABBBody> bodyM;
	private transient Vector3 newPos;
	
	
	public CameraControllerSystem() {
		super(Aspect.getAspectForAll(CameraController.class, Position.class));
		prevPosition = new Vector3();
	}

	@Override
	protected void processEntities(Array<Entity> entities) {
		if (entities.size == 0) return;
		Entity e = entities.get(0);
		
		Vector3 pos = positionM.get(e).pos;
		if (newPos != null){
			pos.set(newPos);
			newPos = null;
		}
		//if (pos.dst2(prevPosition) > 10){
		//AABBBody body = bodyM.get(e);
		//if (body.onGround){
		camera.position.y -= prevPosition.y - pos.y;
		prevPosition.y = pos.y;
		
		camera.position.x -= prevPosition.x - pos.x;
		prevPosition.x = pos.x;
		
		camera.update();
		//Gdx.app.log(TAG, "cam move"+camera.position);
		
			//}
		//}
	}
	
	@Override
	public void initialize(){
		positionM = world.getMapper(Position.class);
		//bodyM = world.getMapper(AABBBody.class);
		camera = world.getSystem(CameraSystem.class).camera;
		world.getSystem(CameraInfluenceSystem.class).notifyPositionChanged.add(this);
		//Entity e = 
		//prevPosition.set(positionM.get(e));
	}

	@Override
	public void onNotify(Entity e, Event event, Component c) {
		Position pos = (Position) c;
		newPos = pos.pos;
		//Gdx.app.log(TAG, "move message"+pos.pos);
	}

}
