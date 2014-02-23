package com.niz.actions;

import voxel.VoxelWorld;

import com.artemis.World;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.math.Vector3;
import com.badlogic.gdx.utils.BinaryHeap;
import com.badlogic.gdx.utils.BinaryHeap.Node;
import com.niz.physics.JPhysicsEngine;

public abstract class Action extends Node{
	public Action() {
		super(0);
		
	}
	public static final float ANIM_TRANSITION_TIME = .5f;

	public static final int LEGS = 1, RIGHT_ARM = 2, LEFT_ARM = 4, HEAD = 8;
	public  static final int LANE_MOVING = 16;
	private static final String TAG = "Action";
	public abstract void update(float dt);
	public abstract void onStart();
	public abstract void onEnd();
	public boolean isFinished, isBlocking;
	public int lanes;
	public float elapsed;
	public float duration;
	protected Action prev, next;
	protected DoublyLinkedList parent;
	public boolean delayed = false;;
	
	public void insertAfterMe(Action node){
		
		next.prev = node;
		node.next = next;
		node.prev = this;
		next = node;
		
		
		
	}
	public void insertBeforeMe(Action node){
		{
			prev.next = node;
			node.prev = prev;
			node.next = this;
			prev = node;
		}
	}
	
	public void removeSelf(){
			prev.next = next;
			next.prev = prev;
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
		Action prog = next;
		while (prog.getClass() != clas && prog != null)prog = prog.next;
		return prog;
	}
	public abstract void onAddToWorld(World world);
		
	
}
