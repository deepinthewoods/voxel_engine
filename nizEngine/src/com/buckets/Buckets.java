package com.buckets;

import com.artemis.Entity;
import com.badlogic.gdx.math.MathUtils;
import com.badlogic.gdx.utils.GdxRuntimeException;
import com.badlogic.gdx.utils.IntMap;
import com.badlogic.gdx.utils.Pool;
import com.badlogic.gdx.utils.Pools;


public class Buckets extends IntMap<BucketEntityArray>{
	private static final String TAG = "Buckets";

	//public int bucketBits = 2;
	
	Pool<BucketEntityArray> arrayPool;// = Pools.get(BucketEntityArray.class);
	
	IntMap<BucketEntityArray> largeBuckets = new IntMap<BucketEntityArray>();
	public Buckets(){
		arrayPool = Pools.get(BucketEntityArray.class);
	}
	private static int hash(int x, int y){
		//return x + (y*100);
		return (x * 73856093) ^ (y * 19349663);
	}
	public void move(int oldx, int oldz, int x, int z, Entity e) {
		if (e == null) throw new GdxRuntimeException("nuilll entity");
		int oldHash = hash(oldx, oldz);
		BucketEntityArray array = this.get(oldHash);
		int index = e.id;
		if (array != null){
			array.remove(index);
			if (array.size == 0){
				arrayPool.free(this.remove(oldHash));
			}
			
		}
		
		
		int newHash = hash(x,z);
		if (this.containsKey(newHash))
			get(newHash).put(e.id, e);
		else {
			BucketEntityArray newArray = arrayPool.obtain();
			newArray.put(e.id, e);
			this.put(newHash, newArray);
		}
		//Gdx.app.log(TAG, "bucket move "+get(newHash).size);
	}
	
	public void moveLargeBuckets(int oldx, int oldz, int x, int z, Entity e) {
		int oldHash = hash(oldx, oldz);
		BucketEntityArray array = largeBuckets.get(oldHash);
		int index = e.id;
		if (array != null){
			array.remove(index);
			if (array.size == 0){
				arrayPool.free(largeBuckets.remove(oldHash));
			}
		}
		
		int newHash = hash(x,z);
		if (largeBuckets.containsKey(newHash))
			largeBuckets.get(newHash).put(e.id, e);
		else {
			BucketEntityArray newArray = arrayPool.obtain();
			newArray.put(e.id, e);
			largeBuckets.put(newHash, newArray);
		}
	}
	
	public void queryXZ1(float x, float z, BucketEntityArray[] array){
		int x2 = MathUtils.round(x);
		int z2 = MathUtils.round(z);
		int tot = 0;
		for (int i = -1; i < 1; i++)
			for (int j = -1; j < 1; j++){
				array[tot++] = this.get(hash(x2+i, z2+j));
			}
	}
	
	public void queryXZ2(float x, float z, BucketEntityArray[] array){
		int x2 = MathUtils.round(x);
		int z2 = MathUtils.round(z);
		int tot = 0;
		for (int i = -2; i < 3; i++)
			for (int j = -2; j < 3; j++){
				array[tot++] = this.get(hash(x2+i, z2+j));
			}
	}
	public void queryLarge(int x, int z, BucketEntityArray[] array) {
		int x2 = MathUtils.round(x);
		int z2 = MathUtils.round(z);
		int tot = 0;
		for (int i = -2; i < 3; i++)
			for (int j = -2; j < 3; j++){
				array[tot++] = this.get(hash(x2+i, z2+j));
			}
		
	}

}
