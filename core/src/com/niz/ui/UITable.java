package com.niz.ui;

import com.badlogic.gdx.scenes.scene2d.ui.Skin;
import com.badlogic.gdx.scenes.scene2d.ui.Table;

/**
 * Created by niz on 26/05/2014.
 * one edge of an edgeUI
 */
public class UITable extends Table{
    private transient int side;
    private Table minTable, maxTable;
    public UIElement[] min, max;
    public UITable(Table min, Table max, int side){
        minTable = min;
        maxTable = max;
        this.side = side;
    }

    public void minimize(int configuration) {
        clear();

    }
    public void init(Skin skin){
        for (UIElement e : min){
            e.init(skin);
        }
        for (UIElement e : max){
            e.init(skin);
        }
    }
    public void addTo(Table table, boolean maximized){
        for (UIElement e : min){
            e.addTo(table);
        }

        if (maximized)
        for (UIElement e : max){
            e.addTo(table);
        }
    }

    public void maximize(int configuration) {
        clear();


    }
}
