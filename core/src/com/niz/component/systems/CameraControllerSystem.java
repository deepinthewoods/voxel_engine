package com.niz.component.systems;

import com.artemis.Aspect;
import com.artemis.Component;
import com.artemis.ComponentMapper;
import com.artemis.Entity;
import com.artemis.systems.EntitySystem;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.Camera;
import com.badlogic.gdx.math.Vector3;
import com.badlogic.gdx.utils.Array;
import com.niz.component.CameraController;
import com.niz.component.Position;
import com.niz.component.UpVectorRollingAverage;
import com.niz.observer.Observer;
import com.niz.observer.Subject;
import com.niz.observer.Subject.Event;

/**
 * @author Niall Quinlan
 * sets camera to Position of this Entity
 *
 */
public class CameraControllerSystem extends EntitySystem{

	private static final String TAG = "camera controller system";
	private transient Camera camera;
	private ComponentMapper<Position> positionM;
	public transient Vector3 prevPosition, prevRotation;
	//private ComponentMapper<AABBBody> bodyM;
	private transient Vector3 newPos, newRot;
    private ComponentMapper<UpVectorRollingAverage> upAvM;
    public Subject upVectorNotifier = new Subject();

    public CameraControllerSystem() {
		super(Aspect.getAspectForAll(CameraController.class, Position.class));

		prevPosition = new Vector3();
        prevRotation = new Vector3();
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
        /*Rotation rotC = rotationM.get(e);
        if (rotC != null){
            Vector3 rot = rotC.rotation;
            if (newRot != null){
                rot.set(newRot);
                newRot = null;
            }
            camera.lookAt(rot);

            Gdx.app.log(TAG, "cam rotate");
            prevRotation.set(rot);
        }*/





		//if (pos.dst2(prevPosition) > 10){
		//AABBBody body = bodyM.get(e);
		//if (body.onGround){
		camera.position.y -= prevPosition.y - pos.y;
		prevPosition.y = pos.y;
		
		camera.position.x -= prevPosition.x - pos.x;
		prevPosition.x = pos.x;

        camera.position.z -= prevPosition.z - pos.z;
        prevPosition.z = pos.z;


		camera.update();
		//Gdx.app.log(TAG, "cam move"+camera.position);
		
			//}
		//}
	}
	
	@Override
	public void initialize(){
	    positionM = world.getMapper(Position.class);
        upAvM = world.getMapper(UpVectorRollingAverage.class);
		camera = world.getSystem(CameraSystem.class).camera;

        Observer posObserver = new Observer(){
            @Override
            public void onNotify(Entity e, Event event, Component c) {
                Position pos = (Position) c;
                newPos = pos.pos;
                //Gdx.app.log(TAG, "move message"+pos.pos);
            }
        };
        world.getSystem(CameraPositionInfluenceSystem.class).notifyPositionChanged.add(posObserver);

        Observer upObserver = new Observer(){

            @Override
            public void onNotify(Entity e, Event event, Component c) {
                UpVectorRollingAverage upAv = (UpVectorRollingAverage) c;
                if (upAv != null){
                    Vector3 up = upAv.result;
                    camera.up.set(up);
                    //Gdx.app.log(TAG, "up vector notified");
                }
            }
        };
        upVectorNotifier.add(upObserver);
        //world.getSystem(CameraRotationInfluenceSystem.class).notifyRotationChanged.add(rotObserver);


        //Entity e =
		//prevPosition.set(positionM.get(e));
	}



}
