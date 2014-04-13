package com.niz.component.systems;

import com.artemis.Aspect;
import com.artemis.ComponentMapper;
import com.artemis.Entity;
import com.artemis.systems.EntityProcessingSystem;
import com.badlogic.gdx.Gdx;
import com.niz.actions.Action;
import com.niz.component.ActionComponent;


public class ActionSystem extends EntityProcessingSystem {
	private static final String TAG = "action system";
	ComponentMapper<ActionComponent> actionMap;
	
	public ActionSystem() {
		super(Aspect.getAspectForOne(ActionComponent.class));
	}
	@Override
	public void initialize(){
		actionMap = world.getMapper(ActionComponent.class);

	}

	
	@Override
	protected void inserted(Entity e) {
		ActionComponent actionC = actionMap.get(e);
		actionC.action.parent = e;
		actionC.action.world = world;
		
	
		//FIXME because this method isn't called right away, already added actions will have a null parent referrence
		Action node = actionC.action.actions.getRoot();
		while (node != null){
			node.setParent(e);
			node.onStart(world);
			node = node.getNext();
		}
		
		Gdx.app.log(TAG, "set e");
	}



	@Override
	protected void process(Entity e) {
		ActionComponent actionC = actionMap.get(e);
		actionC.action.update(world.getDelta());
		
	}

}
