package com.niz;

import com.badlogic.gdx.graphics.Camera;
import com.badlogic.gdx.graphics.g2d.Batch;
import com.badlogic.gdx.math.Vector2;

/**
 * Created by niz on 18/06/2014.
 */
public class LinesBatch{

    //a series of heights.

    //updated sporadically
    Vector2 tmp = new Vector2();
    private float start;
    private float end;
    private boolean flipped;

    void begin(Camera camera){
        tmp.set(camera.direction.x, camera.direction.y);
        float rot = tmp.angle();
        start = rot - 45;
        if (start < 0) start += 360;
        end = rot + 45;
        if (end > 360) end %= 360;


        if (start > end) flipped = true;
        else flipped = false;
    }

    void update(){

    }

    void end(){
        for (int i = (int) start; i < end; i++){

        }
    }

}
