package com.niz.factories;

import com.artemis.World;
import com.artemis.systems.EntitySystem;
import com.badlogic.gdx.utils.Array;
import com.badlogic.gdx.utils.GdxRuntimeException;
import com.badlogic.gdx.utils.Json;
import com.badlogic.gdx.utils.ObjectMap;
import com.badlogic.gdx.utils.ObjectMap.Entries;
import com.badlogic.gdx.utils.ObjectMap.Entry;
import com.badlogic.gdx.utils.reflect.ClassReflection;
import com.badlogic.gdx.utils.reflect.ReflectionException;

public class SystemDefinition{
	private static final String TAG = "system definition";
	transient public ObjectMap<String, Class<? extends EntitySystem>> systemClasses = new ObjectMap<String, Class<? extends EntitySystem>>();
	transient public ObjectMap<String, Class<? extends EntitySystem>> drawSystemClasses = new ObjectMap<String, Class<? extends EntitySystem>>();

	public Array<String> systems = new Array<String>(), drawSystems = new Array<String>();
    private Json json;

    public void setSystem(Class<? extends EntitySystem> class1) {
		String name = ClassReflection.getSimpleName(class1);
		systemClasses .put(name, class1);
        json.addClassTag(name, class1);
	}
	public void setDrawSystem(Class<? extends EntitySystem> class1) {
        String name = ClassReflection.getSimpleName(class1);
        drawSystemClasses.put(name, class1);
        json.addClassTag(name, class1);

    }
	
	public void procesesSystems(World world){
		
		for (String s : systems){
			EntitySystem sys = null;
			try {
				sys = (EntitySystem) ClassReflection.getConstructor(getSystemClass(s)).newInstance();
			} catch (ReflectionException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
			if (sys != null) {
                world.setSystem(sys);

            }
		}
		
		for (String s : drawSystems){
            EntitySystem dsys = null;
			try {
				dsys = (EntitySystem) ClassReflection.getConstructor(getDrawSystemClass(s)).newInstance();
			} catch (ReflectionException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
			if (dsys != null) {
                world.setDrawSystem(dsys);

            }
			
			
		}
	}
	private Class<? extends EntitySystem> getSystemClass(String sys) {
		
		Entries<String, Class<? extends EntitySystem>> iter = systemClasses.entries();
		while (iter.hasNext()){
			Entry<String, Class<? extends EntitySystem>> e = iter.next();
			if (e.key.equals(sys)){
				return e.value;
			} else {
				//Gdx.app.log(TAG, "unequal " + e.key + "  !=  "+sys);
			}
		}
		
		throw new GdxRuntimeException("System error: could not locate system class : "+sys);
		//return null;
	}
	
	private Class<? extends EntitySystem> getDrawSystemClass(String sys) {
		Entries<String, Class<? extends EntitySystem>> diter = drawSystemClasses.entries();
		while (diter.hasNext()){
			Entry<String, Class<? extends EntitySystem>> e = diter.next();
			if (e.key.equals(sys)){
				return (Class<? extends EntitySystem>) e.value;
			} else {
				//Gdx.app.log(TAG, "unequal " + e.key + "  !=  "+sys);
			}
		}
		throw new GdxRuntimeException("System error: could not locate draw system class");
	}
	
	public void preWrite(){
		systems.clear();
		Entries<String, Class<? extends EntitySystem>> iter = systemClasses.entries();
		while (iter.hasNext()){
			Entry<String, Class<? extends EntitySystem>> c = iter.next();
			systems.add(c.key);
		}
		
		drawSystems.clear();
		Entries<String, Class<? extends EntitySystem>> diter = drawSystemClasses.entries();
		while (diter.hasNext()){
			Entry<String, Class<? extends EntitySystem>> c = diter.next();
			drawSystems.add(c.key);
		}
	}


    public void setJson(Json json) {
        this.json = json;
    }
}
