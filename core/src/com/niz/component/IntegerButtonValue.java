package com.niz.component;

import com.artemis.Component;
import com.badlogic.gdx.scenes.scene2d.ui.Button;

/**
 * Created by niz on 31/05/2014.
 */
public class IntegerButtonValue implements Component {
    public int value;
    public transient Button button;
    @Override
    public void reset() {

    }
}
