package com.niz.ui.elements.blockEditor;

import com.artemis.World;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.scenes.scene2d.Actor;
import com.badlogic.gdx.scenes.scene2d.ui.Skin;
import com.badlogic.gdx.scenes.scene2d.ui.Table;
import com.badlogic.gdx.scenes.scene2d.ui.TextButton;
import com.badlogic.gdx.scenes.scene2d.utils.ChangeListener;
import com.niz.component.IntegerButtonValue;
import com.niz.component.systems.AssetsSystem;
import com.niz.component.systems.VoxelEditingSystem;
import com.niz.ui.elements.UIElement;

/**
 * Created by niz on 01/06/2014.
 */
public class EditorViewModeSelector extends UIElement {
    transient IntegerButtonValue intVal = new IntegerButtonValue();
    public EditorViewModeSelector(){
        send = new String[]{"view"};
    }

    @Override
    protected void onInit(Skin skin, AssetsSystem assets, World world) {
        Table table = new Table();
        TextButton but = new TextButton("Left", skin);
        but.addListener(new ChangeListener() {
            @Override
            public void changed(ChangeEvent event, Actor actor) {
                Gdx.app.log("j", "left");
                intVal.value = VoxelEditingSystem.VIEW_MODE_LEFT;
                subjects[0].notify(null, null, intVal);
            }
        });
        table.add(but);

        but = new TextButton("Right", skin);
        but.addListener(new ChangeListener() {
            @Override
            public void changed(ChangeEvent event, Actor actor) {
                intVal.value = VoxelEditingSystem.VIEW_MODE_RIGHT;
                subjects[0].notify(null, null, intVal);
            }
        });
        table.add(but);

        but = new TextButton("Top", skin);
        but.addListener(new ChangeListener() {
            @Override
            public void changed(ChangeEvent event, Actor actor) {
                intVal.value = VoxelEditingSystem.VIEW_MODE_TOP;
                subjects[0].notify(null, null, intVal);
            }
        });
        table.add(but);

        but = new TextButton("Bottom", skin);
        but.addListener(new ChangeListener() {
            @Override
            public void changed(ChangeEvent event, Actor actor) {
                intVal.value = VoxelEditingSystem.VIEW_MODE_BOTTOM;
                subjects[0].notify(null, null, intVal);
            }
        });
        table.add(but);

        but = new TextButton("Front", skin);
        but.addListener(new ChangeListener() {
            @Override
            public void changed(ChangeEvent event, Actor actor) {
                intVal.value = VoxelEditingSystem.VIEW_MODE_FRONT;
                subjects[0].notify(null, null, intVal);
            }
        });
        table.add(but);

        but = new TextButton("Back", skin);
        but.addListener(new ChangeListener() {
            @Override
            public void changed(ChangeEvent event, Actor actor) {
                intVal.value = VoxelEditingSystem.VIEW_MODE_BACK;
                subjects[0].notify(null, null, intVal);
            }
        });
        table.add(but);

        actor = table;
    }
}
