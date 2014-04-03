package com.niz;


import com.badlogic.gdx.scenes.scene2d.Event;
import com.badlogic.gdx.scenes.scene2d.EventListener;
import com.badlogic.gdx.scenes.scene2d.ui.Button;
import com.badlogic.gdx.scenes.scene2d.ui.CheckBox;
import com.badlogic.gdx.scenes.scene2d.ui.Skin;
import com.badlogic.gdx.scenes.scene2d.ui.Table;
import com.badlogic.gdx.scenes.scene2d.ui.TextField;
import com.badlogic.gdx.utils.Json;
import com.badlogic.gdx.utils.Json.ReadOnlySerializer;
import com.badlogic.gdx.utils.JsonValue;
import com.badlogic.gdx.utils.ObjectMap;
import com.badlogic.gdx.utils.reflect.ClassReflection;
import com.badlogic.gdx.utils.reflect.Field;
import com.badlogic.gdx.utils.reflect.Method;

/**
 * uses Pools for creation.
 * also stores cached Methods
 * @author 
 *
 */
public class PojoEditingJson {
	public ObjectMap<Class, Method[]> methodsByClass = new ObjectMap<Class, Method[]>();
	public ObjectMap<Class, Field[]> fieldsByClass = new ObjectMap<Class, Field[]>();
	private Skin skin;
	public PojoEditingJson(Skin skin){
		this.skin = skin;
	}
	/*@Override
	protected Object newInstance (Class type) {
		try {
			return Pools.obtain(type);//ClassReflection.newInstance(type);
		} catch (Exception ex) {
			try {
				// Try a private constructor.
				Constructor constructor = ClassReflection.getDeclaredConstructor(type);
				constructor.setAccessible(true);
				return constructor.newInstance();
			} catch (SecurityException ignored) {
			} catch (ReflectionException ignored) {
				if (type.isEnum()) {
					return type.getEnumConstants()[0];
				}
				if (type.isArray())
					throw new SerializationException("Encountered JSON object when expected array of type: " + type.getName(), ex);
				else if (ClassReflection.isMemberClass(type) && !ClassReflection.isStaticClass(type))
					throw new SerializationException("Class cannot be created (non-static member class): " + type.getName(), ex);
				else
					throw new SerializationException("Class cannot be created (missing no-arg constructor): " + type.getName(), ex);
			} catch (Exception privateConstructorException) {
				ex = privateConstructorException;
			}
			throw new SerializationException("Error constructing instance of class: " + type.getName(), ex);
		}
	}*/
	
	protected Method[] getMethods(Object o){
		Class cl = o.getClass();
		Method[] ret = methodsByClass.get(cl );
		if (ret == null)ret = cacheMethods(cl);
		return ret;
	}

	private Method[] cacheMethods(Class cl) {
		Method[] val = ClassReflection.getMethods(cl);
		methodsByClass.put(cl, val );
		return val;
	}
	
	protected Field[] getFields(Object o){
		Class cl = o.getClass();
		Field[] ret = fieldsByClass.get(cl );
		if (ret == null)ret = cacheFields(cl);
		return ret;
	}

	private Field[] cacheFields(Class cl) {
		Field[] val = ClassReflection.getFields(cl);
		fieldsByClass.put(cl, val );
		return val;
	}
	
	
	public ObjectEditManager buildPojoEditor(Object o){
		
		ObjectEditManager table = new ObjectEditManager();
		Json json = new Json();
		
		String s = json.toJson(o);
		
		JsonValue value = ;
		
		table.set(o, value);
		
		for (JsonValue child =  value ; child != null; child = child.next){
			if (child.isValue()){
				if (child.isBoolean()){
					addBooleanEditor(table, child);
				} else if (child.isString()){
					addStringEditor(table, child);
				} else if (child.isDouble()){
					addFloatEditor(table, child);
				} else if (child.isNumber()){
					addIntEditor(table, child);
				} else if (child.isLong()){
					addIntEditor(table, child);
				}
			} else if (child.isArray()){
				
			} else if (child.isObject()){
				
			}
		}
		
		return table; 
	}

	private void addFloatEditor(Table table, final JsonValue child) {

		final TextField button = new TextField(child.name, skin);
		button.addListener(new EventListener(){

			@Override
			public boolean handle(Event event) {
				//if (event.getTarget().equals(arg0))
					child.set(Float.parseFloat(button.getText())); 
				return false;
			}
			
		});
		table.add(button);
		table.row();
	
	}
	
	private void addBooleanEditor(Table table, final JsonValue child) {
		final CheckBox checkBox = new CheckBox(child.name, skin);
		checkBox.addListener(new EventListener(){

			@Override
			public boolean handle(Event event) {
				//if (event.getTarget().equals(arg0))
					child.set(((Button) checkBox).isChecked()); 
				return false;
			}
			
		});
		table.add(checkBox);
		table.row();
		
	}
	
	private void addStringEditor(Table table, final JsonValue child){
		final TextField button = new TextField(child.name, skin);
		button.addListener(new EventListener(){

			@Override
			public boolean handle(Event event) {
				//if (event.getTarget().equals(arg0))
					child.set(button.getText()); 
				return false;
			}
			
		});
		table.add(button);
		table.row();
	}
	
	private void addIntEditor(Table table, final JsonValue child){
		final TextField button = new TextField(child.name, skin);
		button.addListener(new EventListener(){

			@Override
			public boolean handle(Event event) {
				//if (event.getTarget().equals(arg0))
					child.set(Integer.parseInt(button.getText())); 
				return false;
			}
			
		});
		table.add(button);
		table.row();
	}
	
	
	
	
}
