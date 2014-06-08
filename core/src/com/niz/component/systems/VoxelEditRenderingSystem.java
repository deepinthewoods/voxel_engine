package com.niz.component.systems;

import com.artemis.Aspect;
import com.artemis.Component;
import com.artemis.Entity;
import com.artemis.Filter;
import com.artemis.systems.EntitySystem;
import com.badlogic.gdx.graphics.*;
import com.badlogic.gdx.graphics.g3d.Material;
import com.badlogic.gdx.graphics.g3d.Model;
import com.badlogic.gdx.graphics.g3d.ModelBatch;
import com.badlogic.gdx.graphics.g3d.Renderable;
import com.badlogic.gdx.utils.Array;
import com.niz.observer.Observer;
import com.niz.observer.Subject;
import com.niz.observer.Subjects;

/**
 * Created by niz on 07/06/2014.
 */
public class VoxelEditRenderingSystem extends EntitySystem {
    private VoxelEditingSystem editSys;
    private ModelBatch modelBatch;
    private Camera camera;
    private Mesh mesh;
    private float[] verts;
    private Renderable renderable;


    /**
     * Creates an entity system that uses the specified filter
     * as a matcher against entities.
     *
     *
     */
    public VoxelEditRenderingSystem() {
        super(Aspect.getEmpty());
    }

    @Override
    protected void processEntities(Array<Entity> entities) {
        modelBatch.begin(camera);
        modelBatch.render(renderable);
        modelBatch.end();
    }

    @Override
    public void initialize(){
        mesh = new Mesh(true,
                8,
                24,
                VertexAttribute.Position(),
                VertexAttribute.Color()
                );

        short[] indices = new short[]{0,1,1,2,2,3,3,0, 4,5,5,6,6,7,7,4,  0,4,1,5,2,6,3,7};
        mesh.setIndices(indices);
        verts = new float[8*4];



        editSys = world.getSystem(VoxelEditingSystem.class);
        modelBatch = world.getSystem(GraphicsSystem.class).modelBatch;
        camera = world.getSystem(CameraSystem.class).camera;

        setVerts(mesh, verts, editSys);
        renderable = new Renderable();
        renderable.mesh = mesh;
        renderable.meshPartSize = 24;
        renderable.material = new Material();
        renderable.primitiveType = GL20.GL_LINES;

        Subjects.get("editorSettings").add(new Observer(){

            @Override
            public void onNotify(Entity e, Subject.Event event, Component c) {
                setVerts(mesh, verts, editSys);

            }
        });
    }

    private void setVerts(Mesh mesh, float[] verts, VoxelEditingSystem ed) {
        float c = Color.DARK_GRAY.toFloatBits();
        int i = 0;
        verts[i++] = 0;
        verts[i++] = 0;
        verts[i++] = 0;
        verts[i++] = c;

        verts[i++] = ed.sizeX;
        verts[i++] = 0;
        verts[i++] = 0;
        verts[i++] = c;

        verts[i++] = ed.sizeX;
        verts[i++] = ed.sizeY;
        verts[i++] = 0;
        verts[i++] = c;

        verts[i++] = 0;
        verts[i++] = ed.sizeY;
        verts[i++] = 0;
        verts[i++] = c;

        verts[i++] = 0;
        verts[i++] = 0;
        verts[i++] = ed.sizeZ;
        verts[i++] = c;

        verts[i++] = ed.sizeX;
        verts[i++] = 0;
        verts[i++] = ed.sizeZ;
        verts[i++] = c;

        verts[i++] = ed.sizeX;
        verts[i++] = ed.sizeY;
        verts[i++] = ed.sizeZ;
        verts[i++] = c;

        verts[i++] = 0;
        verts[i++] = ed.sizeY;
        verts[i++] = ed.sizeZ;
        verts[i++] = c;


        mesh.setVertices(verts);
    }
}
