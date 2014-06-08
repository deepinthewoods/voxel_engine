package com.niz.ui.elements.blockEditor;

import com.artemis.World;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.scenes.scene2d.Actor;
import com.badlogic.gdx.scenes.scene2d.ui.*;
import com.badlogic.gdx.scenes.scene2d.utils.ActorGestureListener;
import com.badlogic.gdx.scenes.scene2d.utils.ChangeListener;
import com.niz.component.IntegerButtonValue;
import com.niz.component.systems.AssetsSystem;
import com.niz.ui.elements.UIElement;

/**
 * Created by niz on 30/05/2014.
 *
 */
public class BlockSelector extends UIElement {
    private static final String TAG = "block selector ui element";
    transient final ButtonGroup btnGr = new ButtonGroup();
    //transient final WidgetGroup widget = new WidgetGroup();
    transient final Table table = new Table();
    int totalButtons = 0;
    transient private IntegerButtonValue intVal = new IntegerButtonValue();
    //public transient Button[] buttons = new Button[256];

    public BlockSelector(){
        send = new String[]{"blockTypeSelected"};
    }


    @Override
    protected void onInit(final Skin skin, AssetsSystem assets, World world) {
        Table scrollTable = new Table();
        final ScrollPane pane = new ScrollPane(table, skin);

        Button plus = new Button(skin);
        plus.add(new Label(" + ", skin));
        plus.addListener(new ChangeListener() {
            @Override
            public void changed(ChangeEvent event, Actor actor) {
                //add new color Button
                makeColorButton(skin);
                makeColorButton(skin);

            }
        });

        for (int i = 0; i < 16; i++){
           makeColorButton(skin);
        }



        scrollTable.add(plus);
        scrollTable.row();


        pane.setScrollbarsOnTop(false);
        pane.setScrollBarPositions(true, false);
        pane.setScrollingDisabled(true, false);
        pane.setupFadeScrollBars(.1f, .1f);
        pane.setOverscroll(false, false);


        scrollTable.add(pane);//.height(Gdx.graphics.getHeight()/2);

        actor = scrollTable;
    }

    private void makeColorButton(Skin skin) {
        final Button but = new Button(skin.get("block", Button.ButtonStyle.class));
        final int n = totalButtons+1;
        but.add(new Label(""+ n, skin));
        but.addListener(new ChangeListener() {
            @Override
            public void changed(ChangeEvent event, Actor actor) {
                intVal.value = n;
                intVal.button = but;
                subjects[0].notify(null, null, intVal);
                // Gdx.app.log(TAG, "notify button clicked");
            }
        });
        but.addListener(new ActorGestureListener(){
            public boolean longPress(Actor actor, float x, float y) {
                parent.maximize();
                return true;
            }

        });//*/
        //buttons[totalButtons] = but;
        totalButtons++;
        table.add(but);
        if (totalButtons % 2 == 0 ) table.row();
        btnGr.add(but);
    }

}
