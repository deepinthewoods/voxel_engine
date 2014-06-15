package com.niz.component.systems;

import com.artemis.Aspect;
import com.artemis.Component;
import com.artemis.ComponentMapper;
import com.artemis.Entity;
import com.artemis.systems.EntitySystem;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.Camera;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.math.MathUtils;
import com.badlogic.gdx.math.Vector3;
import com.badlogic.gdx.scenes.scene2d.ui.Button;
import com.badlogic.gdx.tests.g3d.voxel.*;
import com.badlogic.gdx.utils.Array;
import com.badlogic.gdx.utils.Pools;
import com.niz.RayCaster;
import com.niz.actions.AHighlightLerpColor;
import com.niz.actions.AHighlightLerpColorSwitch;
import com.niz.actions.ActionList;
import com.niz.component.*;
import com.niz.observer.Observer;
import com.niz.observer.Subject;
import com.niz.observer.Subjects;
import com.niz.ui.edgeUI.EdgeUI;

/**
 * Created by niz on 31/05/2014.
 * raycasting and various bnlock editing stuff implemented here
 */
public class VoxelEditingSystem extends EntitySystem {
    private static final String TAG = "voxel editing system";
    public static final int EDIT_MODE_ADD = 0, EDIT_MODE_REMOVE = 1, EDIT_MODE_PLACE = 2;
    public static final int VIEW_MODE_TOP = 0, VIEW_MODE_BOTTOM = 1, VIEW_MODE_LEFT = 2
    , VIEW_MODE_RIGHT = 3, VIEW_MODE_FRONT = 4, VIEW_MODE_BACK = 5, VIEW_MODE_FREE = 6;
    public static final int EDIT_MODE_CUBE_ON = 3, EDIT_MODE_CUBE_OFF = 4, EDIT_MODE_FACE_SET = 5, EDIT_MODE_FACE_REMOVE = 6;
    public boolean cubeMode;

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
    private ComponentMapper<BlockHighlight> highM;
    private ComponentMapper<Face> faceM;
    private Subject lookAtChanger;
    // private Subject highlightBlockSubject;
    Vector3 cameraPosition = new Vector3();
    float cameraDistance;
    TextureCreationBatcher textureBatch = new TextureCreationBatcher();
    FacesPreprocessor facePre = textureBatch.facesPre;
    GreedyMesher greedy = new GreedyMesher(textureBatch);
    //MeshBatch defaultMeshBatcher;


    Entity highlighter, outlineHighlighter, selectionHighlighter, selectionStartMarker;

    boolean saving = false;

    Vector3 unp1 = new Vector3(), unp2 = new Vector3();
    private VoxelSystem vSys;
    //private Position selectedBlockPosition = new Position();

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

        if (saving){
            //check for all meshes completed
            boolean allMeshesCompleted = true;
            int plane = 0;
            for (int x = 0; x < sizeX; x += vw.CHUNK_SIZE_X)
                for (int y = 0; y < sizeY; y += vw.CHUNK_SIZE_Y)
                    for (int z = 0; z < sizeZ; z += vw.CHUNK_SIZE_Z){
                        if (vw.getDirtyfromVoxel(x,y,z,plane)) allMeshesCompleted = false;
                    }

            if (allMeshesCompleted){

                EdgeUI.getCurrentlyEnabled().enableTouches();
            }

            saving = false;
        }
    }

    @Override
    public void initialize(){
        vSys = world.getSystemOrSuperClass(VoxelSystem.class);
        vw = vSys.voxelWorld;
        //vSys.mesher.preprocessor = null;
        vSys.setColoredBatcher();
        vSys.setPreprocessor(facePre);

        camera = world.getSystemOrSuperClass(CameraSystem.class).camera;
        posM = world.getMapper(Position.class);
        upM = world.getMapper(UpVector.class);
        highM = world.getMapper(BlockHighlight.class);
        faceM = world.getMapper(Face.class);
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
                ColoredMeshBatcher.blockColors[blockTypeSelectedID].set(col.color);
                selectedBlockButton.setColor(col.color);
                setAllDirty();
                //Gdx.app.log(TAG, "color changed"+col);
            }
        });

        Subjects.get("editorPlaceMode").add(new Observer(){

            @Override
            public void onNotify(Entity e, Subject.Event event, Component c) {
                IntegerButtonValue i = (IntegerButtonValue) c;
                if (i.value == EDIT_MODE_CUBE_ON){
                    cubeMode = true;
                } else if (i.value == EDIT_MODE_CUBE_OFF){
                    if (selectionHighlighter != null){
                        world.deleteEntity(selectionHighlighter);
                        selectionHighlighter = null;
                    }
                    cubeMode = false;
                } else {

                    editModeSelectedID = i.value;
                }
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
                Gdx.app.log(TAG, "view mode "+viewMode);
            }
        });

        lookAtChanger = Subjects.get("setCameraLookAt");

        //highlightBlockSubject = Subjects.get("highlightBlock");

        final Subject freeModeSub = Subjects.get("viewModeFree");

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
                cameraPosition.rotate(tmp, drag.v2.y/2f);
                tmp.set(camera.up);
                cameraPosition.rotate(tmp, -drag.v2.x/2f);

                viewMode = VIEW_MODE_FREE;
                freeModeSub.notify(null, null, null);
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


        Observer zoomObserver = new Observer(){
            @Override
            public void onNotify(Entity e, Subject.Event event, Component c) {
                VectorInput4 vec = (VectorInput4) c;

                viewModeChanged = true;
                float d = vec.v.dst(vec.v2)/vec.v3.dst(vec.v4);
                cameraDistance *= d;
                Gdx.app.log(TAG, "scrolled"+d);
            }
        };
        Subjects.get("backgroundScrolled").add(zoomObserver);
        Subjects.get("editorPinched").add(zoomObserver);

        Subjects.get("editorClear").add(new Observer(){

            @Override
            public void onNotify(Entity e, Subject.Event event, Component c) {
                for (int x = 0; x < sizeX; x++)
                    for (int y = 0; y < sizeY; y++)
                        for (int z = 0; z < sizeZ; z++){
                            int plane = 0;
                            vw.set(x,y,z,plane, (byte) 0);
                        }
                for (int i = 0; i < 256; i++)
                    ColoredMeshBatcher.blockColors[i].set(Color.WHITE);
                for (int f = 0; f < 6; f++) {
                    textureBatch.facesPre.faces[f].clear();
                    textureBatch.faces[f].clear();
                }
            }
        });


        Subjects.get("saveBlock").add(new Observer(){

            @Override
            public void onNotify(Entity e, Subject.Event event, Component c) {
                setAllDirty();
                //saving = true;
                //mesher.preprocessor = textureBatch;
                //mesher.meshBatch = textureBatch;
                for (int x = 0; x < sizeX; x+= vw.CHUNK_SIZE_X)
                    for (int y = 0; y < sizeY; y+= vw.CHUNK_SIZE_Y)
                        for (int z = 0; z < sizeZ; z+= vw.CHUNK_SIZE_Z){
                            int plane = 0;
                            VoxelChunk chunk = vw.getChunkFromVoxel(x,y,z, plane);
                            greedy.begin(chunk, vw);
                            while (!greedy.process());
                            greedy.end();
                        }

                //TODO loop thru all chunks and process immediately
            }
        });

        highlighter = world.createEntity();
        highlighter.add(Transient.class);
        highlighter.add(Position.class);
        highlighter.add(BlockHighlight.class).dirty = true;
        highlighter.add(Face.class);
        ActionList action = highlighter.add(ActionList.class);
        action.addPre(Pools.obtain(AHighlightLerpColor.class));
        action.addPre(Pools.obtain(AHighlightLerpColorSwitch.class));
        highlighter.add(ColorTarget.class);
        world.addEntity(highlighter);

        outlineHighlighter = world.createEntity();
        outlineHighlighter.add(Transient.class);
        outlineHighlighter.add(Position.class);
        outlineHighlighter.add(BlockHighlight.class).dirty = true;
        outlineHighlighter.add(Face.class).face = BlockDefinition.ALL;

        world.addEntity(outlineHighlighter);

        selectionStartMarker = world.createEntity();
        selectionStartMarker.add(Position.class);

        world.addEntity(selectionStartMarker);

        changeViewMode();

    }

    private void setAllDirty() {
        for (int x = 0; x < sizeX/ vw.CHUNK_SIZE_X; x++)
            for (int y = 0; y < sizeY/ vw.CHUNK_SIZE_Y; y++)
                for (int z = 0; z < sizeZ/ vw.CHUNK_SIZE_Z; z++){
                    int plane = 0;
                    vw.setDirty(x,y,z,plane);
                }

    }

    private void changeViewMode() {
        viewModeChanged = true;
        //amOffset.set(sizeX*.8f, sizeY*.8f, sizeZ*.8f);
        centrePoint.pos.set(sizeX/2, sizeY/2, sizeZ/2);
        float scalar = 1.5f;
        cameraDistance = Math.max( sizeY*scalar, sizeZ*scalar);
        cameraDistance = Math.max(cameraDistance, sizeX*scalar);
        highM.get(outlineHighlighter).size.set(sizeX, sizeY, sizeZ);
        highM.get(outlineHighlighter).dirty = true;
    }

    private RayCaster ray = new RayCaster();
    private Vector3 src = new Vector3(), dst = new Vector3(), tmp = new Vector3();
    private void rayCast(float sx, float sy){
        src.set(sx,sy,0);
        dst.set(sx, sy, 1);
        camera.unproject(src);
        camera.unproject(dst);
        //Gdx.app.log(TAG, "trace " +sx+","+sy+ " from "+src+" to "+dst);
        int plane = 0;
        ray.trace(src, dst);
        while (ray.hasNext){
            ray.next();
            if (vw.get(ray.x, ray.y, ray.z, plane) != 0 || outOfBoundsForViewMode(ray)){
                tmp.set(ray.x, ray.y, ray.z);


                switch (editModeSelectedID){
                    case EDIT_MODE_ADD:
                        tmp.add(BlockDefinition.reflectedNormals[ray.face]);
                        break;
                    case EDIT_MODE_PLACE:
                        break;
                    case EDIT_MODE_REMOVE:

                        break;
                }
                if (cubeMode){
                    if (selectionHighlighter == null){
                        selectionHighlighter = world.createEntity();
                        selectionHighlighter.add(Position.class).pos.set(tmp);
                        selectionHighlighter.add(Transient.class);
                        ActionList action = selectionHighlighter.add(ActionList.class);
                        action.addPre(Pools.obtain(AHighlightLerpColor.class));
                        AHighlightLerpColorSwitch swit = Pools.obtain(AHighlightLerpColorSwitch.class);
                        action.addPre(swit);
                        selectionHighlighter.add(ColorTarget.class);
                        //selectedBlockPosition.pos.set(tmp);
                        BlockHighlight hl = selectionHighlighter.add(BlockHighlight.class);
                        hl.dirty = true;
                        hl.color.set(Color.LIGHT_GRAY);
                        selectionHighlighter.add(Face.class).face = BlockDefinition.ALL;
                        world.addEntity(selectionHighlighter);
                        posM.get(selectionStartMarker).pos.set(tmp);
                        break;
                    } else {
                        Position dst = posM.get(selectionStartMarker);
                        Gdx.app.log(TAG, "block from "+tmp+" to "+dst.pos);
                        float x0 = Math.min(dst.pos.x, tmp.x);
                        float x1 = Math.max(dst.pos.x, tmp.x)+1;
                        float y0 = Math.min(dst.pos.y, tmp.y);
                        float y1 = Math.max(dst.pos.y, tmp.y)+1;
                        float z0 = Math.min(dst.pos.z, tmp.z);
                        float z1 = Math.max(dst.pos.z, tmp.z)+1;
                        x0 = MathUtils.clamp(x0, 0, sizeX);
                        x1 = MathUtils.clamp(x1, 0, sizeX);
                        y0 = MathUtils.clamp(y0, 0, sizeY);
                        y1 = MathUtils.clamp(y1, 0, sizeY);
                        z0 = MathUtils.clamp(z0, 0, sizeZ);
                        z1 = MathUtils.clamp(z1, 0, sizeZ);


                        if (editModeSelectedID == EDIT_MODE_REMOVE){
                            for (float y = y0; y < y1; y++)
                                for (float z = z0; z < z1; z++)
                                    for (float x = x0; x < x1; x++){
                                        vw.set(x,y,z,dst.plane, (byte) 0);
                                    }
                        }else if (editModeSelectedID == EDIT_MODE_PLACE){
                            for (float y = y0; y < y1; y++)
                                for (float z = z0; z < z1; z++)
                                    for (float x = x0; x < x1; x++){
                                        if (vw.get(x,y,z,plane) != 0) vw.set(x,y,z,dst.plane, (byte) blockTypeSelectedID);
                                    }

                        } else  if (editModeSelectedID == EDIT_MODE_ADD){
                            for (float y = y0; y < y1; y++)
                                for (float z = z0; z < z1; z++)
                                    for (float x = x0; x < x1; x++){
                                        vw.set(x,y,z,dst.plane, (byte) blockTypeSelectedID);
                                    }

                        } else if (editModeSelectedID == EDIT_MODE_FACE_SET){

                        } else if (editModeSelectedID == EDIT_MODE_FACE_REMOVE){

                        }

                        world.deleteEntity(selectionHighlighter);
                        selectionHighlighter = null;
                        break;
                    }
                }
                if (editModeSelectedID == EDIT_MODE_REMOVE){
                    if (tmp.x < sizeX && tmp.y < sizeY && tmp.z < sizeZ && tmp.x >= 0 && tmp.y >= 0 && tmp.z >= 0)
                        vw.set(tmp, plane, (byte) 0);
                }else if (editModeSelectedID == EDIT_MODE_PLACE || editModeSelectedID == EDIT_MODE_ADD){
                    if (tmp.x < sizeX && tmp.y < sizeY && tmp.z < sizeZ && tmp.x >= 0 && tmp.y >= 0 && tmp.z >= 0)
                        vw.set(tmp, plane, (byte) blockTypeSelectedID);
                } else if (editModeSelectedID == EDIT_MODE_FACE_SET){
                    int face = ray.face;
                    if (tmp.x < sizeX && tmp.y < sizeY && tmp.z < sizeZ && tmp.x >= 0 && tmp.y >= 0 && tmp.z >= 0)
                        facePre.setFace(tmp.x, tmp.y, tmp.z, face, blockTypeSelectedID);
                    setAllDirty();
                } else if (editModeSelectedID == EDIT_MODE_FACE_REMOVE){
                    int face = ray.face;
                    if (tmp.x < sizeX && tmp.y < sizeY && tmp.z < sizeZ && tmp.x >= 0 && tmp.y >= 0 && tmp.z >= 0)
                        facePre.faces[face].remove(facePre.hash(tmp.x, tmp.y, tmp.z), 0);
                    setAllDirty();
                }
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

    }


    private void rayCastForHighlight(float sx, float sy){
        switch(Gdx.app.getType()) {
            case Desktop:
                break;
            default: return;

        }
        int plane = 0;
        src.set(sx,sy,0);
        dst.set(sx, sy, 1);
        camera.unproject(src);
        camera.unproject(dst);
        ray.trace(src, dst);
        while (ray.hasNext){
            ray.next();
            if (vw.get(ray.x, ray.y, ray.z, plane) != 0 || outOfBoundsForViewMode(ray)){
                tmp.set(ray.x, ray.y, ray.z);
                switch (editModeSelectedID){
                    case EDIT_MODE_ADD:
                        tmp.add(BlockDefinition.reflectedNormals[ray.face]);
                        break;
                    case EDIT_MODE_PLACE:
                        break;
                    case EDIT_MODE_REMOVE:

                        break;
                }
                tmp.x = (int)tmp.x;
                tmp.y = (int)tmp.y;
                tmp.z = (int)tmp.z;
                posM.get(highlighter).pos.set(tmp).sub(0.004f);
                if (true)//TODO
                    highM.get(highlighter).dirty = true;
                if (editModeSelectedID == EDIT_MODE_ADD) {
                    faceM.get(highlighter).face = BlockDefinition.ALL;
                }
                else {
                    faceM.get(highlighter).face = BlockDefinition.flipFace(ray.face);
                }


                if (selectionHighlighter != null){
                    BlockHighlight high = highM.get(selectionHighlighter);
                    Position hPos = posM.get(selectionHighlighter);
                    //high.size.set(tmp).sub(hPos.pos);//.add(1f,1f,1f);
                    Position selectedBlockPosition = posM.get(selectionStartMarker);
                    int srcx = (int) selectedBlockPosition.pos.x;
                    int srcy = (int) selectedBlockPosition.pos.y;
                    int srcz = (int) selectedBlockPosition.pos.z;
                    hPos.pos.set(selectedBlockPosition.pos).sub(.005f);
                    int dstx = (int) tmp.x;
                    int dsty = (int) tmp.y;
                    int dstz = (int) tmp.z;

                    high.size.set(dstx, dsty, dstz).sub(srcx, srcy, srcz);

                    if (srcx >= dstx){
                        hPos.pos.x += 1.01f;
                        high.size.x -= 1.01f;
                    } else{
                        high.size.x +=1.01f;
                    }

                    if (srcy >= dsty){
                        hPos.pos.y += 1.01f;
                        high.size.y -= 1.01f;
                       // high.color.set(Color.RED);
                    } else{
                        //high.color.set(Color.GREEN);
                        high.size.y +=1.01f;
                    }

                    if (srcz >= dstz){
                        hPos.pos.z += 1.01f;
                        high.size.z -= 1.01f;
                    } else{
                        high.size.z +=1.01f;
                    }
                    high.dirty = true;
                }
                //Gdx.app.log(TAG, "highlight"+tmp);
                return;

            }
        }
    }





}
