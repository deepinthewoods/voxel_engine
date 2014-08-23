package com.niz;

import com.badlogic.gdx.math.MathUtils;
import com.badlogic.gdx.math.Vector3;

public class Vec3i {

	public int x, y, z;
	public String toString(){
		return "["+x+"  ,  "+y+"  ,  "+z+"]";
	}
	public void set(int i, int j, int k) {
		x = i;
		y = j;
		z = k;
		
	}
	public void set(Vector3 p) {
		x = MathUtils.floor(p.x);
		y = MathUtils.floor(p.y);
		z = MathUtils.floor(p.z);
		
	}
	public void set(float x2, float y2, float z2) {
		set((int)x2,(int)y2,(int)z2);
		
	}

    public int manhattanDist(int ix, int iy, int iz) {
        int d = 0;
        d += Math.abs(ix-x);
        d += Math.abs(iy-y);
        d += Math.abs(iz-z);


        return d;
    }
}
