package com.niz.component.systems;

import com.artemis.Aspect;
import com.artemis.Entity;
import com.artemis.systems.EntitySystem;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.Camera;
import com.badlogic.gdx.graphics.OrthographicCamera;
import com.badlogic.gdx.graphics.g3d.Environment;
import com.badlogic.gdx.graphics.g3d.ModelBatch;
import com.badlogic.gdx.graphics.g3d.attributes.ColorAttribute;
import com.badlogic.gdx.utils.Array;
import com.niz.ShapeBatch;

/**
 * Created by niz on 14/04/2014.
 */
public class GraphicsSystem extends EntitySystem {
    /**
     * Creates an entity system that uses the specified aspect as a matcher against entities.
     *
     *
     */
    protected ModelBatch modelBatch;
    protected Environment env;
    protected ShapeBatch shapeBatch;
    private Camera shapeCamera;


    public GraphicsSystem() {
        super(Aspect.getEmpty());

        shapeCamera = new OrthographicCamera(Gdx.graphics.getWidth()/ Gdx.graphics.getHeight(), 1);

    }

    @Override
    protected void processEntities(Array<Entity> entities) {

        Camera camera = world.getSystem(CameraSystem.class).camera;
        shapeBatch.draw(shapeCamera, camera);

    }

    public void initialize(){
        shapeBatch = new ShapeBatch();
        env = new Environment();
        env.set(new ColorAttribute(ColorAttribute.AmbientLight, 1f, 1f, 1f, 1f));
        modelBatch = new ModelBatch();
    }
}
