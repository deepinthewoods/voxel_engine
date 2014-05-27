package com.niz.ui.EdgeUIs;

import com.artemis.World;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.Camera;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.g2d.Sprite;
import com.badlogic.gdx.graphics.g2d.TextureAtlas;
import com.badlogic.gdx.math.Vector3;
import com.badlogic.gdx.scenes.scene2d.Actor;
import com.badlogic.gdx.scenes.scene2d.InputEvent;
import com.badlogic.gdx.scenes.scene2d.Stage;
import com.badlogic.gdx.scenes.scene2d.ui.*;
import com.badlogic.gdx.scenes.scene2d.utils.ActorGestureListener;
import com.badlogic.gdx.scenes.scene2d.utils.ClickListener;
import com.badlogic.gdx.tests.g3d.voxel.BlockDefinition;
import com.badlogic.gdx.tests.g3d.voxel.VoxelWorld;
import com.niz.RayCaster;
import com.niz.blocks.EmptyBlockDefinition;
import com.niz.component.systems.CameraSystem;
import com.niz.component.systems.EditVoxelSystem;
import com.niz.component.systems.VoxelSystem;
import com.niz.ui.ColorPicker;
import com.niz.ui.ColorPickerButton;

/**
 * Created by niz on 26/05/2014.
 */
public class EditorUI extends EdgeUI {


    private static final String TAG = "editor ui";
    RayCaster ray = new RayCaster();
    Table paletteTable = new Table();
    Vector3 src = new Vector3(), dst = new Vector3();
    public static ColorPicker colorPicker;
    Button okBtn, cancelBtn;
    public static ColorPickerButton currentSelectedColor;
    Table selectTable = new Table();
    private Vector3 tmp = new Vector3();


    protected void editor(final World world, final Stage stage, final Skin skin, Sprite sprite, Sprite spritesel) {
        //color picker
        //VoxelChunk.defs = GeneralFactory.getBlockDefs(world);
        //VoxelChunk.defs = defs(tiles);
        final Color[] blockColors = world.getSystem(EditVoxelSystem.class).BLOCK_COLORS;
        final ButtonGroup btnGr = new ButtonGroup();
        //Gdx.app.log(TAG, "editor"+(sprite == null));
        for (int i = 0; i < 8; i++)
        {
            final ColorPickerButton colorA = new ColorPickerButton(skin, sprite, spritesel, i*2);
            final ColorPickerButton colorB = new ColorPickerButton(skin, sprite, spritesel, i*2+1);
            btnGr.add(colorA);
            btnGr.add(colorB);
            colorA.addListener(new ActorGestureListener(){
                public boolean longPress(Actor actor,
                                         float x,
                                         float y){
                    openColorSelectionScreen(colorA, stage, skin, blockColors);
                    return true;
                }
            });
            colorB.addListener(new ActorGestureListener(){
                public boolean longPress(Actor actor,
                                         float x,
                                         float y){
                    openColorSelectionScreen(colorB, stage, skin, blockColors);
                    return true;
                }
            });
            paletteTable.add(colorA).left();
            paletteTable.add(colorB).left();
            paletteTable.row();
        }

        //paletteTable.top();

        Actor touchActor = new Actor();
        touchActor.setSize(Gdx.graphics.getWidth(), Gdx.graphics.getHeight());

        touchActor.addListener(new ClickListener(){
            @Override
            public void clicked(InputEvent event,
                                float x,
                                float y){
                //raycast
                Camera camera = world.getSystem(CameraSystem.class).camera;
                VoxelWorld vw = world.getSystemOrSuperClass(VoxelSystem.class).voxelWorld;
                float sx = event.getStageX(), sy = Gdx.graphics.getHeight()-event.getStageY();
                src.set(sx,sy,0);
                dst.set(sx,sy,1);
                camera.unproject(src);
                camera.unproject(dst);
                Gdx.app.log(TAG, "trace " +sx+","+sy+ " from "+src+" to "+dst);

                ray.trace(src, dst);
                while (ray.hasNext){
                    ray.next();

                    if (vw.get(ray.x, ray.y, ray.z) != 0 )
                    {
                        tmp.set(ray.x, ray.y, ray.z);
                        tmp.add(BlockDefinition.reflectedNormals[ray.face]);
                        vw.set(tmp, (byte)((ColorPickerButton)(btnGr.getChecked())).colorIndex);
                        Gdx.app.log(TAG, "set"+tmp);

                        break;
                    }
                    // Gdx.app.log(TAG, "trace "+ray.x+","+ray.y+","+ray.z);
                }
                //event.stop();
                //event.handle();
            }

        });

        stage.addActor(touchActor);
        paletteTable.setFillParent(true);
        paletteTable.left();
        stage.addActor(paletteTable);
        VoxelWorld vw = world.getSystemOrSuperClass(VoxelSystem.class).voxelWorld;

        for (int x = 0; x < 15; x++)
            for(int y = 0; y < 2; y++)
                for (int z = 0; z < 15; z++)
                    ;// vw.set(x,y,z,(byte)1);

        //save/load btns
        //default chunk
        //new btn(selectable size)



    }

    private BlockDefinition[] defs(TextureAtlas tiles) {
        BlockDefinition[] defs = new BlockDefinition[256];

        //defs[10] = new TopBottomBlock(tiles, 8, 1, 10);


        for (int i = 0; i < 256; i++){
            if (defs[i] == null){
                defs[i] = new EmptyBlockDefinition(tiles.findRegion("empty"), i);
            }
        }

        return defs;
    }

    private void openColorSelectionScreen(ColorPickerButton actor, final Stage stage, Skin skin, final Color[] blockColors) {
        Gdx.app.log(TAG, "sdfjksdfjksdfjk");
        if (colorPicker == null){
            colorPicker = new ColorPicker(skin);
            okBtn = new Button(skin);
            okBtn.add(new Label("Ok", skin));
            cancelBtn = new Button(skin);
            cancelBtn.add(new Label("Cancel", skin));
            okBtn.addListener(new ClickListener(){
                public void clicked(InputEvent event,
                                    float x,
                                    float y){

                    currentSelectedColor.setColor(colorPicker.getSelectedColor(), blockColors);
                    stage.getActors().removeValue(selectTable, true);


                    stage.addActor(paletteTable);
                }
            });
            cancelBtn.addListener(new ClickListener() {
                public void clicked(InputEvent event,
                                    float x,
                                    float y) {

                    stage.getActors().removeValue(selectTable, true);


                    stage.addActor(paletteTable);

                }
            });

            selectTable.setFillParent(true);
            selectTable.add(colorPicker);
            selectTable.row();
            selectTable.add(okBtn);
            selectTable.add(cancelBtn);


        }

        currentSelectedColor = actor;




        stage.addActor(selectTable);
    }



}
