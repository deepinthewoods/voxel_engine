package com.artemis.systems;

import java.util.BitSet;

import com.artemis.Aspect;
import com.artemis.Entity;
import com.artemis.EntityObserver;
import com.artemis.World;
import com.artemis.utils.SafeArray;
import com.badlogic.gdx.utils.Array;
import com.badlogic.gdx.utils.Json;
import com.badlogic.gdx.utils.JsonValue;
import com.badlogic.gdx.utils.Json.Serializable;
import com.badlogic.gdx.utils.ObjectIntMap;

/**
 * The most raw entity system. It should not typically be used, but you can create your own
 * entity system handling by extending this. It is recommended that you use the other provided
 * entity system implementations.
 * 
 * @author Arni Arent
 *
 */
public abstract class EntitySystem implements EntityObserver, Serializable {
    protected final int systemIndex;

    protected World world;

    protected Array<Entity> actives;

    protected Aspect aspect;

    protected BitSet allSet;
    protected BitSet exclusionSet;
    protected BitSet oneSet;

    protected boolean passive;

    protected boolean dummy;
	

    /**
     * Creates an entity system that uses the specified aspect as a matcher against entities.
     * @param aspect to match against entities
     */
    public EntitySystem(Aspect aspect) {
        actives = new SafeArray<Entity>();
        this.aspect = aspect;
        allSet = aspect.getAllSet();
        exclusionSet = aspect.getExclusionSet();
        oneSet = aspect.getOneSet();
        systemIndex = SystemIndexManager.getIndexFor(this.getClass());
        dummy = allSet.isEmpty() && oneSet.isEmpty(); // This system can't possibly be interested in any entity, so it must be "dummy"
    }

    /**
     * Called before processing of entities begins.
     */
    protected void begin() {
    }

    public final void process() {
        if(checkProcessing()) {
            // clean up entities that have been removed by other systems
            for (int i = 0; i < actives.size; i++) {
                Entity entity = actives.get(i);
                if (!entity.isActive()) {
                    actives.removeIndex(i);
                    i--;
                }
            }
            begin();
            processEntities(actives);
            end();
        }
    }

    /**
     * Called after the processing of entities ends.
     */
    protected void end() {
    }

    /**
     * Any implementing entity system must implement this method and the logic
     * to process the given entities of the system.
     * 
     * @param entities the entities this system contains.
     */
    protected abstract void processEntities(Array<Entity> entities);

    /**
     * 
     * @return true if the system should be processed, false if not.
     */
    protected boolean checkProcessing() {
        return true;
    }

    /**
     * Override to implement code that gets executed when systems are initialized.
     */
    public void initialize() {};

    /**
     * Called if the system has received a entity it is interested in, e.g. created or a component was added to it.
     * @param e the entity that was added to this system.
     */
    protected void inserted(Entity e) {};

    /**
     * Called if a entity was removed from this system, e.g. deleted or had one of it's components removed.
     * @param e the entity that was removed from this system.
     */
    protected void removed(Entity e) {};

    /**
     * Will check if the entity is of interest to this system.
     * @param e entity to check
     */
    protected final void check(Entity e) {
        if(dummy) {
            return;
        }

        boolean contains = e.getSystemBits().get(systemIndex);
        boolean interested = true; // possibly interested, let's try to prove it wrong.

        BitSet componentBits = e.getComponentBits();

        // Check if the entity possesses ALL of the components defined in the aspect.
        if(!allSet.isEmpty()) {
            for (int i = allSet.nextSetBit(0); i >= 0; i = allSet.nextSetBit(i+1)) {
                if(!componentBits.get(i)) {
                    interested = false;
                    break;
                }
            }
        }

        // Check if the entity possesses ANY of the exclusion components, if it does then the system is not interested.
        if(interested && !exclusionSet.isEmpty()) {
            interested = !exclusionSet.intersects(componentBits);
        }

        // Check if the entity possesses ANY of the components in the oneSet. If so, the system is interested.
        if(interested && !oneSet.isEmpty()) {
            interested = oneSet.intersects(componentBits);
        }

        if (interested && !contains) {
            insertToSystem(e);
        } else if (!interested && contains) {
            removeFromSystem(e);
        }
    }

    protected void removeFromSystem(Entity e) {
        actives.removeValue(e, true);
        e.getSystemBits().clear(systemIndex);
        removed(e);
    }

    protected void insertToSystem(Entity e) {
        actives.add(e);
        e.getSystemBits().set(systemIndex);
        inserted(e);
    }


    @Override
    public final void added(Entity e) {
        check(e);
    }

    @Override
    public final void changed(Entity e) {
        check(e);
    }

    @Override
    public final void deleted(Entity e) {
        if(e.getSystemBits().get(systemIndex)) {
            removeFromSystem(e);
        }
    }

    @Override
    public final void disabled(Entity e) {
        if(e.getSystemBits().get(systemIndex)) {
            removeFromSystem(e);
        }
    }

    @Override
    public final void enabled(Entity e) {
        check(e);
    }


    public final void setWorld(World world) {
        this.world = world;
    }

    public boolean isPassive() {
        return passive;
    }

    public void setPassive(boolean passive) {
        this.passive = passive;
    }

    public Array<Entity> getActives() {
        return actives;
    }



    /**
     * Used to generate a unique bit for each system.
     * Only used internally in EntitySystem.
     */
    protected static class SystemIndexManager {
        protected static int INDEX = 0;
        protected static ObjectIntMap<Class<? extends EntitySystem>> indices =
                new ObjectIntMap<Class<? extends EntitySystem>>();

        protected static int getIndexFor(Class<? extends EntitySystem> es){
            int index;
            if (!indices.containsKey(es)) {
                index = INDEX++;
                indices.put(es, index);
            } else {
                index = indices.get(es, -1);
            }
            return index;
        }
    }
    
    @Override
	public void write(Json json) {
    	
	}

	@Override
	public void read(Json json, JsonValue jsonData) {
		
	};

}
