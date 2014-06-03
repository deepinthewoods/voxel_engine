package com.niz.ui.elements;

import com.artemis.World;
import com.badlogic.gdx.scenes.scene2d.Actor;
import com.badlogic.gdx.scenes.scene2d.ui.Skin;
import com.badlogic.gdx.scenes.scene2d.ui.Table;
import com.niz.component.systems.AssetsSystem;
import com.niz.observer.AutoObserver;
import com.niz.observer.Subject;
import com.niz.observer.Subjects;
import com.niz.ui.edgeUI.UITable;

/**
 * Created by niz on 27/05/2014.
 *     //encompasses one element of the ui i.e. a controller, button, health display, etc.

 */
public abstract class UIElement {

    private static final String TAG = "UI element";
    //protected String[] recieve;//Subjects to listen to, and refresh on onNotify
    protected String[] send;//Subject to notify
    protected transient Subject[] subjects;
    protected AutoObserver[] observers;
    protected transient Actor actor;
    protected transient UITable parent;

    public UIElement(){

    }
    public void init(Skin skin, AssetsSystem assets, World world, UITable parent){
        this.parent = parent;
        if (observers != null)
            for (int i = 0; i < observers.length; i++){
                //create Observers
                AutoObserver o = observers[i];
                if (o == null) continue;
                Subject sub = Subjects.get(o.from);
                sub.add(o);
            }
        if (send != null) {
            subjects = new Subject[send.length];
            for (int i = 0; i < send.length; i++) {
                subjects[i] = Subjects.get(send[i]);
            }
        }
        onInit(skin, assets, world);
    }

    /*
    for making the actor

     */
    protected abstract void onInit(Skin skin, AssetsSystem assets, World world);

    public void addTo(Table table){
        table.add(actor);
    }



}
