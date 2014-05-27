package com.niz.ui.elements;

import com.badlogic.gdx.scenes.scene2d.ui.Button;
import com.badlogic.gdx.scenes.scene2d.ui.Label;
import com.badlogic.gdx.scenes.scene2d.ui.Skin;

/**
 * Created by niz on 27/05/2014.
 */
public class StatDisplayElement extends UIElement {

    @Override
    protected void onInit(Skin skin) {


        Button b = new Button(skin);
        actor = b;
        b.add(new Label("test", skin));
    }
}
