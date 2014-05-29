package com.niz.ui.elements;

import com.badlogic.gdx.scenes.scene2d.ui.Label;
import com.badlogic.gdx.scenes.scene2d.ui.Skin;
import com.badlogic.gdx.scenes.scene2d.ui.Table;
import com.niz.Strings;
import com.niz.component.systems.AssetsSystem;
import com.niz.observer.AutoObserver;
import com.niz.ui.AttributeObserver;

/**
 * Created by niz on 27/05/2014.
 */
public class AttributeDisplayElement extends UIElement {
    //public String stat;
    private transient Label valueLabel;
   // private transient int statHash;
    private transient AttributeObserver obs;

    public AttributeDisplayElement(){

        observers = new AutoObserver[1];
        AttributeObserver o = new AttributeObserver();
        observers[0] = o;
        observers[0].from = "playerAttributes";
        o.attributeName = "Strength";
    }

    @Override
    protected void onInit(Skin skin, AssetsSystem assets) {
        obs = (AttributeObserver) observers[0];
        //statHash = Hash.hash(stat);
        //Button b = new Button(skin);
        Table g = new Table();
        actor = g;
        g.add(new Label(obs.attributeName + ":", skin));

        valueLabel = new Label("", skin){
            @Override
            public void act(float delta) {
                super.act(delta);
                setText(Strings.getFloat(obs.attributeLevel));
            }
        };
        g.add(valueLabel);
    }
}
