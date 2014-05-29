package com.niz.ui.elements;

import com.artemis.Component;
import com.badlogic.gdx.scenes.scene2d.ui.Button;
import com.badlogic.gdx.scenes.scene2d.ui.Label;
import com.badlogic.gdx.scenes.scene2d.ui.Skin;
import com.niz.component.ButtonInput;
import com.niz.component.systems.AssetsSystem;
import com.niz.observer.Subject;

/**
 * Created by niz on 29/05/2014.
 */
public class ControllerButton extends UIElement{
    public String text;
    Component c;

    public ControllerButton(){
        text = "X";
        ButtonInput b = new ButtonInput();
        b.code = ButtonInput.InputCode.BUTTON_JUMP;
        c = b;
        send = new String[]{"playerControl"};

    }

    @Override
    protected void onInit(Skin skin, AssetsSystem assets) {
        Button b = new Button(skin){
            @Override
            public void act(float delta){
                super.act(delta);
                if (this.isPressed()){
                    subjects[0].notify(null, Subject.Event.BUTTON_PRESSED, c);
                }
            }
        };
        if (text != null) b.add(new Label(text, skin));
        actor = b;

    }
}
