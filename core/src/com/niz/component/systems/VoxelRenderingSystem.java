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
        String vertexShader = "attribute vec4 a_position;    \n" +
                "attribute vec4 a_color;\n" +
                "attribute vec2 a_texCoord0;\n" +
                "uniform mat4 u_worldView;\n" +
                "varying vec4 v_color;" +
                "varying vec2 v_texCoords;" +
                "void main()                  \n" +
                "{                            \n" +
                "   v_color = vec4(1, 1, 1, 1); \n" +
                "   v_texCoords = a_texCoord0; \n" +
                "   gl_Position =  u_worldView * a_position;  \n"      +
                "}                            \n" ;
        String fragmentShader = "#ifdef GL_ES\n" +
                "precision mediump float;\n" +
                "#endif\n" +
                "varying vec4 v_color;\n" +
                "varying vec2 v_texCoords;\n" +
                "uniform sampler2D u_texture;\n" +
                "void main()                                  \n" +
                "{                                            \n" +
                "  gl_FragColor = v_color * texture2D(u_texture, v_texCoords);\n"+
                "}";
       // Gdx.app.log(TAG, "INIT VW RENDERINGSYSTEM");

    }


	@Override
	protected void processEntities(Array<Entity> entities) {
        //if (shaderProgram.isCompiled())

        //shaderProgram.begin();
        Shader shader = voxelWorld.getShader();

        modelBatch.begin(camera);

       // if (env == null) throw new GdxRuntimeException("null environment");
       modelBatch.render(voxelWorld, shader);

        modelBatch
                .end();


		
	}
	
	@Override
	public void initialize() {
		super.initialize();
        GraphicsSystem grap = world.getSystem(GraphicsSystem.class);
        modelBatch = world.getSystem(GraphicsSystem.class).modelBatch;
        camera = world.getSystem(GraphicsSystem.class).camera;
        voxelWorld = world.getSystem(VoxelSystem.class).voxelWorld;


        Texture voxelTexture = world.getSystem(AssetsSystem.class).assets.get("data/tiles.png", Texture.class);
        Material material = new Material(new ColorAttribute(ColorAttribute.Diffuse,  1f, 1f, 1f, 1)
                , new TextureAttribute(TextureAttribute.Diffuse, voxelTexture)
        );
        voxelWorld.setMaterial(material);
        final Renderable ren = new Renderable();
        ren.material = material;
        ren.environment = grap.env;

        Shader shader = new VoxelShader();
        voxelWorld.setShader(shader);
        shader.init();


	};
}