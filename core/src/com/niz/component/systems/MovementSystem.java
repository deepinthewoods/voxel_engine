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
	
	
	static Vector3 tmp = new Vector3(), oldV = new Vector3();
	
	
	private static void move(float rotation, float speed, float speedLimit, float speedLimit2, float jumpSpeed, Vector3 position, Vector3 oldPosition, Move move) {
		//move.moving = true;
		tmp.set(speed/20f, 0, 0);
		tmp.rotate(-rotation, 0,1,0);
        oldV.set(oldPosition);
		position.x += tmp.x;
		position.z += tmp.z;
        //Vector3 oldV = null;

		tmp.set(position.x, 0, position.z).sub(oldV.x, 0, oldV.z);

        {
            {

                if (tmp.len2() > speedLimit*speedLimit){
                    //tmp.set(position.x, position.y, position.z).sub(oldPosition.x, oldPosition.y, oldPosition.z);

                    position.set(oldV.x, position.y, oldV.z).add(tmp.nor().scl(speedLimit));

                }

            }
        }

        /*if (move.jumping){
            //tmp.set(position.x, position.y, position.z).sub(oldPosition.x, oldPosition.y, oldPosition.z);
            if (tmp.len2() > jumpSpeed*jumpSpeed){
                position.set(oldPosition.x, position.y, oldPosition.z).add(tmp.nor().scl(jumpSpeed));
            }

        }*/




	
	}
	
	
	@Override
	protected void process(Entity e) {
		Vector3 position = posMap.get(e).pos;
		Vector3 oldPosition = physMap.get(e).oldPosition;
		Move c = moveMap.get(e);
		AABBBody body = bodyMap.get(e);

        if (c.jumping)
            if (  !c.jumpQueued){
                c.jumping = false;
                //Gdx.app.log(TAG, "jump"+c.jumpQueued + body.onGround);
            }
        boolean startOfJump = false;

		if (c.jumpQueued && body.onGround && !c.jumping){
			boolean left = c.rotation > 90 && c.rotation < 270;
			position.y = c.moving?c.jumpStrengthMoving+ position.y:c.jumpStrength
            + position.y;
            //tmp.set(position).sub(oldPosition);
           //tmp.nor().scl(c.moving?c.jumpStrengthMoving:c.jumpStrength);
            //position.set(oldPosition).add(tmp);
            c.jumpEndTick = e.tick + (int)(c.jumpTime / e.getWorld().getDelta());
			c.jumping = true;
            body.onGround = false;
            //Gdx.app.log(TAG, "start jump"+c.jumpQueued + body.onGround);
            startOfJump = true;
        }
        /*if (startOfJump){
            body.onGround = false;
            startOfJump = false;
        }*/

        if (c.jumping){
            continuousJump(position, c);
            //Gdx.app.log(TAG, "force");
        }
        if (c.moving){
            move(c.rotation, c.moveAcceleration, c.moveSpeed, c.moveSpeed * c.moveSpeed, c.jumpStrength, position, oldPosition, c);
        }

        if (!c.moving && body.onGround){//apply friction
			//Gdx.app.log(TAG, "friction");
			tmp.set(position).sub(oldPosition);
			tmp.scl(.5f);
			position.sub(tmp);
		}


        /*if (c.jumping)
            if ((!c.jumpQueued )){
                c.jumping = false;

                Gdx.app.log(TAG, "jump"+c.jumpQueued + body.onGround);

            }*/
        if (c.jumping && body.onGround && body.wasOnGround){
            //Gdx.app.log(TAG, "reset");

            c.jumping = false;
        }
		/*if (c.jumpEndTick > e.tick && !c.jumping){
			c.jumping = true;
            Gdx.app.log(TAG, "force on");

        }*/



		
	}


	private void continuousJump(Vector3 position, Move m) {
        //if (oldPosition.y - position.y < speedLimit * jumpSpeed)
        if (m.moving)
            position.y += m.jumpForceMoving;
		else
            position.y += m.jumpForce;
        //Gdx.app.log(TAG, "continuious jkumpj");

	}
	

}
