package com.niz.ui.elements.blockEditor;

import com.artemis.World;
import com.badlogic.gdx.scenes.scene2d.Actor;
import com.badlogic.gdx.scenes.scene2d.Touchable;
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

        ScrollPane pane = new ScrollPane(table, skin);

        Button plus = new Button(skin);
        plus.add(new Label("+", skin));
        plus.addListener(new ChangeListener() {
            @Override
            public void changed(ChangeEvent event, Actor actor) {
                //add new color Button
                final Button but = new Button(skin.get("block", Button.ButtonStyle.class));
                but.add(new Label(""+ totalButtons, skin));
                final int n = totalButtons;
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
                table.row();
                btnGr.add(but);


            }
        });
        table.add(plus);
        table.row();





        actor = pane;
    }

}
