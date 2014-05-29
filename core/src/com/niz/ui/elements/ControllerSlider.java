package com.niz.ui.elements;

import com.artemis.Component;
import com.badlogic.gdx.scenes.scene2d.ui.Skin;
import com.badlogic.gdx.scenes.scene2d.ui.Slider;
import com.niz.component.ButtonInput;
import com.niz.component.VectorInput;
import com.niz.component.systems.AssetsSystem;
import com.niz.observer.Subject;

/**
 * Created by niz on 29/05/2014.
 */
public class ControllerSlider extends UIElement{
    VectorInput inp;
    public ControllerSlider(){
        inp = new VectorInput();
        inp.code = ButtonInput.InputCode.BUTTON_JUMP;

        send = new String[]{"playerControl"};
    }

    @Override
    protected void onInit(Skin skin, AssetsSystem assets) {
        Slider s = new Slider(-1f, 1f, .01f, false, skin){
            @Override
            public void act(float dt){
                super.act(dt);
                if (this.isDragging()){
                    ;
                    inp.v.set(getValue(), 0);
                    subjects[0].notify(null, Subject.Event.SLIDER_PRESSED, inp);
                } else {
                    setValue(getValue()/1.52f);

                }
            }
        };

        actor = s;
    }
}
