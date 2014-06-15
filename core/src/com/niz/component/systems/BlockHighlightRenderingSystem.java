package com.niz.component.systems;

import com.artemis.Aspect;
import com.artemis.ComponentMapper;
import com.artemis.Entity;
import com.artemis.systems.EntitySystem;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.Camera;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.graphics.Mesh;
import com.badlogic.gdx.graphics.g3d.Material;
import com.badlogic.gdx.graphics.g3d.ModelBatch;
import com.badlogic.gdx.graphics.g3d.Renderable;
import com.badlogic.gdx.graphics.g3d.RenderableProvider;
import com.badlogic.gdx.math.Vector3;
import com.badlogic.gdx.tests.g3d.voxel.BlockDefinition;
import com.badlogic.gdx.utils.Array;
import com.badlogic.gdx.utils.Pool;
import com.niz.component.BlockHighlight;
import com.niz.component.Face;
import com.niz.component.Position;

/**
 * Created by niz on 07/06/2014.
 */
public class BlockHighlightRenderingSystem extends EntitySystem implements RenderableProvider
{
    private static final String TAG = "block highlight rendering system";
    short[][] indices = new short[][]{
            {4,5, 5,6, 6,7, 7,4},
            {0,1, 1,2, 2,3, 3,0},
            {0,3, 4,7, 3,7, 0,4},
            {1,5, 2,6, 1,2, 5,6},
            {0,4, 1,5, 0,1, 4,5},
            {2,6, 3,7, 2,3, 6,7},
            {0,1,1,2,2,3,3,0, 4,5,5,6,6,7,7,4,  0,4,1,5,2,6,3,7}
    };
    float[] verts = new float[8*4];

    private ModelBatch modelBatch;
    private Camera camera;
    private ComponentMapper<BlockHighlight> bhM;
    private ComponentMapper<Position> posM;
    private Material material = new Material();
    private ComponentMapper<Face> faceM;

    /**
     * Creates an entity system that uses the specified filter
     * as a matcher against entities.
     *
     */
    public BlockHighlightRenderingSystem() {
        super(Aspect.getAspectForAll(BlockHighlight.class, Position.class, Face.class));
    }

    @Override
    protected void processEntities(Array<Entity> entities) {
        ents = entities;
        modelBatch.begin(camera);
        modelBatch.render(this);
        modelBatch.end();
    }
    private Array<Entity> ents;
    @Override
    public void getRenderables(Array<Renderable> renderables, Pool<Renderable> pool) {
        for (Entity e : ents){
            BlockHighlight hi = bhM.get(e);
            Position pos = posM.get(e);
            Face face = faceM.get(e);
            Renderable renderable = pool.obtain();
            renderable.mesh = hi.mesh;
            if (hi.dirty) {
                setVerts(renderable.mesh, pos.pos, hi.size, hi.color);
                hi.mesh.setIndices(indices[face.face]);
                hi.dirty = false;
                //Gdx.app.log(TAG, "highlight"+pos.pos);
            }
            if (face.face == BlockDefinition.ALL)
                renderable.meshPartSize = 24;
            else renderable.meshPartSize = 8;
            renderable.material = material;
            renderable.primitiveType = GL20.GL_LINES;
            renderable.worldTransform.idt();
            renderables.add(renderable);
            //Gdx.app.log(TAG, "highlight"+pos.pos);

        }

    }

    protected void inserted(Entity e) {

    };

    @Override
    public void initialize(){
        modelBatch = world.getSystem(GraphicsSystem.class).modelBatch;
        camera = world.getSystem(CameraSystem.class).camera;
        bhM = world.getMapper(BlockHighlight.class);
        posM = world.getMapper(Position.class);
        faceM = world.getMapper(Face.class);
    }

    private void setVerts(Mesh mesh, Vector3 offset, Vector3 size, Color color) {

        float c = color.toFloatBits();
        int i = 0;
        verts[i++] = offset.x;
        verts[i++] = offset.y;
        verts[i++] = offset.z;
        verts[i++] = c;

        verts[i++] = offset.x+size.x;
        verts[i++] = offset.y;
        verts[i++] = offset.z+0;
        verts[i++] = c;

        verts[i++] = offset.x+size.x;
        verts[i++] = offset.y+size.y;
        verts[i++] = offset.z+0;
        verts[i++] = c;

        verts[i++] = offset.x+0;
        verts[i++] = offset.y+size.y;
        verts[i++] = offset.z+0;
        verts[i++] = c;

        verts[i++] = offset.x+0;
        verts[i++] = offset.y+0;
        verts[i++] = offset.z+size.z;
        verts[i++] = c;

        verts[i++] = offset.x+size.x;
        verts[i++] = offset.y+0;
        verts[i++] = offset.z+size.z;
        verts[i++] = c;

        verts[i++] = offset.x+size.x;
        verts[i++] = offset.y+size.y;
        verts[i++] = offset.z+size.z;
        verts[i++] = c;

        verts[i++] = offset.x+0;
        verts[i++] = offset.y+size.y;
        verts[i++] = offset.z+size.z;
        verts[i++] = c;


        mesh.setVertices(verts);
    }
}
