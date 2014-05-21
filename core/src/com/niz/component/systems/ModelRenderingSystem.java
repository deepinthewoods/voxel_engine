package com.niz.component.systems;

import com.artemis.Aspect;
import com.artemis.ComponentMapper;
import com.artemis.Entity;
import com.artemis.systems.EntitySystem;
import com.badlogic.gdx.graphics.Camera;
import com.badlogic.gdx.graphics.g3d.Environment;
import com.badlogic.gdx.graphics.g3d.ModelBatch;
import com.badlogic.gdx.math.Matrix4;
import com.badlogic.gdx.math.Vector3;
import com.badlogic.gdx.utils.Array;
import com.niz.component.ModelInfo;
import com.niz.component.Move;
import com.niz.component.Position;

public class ModelRenderingSystem extends EntitySystem {
	private static final String TAG = "ModelRenderingSystem";
    transient ComponentMapper<ModelInfo> miMap;
    transient ComponentMapper<Position> posMap;
    transient ComponentMapper<Move> moveMap;
    private ModelBatch modelBatch;
    private Environment env;
    private Camera camera;


    public ModelRenderingSystem(){
		super(Aspect.getAspectForAll(ModelInfo.class, Position.class, Move.class));
		
	}
	@Override
	public void initialize(){
		miMap = world.getMapper(ModelInfo.class);
		posMap =  world.getMapper(Position.class);
		moveMap = world.getMapper(Move.class);
        modelBatch = world.getSystem(GraphicsSystem.class).modelBatch;
        env = world.getSystem(GraphicsSystem.class).env;
        camera = world.getSystem(CameraSystem.class).camera;
	}
	
	
	
	private transient Matrix4 rotationTransform = new Matrix4(), positionTransform = new Matrix4();

	
	protected void process(com.artemis.Entity e, float dt) {
		ModelInfo mod = miMap.get(e);
		Vector3 pos = posMap.get(e).pos;
		Move move = moveMap.get(e);		
		
		positionTransform.setToTranslation(pos.x, pos.y-.75f, pos.z);
		positionTransform.rotate(0, 1, 0, -move.rotation+90);
		
		mod.anim.update(dt);
		mod.model.transform.set(positionTransform);
		
		modelBatch.render(mod.model, env);		
		
	}

	@Override
	protected void processEntities(Array<Entity> entities) {
        modelBatch.begin(camera);
		for (Entity e : entities)
			process(e, world.getDelta());
        modelBatch.end();
		
	}
}
