package com.niz.ui.elements.blockEditor;

import com.artemis.World;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.scenes.scene2d.Actor;
import com.badlogic.gdx.scenes.scene2d.ui.*;
import com.badlogic.gdx.scenes.scene2d.utils.ChangeListener;
import com.niz.component.ColorValue;
import com.niz.component.systems.AssetsSystem;
import com.niz.ui.ColorPicker;
import com.niz.ui.SlideColorPicker;
import com.niz.ui.elements.UIElement;

/**
 * Created by niz on 31/05/2014.
 */
public class BlockColorSelector extends UIElement {
    private ColorValue colorC = new ColorValue();

    public BlockColorSelector(){
        send = new String[]{"blockColorChanged"};
    }

    @Override
    protected void onInit(Skin skin, AssetsSystem assets, World world) {
        final SlideColorPicker pick = new SlideColorPicker( false, skin);
        final Button color1 = new Button(skin, "white-button");
        final Button color2 = new Button( skin, "white-button");
        color1.add(new Label("          \n   ", skin));
        color2.add(new Label("          \n   ", skin));

        Button ok = new Button(skin);
        ok.add(new Label("Ok", skin));
        Button cancel = new Button(skin);
        cancel.add(new Label("Cancel", skin));
        ok.addListener(new ChangeListener(){

            @Override
            public void changed(ChangeEvent event, Actor actor) {
                colorC.color.set(pick.getSelectedColor());
                subjects[0].notify(null, null, colorC);
                BlockSelector sel =
                        (BlockSelector) parent.min[0];

                parent.minimize();
            }
        });

        cancel.addListener(new ChangeListener() {

            @Override
            public void changed(ChangeEvent event, Actor actor) {
                parent.minimize();
            }
        });
        Table pickTable = new Table();

        pickTable.add(pick).minWidth(Gdx.graphics.getWidth()*.7f);;
        pickTable.row();

        final Slider sat = new Slider(0f, 1f, .001f, false, skin);
        final Slider bri = new Slider(0f, 1f, .001f, false, skin);
        sat.setValue(1f);
        bri.setValue(1f);
        sat.addListener(new ChangeListener(){

            @Override
            public void changed(ChangeEvent event, Actor actor) {
                pick.setSaturation(sat.getValue());
                color1.setColor(pick.getSelectedColor());
                color2.setColor(pick.getSelectedColor
                        ());
            }
        });

        bri.addListener(new ChangeListener(){

            @Override
            public void changed(ChangeEvent event, Actor actor) {
                pick.setBrightness(bri.getValue());
                color1.setColor(pick.getSelectedColor());
                color2.setColor(pick.getSelectedColor());
            }
        });
        pick.addListener(new ChangeListener(){

            @Override
            public void changed(ChangeEvent event, Actor actor) {
                color1.setColor(pick.getSelectedColor());
                color2.setColor(pick.getSelectedColor());

            }
        });



        Table buttonTable = new Table();
        buttonTable.add(new Label("Bri:", skin));
        buttonTable.add(bri);
        buttonTable.add(color1);
        buttonTable.row();
        buttonTable.add(new Label("Sat:", skin));
        buttonTable.add(sat);
        buttonTable.add(color2);
        buttonTable.row();

        buttonTable.add(ok);
        buttonTable.add(cancel);
        //buttonTable.row();
        pickTable.add(buttonTable);
        actor = pickTable;
    }
}
