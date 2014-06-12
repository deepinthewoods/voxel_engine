package com.niz.ui.elements.blockEditor;

import com.artemis.Component;
import com.artemis.Entity;
import com.artemis.World;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.scenes.scene2d.Actor;
import com.badlogic.gdx.scenes.scene2d.Touchable;
import com.badlogic.gdx.scenes.scene2d.ui.ButtonGroup;
import com.badlogic.gdx.scenes.scene2d.ui.Skin;
import com.badlogic.gdx.scenes.scene2d.ui.Table;
import com.badlogic.gdx.scenes.scene2d.ui.TextButton;
import com.badlogic.gdx.scenes.scene2d.utils.ChangeListener;
import com.niz.component.IntegerButtonValue;
import com.niz.component.systems.AssetsSystem;
import com.niz.component.systems.VoxelEditingSystem;
import com.niz.observer.AutoObserver;
import com.niz.observer.Subject;
import com.niz.ui.elements.UIElement;

/**
 * Created by niz on 01/06/2014.
 */
public class EditorViewModeSelector extends UIElement {
    transient IntegerButtonValue intVal = new IntegerButtonValue();
    transient private TextButton freeButton;
    transient ButtonGroup group = new ButtonGroup();

    public EditorViewModeSelector(){

        send = new String[]{"view"};
        observers = new AutoObserver[]{new AutoObserver(){

            @Override
            public void onNotify(Entity e, Subject.Event event, Component c) {
                freeButton.setChecked(true);
            }
        }};
        observers[0].from = "viewModeFree";
    }

    @Override
    protected void onInit(Skin skin, AssetsSystem assets, World world) {
        Table table = new Table();
        TextButton but;

        but = new TextButton("F", skin, "toggle");
        but.addListener(new ChangeListener() {
            @Override
            public void changed(ChangeEvent event, Actor actor) {
                intVal.value = VoxelEditingSystem.VIEW_MODE_FRONT;
                subjects[0].notify(null, null, intVal);
            }
        });
        table.add(but);
        group.add(but);

        but = new TextButton("Ba", skin, "toggle");
        but.addListener(new ChangeListener() {
            @Override
            public void changed(ChangeEvent event, Actor actor) {
                intVal.value = VoxelEditingSystem.VIEW_MODE_BACK;
                subjects[0].notify(null, null, intVal);
            }
        });
        table.add(but);
        group.add(but);

        but = new TextButton("L", skin, "toggle");
        but.addListener(new ChangeListener() {
            @Override
            public void changed(ChangeEvent event, Actor actor) {
                Gdx.app.log("j", "left");
                intVal.value = VoxelEditingSystem.VIEW_MODE_LEFT;
                subjects[0].notify(null, null, intVal);
            }
        });
        table.add(but);
        group.add(but);

        but = new TextButton("R", skin, "toggle");
        but.addListener(new ChangeListener() {
            @Override
            public void changed(ChangeEvent event, Actor actor) {
                intVal.value = VoxelEditingSystem.VIEW_MODE_RIGHT;
                subjects[0].notify(null, null, intVal);
            }
        });
        table.add(but);
        group.add(but);

        but = new TextButton("T", skin, "toggle");
        but.addListener(new ChangeListener() {
            @Override
            public void changed(ChangeEvent event, Actor actor) {
                intVal.value = VoxelEditingSystem.VIEW_MODE_TOP;
                subjects[0].notify(null, null, intVal);
            }
        });
        table.add(but);
        group.add(but);

        but = new TextButton("Bo", skin, "toggle");
        but.addListener(new ChangeListener() {
            @Override
            public void changed(ChangeEvent event, Actor actor) {
                intVal.value = VoxelEditingSystem.VIEW_MODE_BOTTOM;
                subjects[0].notify(null, null, intVal);
            }
        });
        table.add(but);
        group.add(but);


        freeButton = new TextButton("Free", skin, "toggle");
        freeButton.addListener(new ChangeListener() {
            @Override
            public void changed(ChangeEvent event, Actor actor) {
                //intVal.value = VoxelEditingSystem.VIEW_MODE_FREE;
                //subjects[0].notify(null, null, intVal);
            }
        });
        freeButton.setTouchable(Touchable.disabled);
        table.add(freeButton);
        group.add(freeButton);



        actor = table;
    }
}
