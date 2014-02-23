package com.niz.component.systems;

import voxel.BlockDefinition;
import voxel.VoxelChunk;
import voxel.VoxelWorld;

import com.artemis.Aspect;
import com.artemis.ComponentMapper;
import com.artemis.Entity;
import com.artemis.systems.EntityProcessingSystem;
import com.badlogic.gdx.math.MathUtils;
import com.badlogic.gdx.math.Vector3;
import com.niz.component.AABBBody;
import com.niz.component.Body;
import com.niz.component.Physics;
import com.niz.component.Position;

public class AABBBodySystem extends EntityProcessingSystem {
	
	private static final String TAG = "aabb body";
	
	private static Vector3 tmp = new Vector3(), tmpV = new Vector3();;	
	//private VoxelWorld voxelWorld;

	int normalCount;
	
	static Vector3[] returnVectors = {
		new Vector3()
		, new Vector3()
		, new Vector3()
		, new Vector3()
		, new Vector3()
		, new Vector3()
	};
	int[] sides = new int[3];
	Vector3 dir = new Vector3();
	Vector3 start = new Vector3(), end = new Vector3(), tStart = new Vector3(), tEnd = new Vector3();

	private ComponentMapper<Position> posMap;
	private ComponentMapper<Physics> physMap;
	private ComponentMapper<AABBBody> bodyMap;
	

	
	
	public AABBBodySystem(){
		super(Aspect.getAspectForAll(Position.class, Physics.class, AABBBody.class));
		
		
	}
	@Override
	public void initialize(){
		posMap = world.getMapper(Position.class);
		physMap = world.getMapper(Physics.class);
		bodyMap = world.getMapper(AABBBody.class);
	}
	
	public boolean onTick(Vector3 position, Vector3 oldPosition, Body c, VoxelWorld voxelWorld) {
		//if (oldPosition.dst2(position) > .25f)
		//	Gdx.app.log(TAG, "TUNNELLL");
		if (!VoxelChunk.blockDef(voxelWorld.get(position)).isSolid){
			c.onGround = false;
			//return false;
		} else {
			c.onGround = true;
		}
		
		dir.set(position).sub(oldPosition);
		int xside = BlockDefinition.LEFT, yside = BlockDefinition.BOTTOM, zside = BlockDefinition.BACK;
		//if (def.collide(voxel, position)){
		tmp.set(position);
		tmp.sub(oldPosition);
			//boolean r = false, t = false, f = false;
		float xo = c.xs, yo = c.ys, zo = c.zs;
		if (tmp.x < 0){
			xo = -c.xs;
			xside = BlockDefinition.RIGHT;
		} 
		if (tmp.y < 0){
			yo = -c.ys;
			yside = BlockDefinition.TOP;
		}
			
		if (tmp.z < 0){
			zo = -c.zs;
			zside = BlockDefinition.FRONT;
		}
		int vectorCount = 0;
		
		vectorCount += getAdjustedPosition(position, yside, xside, zside,  returnVectors[vectorCount], c, voxelWorld);
		vectorCount += getAdjustedPosition(position, yside, zside, xside,  returnVectors[vectorCount], c, voxelWorld);
		vectorCount += getAdjustedPosition(position, xside, yside, zside,  returnVectors[vectorCount], c, voxelWorld);
		vectorCount += getAdjustedPosition(position, xside, zside, yside,  returnVectors[vectorCount], c, voxelWorld);
		vectorCount += getAdjustedPosition(position, zside, yside, xside,  returnVectors[vectorCount], c, voxelWorld);
		vectorCount += getAdjustedPosition(position, zside, xside, yside,  returnVectors[vectorCount], c, voxelWorld);
		
		float dist2 = 1000000000;
		int smallestIndex = 0;
		//tmp.set(position);
		tmp.set(oldPosition).sub(position);
		//Gdx.app.log(TAG, "co staart");
		for (int i = 0; i < vectorCount; i++){
			//Gdx.app.log(TAG, "ret "+returnVectors[i]+"   ys"+yside);
			if (returnVectors[i].len2() < dist2){//could be distance to delta
				smallestIndex = i;//tmp.set(returnVectors[i]);
				dist2 = returnVectors[i].len2();
				
			}
		}	
		if (vectorCount > 0){
			//Gdx.app.log(TAG, "coll "+ returnVectors[smallestIndex] +"  count"+vectorCount);
			position.add(returnVectors[smallestIndex]);
			c.onGround = true;
		} else {
			c.onGround = false;
		}
		
		//Gdx.app.log(TAG, "gr"+onGround+"  ,  "+moving);
		return false;
		
		
		
		
	}
	private int getAdjustedPosition(Vector3 position, int sidea, int sideb,
			int sidec, Vector3 v, Body body, VoxelWorld voxelWorld) {
		//Vector3 v = vectors[vIndex];
		v.set(0,0,0);
		boolean done = false;
		int vadd = collideLine(position, sidea, v, body, voxelWorld);
		if (vadd != 0)done = true;
		vadd = collideLine(position, sideb, v, body, voxelWorld);
		if (vadd != 0) done = true;
		vadd = collideLine(position, sidec, v, body, voxelWorld);
		if (vadd != 0) done = true;
			return done?1:0;
	}
	private int collideLine(Vector3 position, int side, Vector3 v, Body body, VoxelWorld voxelWorld) {
		
		bounds(position, side, start, end, v, body);
		int vectorsAdded = 0;
		int fx = MathUtils.floor( start.x) - MathUtils.floor(position.x)
				, fy = MathUtils.floor( start.y) - MathUtils.floor(position.y)
				, fz = MathUtils.floor( start.z) - MathUtils.floor(position.z);
				;
		int cx = fx, cy = fy, cz = fz;;
		for (int x = (int) start.x; x <= end.x; x++, cx++){
			cy = fy;
			cz = fz;
			//Gdx.app.log(TAG, "x"+x);
			for (int y = (int) start.y; y <= end.y; y++, cy++){
				cz = fz;
				//Gdx.app.log(TAG, "y"+y);
				for (int z = (int) start.z; z <= end.z; z++, cz++){
					//cx = 0;cy = 0;cz = 0;
					tStart.set(start);
					tStart.add(v);
					tStart.sub(x,y,z)
					//.sub(cx,cy,cz)
					;
					tEnd.set(end)
					.sub(x,y,z)
					.add(v)
					//.sub(cx,cy,cz)
					;
					int voxel = voxelWorld.get(x,y,z);
					
					//
					if (VoxelChunk.blockDef(voxel).collideLineSegment(tStart, tEnd, side, v)){
						vectorsAdded = 1;
					}
					
				}
			}
		}
		return vectorsAdded;
	}
	
	private void bounds(Vector3 position, int side, Vector3 start, Vector3 end, Vector3 offset, Body c) {
		switch (side){

			case BlockDefinition.BOTTOM:
				start.set(position).add(offset).add(-c.xs, c.ys, -c.zs);
				end.set(position).add(offset).add(c.xs, c.ys, c.zs);
				break;
			case BlockDefinition.TOP:
				start.set(position).add(offset).add(-c.xs, -c.ys, -c.zs);
				end.set(position).add(offset).add(c.xs, -c.ys, c.zs);
				break;
			case BlockDefinition.RIGHT:
				start.set(position).add(offset).add(-c.xs, -c.ys, -c.zs);
				end.set(position).add(offset).add(-c.xs, c.ys, c.zs);
				break;
			case BlockDefinition.LEFT:
				start.set(position).add(offset).add(c.xs, -c.ys, -c.zs);
				end.set(position).add(offset).add(c.xs, c.ys, c.zs);
				break;
			case BlockDefinition.FRONT:
				start.set(position).add(offset).add(-c.xs, -c.ys, -c.zs);
				end.set(position).add(offset).add(c.xs, c.ys, -c.zs);
				break;
			case BlockDefinition.BACK:
				start.set(position).add(offset).add(-c.xs, -c.ys, c.zs);
				end.set(position).add(offset).add(c.xs, c.ys, c.zs);
				break;
		}
	}
	

	Vector3[] perpNormal = {
			new Vector3()
			, new Vector3()
			, new Vector3()
			, new Vector3()
			, new Vector3()
			, new Vector3()
	};
	static {
		
	}
	
	@Override
	protected void process(Entity e) {
		Physics phys = physMap.get(e);
		Position pos = posMap.get(e);
		AABBBody body = bodyMap.get(e);
		VoxelWorld voxelWorld = world.getSystem(VoxelSystem.class).voxelWorld;
		onTick(pos.pos, phys.oldPosition, body, voxelWorld);
		
	}

}
