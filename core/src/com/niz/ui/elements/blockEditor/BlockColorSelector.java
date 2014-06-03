package com.niz.ui.elements.blockEditor;

import com.artemis.Component;
import com.artemis.World;
import com.badlogic.gdx.scenes.scene2d.Actor;
import com.badlogic.gdx.scenes.scene2d.ui.Button;
import com.badlogic.gdx.scenes.scene2d.ui.Label;
import com.badlogic.gdx.scenes.scene2d.ui.Skin;
import com.badlogic.gdx.scenes.scene2d.utils.ChangeListener;
import com.niz.component.ColorValue;
import com.niz.component.systems.AssetsSystem;
import com.niz.ui.ColorPicker;
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
        final ColorPicker pick = new ColorPicker(skin);

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

        pick.add(ok);
        pick.add(cancel);
        actor = pick;
    }
}
