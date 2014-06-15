package com.niz.actions;

import com.artemis.ComponentMapper;
import com.artemis.World;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.Color;
import com.niz.component.ColorTarget;

/**
 * Created by niz on 14/06/2014.
 */
public class AHighlightLerpColorSwitch extends Action {
    private static final String TAG = "highlight switch color action";
    boolean black;
    transient private ComponentMapper<ColorTarget> colorM;

    @Override
    public void update(float dt) {
        black = !black;
        if (black) colorM.get(parent.e).color.set(Color.BLACK);
        else colorM.get(parent.e).color.set(Color.WHITE);
        //Gdx.app.log(TAG, "switch");
        delay(1f);
    }

    @Override
    public void onEnd() {

    }

    @Override
    public void onStart(World world) {
        colorM = world.getMapper(ColorTarget.class);
    }
}
