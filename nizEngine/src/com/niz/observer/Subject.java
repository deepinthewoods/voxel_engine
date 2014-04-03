package com.niz.observer;

import com.artemis.Entity;
import com.badlogic.gdx.utils.Array;

public class Subject {
	private Array<Observer> observers = new Array<Observer>();
	public enum Event {TEST};
	public void add(Observer obs){
		observers.add(obs);
	}
	public void remove(Observer obs){
		observers.removeValue(obs, true);
	}
	public void notify(Entity e, Event event){
		for (Observer obs : observers){
			obs.onNotify(e, event);
		}
		
	}
	
	
}
