package com.niz.component.systems;


import com.artemis.Aspect;
import com.artemis.ComponentMapper;
import com.artemis.Entity;
import com.artemis.systems.EntityProcessingSystem;
import com.badlogic.gdx.math.Vector3;
import com.badlogic.gdx.tests.g3d.voxel.BlockDefinition;
import com.badlogic.gdx.tests.g3d.voxel.VoxelChunk;
import com.badlogic.gdx.tests.g3d.voxel.VoxelWorld;
import com.niz.component.AABBBody;
import com.niz.component.Physics;
import com.niz.component.Position;

public class AABBBodySystem extends EntityProcessingSystem {
	
	private static final String TAG = "aabb body";

	private static final float THRESHOLD = 0.00005f;;
    private static final float ONE =    1.00000001f;
    private static final float NOUGHT = 0.00000001f;

    private static Vector3 tmp = new Vector3();
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
    transient int[] sides = new int[3];
	Vector3 dir = new Vector3();
	Vector3 start = new Vector3(), end = new Vector3(), tStart = new Vector3(), tEnd = new Vector3();

	private ComponentMapper<Position> posMap;
	private ComponentMapper<Physics> physMap;
	private ComponentMapper<AABBBody> bodyMap;
    private int smallestYIndex;


    public AABBBodySystem(){
		super(Aspect.getAspectForAll(Position.class, Physics.class, AABBBody.class));
		
		
	}
	@Override
	public void initialize(){
		posMap = world.getMapper(Position.class);
		physMap = world.getMapper(Physics.class);
		bodyMap = world.getMapper(AABBBody.class);
	}
	

	Vector3[] normals = {new Vector3(), new Vector3(), new Vector3()};


    private Vector3 tmpV = new Vector3(), tmpW = new Vector3(), tmpD = new Vector3();
	@Override
	protected void process(Entity e) {
		Physics phys = physMap.get(e);
		Position pos = posMap.get(e);
		AABBBody body = bodyMap.get(e);
		VoxelWorld voxelWorld = world.getSystem(VoxelSystem.class).voxelWorld;
        tmpD.set(pos.pos);
        tmpD.sub(phys.oldPosition);


        /*tmpW.set(pos.pos).add(body.xs,-body.ys,body.zs);
        tickY(tmpW, pos.pos, voxelWorld);
        tmpW.set(pos.pos).add(body.xs,-body.ys,-body.zs);
        tickY(tmpW, pos.pos, voxelWorld);
        tmpW.set(pos.pos).add(-body.xs,-body.ys,body.zs);
        tickY(tmpW, pos.pos, voxelWorld);
        tmpW.set(pos.pos).add(-body.xs,-body.ys,-body.zs);
        tickY(tmpW, pos.pos, voxelWorld);*/

        float xs = body.xs;
        float ys = body.ys;
        float zs = body.zs;
        if (tmpD.x < 0f) {
            xs *= -1;
        }
        if (tmpD.y < 0f) {
            ys *= -1;
        }
        if (tmpD.z < 0f) {
            zs *= -1;
        }
        //tmpV.set(phys.oldPosition).add(ys);

        body.onGround = false;

        if (
        tickY(tmpW.set(pos.pos).add(xs,ys,zs), pos.pos, phys.oldPosition, voxelWorld, body)||
        tickY(tmpW.set(pos.pos).add(-xs,ys,zs), pos.pos, phys.oldPosition, voxelWorld, body)||
        tickY(tmpW.set(pos.pos).add(xs,ys,-zs), pos.pos, phys.oldPosition, voxelWorld, body)||
        tickY(tmpW.set(pos.pos).add(-xs,ys,-zs), pos.pos, phys.oldPosition, voxelWorld, body)
        ){
            phys.oldPosition.y = pos.pos.y;
        }

        tickXZ(tmpW.set(pos.pos).add(xs,ys,zs), pos.pos, voxelWorld);
        tickXZ(tmpW.set(pos.pos).add(xs,-ys,zs), pos.pos, voxelWorld);
        tickXZ(tmpW.set(pos.pos).add(xs,ys,-zs), pos.pos, voxelWorld);
        tickXZ(tmpW.set(pos.pos).add(xs,-ys,-zs), pos.pos, voxelWorld);

        tickXZ(tmpW.set(pos.pos).add(-xs,ys,zs), pos.pos, voxelWorld);
        tickXZ(tmpW.set(pos.pos).add(-xs,-ys,zs), pos.pos, voxelWorld);//*/

        /*tickSmallest(tmpW.set(pos.pos).add(xs,ys,zs), pos.pos, voxelWorld);
        tickSmallest(tmpW.set(pos.pos).add(xs,-ys,zs), pos.pos, voxelWorld);
        tickSmallest(tmpW.set(pos.pos).add(xs,ys,-zs), pos.pos, voxelWorld);
        tickSmallest(tmpW.set(pos.pos).add(xs,-ys,-zs), pos.pos, voxelWorld);

        tickSmallest(tmpW.set(pos.pos).add(-xs,ys,zs), pos.pos, voxelWorld);
        tickSmallest(tmpW.set(pos.pos).add(-xs,-ys,zs), pos.pos, voxelWorld);

*/






        /*
        if (body.onGround) {
            tickY(pos.pos, voxelWorld);
            tickXZ(pos.pos, voxelWorld);
            tickXZLargest(pos.pos, voxelWorld);
        } else {
            tickSmallest(pos.pos, voxelWorld);
            tickMiddle(pos.pos, voxelWorld);
            tickLargest(pos.pos, voxelWorld);
        }
        */
        //tickSmallest(pos, voxelWorld);

		
	}

    private void tickXZLargest(Vector3 pnt, Vector3 pos, VoxelWorld voxelWorld) {
        float x = pnt.x;
        float y = pnt.y;
        float z = pnt.z;

        int plane = 0;
        BlockDefinition b;
        b = VoxelChunk.blockDef(voxelWorld.get(x,y,z, plane));
        if (b.isSolid){
            //closest normal
            x = ((x % 1f)+1f) %1f;
            z = ((z % 1f)+1f) %1f;

            /*x = Math.abs(x);
            z = Math.abs(z);
            x %= 1f;
            z %= 1f;*/

            if (x > .5f)normals[0].set(ONE - x, 0, 0);
            else normals[0].set( - x - NOUGHT, 0, 0);


            if (z > .5f) normals[2].set(0, 0,ONE -z );
            else normals[2].set(0, 0,  - z - NOUGHT);

            if (normals[0].len2() < normals[2].len2()) {
                pos.add(normals[2]);
                //Gdx.app.log(TAG, "solid xz "+normals[2]);

            }
            else {
                pos.add(normals[0]);
                //Gdx.app.log(TAG, "solid xz "+normals[0]);

            }

        }
    }

    private void tickSmallest(Vector3 pnt, Vector3 pos, VoxelWorld voxelWorld) {
        float x = pnt.x;
        float y = pnt.y;
        float z = pnt.z;

        int plane = 0;
        BlockDefinition b;
        b = VoxelChunk.blockDef(voxelWorld.get(x,y,z, plane));
        if (b.isSolid){
            //closest normal
            x = ((x % 1f)+1f) %1f;
            y = ((y % 1f)+1f) %1f;
            z = ((z % 1f)+1f) %1f;


            if (y > .5f) normals[1].set(0, ONE-y , 0);
            else normals[1].set(0,  - y -NOUGHT, 0);

            if (x > .5f)normals[0].set(ONE - x, 0, 0);
            else normals[0].set( - x - NOUGHT, 0, 0);

            if (y > .5f) normals[1].set(0, ONE-y , 0);
            else normals[1].set(0,  - y -NOUGHT, 0);

            if (z > .5f) normals[2].set(0, 0,ONE -z );
            else normals[2].set(0, 0,  - z - NOUGHT);

            if (normals[0].len2() < normals[1].len2()) {
                if (normals[0].len2() < normals[2].len2()){//0
                    pos.add(normals[0]);
                } else {//2
                    pos.add(normals[2]);

                }
            }
            else {//1 smaller
                if (normals[1].len2() < normals[2].len2()){//1

                    pos.add(normals[1]);
                } else {//2
                    pos.add(normals[2]);
                }
            }

        }
    }

    private void tickLargest(Vector3 pnt, Vector3 pos, VoxelWorld voxelWorld) {
        float x = pnt.x;
        float y = pnt.y;
        float z = pnt.z;

        int plane = 0;
        BlockDefinition b;
        b = VoxelChunk.blockDef(voxelWorld.get(x,y,z, plane));
        if (b.isSolid){
            //closest normal
            x = ((x % 1f)+1f) %1f;
            y = ((y % 1f)+1f) %1f;
            z = ((z % 1f)+1f) %1f;


            if (y > .5f) normals[1].set(0, ONE-y , 0);
            else normals[1].set(0,  - y -NOUGHT, 0);

            if (x > .5f)normals[0].set(ONE - x, 0, 0);
            else normals[0].set( - x - NOUGHT, 0, 0);

            if (y > .5f) normals[1].set(0, ONE-y , 0);
            else normals[1].set(0,  - y -NOUGHT, 0);

            if (z > .5f) normals[2].set(0, 0,ONE -z );
            else normals[2].set(0, 0,  - z - NOUGHT);

            if (normals[0].len2() > normals[1].len2()) {
                if (normals[0].len2() > normals[2].len2()){//0
                    pos.add(normals[0]);
                } else {//2
                    pos.add(normals[2]);

                }
            }
            else {//1 smaller
                if (normals[1].len2() > normals[2].len2()){//1

                    pos.add(normals[1]);
                } else {//2
                    pos.add(normals[2]);
                }
            }

        }
    }

    private void tickMiddle(Vector3 pnt, Vector3 pos, VoxelWorld voxelWorld) {
        float x = pnt.x;
        float y = pnt.y;
        float z = pnt.z;

        int plane = 0;
        BlockDefinition b;
        b = VoxelChunk.blockDef(voxelWorld.get(x,y,z, plane));
        if (b.isSolid){
            //closest normal
            x = ((x % 1f)+1f) %1f;
            y = ((y % 1f)+1f) %1f;
            z = ((z % 1f)+1f) %1f;


            if (y > .5f) normals[1].set(0, ONE-y , 0);
            else normals[1].set(0,  - y -NOUGHT, 0);

            if (x > .5f)normals[0].set(ONE - x, 0, 0);
            else normals[0].set( - x - NOUGHT, 0, 0);

            if (y > .5f) normals[1].set(0, ONE-y , 0);
            else normals[1].set(0,  - y -NOUGHT, 0);

            if (z > .5f) normals[2].set(0, 0,ONE -z );
            else normals[2].set(0, 0,  - z - NOUGHT);

            if (normals[0].len2() < normals[1].len2()) {
                if (normals[0].len2() < normals[2].len2()){//0
                    pos.add(normals[2]);
                } else {//2
                    pos.add(normals[0]);

                }
            }
            else {//1 smaller
                if (normals[1].len2() < normals[2].len2()){//1

                    pos.add(normals[2]);
                } else {//2
                    pos.add(normals[1]);
                }
            }

        }
    }
    private void tickXZ(Vector3 pnt, Vector3 pos, VoxelWorld voxelWorld) {
        float x = pnt.x;
        float y = pnt.y;
        float z = pnt.z;

        int plane = 0;
        BlockDefinition b;
        b = VoxelChunk.blockDef(voxelWorld.get(x,y,z, plane));
        if (b.isSolid){
            //closest normal
            x = ((x % 1f)+1f) %1f;
            z = ((z % 1f)+1f) %1f;

            /*x = Math.abs(x);
            z = Math.abs(z);
            x %= 1f;
            z %= 1f;*/

            if (x > .5f)normals[0].set(ONE - x, 0, 0);
            else normals[0].set( - x - NOUGHT, 0, 0);


            if (z > .5f) normals[2].set(0, 0,ONE -z );
            else normals[2].set(0, 0,  - z - NOUGHT);

            if (normals[0].len2() > normals[2].len2()) {
                pos.add(normals[2]);
                //Gdx.app.log(TAG, "solid xz "+normals[2]);

            }
            else {
                pos.add(normals[0]);
                //Gdx.app.log(TAG, "solid xz "+normals[0]);

            }

        }
    }

    private boolean tickY(Vector3 pnt, Vector3 pos, Vector3 oldPos, VoxelWorld voxelWorld, AABBBody body) {
        float x = pnt.x;
        float y = pnt.y;
        float z = pnt.z;

        int plane = 0;
        BlockDefinition b;
        b = VoxelChunk.blockDef(voxelWorld.get(x,y,z, plane));
        if (b.isSolid){
            //closest normal
            body.onGround = true;
            //y = Math.abs(y);
            //y %= 1f;
            y = ((y % 1f)+1f) %1f;

            if (y > .5f) normals[1].set(0, ONE-y , 0);
            else normals[1].set(0,  - y -NOUGHT, 0);



            if (normals[1].y > 0) {
                pos.add(normals[1]);

                //Gdx.app.log(TAG, "solid y " + normals[1]);
            } else {
                pos.add(0, normals[1].y, 0);
                //Gdx.app.log(TAG, "solid yyy " + normals[1]);
            }
            return true;
        }
        return false;
    }



}
