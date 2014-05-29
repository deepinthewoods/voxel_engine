package com.niz.observer;

import com.artemis.Component;
import com.artemis.Entity;
import com.badlogic.gdx.utils.Array;

public class Subject {
	private Array<Observer> observers = new Array<Observer>();
	public enum Event {TEST, POSITION_CHANGE, BUTTON_PRESSED, SLIDER_PRESSED, ACCUMULATE_INFLUENCE};
	public void add(Observer obs){
		observers.add(obs);
	}
	public void remove(Observer obs){
		observers.removeValue(obs, true);
	}
	public void notify(Entity e, Event event, Component c){
		for (Observer obs : observers){
			obs.onNotify(e, event, c);
		}
		
	}
	
	
}
