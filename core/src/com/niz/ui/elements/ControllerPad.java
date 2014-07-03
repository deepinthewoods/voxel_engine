package com.niz.ui.elements;

import com.artemis.World;
import com.badlogic.gdx.scenes.scene2d.ui.Skin;
import com.badlogic.gdx.scenes.scene2d.ui.Touchpad;
import com.niz.Input;
import com.niz.component.ButtonInput;
import com.niz.component.VectorInput;
import com.niz.component.systems.AssetsSystem;
import com.niz.observer.Subject;

/**
 * Created by niz on 30/05/2014.
 */
public class ControllerPad extends UIElement {
    VectorInput inp;
    public ControllerPad(){
        inp = new VectorInput();
        inp.code = Input.Code.BUTTON_JUMP;

        send = new String[]{"playerControl"};
    }
    @Override
    protected void onInit(Skin skin, AssetsSystem assets, World world) {
        Touchpad p = new Touchpad(.1f, skin){
            @Override
            public void act(float dt){
                super.act(dt);
                if (this.isTouched()){
                    inp.v.set(getKnobX(), getKnobY());
                    subjects[0].notify(null, Subject.Event.SLIDER_PRESSED, inp);
                }
            }
        };
        //p.setScale(12f);
        p.getStyle().background.setMinHeight(100f);
        p.getStyle().background.setMinWidth(100f);
        actor = p;
    }
}
