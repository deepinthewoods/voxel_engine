package com.artemis;

import com.badlogic.gdx.utils.Array;


/**
 * High performance component retrieval from entities. Use this wherever you
 * need to retrieve components from entities often and fast.
 * 
 * @author Arni Arent
 *
 * @param <A> the class type of the component
 */
public class ComponentMapper<A extends Component> {

    private Class<A> classType;
    private Array<Component> components;

    public ComponentMapper(Class<A> type, World world) {
        components = world.getComponentManager().getComponents(type);
        this.classType = type;
    }

    /**
     * Fast but unsafe retrieval of a component for this entity.
     * No bounding checks, so this could throw an ArrayIndexOutOfBoundsExeption,
     * however in most scenarios you already know the entity possesses this component.
     * 
     * @param e the entity that should possess the component
     * @return the instance of the component
     */
    public A get(Entity e) {
        return classType.cast(components.get(e.id));
    }

    /**
     * Fast and safe retrieval of a component for this entity.
     * If the entity does not have this component then null is returned.
     * 
     * @param e the entity that should possess the component
     * @return the instance of the component
     */
    public A getSafe(Entity e) {
        if(e.id < components.size) {
            return classType.cast(components.get(e.id));
        }
        return null;
    }

    /**
     * Checks if the entity has this type of component.
     * @param e the entity to check
     * @return true if the entity has this component type, false if it doesn't.
     */
    public boolean has(Entity e) {
        return getSafe(e) != null;
    }
}
