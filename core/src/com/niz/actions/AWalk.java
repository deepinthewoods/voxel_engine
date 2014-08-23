package com.niz.actions;

import com.artemis.ComponentMapper;
import com.artemis.World;
import com.badlogic.gdx.Gdx;
import com.niz.component.Move;

/**
 * Created by niz on 29/06/2014.
 */
public class AWalk extends Action {
    private static final String TAG = "walk action";
    private ComponentMapper<Move> moveM;

    @Override
    public void update(float dt) {
        //if (!isBlocking) return;
        //Gdx.app.log(TAG, "walk");
        Move move = moveM.get(parent.e);
        move.moving = true;
    }

    @Override
    public void onEnd() {
        //Gdx.app.log(TAG, "walk end");

        Move move = moveM.get(parent.e);
        move.moving = false;
    }

    @Override
    public void onStart(World world) {
        moveM = world.getMapper(Move.class);
    }
}
