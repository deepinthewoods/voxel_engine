package com.niz.component.systems;

import com.artemis.Aspect;
import com.artemis.Component;
import com.artemis.ComponentMapper;
import com.artemis.Entity;
import com.artemis.systems.EntitySystem;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.Camera;
import com.badlogic.gdx.math.Vector3;
import com.badlogic.gdx.scenes.scene2d.ui.Button;
import com.badlogic.gdx.tests.g3d.voxel.BlockDefinition;
import com.badlogic.gdx.tests.g3d.voxel.VoxelWorld;
import com.badlogic.gdx.utils.Array;
import com.niz.RayCaster;
import com.niz.component.*;
import com.niz.observer.Observer;
import com.niz.observer.Subject;
import com.niz.observer.Subjects;

/**
 * Created by niz on 31/05/2014.
 * raycasting and various bnlock editing stuff implemented here
 */
public class VoxelEditingSystem extends EntitySystem {
    private static final String TAG = "voxel editing system";
    public static final int EDIT_MODE_ADD = 0, EDIT_MODE_REMOVE = 1, EDIT_MODE_PLACE = 2;
    public static final int VIEW_MODE_TOP = 0, VIEW_MODE_BOTTOM = 1, VIEW_MODE_LEFT = 2
            , VIEW_MODE_RIGHT = 3, VIEW_MODE_FRONT = 4, VIEW_MODE_BACK = 5, VIEW_MODE_FREE = 6;


    private int blockTypeSelectedID, editModeSelectedID, viewMode;
    private Button selectedBlockButton;
    //private Color[] blockColors = new Color[256];

    public int sizeX = 16, sizeY = 16, sizeZ = 16;
    Position centrePoint = new Position();

    private Camera camera;
    private VoxelWorld vw;
    private boolean viewModeChanged = true;
    private ComponentMapper<Position> posM;
    private ComponentMapper<UpVector> upM;
    private EditVoxelSystem eVs;
    private Subject lookAtChanger;
    private Subject highlightBlockSubject;
    Vector3 cameraPosition = new Vector3();
    float cameraDistance;
    FacePosition highlightPos = new FacePosition();
    //Vector3 camOffset = new Vector3(16,16,16);

    Vector3 unp1 = new Vector3(), unp2 = new Vector3();

    /**
     * Creates an entity system that uses the specified filter
     * as a matcher against entities.
     *
     */
    public VoxelEditingSystem() {
        super(Aspect.getAspectForAll(CameraPositionInfluencer.class, Player.class));

    }

    @Override
    protected void processEntities(Array<Entity> entities) {
        if (entities.size < 1) return;
        if (viewModeChanged){
            Entity e = entities.get(0);
            Position pos = posM.get(e);
            UpVector up = upM.get(e);
            if (pos == null) return;

            switch (viewMode){
                case VIEW_MODE_BACK:
                    //pos.pos.set(sizeX/2, sizeY/2, -camOffset.z);
                    cameraPosition.set(0,0,-1);
                    if (up != null) up.up.set(0,1,0);
                    break;
                case VIEW_MODE_FRONT:
                    //pos.pos.set(sizeX/2, sizeY/2, sizeZ + camOffset.z);
                    cameraPosition.set(0,0,1);
                    if (up != null) up.up.set(0,1,0);
                    break;
                case VIEW_MODE_LEFT:
                    //pos.pos.set(-camOffset.x, sizeY/2, sizeZ/2);
                    cameraPosition.set(-1,0,0);
                    if (up != null) up.up.set(0,1,0);
                    break;
                case VIEW_MODE_RIGHT:
                    //pos.pos.set(sizeX + camOffset.x, sizeY/2, sizeZ/2);
                    cameraPosition.set(1,0,0);
                    if (up != null) up.up.set(0,1,0);
                    break;
                case VIEW_MODE_TOP:
                    //pos.pos.set(sizeX/2, sizeY + camOffset.y, sizeZ/2);
                    cameraPosition.set(0,1,0);
                    if (up != null) up.up.set(0,0,-1);
                    break;
                case VIEW_MODE_BOTTOM:
                    //pos.pos.set(sizeX/2, -camOffset.y, sizeZ/2);
                    cameraPosition.set(0,-1,0);
                    if (up != null) up.up.set(0,0,-1);
                    break;
                case VIEW_MODE_FREE:
                    //pos.pos.set(cameraPosition);
                    if (up != null) up.up.set(0,1,0);
                    break;
                default:

            }
            cameraPosition.nor();
            tmp.set(cameraPosition);
            tmp.scl(cameraDistance);

            pos.pos.set(tmp).add(centrePoint.pos);

            viewModeChanged = false;
        }

        float mx = Gdx.input.getX();
        float my = Gdx.input.getY();
        //Gdx.app.log(TAG, "highlight "+mx+"  ,  "+my);
        rayCastForHighlight(mx, my);
    }

    @Override
    public void initialize(){
        vw = world.getSystemOrSuperClass(VoxelSystem.class).voxelWorld;
        camera = world.getSystemOrSuperClass(CameraSystem.class).camera;
        posM = world.getMapper(Position.class);
        upM = world.getMapper(UpVector.class);
        eVs = world.getSystem(EditVoxelSystem.class);
        Subjects.get("blockTypeSelected").add(new Observer(){

            @Override
            public void onNotify(Entity e, Subject.Event event, Component c) {
                IntegerButtonValue i = (IntegerButtonValue) c;
                blockTypeSelectedID = i.value;
                selectedBlockButton = i.button;
               // Gdx.app.log(TAG, "type selected"+blockTypeSelectedID);
            }
        });

        Subjects.get("blockColorChanged").add(new Observer(){

            @Override
            public void onNotify(Entity e, Subject.Event event, Component c) {
               // IntegerValue i = (IntegerValue) c;
                ColorValue col = (ColorValue) c;
                eVs.BLOCK_COLORS[blockTypeSelectedID].set(col.color);
                selectedBlockButton.setColor(col.color);
                //Gdx.app.log(TAG, "color changed"+col);
            }
        });

        Subjects.get("editorPlaceMode").add(new Observer(){

            @Override
            public void onNotify(Entity e, Subject.Event event, Component c) {
                IntegerButtonValue i = (IntegerButtonValue) c;
                editModeSelectedID = i.value;
                //Gdx.app.log(TAG, "edit mode selected"+editModeSelectedID);
            }
        });

        Subjects.get("editorClicked").add(new Observer(){

            @Override
            public void onNotify(Entity e, Subject.Event event, Component c) {
                VectorInput i = (VectorInput) c;
                rayCast(i.v.x, i.v.y);
                //Gdx.app.log(TAG, "screen clicked"+i.v);
            }
        });

        Subjects.get("view").add(new Observer(){

            @Override
            public void onNotify(Entity e, Subject.Event event, Component c) {
                IntegerButtonValue i = (IntegerButtonValue) c;
                viewMode = i.value;
                changeViewMode();
                //rayCast(i.v.x, i.v.y);
               // Gdx.app.log(TAG, "view mode "+viewMode);
            }
        });

        lookAtChanger = Subjects.get("setCameraLookAt");

        highlightBlockSubject = Subjects.get("highlightBlock");

        Subjects.get("editorDragged").add(new Observer(){

            @Override
            public void onNotify(Entity e, Subject.Event event, Component c) {
                VectorInput2 drag = (VectorInput2) c;
                unp1.set(drag.v.x, drag.v.y, 0);
                unp2.set(drag.v2.x, drag.v2.y, 0);
                camera.unproject(unp1);
                camera.unproject(unp2);
                unp1.sub(centrePoint.pos);
                unp2.sub(centrePoint.pos);
                unp1.nor();
                unp2.nor();
                tmp.set(unp1).crs(unp2);

                tmp.set(camera.up);
                tmp.crs(cameraPosition);
                cameraPosition.rotate(tmp, drag.v2.y);
                tmp.set(camera.up);
                cameraPosition.rotate(tmp, -drag.v2.x);

                viewMode = VIEW_MODE_FREE;
                changeViewMode();

            }
        });



        Subjects.get("editorSettings").add(new Observer(){

            @Override
            public void onNotify(Entity e, Subject.Event event, Component c) {
                Vector3Input size = (Vector3Input) c;
                sizeX = (int) size.v.x;
                sizeY = (int) size.v.y;
                sizeZ = (int) size.v.z;
                lookAtChanger.notify(null, null, centrePoint);
                changeViewMode();
            }
        });

        Subjects.get("editorPinched").add(new Observer(){

            @Override
            public void onNotify(Entity e, Subject.Event event, Component c) {
                VectorInput4 vec = (VectorInput4) c;
                float dx = vec.v.x - vec.v3.x;
                float dy = vec.v2.y - vec.v4.y;


            }
        });

        changeViewMode();

    }

    private void changeViewMode() {
        viewModeChanged = true;
        //amOffset.set(sizeX*.8f, sizeY*.8f, sizeZ*.8f);
        centrePoint.pos.set(sizeX/2, sizeY/2, sizeZ/2);
        float scalar = 1.5f;
        cameraDistance = Math.max( sizeY*scalar, sizeZ*scalar);
        cameraDistance = Math.max(cameraDistance, sizeX*scalar);

    }

    private RayCaster ray = new RayCaster();
    private Vector3 src = new Vector3(), dst = new Vector3(), tmp = new Vector3();
    private void rayCast(float sx, float sy){
        src.set(sx,sy,0);
        dst.set(sx, sy, 1);
        camera.unproject(src);
        camera.unproject(dst);
        //Gdx.app.log(TAG, "trace " +sx+","+sy+ " from "+src+" to "+dst);

        ray.trace(src, dst);
        while (ray.hasNext){
            ray.next();
            if (vw.get(ray.x, ray.y, ray.z) != 0 || outOfBoundsForViewMode(ray)){
                tmp.set(ray.x, ray.y, ray.z);
                switch (editModeSelectedID){
                    case EDIT_MODE_ADD:
                        tmp.add(BlockDefinition.reflectedNormals[ray.face]);
                        break;
                    case EDIT_MODE_PLACE:
                        break;
                    case EDIT_MODE_REMOVE:
                        vw.set(tmp, (byte) 0);
                        return;
                }
                vw.set(tmp, (byte)blockTypeSelectedID);
                //Gdx.app.log(TAG, "collided, set "+tmp + " to "+blockTypeSelectedID);
                break;
            }
            //Gdx.app.log(TAG, "trace "+ray.x+","+ray.y+","+ray.z);
        }
    }

    private boolean outOfBoundsForViewMode(RayCaster ray) {
        boolean out = false;
        if (ray.stepX > 0){
            if (ray.x >= sizeX)out = true;
        } else {
            if (ray.x < 0) out = true;
        }
        if (ray.stepY > 0){
            if (ray.y >= sizeY)out = true;

        } else {
            if (ray.y < 0) out = true;
        }
        if (ray.stepZ > 0){
            if (ray.z >= sizeZ)out = true;

        } else {
            if (ray.z < 0) out = true;
        }
        return out;
        /*switch (viewMode){
            case VIEW_MODE_BACK:
                return ray.z >= sizeZ;
            case VIEW_MODE_FRONT:
                return ray.z < 0;
            case VIEW_MODE_LEFT:
                return ray.x >= sizeX;
            case VIEW_MODE_RIGHT:
                return ray.x < 0;
            case VIEW_MODE_TOP:
                return ray.y < 0;
            case VIEW_MODE_BOTTOM:
                return ray.y >= sizeY;
            default:
                return false;
        }*/
    }


    private void rayCastForHighlight(float sx, float sy){
        switch(Gdx.app.getType()) {
            case Desktop:
                break;
            default: return;

        }
        src.set(sx,sy,0);
        dst.set(sx, sy, 1);
        camera.unproject(src);
        camera.unproject(dst);
        ray.trace(src, dst);
        while (ray.hasNext){
            ray.next();
            if (vw.get(ray.x, ray.y, ray.z) != 0 || outOfBoundsForViewMode(ray)){
                tmp.set(ray.x, ray.y, ray.z);
                tmp.sub(.04f);
                highlightPos.pos.set(tmp);
                highlightPos.face = ray.face;
                highlightBlockSubject.notify(null, null, highlightPos);
                //Gdx.app.log(TAG, "highlight"+tmp);
                return;

            }
        }
    }

}
