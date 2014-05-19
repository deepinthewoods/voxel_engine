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
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g3d.Material;
import com.badlogic.gdx.graphics.g3d.ModelBatch;
import com.badlogic.gdx.graphics.g3d.Renderable;
import com.badlogic.gdx.graphics.g3d.Shader;
import com.badlogic.gdx.graphics.g3d.attributes.ColorAttribute;
import com.badlogic.gdx.graphics.g3d.attributes.TextureAttribute;
import com.badlogic.gdx.graphics.g3d.shaders.BaseShader;
import com.badlogic.gdx.graphics.g3d.shaders.DefaultShader;
import com.badlogic.gdx.graphics.g3d.utils.DefaultTextureBinder;
import com.badlogic.gdx.graphics.g3d.utils.RenderContext;
import com.badlogic.gdx.graphics.glutils.ShaderProgram;
import com.badlogic.gdx.math.MathUtils;
import com.badlogic.gdx.tests.g3d.voxel.VoxelShader;
import com.badlogic.gdx.tests.g3d.voxel.VoxelWorld;
import com.badlogic.gdx.utils.Array;
import com.badlogic.gdx.utils.GdxRuntimeException;

public class VoxelRenderingSystem extends DrawSystem{
	private static final String TAG = "voxel rendering system";
    private ModelBatch modelBatch;
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
        if (voxS != null)
            voxelWorld = voxS.voxelWorld;
        else {
            voxelWorld = world.getSystem(EditVoxelSystem.class).voxelWorld;
        }


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