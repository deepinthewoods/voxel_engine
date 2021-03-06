package com.artemis;

import com.badlogic.gdx.utils.Array;
import com.badlogic.gdx.utils.Pools;
import com.badlogic.gdx.utils.reflect.ClassReflection;
import com.badlogic.gdx.utils.reflect.Field;
import com.badlogic.gdx.utils.reflect.ReflectionException;
import com.niz.component.Transient;

/**
 * Created by niz on 26/05/2014.
 */
public class EntityDefinition {
    String name;
    int id;

    public ComponentArray c = new ComponentArray();

    public boolean addAll(Array<Component> cs) {
        boolean valid = true;
        for (int i = 0, n = cs.size; i < n; i++){
            Component com = cs.get(i);
            if (com instanceof Transient)
                valid = false;
            c.add(com);
        }
        return valid;


    }
    public void add(EntityDefinition def){
        Component defComponent;

        for (int i = 0; i < def.c.size; i++) {
           for (int j = 0; j < c.size; j++){
               if (def.c.get(i).getClass().isAssignableFrom(c.get(j).getClass())){
                   for (Component com : def.c){
                       Class cl = com.getClass();
                       Field[] fields = ClassReflection.getFields(cl);

                       Component component = (Component) c.get(i);

                       for (Field f : fields){
                           try {
                               f.set(component, f.get(com));
                           } catch (ReflectionException e1) {
                               e1.printStackTrace();

                           }
                       }

                   }
                   return;
               }
           }

        }

        for (Component com : def.c){
            Class cl = com.getClass();
            Field[] fields = ClassReflection.getFields(cl);

            Component component = (Component) Pools.obtain(cl);

            for (Field f : fields){
                try {
                    f.set(component, f.get(com));
                } catch (ReflectionException e1) {
                    e1.printStackTrace();

                }
            }

        }

    }
    //@returns true if valid
    public boolean setFrom(Entity e){

        c.clear();
        Array<Component> cs = e.getComponents();
        return addAll(cs);

    }

    public void setEntityFromThisOnce(Entity e){
        for (Component com : c){
            e.addComponent(com);
        }
        c.clear();
        //Pools.free(components);
        //components = null;
    }

    public void setEntityFromThisRepeatable(Entity e){
        for (Component com : c){
            Class cl = com.getClass();
            Field[] fields = ClassReflection.getFields(cl);

            Component component = e.add(com.getClass());
            for (Field f : fields){
                try {
                    f.set(component, f.get(com));
                } catch (ReflectionException e1) {
                    e1.printStackTrace();

                }
            }

        }
    }


}
