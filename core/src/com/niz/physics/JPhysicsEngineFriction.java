package com.niz.physics;

import com.badlogic.gdx.math.Vector3;

public class JPhysicsEngineFriction extends JPhysicsEngine {

	private static final String TAG = "jphysics";

    public JPhysicsEngineFriction(int constraintReps, int maxParticle, float timeStep) {
        super(constraintReps, maxParticle, timeStep);
    }


    @Override
		public void satisfyConstraints() {
			for (int r = 0; r < constraintRepetitions; r++){
				for(int i=0; i<particleTotal; i++) {
					Vector3 pos = particles[i*4], prev = particles[1*4+1];
					//pos.x = Math.min(Math.max(pos.x,  xLimit1), xLimit2);
					if (pos.x < xLimit1)		
						pos.x = xLimit1;
					 else if (pos.x > xLimit2)
						pos.x = xLimit2;
					
					if (pos.y < yLimit1){	
						//Gdx.app.log(TAG, "friction");
						pos.y = yLimit1;
						pos.x = prev.x;
						pos.z = prev.z;
					}
					 else if (pos.y > yLimit2)
						pos.y = yLimit2;
						
					if (pos.z < zLimit1)				
						pos.z = zLimit1;
					 else if (pos.z > zLimit2)
						pos.z = zLimit2;
					//pos.y = Math.min(Math.max(pos.y,  yLimit1), yLimit2);
				}
			}
			
			/*Values<Constraint> vals = constraints.values();
			while (vals.hasNext()){
				Constraint c = vals.next();
				 Vector3 x1 = c.src;
		         Vector3 x2 = c.dst;
		         delta.set(x2).sub(x1);
		         float restLength2 = c.restLength * c.restLength;
		        // delta = x2-x1;
		         delta.scl(restLength2/(delta.dot(delta)+restLength2)-0.5f);
		         x1.sub(delta);
		         x2.add(delta);
			}*/
			
		}
	
	
}
