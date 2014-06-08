package com.niz.actions;

import com.artemis.Component;
import com.artemis.ComponentMapper;
import com.artemis.Entity;
import com.artemis.World;
import com.badlogic.gdx.Gdx;
import com.niz.component.BlockHighlight;
import com.niz.component.FacePosition;
import com.niz.component.Position;
import com.niz.observer.Observer;
import com.niz.observer.Subject;
import com.niz.observer.Subjects;

/**
 * Created by niz on 08/06/2014.
 */
public class AHiglightBlock extends Action implements Observer{

    private static final String TAG = "highlight block action";
    private transient ComponentMapper<Position> posM;
    private transient ComponentMapper<BlockHighlight> highM;

    @Override
    public void update(float dt) {

    }

    @Override
    public void onEnd() {
        Subjects.get(subHash).remove(this);
    }

    static transient int subHash = Subjects.hash("highlightBlock");
    @Override
    public void onStart(World world) {
        Subjects.get(subHash).add(this);
        posM = world.getMapper(Position.class);

        highM = world.getMapper(BlockHighlight.class);
        //throw new GdxRuntimeException("jksdjk");

    }

    @Override
    public void onNotify(Entity e, Subject.Event event, Component c) {
        FacePosition newPos = (FacePosition) c;
        //if (parent == null) throw new GdxRuntimeException("null pernt");
        //if (parent.e == null) throw new GdxRuntimeException("null e");
        //Gdx.app.log(TAG, "pos"+newPos.pos);
        Position pos = posM.get(parent.e);
        BlockHighlight high = highM.get(parent.e);

        if (pos.pos.dst2(newPos.pos) > .1f || high.face != newPos.face){
            high.dirty = true;
            high.face = newPos.face;
            pos.pos.set(newPos.pos);
            ///Gdx.app.log(TAG, "pos"+newPos.pos);

        }
    }
}
