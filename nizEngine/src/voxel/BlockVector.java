package voxel;

import com.badlogic.gdx.utils.Pool.Poolable;

public class BlockVector implements Poolable{
	public int x,y,z;
	@Override
	public void reset() {
		// TODO Auto-generated method stub
		
	}
	public void set(int x2, int y2, int z2) {
		x = x2;y = y2;z = z2;
		
	}
	public String toString(){
		return "x"+x+" y"+y+" z"+z;
	}
}
