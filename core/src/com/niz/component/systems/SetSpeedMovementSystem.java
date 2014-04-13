package com.niz.component.systems;



import com.artemis.Aspect;
import com.artemis.ComponentMapper;
import com.artemis.Entity;
import com.artemis.systems.EntityProcessingSystem;
import com.badlogic.gdx.math.Vector3;
import com.niz.component.AABBBody;
import com.niz.component.Body;
import com.niz.component.Move;
import com.niz.component.Physics;
import com.niz.component.Position;

public class SetSpeedMovementSystem extends EntityProcessingSystem {
	//TODO abbbbody/body
	public SetSpeedMovementSystem() {
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
	private static void move(float rotation, float speed, Vector3 position, Vector3 oldPosition, Move move) {
		//move.moving = true;
		tmp.set(speed, 0, 0);
		tmp.rotate(-rotation, 0,1,0);
		tmp.add(oldPosition);
		position.x = tmp.x;
		position.z = tmp.z;
		
		//Gdx.app.log(TAG, "move"+move.speed);
	}
	
	
	
	
	@Override
	protected void process(Entity e) {
		Vector3 position = posMap.get(e).pos;
		Vector3 oldPosition = physMap.get(e).oldPosition;
		Move c = moveMap.get(e);
		AABBBody body = bodyMap.get(e);
		if (c.moving){
			move(c.rotation, c.speed, position, oldPosition, c);
		} else if (!c.moving && body.onGround){//apply friction
			//Gdx.app.log(TAG, "friction");
			tmp.set(position).sub(oldPosition);
			tmp.scl(.5f);
			position.sub(tmp);
		}
		
		if (c.jumpQueued){
			//Vector3 position = entity.get(Position.class).pos;
			//if (body.onGround)
			position.add(0,c.jumpStrength, 0);
			c.jumpQueued = false;
		}
		
	}
	

}
