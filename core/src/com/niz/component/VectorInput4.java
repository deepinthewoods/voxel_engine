package com.niz.component;

import com.artemis.Component;
import com.badlogic.gdx.math.Vector2;
import com.niz.Input;

/**
 * Created by niz on 29/05/2014.
 */
public class VectorInput4 implements Component{
    public Vector2 v = new Vector2(), v2 = new Vector2(), v3 = new Vector2(), v4 = new Vector2();
    public Input.Code code;

    @Override
    public void reset() {

    }
}
