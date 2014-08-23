package com.niz.component.systems;

import com.artemis.Aspect;
import com.artemis.ComponentMapper;
import com.artemis.Entity;
import com.artemis.EntityObserver;
import com.artemis.systems.EntitySystem;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.math.Vector3;
import com.badlogic.gdx.utils.Array;
import com.badlogic.gdx.utils.Pools;
import com.niz.component.Physics;
import com.niz.component.Position;
import com.niz.physics.JPhysicsEngine;

public class PhysicsSystem extends EntitySystem {
    private static final String TAG = "physics System ";
    public transient JPhysicsEngine engine;
	private ComponentMapper<Physics> physMap;
	private ComponentMapper<Position> posMap;
	int reps = 1; int max = 100; float step = 1f/128f;


	public PhysicsSystem(){
		super(Aspect.getAspectForOne(Physics.class));
		engine = new JPhysicsEngine(reps, max, step);
	}
	
	@Override
	public void initialize(){
		physMap = world.getMapper(Physics.class);
		posMap = world.getMapper(Position.class);

	}
	
	
	
	@Override
	protected void inserted(Entity e) {
        onEnabled2(e);

    }

    @Override
    protected void removed(Entity e) {


    }

    @Override
    protected void onDeleted(Entity e) {
        engine.removeParticle(physMap.get(e).id);
        posMap.get(e).pos = Pools.obtain(Vector3.class);
    }

    @Override
	protected void processEntities(Array<Entity> entities) {
		engine.step();
	}


    public void onEnabled2(Entity e) {
        Physics p = physMap.get(e);
        Position pos = posMap.get(e);




        if (p.id != -1){
            engine.enable(physMap.get(e));
            return;
        }

        p.id = engine.addParticle(pos.pos.x, pos.pos.y, pos.pos.z);
        Pools.free(pos.pos);
        pos.pos = engine.getPositionVector(p.id);


        //pos.pos = physics.getPositionVector(id);
        p.oldPosition = engine.getOldPositionVector(p.id);
        engine.enable(physMap.get(e));
    }

    @Override
    protected void onDisabled(Entity e) {
        engine.disable(physMap.get(e));

    }
}
