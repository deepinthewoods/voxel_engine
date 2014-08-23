package com.niz.component.systems;

import com.artemis.Component;
import com.badlogic.gdx.math.MathUtils;
import com.badlogic.gdx.math.Vector3;
import com.niz.Vec3i;

/**
 * Created by niz on 17/08/2014.
 */
public class HeightMap implements Component {
    public int size = 1;
    private Vec3i centre = new Vec3i();
    public float[] sides = new float[9];


    public void setCentre(float x, float y, float z){
        int ix = MathUtils.floor(x)/size, iy = MathUtils.floor(y)/size, iz = MathUtils.floor(z)/size;
        if (ix != centre.x / size || iy != centre.y / size  || iz != centre.z / size );

    }

    @Override
    public void reset() {

    }
}
