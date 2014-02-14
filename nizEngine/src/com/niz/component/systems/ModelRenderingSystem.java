package com.niz.component.systems;

import com.artemis.Aspect;
import com.artemis.ComponentMapper;
import com.artemis.Entity;
import com.artemis.systems.DrawSystem;
import com.badlogic.gdx.graphics.Camera;
import com.badlogic.gdx.graphics.g3d.Environment;
import com.badlogic.gdx.graphics.g3d.ModelBatch;
import com.badlogic.gdx.math.Matrix4;
import com.badlogic.gdx.math.Vector3;
import com.badlogic.gdx.utils.Array;
import com.niz.component.ModelInfo;
import com.niz.component.Move;
import com.niz.component.Position;

public class ModelRenderingSystem extends DrawSystem{
	private static final String TAG = "ModelRenderingSystem";
	ComponentMapper<ModelInfo> miMap;
	ComponentMapper<Position> posMap;
	ComponentMapper<Move> moveMap;
	
	ModelBatch batch;
	Camera cam;
	Environment env;
	public ModelRenderingSystem(){
		super(Aspect.getAspectForAll(ModelInfo.class, Position.class, Move.class));
		miMap = world.getMapper(ModelInfo.class);
		posMap =  world.getMapper(Position.class);
		moveMap = world.getMapper(Move.class);
	}
	
	public void set(ModelBatch batch, Camera cam, Environment env){
		this.batch = batch;
		this.cam = cam;
		this.env = env;
	}
	
	private Matrix4 rotationTransform = new Matrix4(), positionTransform = new Matrix4();

	
	protected void process(com.artemis.Entity e, float dt) {
		ModelInfo mod = miMap.get(e);
		Vector3 pos = posMap.get(e).pos;
		Move move = moveMap.get(e);
		//Gdx.app.log("cont", "modelrender"+(pos));
		
		positionTransform.setToTranslation(pos.x, pos.y, pos.z);
		positionTransform.rotate(0, 1, 0, -move.rotation+90);
		
		mod.anim.update(dt);
		
		mod.model.transform.set(positionTransform);
		
		batch.render(mod.model, env);		
		
	}

	@Override
	protected void processEntities(Array<Entity> entities) {
		for (Entity e : entities)
			process(e, delta);
		
	}
}
