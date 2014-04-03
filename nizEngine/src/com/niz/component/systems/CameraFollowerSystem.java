package com.niz.component.systems;

import com.artemis.Aspect;
import com.artemis.ComponentMapper;
import com.artemis.Entity;
import com.artemis.systems.EntitySystem;
import com.badlogic.gdx.graphics.Camera;
import com.badlogic.gdx.math.Vector3;
import com.badlogic.gdx.utils.Array;
import com.niz.component.AABBBody;
import com.niz.component.CameraController;
import com.niz.component.Position;

/**
 * @author Niall Quinlan
 * just follows the entity with Cameracontroller
 *
 */
public class CameraFollowerSystem extends EntitySystem {

	private Camera camera;
	private ComponentMapper<Position> positionM;
	public Vector3 prevPosition;
	private ComponentMapper<AABBBody> bodyM;
	
	public CameraFollowerSystem() {
		super(Aspect.getAspectForAll(CameraController.class, Position.class));
		this.camera = camera;
		prevPosition = new Vector3();
	}

	@Override
	protected void processEntities(Array<Entity> entities) {
		if (entities.size == 0) return;
		Entity e = entities.get(0);
		
		Vector3 pos = positionM.get(e).pos;
		//if (pos.dst2(prevPosition) > 10){
		AABBBody body = bodyM.get(e);
		//if (body.onGround){
		camera.position.y -= prevPosition.y - pos.y;
		prevPosition.y = pos.y;
		
		camera.position.x -= prevPosition.x - pos.x;
		prevPosition.x = pos.x;
		
		camera.update();
				
			//}
		//}
	}
	
	@Override
	public void initialize(){
		positionM = world.getMapper(Position.class);
		bodyM = world.getMapper(AABBBody.class);
		camera = world.getSystem(CameraSystem.class).camera;
		//Entity e = 
		//prevPosition.set(positionM.get(e));
	}
}
