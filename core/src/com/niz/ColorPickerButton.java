package com.niz;

import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.g2d.Sprite;
import com.badlogic.gdx.scenes.scene2d.Actor;
import com.badlogic.gdx.scenes.scene2d.Group;
import com.badlogic.gdx.scenes.scene2d.ui.*;
import com.badlogic.gdx.scenes.scene2d.utils.SpriteDrawable;

/**
 * Created by niz on 18/05/2014.
 */
public class ColorPickerButton extends Button {
    public Color color = new Color();
    public int colorIndex;
    public ColorPickerButton(Skin skin, Sprite sprite, int i) {

        super(new ButtonStyle(
                new SpriteDrawable(sprite)
                , new SpriteDrawable(sprite)
                , new SpriteDrawable(sprite)

        ));
       // super();
        add(new Label("" + i, skin));
        colorIndex = i;
        this.setColor(Color.WHITE);
    }
}
