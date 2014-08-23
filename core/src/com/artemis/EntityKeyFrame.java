package com.artemis;

import com.badlogic.gdx.utils.Pool;

/**
 * Created by niz on 17/08/2014.
 */
public class EntityKeyFrame implements Pool.Poolable{
    public long tick;
    public Entity  parent;// = new Entity();
    public EntityDefinition def = new EntityDefinition();;
    public EntityKeyFrame(Entity entity) {
    }

    public void setFrom(Entity e){

        tick = e.tick;
        def.setFrom(e);

    }

    @Override
    public void reset() {
        parent = null;
    }


}
