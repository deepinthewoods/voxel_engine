package com.niz.commands;

import com.artemis.World;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.math.Vector3;
import com.badlogic.gdx.scenes.scene2d.ui.Table;
import com.badlogic.gdx.utils.Pools;
import com.badlogic.gdx.utils.reflect.Method;
import com.badlogic.gdx.utils.reflect.ReflectionException;

public class InvokeSpecialFunction extends Command{
private static final String TAG = "InvokeSpecialFunction command";
public Object subject;
public Method method;
public Object[] args = {new Vector3()};
@Override
public int execute(World world) {
	try {
		
		
		//Gdx.app.log(TAG, "none of the aob"+args[0].getClass());
		//method.getParameterTypes();
		
		method.invoke(subject, args);
	} catch (ReflectionException e) {
		// TODO Auto-generated catch block
		e.printStackTrace();
	}
	return 0;
}

	public void prepareStageForEditing(Table table){
		Class[] cl = method.getParameterTypes();
		
		for (int i = 0; i < args.length; i++){
			cl[i] = args[i].getClass();
			if (cl[i].equals(int.class)){
				createIntInput(table);
				Gdx.app.log(TAG, "int"+args[0]);
			} else if (cl[i].equals(float.class) || cl[i].equals(Float.class)){
				createFloatInput(table);
				Gdx.app.log(TAG, "float"+args[0]);
			} else if (cl[i].equals(Vector3.class)){
				createVectorInput(table);
				Gdx.app.log(TAG, "Vector"+args[0]);
			}
		
		}
	}

	private void createVectorInput(Table table) {
		Pools.obtain(VectorInputButton.class);
	}

	private void createFloatInput(Table table) {
		
	}

	private void createIntInput(Table table) {
		
	}

}
