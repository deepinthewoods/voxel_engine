/*******************************************************************************
 * Copyright 2011 See AUTHORS file.
 * 
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 * 
 *   http://www.apache.org/licenses/LICENSE-2.0
 * 
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 ******************************************************************************/

package com.niz.component.systems;

import com.artemis.Aspect;
import com.artemis.Entity;
import com.artemis.systems.DrawSystem;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.Camera;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.g3d.Environment;
import com.badlogic.gdx.graphics.g3d.attributes.ColorAttribute;
import com.badlogic.gdx.graphics.g3d.environment.DirectionalLight;
import com.badlogic.gdx.graphics.g3d.shaders.DefaultShader;
import com.badlogic.gdx.graphics.g3d.shaders.GLES10Shader;
import com.badlogic.gdx.math.MathUtils;
import com.badlogic.gdx.tests.g3d.voxel.VoxelWorld;
import com.badlogic.gdx.utils.Array;
import com.badlogic.gdx.utils.GdxRuntimeException;

public class VoxelRenderingSystem extends DrawSystem{
	private static final String TAG = "voxel rendering system";
	public VoxelRenderingSystem() {
		super(Aspect.getEmpty());
		//this.defs = defs;
		
	}

	SpriteBatch spriteBatch;
	//PerspectiveCamera camera;
	public Environment lights;
	//FirstPersonCameraController controller;
	//public VoxelWorld voxelWorld;
	
	
	public void set(){
		
		
	}

	public void create (Camera camera) {
		spriteBatch = new SpriteBatch();
		
		
		DefaultShader.defaultCullFace = GL20.GL_FRONT;
		GLES10Shader.defaultCullFace = GL20.GL_FRONT;

		lights = new Environment();
		lights.set(new ColorAttribute(ColorAttribute.AmbientLight, 0.4f, 0.4f, 0.4f, 1.f));
		lights.add(new DirectionalLight().set(1, 1, 1, 0, -1, .5f));

		MathUtils.random.setSeed(0);
		Gdx.app.log(TAG, "new voxelworld");
		//PerlinNoiseGenerator.generateVoxels(voxelWorld, 0, 63, 10);

	}

	

	
	

	@Override
	protected void processEntities(Array<Entity> entities) {
		VoxelWorld voxelWorld = world.getSystem(VoxelSystem.class).voxelWorld;
		modelBatch.render(voxelWorld, lights);
		
		
	}
	
	@Override
	public void initialize() {
		super.initialize();
		create(camera);
	};
}