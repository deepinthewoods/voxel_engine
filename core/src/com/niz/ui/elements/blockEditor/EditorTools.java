package com.niz.ui.elements.blockEditor;

import com.artemis.World;
import com.badlogic.gdx.scenes.scene2d.Actor;
import com.badlogic.gdx.scenes.scene2d.ui.*;
import com.badlogic.gdx.scenes.scene2d.utils.ActorGestureListener;
import com.badlogic.gdx.scenes.scene2d.utils.ChangeListener;
import com.niz.component.IntegerButtonValue;
import com.niz.component.systems.AssetsSystem;
import com.niz.component.systems.VoxelEditingSystem;
import com.niz.ui.elements.UIElement;

/**
 * Created by niz on 01/06/2014.
 */
public class EditorTools extends UIElement {
    transient Table tab = new Table();
    transient ButtonGroup btnGr = new ButtonGroup();
    transient IntegerButtonValue intVal = new IntegerButtonValue();

    public EditorTools(){
        send = new String[]{"editorPlaceMode", "editorClear", "saveBlock"};
    }

    @Override
    protected void onInit(Skin skin, AssetsSystem assets, World world) {
        TextButton add = new TextButton("Add", skin.get("toggle", TextButton.TextButtonStyle.class));
        btnGr.add(add);
        TextButton remove = new TextButton("Rem", skin.get("toggle", TextButton.TextButtonStyle.class));
        btnGr.add(remove);
        TextButton set = new TextButton("Set", skin.get("toggle", TextButton.TextButtonStyle.class));
        btnGr.add(set);
        TextButton faceSet = new TextButton("Face+", skin.get("toggle", TextButton.TextButtonStyle.class));
        TextButton faceRem = new TextButton("Face-", skin.get("toggle", TextButton.TextButtonStyle.class));
        btnGr.add(faceSet);
        btnGr.add(faceRem);

        faceSet.addListener(new ChangeListener() {
            @Override
            public void changed(ChangeEvent event, Actor actor) {
                intVal.value = VoxelEditingSystem.EDIT_MODE_FACE_SET;
                subjects[0].notify(null, null, intVal);
            }
        });

        faceRem.addListener(new ChangeListener() {
            @Override
            public void changed(ChangeEvent event, Actor actor) {
                intVal.value = VoxelEditingSystem.EDIT_MODE_FACE_REMOVE;
                subjects[0].notify(null, null, intVal);
            }
        });

        TextButton cube = new TextButton("Cube", skin.get("toggle", TextButton.TextButtonStyle.class));

        add.addListener(new ChangeListener() {
            @Override
            public void changed(ChangeEvent event, Actor actor) {

                intVal.value = 0;
                subjects[0].notify(null, null, intVal);
            }
        });

        remove.addListener(new ChangeListener() {
            @Override
            public void changed(ChangeEvent event, Actor actor) {
                intVal.value = 1;
                subjects[0].notify(null, null, intVal);
            }
        });

        set.addListener(new ChangeListener() {
            @Override
            public void changed(ChangeEvent event, Actor actor) {
                intVal.value = 2;
                subjects[0].notify(null, null, intVal);
            }
        });

        cube.addListener(new ChangeListener() {
            @Override
            public void changed(ChangeEvent event, Actor actor) {
                TextButton but = (TextButton) actor;
                if (but.isChecked()){
                    intVal.value = VoxelEditingSystem.EDIT_MODE_CUBE_ON;
                } else {
                    intVal.value = VoxelEditingSystem.EDIT_MODE_CUBE_OFF;

                }
                subjects[0].notify(null, null, intVal);
            }
        });

        TextButton newBtn = new TextButton("New", skin);

        newBtn.addListener(new ChangeListener() {

            @Override
            public void changed(ChangeEvent event, Actor actor) {
                parent.maximize();
            }
        });

        TextButton clearBtn = new TextButton("Clr", skin);

        clearBtn.addListener(new ActorGestureListener(){
            @Override
            public boolean longPress(Actor actor, float x, float y) {
                subjects[1].notify(null, null, null);

                return true;
            }
        });

        TextButton saveBtn = new TextButton("Save", skin);

        saveBtn.addListener(new ChangeListener() {

            @Override
            public void changed(ChangeEvent event, Actor actor) {
                subjects[2].notify(null, null, null);
                //parent.parent.disableTouches();
            }
        });

        tab.add(cube);
        tab.add(new Label("   ", skin));
        tab.add(faceSet);
        tab.add(faceRem);

        tab.add(add);
        //tab.row();
        tab.add(remove);
       //tab.row();
        tab.add(set);
        //tab.row();
        //tab.top();
        tab.add(new Label("   ", skin));
        tab.add(newBtn);

        tab.add(new Label("   ", skin));
        tab.add(clearBtn);

        tab.add(new Label("   ", skin));
        tab.add(saveBtn);

        actor = tab;
    }
}
