package com.niz.ui;

import com.badlogic.gdx.scenes.scene2d.Actor;
import com.badlogic.gdx.scenes.scene2d.ui.Skin;
import com.badlogic.gdx.scenes.scene2d.ui.Table;
import com.niz.observer.Observer;
import com.niz.observer.Subject;
import com.niz.observer.Subjects;

/**
 * Created by niz on 27/05/2014.
 *     //encompasses one element of the ui i.e. a controller, button, health display, etc.

 */
public abstract class UIElement {

    private String[] recieve;//Subjects to listen to, and refresh on onNotify
    private String[] send;//Subject to notify
    transient Subject[] subjects;
    public Observer[] observers;
    protected transient Actor actor;
    
    public UIElement(){

    }
    public void init(Skin skin){
        onInit(skin);
        if (recieve != null)
            for (int i = 0; i < recieve.length; i++){
                //create Observers
                Observer o = observers[i];
                Subject sub = Subjects.get(recieve[i]);
                sub.add(o);
            }
        if (send != null) {
            subjects = new Subject[send.length];
            for (int i = 0; i < send.length; i++) {
                subjects[i] = Subjects.get(send[i]);
            }
        }
    }

    /*
    for making the actor

     */
    protected abstract void onInit(Skin skin);

    public void addTo(Table table){
        if (actor == null) return;
        table.add(actor);
    }



}
