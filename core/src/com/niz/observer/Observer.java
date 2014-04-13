package com.niz.observer;

import com.artemis.Component;
import com.artemis.Entity;
import com.niz.observer.Subject.Event;

public interface Observer {


	void onNotify(Entity e, Event event, Component c);

}
