package com.niz.ui.edgeUI;

import com.badlogic.gdx.math.Vector3;
import com.badlogic.gdx.scenes.scene2d.ui.Table;
import com.niz.RayCaster;

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
