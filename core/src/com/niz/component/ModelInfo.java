/**
 * 
 */
package com.niz.component;


import com.artemis.Component;
import com.badlogic.gdx.graphics.g3d.ModelInstance;
import com.badlogic.gdx.graphics.g3d.utils.AnimationController;

/**
 * @author User
 *
 */
public class ModelInfo implements Component {
	
	public static final float TRANSITION_TIME = 1f;
	public ModelInstance model;
	public AnimationController anim;
	/**
	 * 
	 */
	
	
	public ModelInfo() {
		
	}
	
	public void set(ModelInstance playerModel, AnimationController animController){
		//Gdx.app.log("mod", "set");
		this.model = playerModel;
		anim = animController;
	}

	@Override
	public void reset() {
		// TODO Auto-generated method stub
		
	}
}
