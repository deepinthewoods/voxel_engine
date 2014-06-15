package com.niz.actions;

import com.artemis.ComponentMapper;
import com.artemis.World;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.Color;
import com.niz.component.BlockHighlight;
import com.niz.component.ColorTarget;

/**
 * Created by niz on 14/06/2014.
 */
public class AHighlightLerpColor extends Action {

    private static final String TAG = "h lerp col";
    transient private ComponentMapper<BlockHighlight> highM;
    transient private ComponentMapper<ColorTarget> colorM;

    @Override
    public void update(float dt) {
        Color lerpColor = colorM.get(parent.e).color;
        BlockHighlight high = highM.get(parent.e);
        high.color.lerp(lerpColor, .025f);
       // Gdx.app.log(TAG, "switch");

        high.dirty = true;
    }

    @Override
    public void onEnd() {

    }

    @Override
    public void onStart(World world) {
        highM = world.getMapper(BlockHighlight.class);
        colorM = world.getMapper(ColorTarget.class);
    }
}
