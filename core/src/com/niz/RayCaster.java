package com.niz;


import com.badlogic.gdx.math.Vector3;
import com.badlogic.gdx.tests.g3d.voxel.BlockDefinition;

public class RayCaster{
	//converted from xna wiki http://www.xnawiki.com/index.php?title=Voxel_traversal
	private static final float PositiveInfinity = Float.POSITIVE_INFINITY;
	public int x,y,z;
	public int stepX, stepY, stepZ;
	//public IEnumerable<Point3D> GetCellsOnRay(Ray ray, int maxDepth)
	Vec3i cellBoundary = new Vec3i(), start = new Vec3i();
	Vector3 tDelta = new Vector3(), tMax = new Vector3(), tmpDirection = new Vector3();
	public int progress;
	public int face, limit;
	private int xFace, yFace, zFace;
	public boolean hasNext, hasTarget;
	public int targetX, targetY, targetZ, srcX, srcY, srcZ;
	
	public void trace(Vector3 src, Vector3 dst){
		trace(src.x, src.y, src.z, dst);
	}
	public void trace(float x, float y, float z, Vector3 dst) {
		trace(x,y,z,dst.x, dst.y, dst.z);
	}
		
	public void trace(float x, float y, float z, float dstX, float dstY, float dstZ) {
        srcX = (int) x;
        srcY = (int) y;
        srcZ = (int) z;
		targetX = (int) dstX;
		targetY = (int) dstY;
		targetZ = (int) dstZ;
		tmpDirection.set(dstX, dstY, dstZ).sub(x,y,z);
		limit = (int) 100;//Math.max(tmpDirection.x,tmpDirection.z)+4;
		hasTarget = true;
		//Gdx.app.log("rayc", "trace limit"+limit);
		traceWithDirectionReal(x, y, z, tmpDirection.nor());
	}
	
	public void traceWithDirection(float x2, float y2, float z2, Vector3 direction)
	{
		hasTarget = false;
		traceWithDirectionReal(x2, y2, z2, direction);
	}
	public void traceWithDirection(Vector3 position, Vector3 direction) {
		hasTarget = false;
		traceWithDirectionReal(position.x, position.y, position.z, direction);
		
	}

	public void traceWithDirectionReal(float x2, float y2, float z2, Vector3 direction)
	{
	    // Implementation is based on:
	    // "A Fast Voxel Traversal Algorithm for Ray Tracing"
	    // John Amanatides, Andrew Woo
	    // http://www.cse.yorku.ca/~amana/research/grid.pdf
	    // http://www.devmaster.net/articles/raytracing_series/A%20faster%20voxel%20traversal%20algorithm%20for%20ray%20tracing.pdf
	 
	    // NOTES:
	    // * This code assumes that the ray's position and direction are in 'cell coordinates', which means
	    //   that one unit equals one cell in all directions.
	    // * When the ray doesn't start within the voxel grid, calculate the first position at which the
	    //   ray could enter the grid. If it never enters the grid, there is nothing more to do here.
	    // * Also, it is important to test when the ray exits the voxel grid when the grid isn't infinite.
	    // * The Point3D structure is a simple structure having three integer fields (X, Y and Z).
		hasNext = true;
	    // The cell in which the ray starts.
		progress = 0;
		start.set(x2, y2, z2);        // Rounds the position's X, Y and Z down to the nearest integer values.
	    x = start.x;
	    y = start.y;
	    z = start.z;
	 
	    // Determine which way we go.
	    stepX = sign(direction.x);
	    stepY = sign(direction.y);
	    stepZ = sign(direction.z);
	 
	    // Calculate cell boundaries. When the step (i.e. direction sign) is positive,
	    // the next boundary is AFTER our current position, meaning that we have to add 1.
	    // Otherwise, it is BEFORE our current position, in which case we add nothing.
	    cellBoundary.set(
	        x + (stepX > 0 ? 1 : 0),
	        y + (stepY > 0 ? 1 : 0),
	        z + (stepZ > 0 ? 1 : 0));
	 
	    // NOTE: For the following calculations, the result will be Single.PositiveInfinity
	    // when direction.x, Y or Z equals zero, which is OK. However, when the left-hand
	    // value of the division also equals zero, the result is Single.NaN, which is not OK.
	 
	    // Determine how far we can travel along the ray before we hit a voxel boundary.
	    tMax.set(
	        (cellBoundary.x - x2) / direction.x,    // Boundary is a plane on the YZ axis.
	        (cellBoundary.y - y2) / direction.y,    // Boundary is a plane on the XZ axis.
	        (cellBoundary.z - z2) / direction.z);    // Boundary is a plane on the XY axis.
	    //Gdx.app.log("ray", "MMMMMMMMMMMMMAAAABBBBBBBBBBAAAAAXXXXXXXXXXXX"+tMax);
	    if (isNaN(tMax.x)) tMax.x = PositiveInfinity;
	    if (isNaN(tMax.y)) tMax.y = PositiveInfinity;
	    if (isNaN(tMax.z)) tMax.z = PositiveInfinity;
	    //Gdx.app.log("ray", "MMMMMMMMMMMMMAAAAAAAAAXXXXXXXXXXXX"+tMax);
	    // Determine how far we must travel along the ray before we have crossed a gridcell.
	    tDelta.set(
	        stepX / direction.x,                    // Crossing the width of a cell.
	        stepY / direction.y,                    // Crossing the height of a cell.
	        stepZ / direction.z);                    // Crossing the depth of a cell.
	    if (isNaN(tDelta.x)) tDelta.x = PositiveInfinity;
	    if (isNaN(tDelta.y)) tDelta.y = PositiveInfinity;
	    if (isNaN(tDelta.z)) tDelta.z = PositiveInfinity;
	 
	    // For each step, determine which distance to the next voxel boundary is lowest (i.e.
	    // which voxel boundary is nearest) and walk that way.
	   // for (int i = 0; i < maxDepth; i++)
	    xFace = stepX>0?BlockDefinition.LEFT:BlockDefinition.RIGHT;
	    yFace = stepY>0?BlockDefinition.BOTTOM:BlockDefinition.TOP;
	    zFace = stepZ>0?BlockDefinition.BACK:BlockDefinition.FRONT;
	}
	
	public void next()
		{
			progress++;
	        // Do the next step.
	        if (tMax.x < tMax.y && tMax.x < tMax.z)
	        {
	            // tMax.x is the lowest, an YZ cell boundary plane is nearest.
	            x += stepX;
	            tMax.x += tDelta.x;
	            face = xFace;
	           // Gdx.app.log("rayc", "nx");
	        }
	        else if (tMax.y < tMax.z)
	        {
	            // tMax.y is the lowest, an XZ cell boundary plane is nearest.
	            y += stepY;
	            tMax.y += tDelta.y;
	            face = yFace;
	           // Gdx.app.log("rayc", "ny");
	        }
	        else
	        {
	            // tMax.z is the lowest, an XY cell boundary plane is nearest.
	            z += stepZ;
	            tMax.z += tDelta.z;
	            face = zFace;
	            //Gdx.app.log("rayc", "nz");
	        }
	        if (progress >= limit) hasNext = false;
	       if (hasTarget){
	    	   if (stepX > 0){
	    		   if (x > targetX) hasNext = false;
	    	   }
	    	   else if (stepX < 0){
	    		   if (x < targetX) hasNext = false;
	    	   }
	    	   
	    	   if (stepY > 0){
	    		   if (y > targetY) hasNext = false;
	    	   }
	    	   else if (stepY < 0){
	    		   if (y < targetY) hasNext = false;
	    	   }
	    	   
	    	   if (stepZ > 0){
	    		   if (z > targetZ) hasNext = false;
	    	   }
	    	   else if (stepZ < 0){
	    		   if (z < targetZ) hasNext = false;
	    	   }
	       }
	       //Gdx.app.log("rayc", "next "+x+","+y+","+z + " delat "+tMax);
	    }
	
	
	private int sign(float x2) {
		if (x2 == 0f) return 0;
		return x2>0?1:-1;
	}
	private boolean isNaN(float x) {
		if (x == Float.NEGATIVE_INFINITY) return true;
		if (x == x) return false;
		return true;
	}



}