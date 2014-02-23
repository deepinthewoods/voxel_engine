package com.niz.component.systems;

import com.artemis.Aspect;
import com.artemis.ComponentMapper;
import com.artemis.Entity;
import com.artemis.systems.EntityProcessingSystem;
import com.badlogic.gdx.math.Vector3;
import com.niz.buckets.Buckets;
import com.niz.component.Bucketed;
import com.niz.component.Position;


public class BucketedSystem extends EntityProcessingSystem {
	ComponentMapper<Bucketed> bm;
	ComponentMapper<Position> pm;
	public static Buckets buckets;
	public BucketedSystem() {
		super(Aspect.getAspectForAll(Position.class, Bucketed.class));
		buckets = new Buckets();
	}

	 @Override
	 public void initialize() {
	     bm = world.getMapper(Bucketed.class);	  
	     pm = world.getMapper(Position.class);

	 }
	//IntMap<Entity> entities;
	private static final int bits = 1, largeBits = 4;
	
	
	
		//entities = engine.getEntitiesFor(Family.getFamilyFor(Position.class, Bucketed.class));

	

	

	
	


		@Override
		protected void process(Entity e) {
			Vector3 pos = pm.get(e).pos;
			
			Bucketed bucket = bm.get(e);
			
			int x = (int)pos.x<<bits;
			int z = (int)pos.z<<bits;
			if (x != bucket.x || z != bucket.z){
				buckets.move(bucket.x, bucket.z, x, z, e);
				bucket.x = x;
				bucket.z = z;
				x <<= largeBits;
				z <<= largeBits;
				if (x != bucket.largeX || z != bucket.largeZ){
					buckets.moveLargeBuckets(bucket.largeX, bucket.largeZ, x, z, e);
					bucket.largeX = x;
					bucket.largeZ = z;
				}
			}
			
		}

}
