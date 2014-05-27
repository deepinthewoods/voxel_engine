package com.niz.ui;

import com.badlogic.gdx.scenes.scene2d.ui.Skin;
import com.badlogic.gdx.scenes.scene2d.ui.Table;

/**
 * Created by niz on 26/05/2014.
 */
public class EdgeUI extends Table {
    private int selectedSide, configuration;
    /*
    Configurations:

    rows: top and bottom take up entire width
    TTTTTT
    LLMMRR
    BBBBBB

    columns: left and right take up entire height
    LLTTRR
    LLMMRR
    LLBBRR

    9-way grid now always

     */
    public UITable[] sides = new UITable[9];
    //
    public void minimal(){
        for (int i = 0; i < 5; i++){
            sides[i].minimize(configuration);
        }
    }

    public void switchSelected(int side){
        sides[selectedSide].minimize(configuration);
        selectedSide = side;
        sides[selectedSide].maximize(configuration);

    }

    public EdgeUI(){
        setFillParent(true);
    }

    public void init(Skin skin){
        for (int y = 0, i = 0; y < 3; y++){
            for (int x = 0; x < 3; x++, i++){
                if (sides[i] != null){
                    sides[i].init(skin);
                    sides[i].addTo(this);

                }
            }
            row();
        }
    }

}
