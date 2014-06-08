package com.niz.ui.elements.blockEditor;

import com.artemis.World;
import com.badlogic.gdx.scenes.scene2d.Actor;
import com.badlogic.gdx.scenes.scene2d.actions.Actions;
import com.badlogic.gdx.scenes.scene2d.ui.*;
import com.badlogic.gdx.scenes.scene2d.utils.ChangeListener;
import com.niz.component.Vector3Input;
import com.niz.component.systems.AssetsSystem;
import com.niz.ui.elements.UIElement;

/**
 * Created by niz on 07/06/2014.
 */
public class EditorSettings extends UIElement {
    transient Vector3Input v3 = new Vector3Input();
    transient private Label errorLabel;
    transient private Label successLabel;

    public EditorSettings(){
        send = new String[]{"editorSettings"};
    }

    @Override
    protected void onInit(Skin skin, AssetsSystem assets, World world) {
        final TextField wid = new TextField("16", skin);

        final TextField hei = new TextField("16", skin);

        final TextField dep = new TextField("16", skin);

        Table table = new Table();

        table.add(new Label("width:", skin));
        table.add(wid);
        table.row();
        table.add(new Label("height:", skin));

        table.add(hei);
        table.row();
        table.add(new Label("depth:", skin));

        table.add(dep);
        table.row();

        errorLabel = new Label("Error in settings", skin);
        errorLabel.addAction(Actions.fadeOut(.001f));

        successLabel = new Label("settings saved Successfully", skin);
        successLabel.addAction(Actions.fadeOut(.01f));

        TextButton ok  = new TextButton("Ok", skin);

        ok.addListener(
                new ChangeListener() {

                    @Override
                    public void changed(ChangeEvent event, Actor actor) {
                        int w = 16, h = 16, d = 16;
                        boolean success = true;
                        try {
                            w = Integer.parseInt(wid.getText());
                            h = Integer.parseInt(hei.getText());
                            d = Integer.parseInt(dep.getText());
                        } catch (NumberFormatException ex) {
                            success = false;
                        }
                        if (success) {
                            v3.v.set(w, h, d);
                            subjects[0].notify(null, null, v3);
                            successLabel.addAction(
                                    Actions.sequence(
                                            Actions.fadeIn(.1f)
                                            , Actions.delay(5f)
                                            , Actions.fadeOut(1f)
                                    )
                            );
                            parent.minimize();
                        } else {
                            //error message
                            errorLabel.addAction(
                                    Actions.sequence(
                                            Actions.fadeIn(.1f)
                                            , Actions.delay(5f)
                                            , Actions.fadeOut(1f)
                                    )
                            );
                        }


                    }
                }
        );

        TextButton cancel = new TextButton("Cancel", skin);
        cancel.addListener(new ChangeListener(){

            @Override
            public void changed(ChangeEvent event, Actor actor) {
                parent.minimize();
            }
        });

        table.add(ok);
        table.add(cancel);
        table.row();
        table.add(errorLabel);
        table.row();
        table.add(successLabel);
        table.row();

        actor = table;
    }

    @Override
    public void onMaximize(){
        errorLabel.addAction(Actions.fadeOut(.001f));
        successLabel.addAction(Actions.fadeOut(.01f));
        errorLabel.act(10f);
        successLabel.act(10f);
    }
}
