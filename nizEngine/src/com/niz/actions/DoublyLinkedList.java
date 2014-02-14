package com.niz.actions;

import com.artemis.Entity;
import com.badlogic.gdx.utils.Pools;

public class DoublyLinkedList {
	//protected Action root;
	protected Action rootNode, lastNode;
	private Action progress;
	//protected Action last;
	//private Action prevProgress;
	//boolean hasNext;
	public Entity e;
	public ActionList parent;
	
	public DoublyLinkedList(){
		rootNode = new RootAction();
		lastNode = new RootAction();
		lastNode.prev = rootNode;
		rootNode.next = lastNode;
	}
	
	public void iter() {
		//progress = root;
		progress = rootNode;
	}
	
	public Action next() {
		progress = progress.next;
		return  (Action) progress;
	}

	
	
	public void remove() {
		progress.next.prev = progress.prev;
		progress.prev.next = progress.next;
		//progress.onEnd();
		
		Pools.free(progress);
		//progress = progress.next;
	}

	private void add(Action node){
		node.parent = this;
		node.next = lastNode;
		node.prev = lastNode.prev;
		lastNode.prev.next = node;
		lastNode.prev = node;
		node.isFinished = false;
		//Gdx.app.log("jfdskl", "added"+ (rootNode.next == lastNode )   );

		node.onStart();
	}
	public<M extends Action> M add(Class<M> class1){
		M instance = Pools.obtain(class1);
		add(instance);
		return instance;
	}
	/*public Action pop(){
		if (rootNode.next == lastNode.prev) return null;//no items
		
		lastNode.prev = lastNode.prev.prev;
		lastNode.prev.next = lastNode;
		Action ret = (Action) lastNode;
		
		return ret;
	}*/

	public boolean hasNext() {
		//if (rootNode.next == lastNode) return false;
		return progress.next != lastNode;
	}
	



	public Action getRoot() {
		return rootNode;
	}
	public boolean contains(Class<? extends Action> class1) {
		Action n = rootNode;
		while (n != null){
			if (n.getClass() == class1) return true;
			n = n.next;
		}
		return false;
	}

	public int size() {
		Action prog = rootNode;
		int tot = 0;
		while (prog.next != lastNode){
			tot++;
			prog = prog.next;
		}
		return tot;
	}

	public<M extends Action> M addFirst(Class<M> class1) {
		M instance = Pools.obtain(class1);
		addFirst(instance);
		return instance;
		
	}
	
	public void addFirst(Action node){
		node.parent = this;

		rootNode.insertAfterMe(node);
		node.isFinished = false;
		//Gdx.app.log("jfdskl", "added"+ (rootNode.next == lastNode )   );

		node.onStart();
	}
	
	
	
	
	
	
	
	
	
	
	
}
