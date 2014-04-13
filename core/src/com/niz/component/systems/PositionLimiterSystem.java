package com.niz.component.systems;

import com.artemis.Aspect;
import com.artemis.ComponentMapper;
import com.artemis.Entity;
import com.artemis.systems.EntityProcessingSystem;
import com.badlogic.gdx.math.Vector3;
import com.niz.component.Position;
import com.niz.component.PositionLimiter;

public class PositionLimiterSystem extends EntityProcessingSystem {

	private ComponentMapper<Position> posM;
	private ComponentMapper<PositionLimiter> limitM;

	public PositionLimiterSystem() {
		super(Aspect.getAspectForAll(Position.class, PositionLimiter.class));
		// TODO Auto-generated constructor stub
	}

	
	
	@Override
	public void initialize(){
		posM = world.getMapper(Position.class);
		limitM = world.getMapper(PositionLimiter.class);
	}



	@Override
	protected void process(Entity e) {
		Vector3 position = posM.get(e).pos;
		PositionLimiter lim = limitM.get(e);
		
		
		
	}
}
