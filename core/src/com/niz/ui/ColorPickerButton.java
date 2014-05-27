package com.niz.ui;

import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.g2d.Sprite;
import com.badlogic.gdx.scenes.scene2d.ui.Button;
import com.badlogic.gdx.scenes.scene2d.ui.Label;
import com.badlogic.gdx.scenes.scene2d.ui.Skin;
import com.badlogic.gdx.scenes.scene2d.utils.SpriteDrawable;

/**
 * Created by niz on 18/05/2014.
 */
public class ColorPickerButton extends Button {

    public int colorIndex;
    public ColorPickerButton(Skin skin, Sprite sprite, Sprite spritesel, int i) {

        super(new ButtonStyle(
                new SpriteDrawable(sprite)
                , new SpriteDrawable(sprite)
                , new SpriteDrawable(spritesel)

        ));
       // super();
        colorIndex = i+1;
        add(new Label("" + colorIndex, skin));

    }

    public void setColor(Color selectedColor, Color[] blockColors) {
        setColor(selectedColor);
        blockColors[colorIndex].set(selectedColor);

    }
}
