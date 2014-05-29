package com.niz.ui.EdgeUIs;

import com.badlogic.gdx.scenes.scene2d.ui.Skin;
import com.badlogic.gdx.scenes.scene2d.ui.Table;
import com.niz.component.systems.AssetsSystem;
import com.niz.ui.elements.UIElement;

/**
 * Created by niz on 26/05/2014.
 * one edge of an edgeUI
 */
public class UITable{
    //private transient int side;
    private transient Table minTable, maxTable;
    public UIElement[] min, max;
    transient Table table = new Table();
    public UITable(){
        minTable = new Table();
        maxTable = new Table();

    }

    public void minimize(int configuration) {


    }
    public void init(Skin skin, AssetsSystem assetsSys){
        for (UIElement e : min){
            e.init(skin, assetsSys);
        }
        if (max != null)
        for (UIElement e : max){
            e.init(skin, assetsSys);
        }
    }
    public void addTo(EdgeUI edgeUI, boolean maximized, boolean expand){
        for (UIElement e : min){
            e.addTo(table);
        }

        if (maximized)
        for (UIElement e : max){
            e.addTo(table);
        }
        if (expand)
            edgeUI.table.add(table).expand();
        else
            edgeUI.table.add(table);
    }

    public void maximize(int configuration) {

        table.clear();
    }

}
