package com.niz.component.systems;

import com.artemis.Aspect;
import com.artemis.ComponentMapper;
import com.artemis.Entity;
import com.artemis.systems.DrawSystem;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.math.Vector3;
import com.badlogic.gdx.utils.Array;
import com.niz.ShapeBatch;
import com.niz.component.DebugPosition;
import com.niz.component.Position;

public class DebugPositionSystem extends DrawSystem {

	private static final String TAG = "debug position system";
	private ComponentMapper<Position> posM;
    private ShapeBatch shapeBatch;

    public DebugPositionSystem() {
		
		super(Aspect.getAspectForAll(DebugPosition.class, Position.class));
		// TODO Auto-generated constructor stub
	}

	@Override
	protected void processEntities(Array<Entity> entities) {
		

		for (Entity e : entities){
			Vector3 pos = posM.get(e).pos;
			shapeBatch.drawDebug(pos.x, pos.y, pos.z, Color.WHITE);
			//Gdx.app.log(TAG, "debug draw");

		}
	}
	@Override
	public void initialize(){
        shapeBatch = world.getSystem(GraphicsSystem.class).shapeBatch;
		posM = world.getMapper(Position.class);
	}
}
