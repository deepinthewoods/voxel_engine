package com.niz.actions;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.utils.Array;
import com.badlogic.gdx.utils.Pools;

public class DoublyLinkedList {
	private static final String TAG = "doublyLinkedList";
	//protected Action root;
	protected Action rootNode, lastNode;
	private Action progress;
	//protected Action last;
	//private Action prevProgress;
	//boolean hasNext;
	//public Entity e;
	public ActionList parent;
	//private Array<Action> added = new Array<Action>(false, 1);
	public DoublyLinkedList(ActionList list){
		parent = list;
		rootNode = new RootAction();
		lastNode = new RootAction();
		lastNode.prev = rootNode;
		rootNode.setNext(lastNode);
		rootNode.parent = parent.parent;
		rootNode.parentList = this;
		lastNode.parent = parent.parent;
		lastNode.parentList = this;
	}
	
	public void iter() {
		//progress = root;
		progress = rootNode;
	}
	
	public Action next() {
		progress = progress.getNext();
		return  (Action) progress;
	}

	
	
	public void remove() {
		progress.getNext().prev = progress.prev;
		progress.prev.setNext(progress.getNext());
		//progress.onEnd();
		
		Pools.free(progress);
		//progress = progress.next;
	}

	private void add(Action node){
		node.parentList = this;
		node.setParent(parent.parent);
		node.setNext(lastNode);
		node.prev = lastNode.prev;
		lastNode.prev.setNext(node);
		lastNode.prev = node;
		node.isFinished = false;
		//Gdx.app.log(TAG, "added"+ (parent.parent == null)   );

		
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
		return progress.getNext() != lastNode;
	}
	



	public Action getRoot() {
		return rootNode;
	}
	public boolean contains(Class<? extends Action> class1) {
		Action n = rootNode;
		while (n != null){
			if (n.getClass() == class1) return true;
			n = n.getNext();
		}
		return false;
	}

	public int size() {
		Action prog = rootNode;
		int tot = 0;
		while (prog.getNext() != lastNode){
			tot++;
			prog = prog.getNext();
		}
		return tot;
	}

	public void clear() {
		iter();
		while (hasNext()) {
			next();
			remove();
		}
	}

	
	
	
	
	
	
	
	
	
	
	
	
}
