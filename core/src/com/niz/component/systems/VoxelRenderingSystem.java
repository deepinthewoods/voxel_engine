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
import com.artemis.systems.EntitySystem;
import com.badlogic.gdx.graphics.Camera;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g3d.Material;
import com.badlogic.gdx.graphics.g3d.ModelBatch;
import com.badlogic.gdx.graphics.g3d.Shader;
import com.badlogic.gdx.graphics.g3d.attributes.ColorAttribute;
import com.badlogic.gdx.graphics.g3d.attributes.TextureAttribute;
import com.badlogic.gdx.tests.g3d.voxel.VoxelShader;
import com.badlogic.gdx.tests.g3d.voxel.VoxelWorld;
import com.badlogic.gdx.utils.Array;

public class VoxelRenderingSystem extends EntitySystem {
	private static final String TAG = "voxel rendering system";
    private transient ModelBatch modelBatch;
    private Camera camera;





    private VoxelWorld voxelWorld;


    public VoxelRenderingSystem() {
		super(Aspect.getEmpty());
		//this.defs = defs;

       // Gdx.app.log(TAG, "INIT VW RENDERINGSYSTEM");

    }


	@Override
	protected void processEntities(Array<Entity> entities) {
        //if (shaderProgram.isCompiled())
       // Gdx.gl.glClear(GL20.GL_COLOR_BUFFER_BIT | GL20.GL_DEPTH_BUFFER_BIT);

        Shader shader = voxelWorld.getShader();
        Texture voxelTexture = world.getSystem(AssetsSystem.class).assets.get("data/tiles.png", Texture.class);
        voxelTexture.bind();
        modelBatch.begin(camera);

        modelBatch.render(voxelWorld);

        modelBatch.end();


		
	}
	
	@Override
	public void initialize() {
		super.initialize();
        GraphicsSystem grap = world.getSystem(GraphicsSystem.class);
        modelBatch = world.getSystem(GraphicsSystem.class).modelBatch;
        camera = world.getSystem(CameraSystem.class).camera;
        VoxelSystem voxS = world.getSystem(VoxelSystem.class);

        voxelWorld = voxS.voxelWorld;



        Texture voxelTexture = world.getSystem(AssetsSystem.class).assets.get("data/tiles.png", Texture.class);
        Material material = new Material( new ColorAttribute(ColorAttribute.Diffuse,  1f, 1f, 1f, 1)
                , new TextureAttribute(TextureAttribute.Diffuse, voxelTexture)
        );
        voxelWorld.setMaterial(material);


        Shader shader = new VoxelShader();
        shader.init();
        voxelWorld.setShader(shader);



	};
}