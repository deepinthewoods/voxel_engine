package com.badlogic.gdx.tests.g3d.voxel;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.Camera;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g3d.Renderable;
import com.badlogic.gdx.graphics.g3d.Shader;
import com.badlogic.gdx.graphics.g3d.utils.RenderContext;
import com.badlogic.gdx.graphics.glutils.ShaderProgram;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.utils.GdxRuntimeException;

/**
 * Created by niz on 14/04/2014.
 */
public class VoxelShader implements Shader {
    private static final String TAG ="voxel shader" ;
    private final Texture voxelTexture;
    private ShaderProgram program;
    private Camera camera;
    private RenderContext context;
    private Vector2 tileSize = new Vector2();
    public VoxelShader(Texture voxelTexture) {
        this.voxelTexture = voxelTexture;
        tileSize.set(1f/voxelTexture.getWidth(), 1f/voxelTexture.getHeight());//1 pixel
        tileSize.scl(16);
    }

    @Override
    public void init() {
        String vert = Gdx.files.internal("data/test.vertex.glsl").readString();
        String frag = Gdx.files.internal("data/test.fragment.glsl").readString();
        program = new ShaderProgram(vert, frag);
        if (!program.isCompiled())
            throw new GdxRuntimeException(program.getLog());

    }

    @Override
    public int compareTo(Shader other) {
        return 0;
    }

    @Override
    public boolean canRender(Renderable instance) {
        return true;
    }

    @Override
    public void begin(Camera camera, RenderContext context) {
        this.camera = camera;
        this.context = context;
        voxelTexture.bind();
        program.begin();
        program.setUniformMatrix("u_projViewTrans", camera.combined);
        program.setUniformf("u_tileSizeXY", tileSize);
        context.setDepthTest(GL20.GL_LEQUAL);
        context.setDepthMask(true);
        context.setCullFace(GL20.GL_BACK);
    }

    @Override
    public void render(Renderable renderable) {
        //
        // Gdx.app.log(TAG, "render");
        program.setUniformMatrix("u_worldTrans", renderable.worldTransform);
        renderable.mesh.render(program,
                renderable.primitiveType,
                renderable.meshPartOffset,
                renderable.meshPartSize);

    }

    @Override
    public void end() {
        program.end();
    }

    @Override
    public void dispose() {
        program.dispose();
    }
}






