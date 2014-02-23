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

package com.badlogic.gdx.tests.g3d.voxel;

import voxel.BlockDefinition;
import voxel.PerlinNoiseGenerator;

import com.artemis.Aspect;
import com.artemis.Entity;
import com.artemis.systems.DrawSystem;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.Camera;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.BitmapFont;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.graphics.g3d.Environment;
import com.badlogic.gdx.graphics.g3d.ModelBatch;
import com.badlogic.gdx.graphics.g3d.attributes.ColorAttribute;
import com.badlogic.gdx.graphics.g3d.environment.DirectionalLight;
import com.badlogic.gdx.graphics.g3d.shaders.DefaultShader;
import com.badlogic.gdx.graphics.g3d.shaders.GLES10Shader;
import com.badlogic.gdx.graphics.g3d.utils.FirstPersonCameraController;
import com.badlogic.gdx.math.MathUtils;
import com.badlogic.gdx.utils.Array;
import com.niz.blocks.TopBottomBlock;

public class VoxelTest extends DrawSystem{
	public VoxelTest() {
		super(Aspect.getEmpty());
		
	}

	SpriteBatch spriteBatch;
	BitmapFont font;
	//PerspectiveCamera camera;
	Environment lights;
	FirstPersonCameraController controller;
	public VoxelWorld voxelWorld;
	private ModelBatch modelBatch;
	private Camera camera;
	

	public void create (Camera camera) {
		spriteBatch = new SpriteBatch();
		font = new BitmapFont();
		
		DefaultShader.defaultCullFace = GL20.GL_FRONT;
		GLES10Shader.defaultCullFace = GL20.GL_FRONT;
		
		controller = new FirstPersonCameraController(camera);
		Gdx.input.setInputProcessor(controller);
		
		lights = new Environment();
		lights.set(new ColorAttribute(ColorAttribute.AmbientLight, 0.4f, 0.4f, 0.4f, 1.f));
		lights.add(new DirectionalLight().set(1, 1, 1, 0, -1, 0));
		
		
		
		
		Texture texture = new Texture(Gdx.files.internal("data/g3d/tiles.png"));
		TextureRegion[][] tiles = TextureRegion.split(texture, 16, 16);
		
		MathUtils.random.setSeed(0);
		
		BlockDefinition[] defs = getDefaultBlockDefinitions(tiles);
		
		voxelWorld = new VoxelWorld(defs, tiles[0], 1, 1, 1);
		PerlinNoiseGenerator.generateVoxels(voxelWorld, 0, 63, 10);
		float camX = voxelWorld.voxelsX / 2f;
		float camZ = voxelWorld.voxelsZ / 2f;
		float camY = voxelWorld.getHighest(camX, camZ) + 1.5f;
		camera.position.set(camX, camY, camZ);
	}

	private BlockDefinition[] getDefaultBlockDefinitions(TextureRegion[][] tiles) {
		BlockDefinition[] defs = new BlockDefinition[32];
		defs[0] = new BlockDefinition(tiles, 0)
		{

			@Override
			public void onUpdate(int x, int y, int z, VoxelWorld world) {
			}
			
		};
		defs[0].dayLightLoss = 0;
		defs[0].isSolid = false;
		//for (int i = 1; i < 32; i++){
		//	defs[i] = new BlockDefinition(tiles, i);
			//BlockDefinition.add(i, defs[i]);
		//}
		
		defs[1] = new BlockDefinition(tiles, 1){

			@Override
			public void onUpdate(int x, int y, int z, VoxelWorld world) {
			}
			
		};
		
		defs[10] = new TopBottomBlock(tiles, 8, 1, 10);
		
		
		return defs;
	
	}

	public void render (ModelBatch modelBatch) {
		
		modelBatch.render(voxelWorld, lights);

		controller.update();
		
		spriteBatch.begin();
		font.draw(spriteBatch, "fps: " + Gdx.graphics.getFramesPerSecond() + ", #visible chunks: " + voxelWorld.renderedChunks + "/" + voxelWorld.numChunks, 0, 20);
		spriteBatch.end();
	}
	
	public void resize (int width, int height) {
		spriteBatch.getProjectionMatrix().setToOrtho2D(0, 0, width, height);
		
	}

	public boolean needsGL20 () {
		return false;
	}

	@Override
	protected void processEntities(Array<Entity> entities) {
		render(modelBatch);
		
	}
	
	@Override
	public void initialize() {
		create(camera);
		
	};
}