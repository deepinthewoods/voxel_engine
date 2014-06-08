package com.niz.component;

import com.artemis.Component;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.math.Vector3;

/**
 * Created by niz on 29/05/2014.
 */
public class Vector3Input implements Component{
    public Vector3 v = new Vector3();
    public ButtonInput.InputCode code;

    @Override
    public void reset() {

    }
}
