package com.niz.component.systems;

import com.artemis.Aspect;
import com.artemis.ComponentMapper;
import com.artemis.Entity;
import com.artemis.systems.EntityProcessingSystem;
import com.artemis.systems.EntitySystem;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.utils.Array;
import com.badlogic.gdx.utils.BinaryHeap;
import com.niz.actions.Action;
import com.niz.actions.ActionList;


public class ActionSystem extends EntitySystem {
	private static final String TAG = "action system";
	ComponentMapper<ActionList> actionMap;

    public BinaryHeap<Action> actions = new BinaryHeap<Action>();

	public ActionSystem() {
		super(Aspect.getAspectForOne(ActionList.class));
	}
	@Override
	public void initialize(){
		actionMap = world.getMapper(ActionList.class);



	}

	
	@Override
	protected void inserted(Entity e) {
		ActionList actionC = actionMap.get(e);
        actionC.delayedActions = actions;
        actionC.inserted(e, world);

	}

    @Override
    protected void removed(Entity e) {
        ActionList actionC = actionMap.get(e);
        actionC.removed(e, world);
    }

    @Override
    protected void processEntities(Array<Entity> entities) {


        while (actions.size > 0 ){// del.actions.peek().getValue()<del.currentTime){
            Action a = actions.pop();
            if (a.getValue() >= a.parent.currentTime){
                actions.add(a);
                break;
            }
            a.unDelay();
        }
        //Gdx.app.log(TAG, "update");

        for (int i = 0, s = entities.size; i < s; i++) {
            process(entities.get(i));
        }

    }



	protected void process(Entity e) {
		ActionList actionC = actionMap.get(e);
		actionC.update(world.getDelta());
		
	}

}
