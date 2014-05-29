package com.niz.component;

import com.artemis.Component;
import com.badlogic.gdx.math.Vector2;

/**
 * Created by niz on 29/05/2014.
 */
public class VectorInput implements Component{
    public Vector2 v = new Vector2();
    public ButtonInput.InputCode code;

    @Override
    public void reset() {

    }
}
