package com.niz.component.systems;

import com.artemis.Aspect;
import com.artemis.ComponentMapper;
import com.artemis.Entity;
import com.artemis.systems.EntityProcessingSystem;
import com.niz.component.UpVectorRollingAverage;
import com.niz.observer.Subject;

/**
 * Created by niz on 03/06/2014.
 */
public class CameraUpVectorSystem extends EntityProcessingSystem {
    private ComponentMapper<UpVectorRollingAverage> upM;
    private Subject upNotifier;

    public CameraUpVectorSystem() {
        super(Aspect.getAspectForAll(UpVectorRollingAverage.class));
    }

    @Override
    protected void process(Entity e) {
        UpVectorRollingAverage up = upM.get(e);
        upNotifier.notify(null, null, up);
    }

    public void initialize(){
        upM = world.getMapper(UpVectorRollingAverage.class);
        upNotifier = world.getSystem(CameraControllerSystem.class).upVectorNotifier;
    }
}
