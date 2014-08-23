package com.niz.component.systems;

import com.artemis.Aspect;
import com.artemis.Entity;
import com.artemis.Filter;
import com.artemis.systems.EntitySystem;
import com.badlogic.gdx.utils.Array;

/**
 * Created by niz on 17/08/2014.
 */
public class LineBackgroundRenderingSystem extends EntitySystem {
    /**
     * Creates an entity system that uses the specified filter
     * as a matcher against entities.
     *
     */
    public LineBackgroundRenderingSystem() {
        super(Aspect.getAspectForAll(HeightMap.class));
    }

    @Override
    protected void processEntities(Array<Entity> entities) {

    }
}
