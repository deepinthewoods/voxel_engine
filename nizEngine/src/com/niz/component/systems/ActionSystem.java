package com.niz.component.systems;

import com.artemis.Aspect;
import com.artemis.ComponentMapper;
import com.artemis.Entity;
import com.artemis.systems.EntityProcessingSystem;
import com.niz.component.ActionComponent;


public class ActionSystem extends EntityProcessingSystem {
	ComponentMapper<ActionComponent> actionMap;
	
	public ActionSystem(Aspect aspect) {
		super(Aspect.getAspectForOne(ActionComponent.class));
		actionMap = world.getMapper(ActionComponent.class);
	}


	
	



	@Override
	protected void process(Entity e) {
		ActionComponent actionC = actionMap.get(e);
		actionC.action.update(world.getDelta());
		
	}

}
