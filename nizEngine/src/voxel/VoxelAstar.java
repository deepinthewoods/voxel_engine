package voxel;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.utils.BinaryHeap;
import com.badlogic.gdx.utils.BinaryHeap.Node;
import com.badlogic.gdx.utils.GdxRuntimeException;
import com.badlogic.gdx.utils.IntArray;
import com.badlogic.gdx.utils.Pools;

public class VoxelAstar{

	private static final String TAG = "voxel astar";

	private static final int STEP_LIMIT = 500;

	private VoxelWorld world;
	
	public int offX, offY;
	
	private final int width, height;
	private final BinaryHeap<PathNode> open;
	private final PathNode[] nodes;
	int runID;
	private IntArray path;
	private int targetX, targetY;
	private int lastColumn, lastRow, totalProcessed;
	private PathNode root;
	
	public IntArray process(){
		totalProcessed = 0;
		while (open.size > 0 && totalProcessed < STEP_LIMIT) {
			PathNode node = open.pop();
			if (node.x == targetX && node.y == targetY) {
				while (node != root) {
					path.add(node.x+offX);
					path.add(node.y+offY);
					node = node.parent;
				}
				break;
			}
			node.closedID = runID;
			int x = node.x;
			int y = node.y;
			if (x < lastColumn) addNode(node, x + 1, y);
			if (x > 0) addNode(node, x - 1, y);
			if (y < lastRow) addNode(node, x, y + 1);
			if (y > 0) addNode(node, x, y - 1);
			totalProcessed++;
		}
		
		if (open.size == 0){
			//Gdx.app.log(TAG, "path done");
			return path;
		}
		return null;
	}
	

	/** Returns x,y pairs that are the path from the target to the start. */
	public void getPath (int startX, int startY, int targetX, int targetY) {
		if (open.size != 0)
			throw new GdxRuntimeException("already started a path");
		offX = startX;
		offX -= width/2;
		targetX -= offX;
		startX -= offX;
		
		offY = startY;
		offY -= height/2;
		targetY -= offY;
		startY -= offY;
		
		if (targetX < 0){
			targetX = 0;
		} else if (targetX > width){
			targetX = width;
		}
		if (targetY < 0){
			targetY = 0;
		} else if (targetY > height){
			targetY = height;
		}
		//Gdx.app.log(TAG, "start "+startX+" , "+startY+" , "+targetX + " , "+targetY);
		this.targetX = targetX;
		this.targetY = targetY;
		path = Pools.obtain(IntArray.class);
		path.clear();
		open.clear();

		runID++;
		if (runID < 0) runID = 1;

		int index = startY * width + startX;
		root = nodes[index];
		if (root == null) {
			root = new PathNode(0);
			root.x = startX;
			root.y = startY;
			nodes[index] = root;
		}
		root.parent = null;
		root.pathCost = 0;
		open.add(root, 0);

		lastColumn = width - 1;
		lastRow = height - 1;
		
		totalProcessed = 0;
		//IntArray path2 = Pools.obtain(IntArray.class);
		//path2.clear();
		//path2.addAll(path, 0, path.size);
		//return path;
	}

	private void addNode (PathNode parent, int x, int y) {
		if (!isValid(x, y)) return;

		int pathCost = parent.pathCost + 1;
		float score = pathCost + 
				Math.abs(x - targetX) + Math.abs(y - targetY);//manhattan
				

		int index = y * width + x;
		PathNode node = nodes[index];
		if (node != null && node.runID == runID) { // Node already encountered for this run.
			if (node.closedID != runID && pathCost < node.pathCost) { // Node isn't closed and new cost is lower.
				// Update the existing node.
				open.setValue(node, score);
				node.parent = parent;
				node.pathCost = pathCost;
			}
		} else {
			// Use node from the cache or create a new one.
			if (node == null) {
				node = new PathNode(0);
				node.x = x;
				node.y = y;
				nodes[index] = node;
			}
			open.add(node, score);
			node.runID = runID;
			node.parent = parent;
			node.pathCost = pathCost;
		}
	}

	

	public int getWidth () {
		return width;
	}

	public int getHeight () {
		return height;
	}

	static private class PathNode extends Node {
		int runID, closedID, x, y, pathCost;
		PathNode parent;

		public PathNode (float value) {
			super(value);
		}
	}
	public VoxelAstar(int width, int height, VoxelWorld world) {
		this.width = width;
		this.height = height;
		open = new BinaryHeap(width * 4, false);
		nodes = new PathNode[width * height];
		
		this.world = world;
		// TODO Auto-generated constructor stub
	}

 
	protected boolean isValid(int x, int y){
		if (x < 1 || y < 1 || x >= world.voxelsX || y >= world.voxelsZ)return false;
		
			byte block = world.get(x+offX,2,y+offY);
			if (block == 0)return true;
			return false;
		
		
	}

	public void getPath(float x, float y, float x2, float y2) {
		this.getPath((int)x, (int)y, (int)x2, (int)y2);
	}
}
