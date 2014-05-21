package com.niz.component;

import com.artemis.Component;
import com.artemis.Entity;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.math.Vector3;

public class Brain implements Component {
	private Vector3 target = new Vector3(), shortTarget = new Vector3();
	private Vector3 targetLastUpdated = new Vector3();;
	
	public Vector3 getTarget() {
		return target;
	}

	
	/*public Entity getTargetEntity(){
		return targetEntity;
	}
	
	public void setTargetEntity(Entity e){
		targetEntity = e;
	}*/
	public Vector3 getShortTarget() {
		return shortTarget;
	}

	
	public Vector3 getTargetLastUpdated() {
		return targetLastUpdated;
	}
	
	/*public void requestPath(Entity e) {
		//pathr.progress = 1000000;
		Vector3 position = e.get(Position.class).pos;
		//Gdx.app.log(TAG, "request path");
		if (!e.hasComponent(PathWaiter.class)){
			e.add(PathWaiter.class).set(position, target);
			e.add(PathReturn.class).valid = false;
			//Gdx.app.log("jdfksl", "astar add"+position+"  to "+shortTarget);
		}
		else {
			e.get(PathWaiter.class)
				.set(position, target);
			IntArray path = e.get(PathReturn.class).path;
			e.get(PathReturn.class).valid = false;
			shortTarget.set(target);
			//Gdx.app.log(TAG, "request path move"+ () );
		}
		
	}*/
	
	private static Vector2 tmp = new Vector2();
	public void setTargetAngle(Entity e) {
		//Entity e = parent;
		Move mov = e.get(Move.class);
		Vector3 pos = e.get(Position.class).pos;
		tmp.set(shortTarget.x, shortTarget.z).sub(pos.x, pos.z);
		float rot = tmp.angle();
		
		mov.rotation = rot;
	//	mov.moving = true;
		Body body = e.get(Body.class);
		//if (needsToJump(pos, rot)) mov.jumpQueued = true;
		

	}


	@Override
	public void reset() {
	}
	
	
}
