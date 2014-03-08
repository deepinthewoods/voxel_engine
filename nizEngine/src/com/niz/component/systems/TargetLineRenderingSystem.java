package com.niz.component.systems;

import com.artemis.Aspect;
import com.artemis.ComponentMapper;
import com.artemis.Entity;
import com.artemis.systems.DrawSystem;
import com.badlogic.gdx.utils.Array;
import com.niz.ShapeBatch;
import com.niz.actions.AStand;
import com.niz.component.ActionComponent;
import com.niz.component.Player;

public class TargetLineRenderingSystem extends DrawSystem {

	private ShapeBatch batch;
	private ComponentMapper<ActionComponent> actionMap;
	

	public TargetLineRenderingSystem(ShapeBatch batch) {
		super(Aspect.getAspectForAll(Player.class, ActionComponent.class));
		this.batch = batch;
	}

	
	
	@Override
	public void initialize(){
		super.initialize();
		actionMap = world.getMapper(ActionComponent.class);
	}

	@Override
	protected void processEntities(Array<Entity> entities) {
		for (Entity e: entities){
			ActionComponent action = actionMap.get(e);
			if (!action.action.actions.contains(AStand.class)){
				batch.drawCentralLine();
			}
		}
	}

}
