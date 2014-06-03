package com.niz.ui.edgeUI;

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

/**
 * Created by niz on 26/05/2014.
 */
public class EditorUI extends EdgeUI {


    private static final String TAG = "editor ui";
    RayCaster ray = new RayCaster();
    Table paletteTable = new Table();
    Vector3 src = new Vector3(), dst = new Vector3();
    //Button okBtn, cancelBtn;
    //public static ColorPickerButton currentSelectedColor;
    Table selectTable = new Table();
    private Vector3 tmp = new Vector3();


    public EditorUI(){

    }

}
