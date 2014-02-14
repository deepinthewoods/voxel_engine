package com.niz.component.input;

import voxel.VoxelWorld;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.Camera;
import com.badlogic.gdx.graphics.g3d.Renderable;
import com.badlogic.gdx.math.Intersector;
import com.badlogic.gdx.math.Plane;
import com.badlogic.gdx.math.Vector3;
import com.badlogic.gdx.math.collision.Ray;
import com.badlogic.gdx.utils.Array;
import com.badlogic.gdx.utils.Pool;

public class TopDown {
	private Vector3 viewPlaneNormal = new Vector3(0,1,0), viewPlanePoint = new Vector3()
	, viewRayOrigin = new Vector3(),  viewRayDirection = new Vector3();
	private Plane viewPlane = new Plane(viewPlaneNormal, viewPlanePoint);
	private Ray viewRay = new Ray(viewRayOrigin, viewRayDirection);
	Vector3 cornerPt0Near = new Vector3(), cornerPt1Near = new Vector3(), cornerPt0Far = new Vector3()
	, cornerPt1Far = new Vector3(), cornerTmp = new Vector3();
	private void getRenderablesForTopDown(Array<Renderable> renderables,
			Pool<Renderable> pool, Camera camera) {
		//Gdx.app.log(TAG, "corner11 "+ "  dir "+camera.direction);

		//camera.direction.set(0,-1,0);
		//camera.update();
		//Gdx.app.log(TAG, "corner12 "+ "  dir "+camera.direction);

		//Gdx.app.log(TAG, "camera"+camera.direction);
		//Gdx.app.log(TAG,  "camera dir"+camera.direction + "     "+camera.viewportHeight);
		//renderedChunks = 0;
		int x0, x1, z0, z1;
		cornerPt0Far.set(0,0,1);
		cornerPt1Far.set(Gdx.graphics.getWidth(), Gdx.graphics.getHeight(), 1);
		cornerPt0Near.set(0,0,0);
		cornerPt1Near.set(Gdx.graphics.getWidth(), Gdx.graphics.getHeight(), 0);
		camera.unproject(cornerPt0Far);
		camera.unproject(cornerPt1Far);
		camera.unproject(cornerPt0Near);
		camera.unproject(cornerPt1Near);
		
		//Gdx.app.log(TAG, "unproj  "+cornerPt1Near +"  "+camera.viewportHeight+ "  "+cornerPt1Far);
		viewRayOrigin.set(cornerPt0Near);
		viewRayDirection.set(cornerPt0Far.sub(cornerPt0Near)).nor();
		viewPlanePoint.set(0, 1, 0);
		viewRay.set(viewRayOrigin, viewRayDirection);
		viewPlane.set(viewPlanePoint, viewPlaneNormal);
		Intersector.intersectRayPlane(viewRay, viewPlane, cornerTmp);
		x0 = (int) cornerTmp.x;
		z0 = (int) cornerTmp.z;
		//Gdx.app.log(TAG, "corner1 "+x0+","+z0+"  ,  "+cornerTmp + "  dir "+camera.direction);
		
		viewRayOrigin.set(cornerPt1Near);
		viewRayDirection.set(cornerPt1Far.sub(cornerPt1Near)).nor();
		viewPlanePoint.set(0, 1, 0);
		viewRay.set(viewRayOrigin, viewRayDirection);
		viewPlane.set(viewPlanePoint, viewPlaneNormal);
		Intersector.intersectRayPlane(viewRay, viewPlane, cornerTmp);
		x1 = (int) cornerTmp.x;
		z1 = (int) cornerTmp.z;

		
		x0 /= VoxelWorld.CHUNK_SIZE_X;
		x1 /= VoxelWorld.CHUNK_SIZE_X;
		z0 /= VoxelWorld.CHUNK_SIZE_Z;
		z1 /= VoxelWorld.CHUNK_SIZE_Z;
		x1++;
		z1++;
		//z1++;
		//x0++;
		//z1--;
		if (x0 > x1){
			int tmp = x0;
			x0 = x1;
			x1 = tmp;
		}
		if (z0 > z1){
			int tmp = z0;
			z0 = z1;
			z1 = tmp;
		}
		
		//getRenderables(x0, x1, 0, 0, z0, z1, renderables, pool);
	}
}
