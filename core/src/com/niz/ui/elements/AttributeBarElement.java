package com.niz.ui.elements;

import com.badlogic.gdx.graphics.g2d.Sprite;
import com.badlogic.gdx.scenes.scene2d.Actor;
import com.badlogic.gdx.scenes.scene2d.ui.*;
import com.badlogic.gdx.scenes.scene2d.utils.BaseDrawable;
import com.badlogic.gdx.scenes.scene2d.utils.SpriteDrawable;
import com.niz.component.systems.AssetsSystem;
import com.niz.observer.AutoObserver;
import com.niz.ui.AttributeObserver;

/**
 * Created by niz on 27/05/2014.
 */
public class AttributeBarElement extends UIElement {
    //public String stat;
   // private transient int statHash;
    private transient AttributeObserver obs;

    public AttributeBarElement(){

        observers = new AutoObserver[1];
        AttributeObserver o = new AttributeObserver();
        observers[0] = o;
        observers[0].from = "playerAttributes";
        o.attributeName = "Health";
    }

    @Override
    protected void onInit(Skin skin, AssetsSystem assets) {
        obs = (AttributeObserver) observers[0];
        //statHash = Hash.hash(stat);
        //Button b = new Button(skin);
        WidgetGroup g = new Stack();
        actor = g;
        g.addActor(new Label(obs.attributeName, skin));

        Sprite sprite = assets.getTextureAtlas("tiles").createSprite("air");
        //final Gauge gauge = new Gauge(sprite);
        BaseDrawable back = new SpriteDrawable(sprite), knob = new BaseDrawable();
        ProgressBar.ProgressBarStyle style = new ProgressBar.ProgressBarStyle(back, knob);

        Actor gaugeAct = new ProgressBar(0, 1f, .01f, false, style){
            @Override
            public void act(float delta) {
                super.act(delta);
                setValue(obs.attributeLevel/obs.attributeMax);
            }
        };
        g.addActor(gaugeAct);
    }
}
