package com.niz.actions;

import com.artemis.Entity;
import com.artemis.World;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.tests.g3d.voxel.VoxelWorld;
import com.badlogic.gdx.utils.BinaryHeap;
import com.badlogic.gdx.utils.BinaryHeap.Node;
import com.badlogic.gdx.utils.Pool.Poolable;
import com.niz.physics.JPhysicsEngine;

public abstract class Action extends Node implements Poolable{
	public Action() {
		super(0);
		
	}
	public static final float ANIM_TRANSITION_TIME = .25f;

	public static final int LEGS = 1, RIGHT_ARM = 2, LEFT_ARM = 4, HEAD = 8;
	public  static final int LANE_MOVING = 16;
	private static final String TAG = "Action";
	public abstract void update(float dt);
	public abstract void onStart(World world);
	public abstract void onEnd();
	public boolean isFinished, isBlocking;
	public int lanes;
	public float elapsed;
	public float duration;
	protected Action prev;

	private Action next;
	protected DoublyLinkedList parentList;
	protected Entity parent;
	public boolean delayed = false;;
	
	public void insertAfterMe(Action node){
		
		getNext().prev = node;
		node.setNext(next);
		node.prev = this;
		setNext(node);
		node.parent = parent;
		node.parentList = parentList;

		node.onStart(parentList.parent.world);
		
		
	}
	public void insertBeforeMe(Action node){
		//Gdx.app.log(TAG, "parent"+(parent == null));
		prev.setNext(node);
		node.prev = prev;
		node.setNext(this);
		prev = node;
		node.parent = parent;
		node.parentList = parentList;
		node.onStart(parentList.parent.world);
	}
	
	
	public static void setStaticReferences(JPhysicsEngine physics1,
			VoxelWorld voxelWorld1) {
		physics = physics1;
		world = voxelWorld1;
		
	}
	protected static JPhysicsEngine physics;
	protected static VoxelWorld world;

	
	private static float currentTime;
	public static BinaryHeap<Action> delayedActions;

	public void delay(float f){
		if (f == 0) return;
		delayed = true;
		//removeSelf();
		float value = currentTime+f;
		delayedActions.add(this, value);
		//Gdx.app.log(TAG, "delay"+delayedActions.size);
		//Gdx.app.log(TAG, "delay"+ticks);
	}
	
	public void reInsert(){
		{


			delayed = false;
			//prog.insertAfterMe(this);
		}
	}
	
	public static void updateDelays(float currTick){
		currentTime = currTick;
		while (delayedActions.size > 0 && delayedActions.peek().getValue()<currentTime){
			delayedActions.pop().reInsert();
			;
			
		}
	}
	
	public Action getAfterMe(Class<? extends Action> clas){
		Action prog = getNext();
		while (prog.getClass() != clas && prog != null)prog = prog.getNext();
		return prog;
	}
	
	public Action getNext() {
		return next;
	}
	public void setNext(Action next) {
		this.next = next;
	}
	public Entity getParent() {
		return parent;
	}
	public void setParent(Entity parent) {
		this.parent = parent;
	}
	@Override
	public void reset(){
		isFinished = false;
	}
	
}
