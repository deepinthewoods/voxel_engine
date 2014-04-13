package com.niz.physics;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.math.Vector3;
import com.badlogic.gdx.utils.IntArray;
import com.badlogic.gdx.utils.IntMap;
import com.badlogic.gdx.utils.IntMap.Values;
import com.badlogic.gdx.utils.Pool;
import com.badlogic.gdx.utils.Pools;

public class JPhysicsEngine{
	private static int maxParticles;
	private static final String TAG = "JPhysicsEngine";
	public static final int COLLISION_GRANULARITY = 16;
	public Vector3[] particles;// = new Vector3[MAX_PARTICLES*3];
	public int[] ids;
	public IntMap<Constraint> constraints;
	protected int constraintRepetitions;
	private Vector3 temp = new Vector3(), gravity = new Vector3(0f,-60f, 0f);;
	protected int particleTotal = 0;
	private float fTimeStep, fTimeStep2;//Vector3.fixedMul(fTimeStep, fTimeStep);
	
	float xLimit1 = 0f, xLimit2 = 11111512, yLimit1 = 0f, yLimit2 = 512, zLimit1 = .5f, zLimit2 = .5f;
	
	
	public JPhysicsEngine(int constraintReps, int maxParticle, Vector3[] vectors) {
		maxParticles = maxParticle;
		constraintRepetitions = constraintReps;
		particles = vectors;
		ids = new int[maxParticles];
		constraints = new IntMap<Constraint>();
		
	}
	
	public JPhysicsEngine(int constraintReps, int maxParticle, float timeStep) {
		maxParticles = maxParticle;
		constraintRepetitions = constraintReps;
		particles = new Vector3[maxParticles*4];
		ids = new int[maxParticles];
		constraints = new IntMap<Constraint>();
		fTimeStep = timeStep;
		fTimeStep2 = fTimeStep*fTimeStep;
	}
	public JPhysicsEngine(int constraintReps, int maxParticle, float timeStep, int sizeX,
			int sizeY, int sizeZ) {
		this(constraintReps, maxParticle, timeStep);
		xLimit2 = sizeX-1;
		yLimit2 = sizeY-1;
		zLimit2 = sizeZ-1;
	}
	Vector3 tmp2 = new Vector3(), ta = new Vector3();
	// Verlet integration step
	private void verlet(){
		for(int i=0; i<particleTotal; i++) {
			Vector3 x = particles[i*4];
			temp.set(x);
			Vector3 oldx = particles[i*4+1];
			Vector3 a = particles[i*4+2];
			//x += x-oldx+a*fTimeStep*fTimeStep;
			tmp2.set(x);
			tmp2.sub(oldx);
			ta.set(a);
			ta.scl(fTimeStep);
			ta.scl(fTimeStep);
			//Gdx.app.log("physics", "ta  "+ta + "tmp2  "+temp);
			tmp2.add(ta);
			x.add(tmp2);
			//x.sub(oldx);
			//x.add(a.scl(fTimeStep2)).add(temp);
			oldx.set(temp);
			
			
			/*Vector3 x = m_x[i];
			Vector3 temp = x;
			Vector3& oldx = m_oldx[i];
			Vector3& a = m_a[i];
			x += x-oldx+a*fTimeStep*fTimeStep;
			oldx = temp;*/
		}
	}
	// This function should accumulate forces for each particle
	private void accumulateForces()
	{
		// All particles are influenced by gravity
		for(int i=0; i<particleTotal; i++) 
			particles[i*4+2].set( gravity );
		
		
	}
	private Vector3 delta = new Vector3();
	// Here constraints should be satisfied
	public void satisfyConstraints() {
		for (int r = 0; r < constraintRepetitions; r++){
			for(int i=0; i<particleTotal; i++) {
				Vector3 pos = particles[i*4], prev = particles[1*4+1];
				//pos.x = Math.min(Math.max(pos.x,  xLimit1), xLimit2);
				if (pos.x < xLimit1)		
					pos.x = xLimit1;
				 else if (pos.x > xLimit2)
					pos.x = xLimit2;
				
				if (pos.y < yLimit1)				
					pos.y = yLimit1;
				 else if (pos.y > yLimit2)
					pos.y = yLimit2;
					
				if (pos.z < zLimit1)				
					pos.z = zLimit1;
				 else if (pos.z > zLimit2)
					pos.z = zLimit2;
				//pos.y = Math.min(Math.max(pos.y,  yLimit1), yLimit2);
			}
		}
		
		Values<Constraint> vals = constraints.values();
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
		}
		
	}
	

	public void step() {
		updateRemovals();
		accumulateForces();
		verlet();
		satisfyConstraints();
		//Gdx.app.log(TAG, "step"+particleTotal);
	}
	public void step(float delta) {
		fTimeStep = delta;
		fTimeStep2 = fTimeStep*fTimeStep;
		updateRemovals();
		accumulateForces();
		verlet();
		satisfyConstraints();
		
	}
	private static Pool<Vector3> Vector3Pool = Pools.get(Vector3.class);
	public synchronized int addParticle(float x, float y, float z) {
		int id = particleTotal;
		particles[id*4] = Vector3Pool.obtain().set(x,y,z);
		particles[id*4+1] = Vector3Pool.obtain().set(x,y,z);
		particles[id*4+2] = Vector3Pool.obtain();
		particles[id*4+3] = Vector3Pool.obtain();
		
		particleTotal++;
		ids[id] = id;
		return id;///getPositionVector(id);
	}
	public int addParticle(float x, float y, float z, Vector3 position) {
		int id = particleTotal;
		particles[id*4] = position.set(x,y,z);
		particles[id*4+1] = Vector3Pool.obtain().set(x,y,z);
		particles[id*4+2] = Vector3Pool.obtain();
		particles[id*4+3] = Vector3Pool.obtain();
		
		particleTotal++;
		ids[id] = id;
		return id;
	}
	public Vector3 getPositionVector(int id) {
		return particles[ids[id]*4];
	}
	public Vector3 getOldPositionVector(int id) {
		return particles[ids[id]*4+1];
		
	}
	IntArray removals = new IntArray();
	public void removeParticle(int id){
		removals.add(id);
	}
	public void updateRemovals(){
		while (removals.size > 0){
			int rid = removals.pop();
			for (int i = 0; i < particleTotal; i++){
				if (ids[i] == rid){
					particleTotal--;
					ids[i] = ids[particleTotal];
					particles[i*4] = particles[particleTotal*4];
					particles[i*4+1] = particles[particleTotal*4+1];
					particles[i*4+2] = particles[particleTotal*4+2];
					particles[i*4+3] = particles[particleTotal*4+3];
					break;
				}
			}
		}
	}
	
	public void setParticleTotal(int tot){
		particleTotal = tot;
	}

	public void applyForce(int id, float x, float y, float z) {
		particles[id*4].add(x,y,z);		
		
	}
}
