package com.niz.component.systems;

import com.artemis.Aspect;
import com.artemis.Entity;
import com.artemis.Filter;
import com.artemis.systems.EntityProcessingSystem;
import com.niz.component.Position;

/**
 * Created by niz on 18/08/2014.
 */
public class PositionSystem extends EntityProcessingSystem {
    public PositionSystem() {
        super(Aspect.getAspectForAll(Position.class));
    }

    @Override
    protected void process(Entity e) {

    }

}
