package com.niz.ui.elements;

import com.badlogic.gdx.scenes.scene2d.ui.Label;
import com.badlogic.gdx.scenes.scene2d.ui.Skin;
import com.niz.ui.UIElement;

/**
 * Created by niz on 27/05/2014.
 */
public class StatDisplayElement extends UIElement {

    @Override
    protected void onInit(Skin skin) {
        actor = new Label("test", skin);
    }
}
