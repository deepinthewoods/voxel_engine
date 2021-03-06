package com.niz.actions;

import com.artemis.ComponentMapper;
import com.artemis.World;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Input.Keys;
import com.niz.component.Move;
import com.niz.component.Physics;
import com.niz.component.Position;

/**
 * Created by niz on 07/06/2014.
 */
public class AStand extends Action{
    private ComponentMapper<Move> moveM;
    private ComponentMapper<Physics> physM;
    private ComponentMapper<Position> posM;

    @Override
    public void update(float dt) {
    	if (Gdx.input.isKeyJustPressed(Keys.H)){
    		posM.get(parent.e).pos.set(10, 10, 300);
    	}
        //posM.get(parent.e).pos.set(physM.get(parent.e).oldPosition);
    }

    @Override
    public void onEnd() {

    }

    @Override
    public void onStart(World world) {
        moveM = world.getMapper(Move.class);
        posM = world.getMapper(Position.class);;
        physM = world.getMapper(Physics.class);;
    }


}
