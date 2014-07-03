package com.niz.ui.elements;

import com.artemis.Component;
import com.artemis.World;
import com.badlogic.gdx.scenes.scene2d.Actor;
import com.badlogic.gdx.scenes.scene2d.ui.Skin;
import com.badlogic.gdx.scenes.scene2d.ui.Table;
import com.badlogic.gdx.scenes.scene2d.ui.TextButton;
import com.badlogic.gdx.scenes.scene2d.utils.ChangeListener;
import com.niz.Input;
import com.niz.component.ButtonInput;
import com.niz.component.systems.AssetsSystem;
import com.niz.observer.Subject;

/**
 * Created by niz on 29/06/2014.
 */
public class ButtonPad extends UIElement {

    private ButtonInput input_fwd = new ButtonInput(),
            input_l = new ButtonInput(),
            input_r = new ButtonInput(),
            input_back = new ButtonInput(),
            input_jump = new ButtonInput()
                    ;

    @Override
    protected void onInit(Skin skin, AssetsSystem assets, World world) {
        Table table = new Table();

        table.add(new Actor());
        Actor fwd_btn = new TextButton("F", skin);
        fwd_btn.addListener(new ChangeListener() {
            @Override
            public void changed(ChangeEvent changeEvent, Actor actor) {
                subjects[0].notify(null, Subject.Event.BUTTON_PRESSED, input_fwd);
            }
        });
        table.add(fwd_btn);
        table.add(new Actor());
        table.row();
        Actor l_btn = new TextButton("F", skin);
        l_btn.addListener(new ChangeListener() {
            @Override
            public void changed(ChangeEvent changeEvent, Actor actor) {
                subjects[0].notify(null, Subject.Event.BUTTON_PRESSED, input_l);
            }
        });;
        table.add(l_btn);
        Actor middle_btn = new TextButton("F", skin);
        middle_btn.addListener(new ChangeListener() {
            @Override
            public void changed(ChangeEvent changeEvent, Actor actor) {
                subjects[0].notify(null, Subject.Event.BUTTON_PRESSED, input_jump);
            }
        });;
        table.add(middle_btn);
        Actor r_btn = new TextButton("F", skin);
        r_btn.addListener(new ChangeListener() {
            @Override
            public void changed(ChangeEvent changeEvent, Actor actor) {
                subjects[0].notify(null, Subject.Event.BUTTON_PRESSED, input_r);
            }
        });;
        table.add(r_btn);
        table.row();
        table.add(new Actor());
        Actor back_btn = new TextButton("F", skin);
        back_btn.addListener(new ChangeListener() {
            @Override
            public void changed(ChangeEvent changeEvent, Actor actor) {
                subjects[0].notify(null, Subject.Event.BUTTON_PRESSED, input_back);
            }
        });;
        table.add(back_btn);
        table.add(new Actor());



        actor = table;
    }
}
