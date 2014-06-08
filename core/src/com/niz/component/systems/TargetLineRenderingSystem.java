package com.niz.component.systems;

import com.artemis.Aspect;
import com.artemis.ComponentMapper;
import com.artemis.Entity;
import com.artemis.systems.EntitySystem;
import com.badlogic.gdx.utils.Array;
import com.niz.ShapeBatch;
import com.niz.actions.ActionList;
import com.niz.component.Player;

public class TargetLineRenderingSystem extends EntitySystem {

	//private ShapeBatch batch;
	private ComponentMapper<ActionList> actionMap;
    private ShapeBatch shapeBatch;


    public TargetLineRenderingSystem() {
		super(Aspect.getAspectForAll(Player.class, ActionList.class));
		//this.batch = batch;
	}

	
	
	@Override
	public void initialize(){
		super.initialize();
		actionMap = world.getMapper(ActionList.class);
        shapeBatch = world.getSystem(GraphicsSystem.class).shapeBatch;
	}

	@Override
	protected void processEntities(Array<Entity> entities) {
		for (Entity e: entities){
			ActionList action = actionMap.get(e);
			//if (!action.contains(AStand.class)){
				shapeBatch.drawCentralLine();
			//}
		}
	}

}
