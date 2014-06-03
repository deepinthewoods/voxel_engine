package com.niz.component.systems;

import com.artemis.Aspect;
import com.artemis.ComponentMapper;
import com.artemis.Entity;
import com.badlogic.gdx.math.Vector3;
import com.niz.component.RollingAverage;
import com.niz.component.UpVector;
import com.niz.component.UpVectorRollingAverage;

/**
 * Created by niz on 02/06/2014.
 */
public class UpVectorRollingAverageSystem extends RollingAverageSystem {

    private ComponentMapper<UpVector> upM;

    /**
     * uses Position to create a rolling aversge, stored in RollingAverage.result
     *

     */
    public UpVectorRollingAverageSystem() {
        super(Aspect.getAspectForAll(UpVectorRollingAverage.class, UpVector.class));
    }

    @Override
    public void initialize(){

        raM = world.getMapper(UpVectorRollingAverage.class);
        upM = world.getMapper(UpVector.class);
    }

    @Override
    public Vector3 getTarget(Entity e) {
        return upM.get(e).up;
    }
}
