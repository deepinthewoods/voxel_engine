package com.niz;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.InputAdapter;
import com.badlogic.gdx.scenes.scene2d.InputEvent;
import com.niz.component.VectorInput4;
import com.niz.observer.Subject;
import com.niz.observer.Subjects;

/**
 * Created by niz on 09/06/2014.
 */
public class ScrollAdapter extends InputAdapter {
   VectorInput4 vec4 = new VectorInput4();
    Subject subject = Subjects.get("backgroundScrolled");

    @Override
    public boolean scrolled(int amount) {
        float resx = Gdx.graphics.getWidth(), resy = Gdx.graphics.getHeight();
        float dx = 10f/resx, dy = 10f/resy;
        resx /= 2;
        resy /= 2;
        dx *= amount;
        dy *= amount;
        vec4.v.set(resx-dx, resy);
        vec4.v2.set(resx+dx, resy);
        vec4.v3.set(resx-dx-dx/4f*amount*-1, resy);
        vec4.v4.set(resx+dx+dx/4f*amount*-1, resy);
        //Gdx.app.log("scrolladapter", "scrolled");
        //if (subjects != null && subjects.length >2)
        subject.notify(null, null, vec4);
        return true;

    }



}
