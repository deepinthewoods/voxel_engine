package com.niz.ui.edgeUI;

import com.artemis.World;
import com.badlogic.gdx.scenes.scene2d.ui.Skin;
import com.badlogic.gdx.scenes.scene2d.ui.Table;
import com.niz.component.systems.AssetsSystem;
import com.niz.ui.elements.UIElement;

/**
 * Created by niz on 26/05/2014.
 * one edge of an edgeUI
 */
public class UITable{
    //private transient int face;
   // private transient Table minTable, maxTable;
    public UIElement[] min, max;
    transient Table table = new Table();
    public boolean vertical = false;
    transient private EdgeUI parent;
    public UITable(){
       // minTable = new Table();
        //maxTable = new Table();

    }


    public void init(Skin skin, AssetsSystem assetsSys, World world, EdgeUI parent){
        this.parent = parent;
        for (UIElement e : min){
            if (e != null)
                e.init(skin, assetsSys, world, this);
        }
        if (max != null)
        for (UIElement e : max){
            if (e != null)
                e.init(skin, assetsSys, world, this);
        }
    }
    public void addTo(EdgeUI edgeUI, boolean expand){
        for (UIElement e : min){
            if (e != null){
                e.addTo(table);
                if (vertical) table.row();
            }
        }
        if (expand)
            edgeUI.table.add(table).expand();
        else
            edgeUI.table.add(table);
    }

    public void maximizeTo(Table maxTable){
        for (UIElement e : max) {
            if (e != null)
                e.addTo(maxTable);
        }
    }

    public void maximize() {
        parent.setMiddleScreen(this);

    }

    public void minimize() {
        parent.unsetMiddleScreen();
        
    }


    public void onMinimize() {
        for (UIElement e : max){
            if (e != null)
                e.onMinimize();
        }
    }

    public void onMaximize(){
        for (UIElement e : max){
            if (e != null)
                e.onMaximize();
        }
    }
}
