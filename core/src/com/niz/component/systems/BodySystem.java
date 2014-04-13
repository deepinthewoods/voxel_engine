package com.niz.component.systems;


import com.artemis.Aspect;
import com.artemis.ComponentMapper;
import com.artemis.Entity;
import com.artemis.systems.EntityProcessingSystem;
import com.badlogic.gdx.math.Vector3;
import com.badlogic.gdx.tests.g3d.voxel.BlockDefinition;
import com.badlogic.gdx.tests.g3d.voxel.VoxelChunk;
import com.badlogic.gdx.tests.g3d.voxel.VoxelWorld;
import com.niz.RayCaster;
import com.niz.component.Body;
import com.niz.component.Move;
import com.niz.component.Physics;
import com.niz.component.Position;

public class BodySystem extends EntityProcessingSystem {

	private ComponentMapper<Position> posMap;
	private ComponentMapper<Physics> physMap;
	private ComponentMapper<Move> moveMap;
	private ComponentMapper<Body> bodyMap;
	private static VoxelWorld voxelWorld;
	public BodySystem(){
		super(Aspect.getAspectForAll(Body.class, Physics.class, Position.class));
		ray = new RayCaster();
		

	}
	@Override
	public void initialize(){
		posMap = world.getMapper(Position.class);
		physMap = world.getMapper(Physics.class);
		//moveMap = world.getMapper(Move.class);
		bodyMap = world.getMapper(Body.class);
		voxelWorld = world.getSystem(VoxelSystem.class).voxelWorld;

	}
	
	

	private static Vector3 tmp = new Vector3(), tmpV = new Vector3();;
	private static int[] tmpVoxels = new int[8];
	
	
	RayCaster ray;// = new RayCaster2(0f, 0f, 0f, 1, 1, 1);

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
	static Vector3 dir = new Vector3();
	
	public static void  onTick(Vector3 position, Vector3 oldPosition, Body c) {
		if (!VoxelChunk.blockDef(voxelWorld.get(position)).isSolid){
			c.onGround = false;
			return;
		}
		c.onGround = true;
		dir.set(position).sub(oldPosition);
		int xside = BlockDefinition.LEFT, yside = BlockDefinition.BOTTOM, zside = BlockDefinition.BACK;
		tmp.set(position);
		tmp.sub(oldPosition);
		//float xo = c.xs, yo = c.ys, zo = c.zs;
		if (tmp.x < 0){
			//xo = -c.xs;
			xside = BlockDefinition.RIGHT;
		} 
		if (tmp.y < 0){
			//yo = -c.ys;
			yside = BlockDefinition.TOP;
		}
			
		if (tmp.z < 0){
			//zo = -c.zs;
			zside = BlockDefinition.FRONT;
		}
		
		getAdjustedPosition(position, yside, xside, zside, returnVectors[0]);
		getAdjustedPosition(position, yside, zside, xside, returnVectors[1]);
		getAdjustedPosition(position, xside, yside, zside, returnVectors[2]);
		getAdjustedPosition(position, xside, zside, yside, returnVectors[3]);
		getAdjustedPosition(position, zside, yside, xside, returnVectors[4]);
		getAdjustedPosition(position, zside, xside, yside, returnVectors[5]);
		
		float dist2 = 1000000000;
		int smallestIndex = 0;
		for (int i = 0; i < 6; i++){
			if (returnVectors[i].dst2(position) < dist2){
				smallestIndex = i;//tmp.set(returnVectors[i]);
				dist2 = returnVectors[i].dst2(position);
			}
		}
		position.set(returnVectors[smallestIndex]);
		return;
	}
	private static void getAdjustedPosition(Vector3 position, int sidea, int sideb,
			int sidec, Vector3 v) {
		v.set(position);
		int voxel = voxelWorld.get(v);
		if (VoxelChunk.blockDef(voxel).collide(sidea, v)){
			voxel = voxelWorld.get(v);
			if (VoxelChunk.blockDef(voxel).collide(sideb, v)){
				voxel = voxelWorld.get(v);
				VoxelChunk.blockDef(voxel).collide(sidec, v);
			}
		}
		
	}


	@Override
	protected void process(Entity e) {
		Physics physics = physMap.get(e);
		Position pos = posMap.get(e);
		Body body = bodyMap.get(e);
		onTick(pos.pos, physics.oldPosition, body);
		
	}
	
	
}
