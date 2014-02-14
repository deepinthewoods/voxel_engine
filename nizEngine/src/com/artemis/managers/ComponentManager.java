package com.artemis.managers;

import java.util.BitSet;

import com.artemis.Component;
import com.artemis.ComponentMapper;
import com.artemis.Entity;
import com.artemis.utils.SafeArray;
import com.badlogic.gdx.utils.Array;
import com.badlogic.gdx.utils.ObjectIntMap;
import com.badlogic.gdx.utils.ObjectMap;
import com.badlogic.gdx.utils.Pools;

/**
 * Responsible for pooling and managing of Components and their
 * mapping to entities.
 */
public class ComponentManager extends Manager {
    protected Array<Array<Component>> componentsByType;
    protected Array<Entity> deleted;

    protected ObjectMap<Class<?>, ComponentMapper<?>> mappers;

    protected static int nextComponentClassIndex = 0;
    protected static ObjectIntMap<Class<? extends Component>> componentClassIndeces =
            new ObjectIntMap<Class<? extends Component>>();    /**
             * Returns the index of a Component class. Indices are cached, so retrieval
             * should be fast.
             * 
             * @param type Component class to retrieve the index for.
             * @return Index of a specific component class.
             */
    public static int getComponentClassIndex(Class<? extends Component> type) {
        if (componentClassIndeces.containsKey(type)) {
            return componentClassIndeces.get(type, -1);
        } else {
            componentClassIndeces.put(type, nextComponentClassIndex);
            return nextComponentClassIndex++;
        }
    }

    /**
     * Default constructor
     */
    public ComponentManager() {
        componentsByType = new SafeArray<Array<Component>>();
        deleted = new SafeArray<Entity>();
        this.mappers = new ObjectMap<Class<?>, ComponentMapper<?>>();
    }

    /**
     * Preferred way to create Components to allow for pooling.
     * 
     * @param type
     * @return
     */
    public <T extends Component> T createComponent(Class<T> type) {
        return Pools.obtain(type);
    }

    /**
     * Clean up Components belonging to the Entity
     * @param e Entity to clear components for.
     */
    public void removeComponentsOfEntity(Entity e) {
        BitSet componentBits = e.getComponentBits();
        for (int i = componentBits.nextSetBit(0); i >= 0; i = componentBits.nextSetBit(i+1)) {
            removeComponent(e.id, i);
        }
        componentBits.clear();
    }

    /**
     * Adds a Component bellonging to the specified Entity to the manager.
     * 
     * @param e Entity the component belongs to
     * @param component Component to add
     */
    public void addComponent(Entity e, Component component) {
        int classIndex = getComponentClassIndex(component.getClass());
        Array<Component> components = componentsByType.get(classIndex);
        if(components == null) {
            components = new SafeArray<Component>();
            componentsByType.set(classIndex, components);
        }
        components.set(e.id, component);

        e.getComponentBits().set(classIndex);
    }

    /**
     * Remove Component of specified class for a given Entity.
     * 
     * @param e Entity to remove the component for.
     * @param type Component class to remove.
     */
    public void removeComponent(Entity e, Class<? extends Component> type) {
        int classIndex = getComponentClassIndex(type);
        if(e.getComponentBits().get(classIndex)) {
            removeComponent(e.id, classIndex);
            e.getComponentBits().clear(classIndex);
        }
    }

    /**
     * Returns an Array of all Components of specified type.
     * 
     * @param type Type of Componets to return
     * @return an Array of said components.
     */
    public Array<Component> getComponents(Class<? extends Component> type) {
        int classIndex = getComponentClassIndex(type);
        Array<Component> components = componentsByType.get(classIndex);
        if(components == null) {
            components = new SafeArray<Component>();
            componentsByType.set(classIndex, components);
        }
        return components;
    }

    /**
     * Returns Component of the specified type belonging to specified Entity.
     * Null if not found.
     * 
     * @param e Entity to return Component for.
     * @param type Type of Component to return.
     * @return Component or null if not found.
     */
    public <T extends Component> T getComponent(Entity e, Class<T> type) {
        int classIndex = getComponentClassIndex(type);
        Array<Component> components = componentsByType.get(classIndex);
        if(components != null) {
            return type.cast(components.get(e.id));
        }
        return null;
    }

    /**
     * Fills an array with Components belonging to the specified Entity.
     * @param e Entity to get Components with.
     * @param array Array of Components to fill.
     */
    public void getComponents(Entity e, Array<Component> array) {
        BitSet componentBits = e.getComponentBits();

        for (int i = componentBits.nextSetBit(0); i >= 0; i = componentBits.nextSetBit(i+1)) {
            array.add(componentsByType.get(i).get(e.id));
        }
    }

    /**
     * Marks entity for clean up.
     */
    @Override
    public void deleted(Entity e) {
        deleted.add(e);
    }

    /**
     * Cleans up deleted Entry's components. Need to do it separately
     * to avoid freeing Components while other observers are processing
     * the removal of an entity.
     */
    public void clean() {
        if(deleted.size > 0) {
            for(int i = 0; deleted.size > i; i++) {
                removeComponentsOfEntity(deleted.get(i));
            }
            deleted.clear();
        }
    }

    /**
     * Helper method to remove a Component for specified Entity and
     * Component class index. Frees the Component to the pool.
     * 
     * @param entityId Entity to remove the component for.
     * @param componentClassIndex Component index to remove.
     */
    protected void removeComponent(int entityId, int componentClassIndex) {
        Array<Component> components = componentsByType.get(componentClassIndex);
        if (components != null) {
            Component compoment = components.get(entityId);
            if (compoment != null) {
                components.set(entityId, null);
                Pools.free(compoment);
            }
        }
    }

    /**
     * Retrieves a ComponentMapper instance for fast retrieval of components from entities.
     * 
     * @param type of component to get mapper for.
     * @return mapper for specified component type.
     */
    @SuppressWarnings("unchecked")
    public <T extends Component> ComponentMapper<T> getMapper(Class<T> type) {
        ComponentMapper<T> mapper;
        if (mappers.containsKey(type)) {
            mapper = (ComponentMapper<T>) mappers.get(type);
        } else {
            mapper = new ComponentMapper<T>(type, world);
            mappers.put(type, mapper);
        }
        return mapper;
    }

    @Override
    public void dispose() {
        for (Array<Component> components : componentsByType) {
            if (components != null) {
                Pools.freeAll(components);
                components.clear();
            }
        }
        componentsByType.clear();
        deleted.clear();
        mappers.clear();
    }

}
