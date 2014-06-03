package com.niz.ui.elements.blockEditor;

import com.artemis.World;
import com.badlogic.gdx.scenes.scene2d.Actor;
import com.badlogic.gdx.scenes.scene2d.ui.ButtonGroup;
import com.badlogic.gdx.scenes.scene2d.ui.Skin;
import com.badlogic.gdx.scenes.scene2d.ui.Table;
import com.badlogic.gdx.scenes.scene2d.ui.TextButton;
import com.badlogic.gdx.scenes.scene2d.utils.ChangeListener;
import com.niz.component.IntegerButtonValue;
import com.niz.component.systems.AssetsSystem;
import com.niz.ui.elements.UIElement;

/**
 * Created by niz on 01/06/2014.
 */
public class EditorTools extends UIElement {
    transient Table tab = new Table();
    transient ButtonGroup btnGr = new ButtonGroup();
    transient IntegerButtonValue intVal = new IntegerButtonValue();

    public EditorTools(){
        send = new String[]{"editorPlaceMode"};
    }

    @Override
    protected void onInit(Skin skin, AssetsSystem assets, World world) {
        TextButton add = new TextButton("Add", skin.get("toggle", TextButton.TextButtonStyle.class));
        btnGr.add(add);
        TextButton remove = new TextButton("Remove", skin.get("toggle", TextButton.TextButtonStyle.class));
        btnGr.add(remove);
        TextButton set = new TextButton("Set", skin.get("toggle", TextButton.TextButtonStyle.class));
        btnGr.add(set);


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


        tab.add(add);
        tab.add(remove);
        tab.add(set);

        actor = tab;
    }
}
