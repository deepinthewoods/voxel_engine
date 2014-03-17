package com.niz.component.systems;



import com.artemis.Aspect;
import com.artemis.ComponentMapper;
import com.artemis.Entity;
import com.artemis.systems.EntityProcessingSystem;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.math.Vector3;
import com.niz.component.AABBBody;
import com.niz.component.Move;
import com.niz.component.Physics;
import com.niz.component.Position;

public class MovementSystem extends EntityProcessingSystem {
	//TODO abbbbody/body
	public MovementSystem() {
		super(Aspect.getAspectForAll(Position.class, Physics.class, Move.class, AABBBody.class));
		
		
	}
	private static final String TAG = "movement sys";
	
	private ComponentMapper<Position> posMap;
	private ComponentMapper<Physics> physMap;
	private ComponentMapper<Move> moveMap;
	private ComponentMapper<AABBBody> bodyMap;
	
	@Override
	public void initialize(){
		posMap = world.getMapper(Position.class);
		physMap = world.getMapper(Physics.class);
		moveMap = world.getMapper(Move.class);
		bodyMap = world.getMapper(AABBBody.class);
		
	}
	
	
	static Vector3 tmp = new Vector3();
	
	
	private static void move(float rotation, float speed, float speedLimit, float speedLimit2, float jumpSpeed, Vector3 position, Vector3 oldPosition, Move move) {
		//move.moving = true;
		tmp.set(speed/20f, 0, 0);
		tmp.rotate(-rotation, 0,1,0);
		position.x += tmp.x;
		position.z += tmp.z;
		
		/*tmp.add(oldPosition);
		//Gdx.app.log(TAG, "move"+tmp.dst2(position));
		if (tmp.dst2(position) > speedLimit2){
			tmp.sub(position);
			tmp.nor().scl(-speedLimit).add(oldPosition);
			position.set(tmp);
			tmp.set(speed/20f, 0, 0);
			tmp.rotate(-rotation, 0,1,0);
			position.add(tmp);
			//Gdx.app.log(TAG, "limit");
		}*/
		tmp.set(oldPosition).sub(position);
		float 
		vx = Math.abs(tmp.x)
		, vy = Math.abs(tmp.y)
		, vz = Math.abs(tmp.z);
		
		float dx = speedLimit/vx
				, dy = (speedLimit*jumpSpeed)/vy
				, dz = speedLimit / vz;

		float alpha = Math.min(Math.min(dx, dy), dz);
		if (alpha < 1f){
			tmp.scl(-alpha);
			position.set(oldPosition).add(tmp.x, tmp.y, 0);
			//Gdx.app.log(TAG, "move"+alpha);
		}
	
	}
	
	
	@Override
	protected void process(Entity e) {
		Vector3 position = posMap.get(e).pos;
		Vector3 oldPosition = physMap.get(e).oldPosition;
		Move c = moveMap.get(e);
		AABBBody body = bodyMap.get(e);
		
		if (c.jumpQueued){
			//Vector3 position = entity.get(Position.class).pos;
			//if (body.onGround)
			boolean left = c.rotation > 90 && c.rotation < 270;
			position.set((left?-c.speedLimit:c.speedLimit), c.speedLimit*c.jumpStrength, 0).add(oldPosition);
			c.jumpQueued = false;
			
		} else 
		
		if (c.moving){
			move(c.rotation, c.speed, c.speedLimit, c.speedLimit * c.speedLimit, c.jumpStrength, position, oldPosition, c);
		} else if (!c.moving && body.onGround){//apply friction
			//Gdx.app.log(TAG, "friction");
			tmp.set(position).sub(oldPosition);
			tmp.mul(.5f);
			//position.sub(tmp);
		}
		
		if (c.jumping){
			continuousJump(position, oldPosition, c.speedLimit, c.jumpStrength);
		}
		
		
		
	}


	private void continuousJump(Vector3 position, Vector3 oldPosition, float speedLimit, float jumpSpeed) {
		position.y = oldPosition.y + speedLimit*jumpSpeed;
		tmp.set(oldPosition).sub(position);
		float 
		vx = Math.abs(tmp.x)
		, vy = Math.abs(tmp.y)
		, vz = Math.abs(tmp.z);
		
		float dx = speedLimit/vx
				, dy = (speedLimit*jumpSpeed)/vy
				, dz = speedLimit / vz;

		float alpha = Math.min(Math.min(dx, dy), dz);
		if (alpha < 1f){
			tmp.scl(-alpha);
			position.set(oldPosition).add(tmp.x, tmp.y, 0);
			//Gdx.app.log(TAG, "move"+alpha);
		}
	}
	

}
