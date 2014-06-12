package com.niz.ui.edgeUI;

import com.artemis.World;
import com.badlogic.gdx.scenes.scene2d.Stage;
import com.badlogic.gdx.scenes.scene2d.ui.Skin;
import com.badlogic.gdx.scenes.scene2d.ui.Table;
import com.niz.component.systems.AssetsSystem;
import com.niz.ui.elements.BackgroundClickDrag;
import com.niz.ui.elements.NullClickDrag;
import com.niz.ui.elements.UIElement;

/**
 * Created by niz on 26/05/2014.
 */
public class EdgeUI{
    private static final String TAG = "EdgeUI";
    protected transient Table table = new Table();
    transient UITable middleScreen = null;

    public UITable[] sides = new UITable[9];
    public UIElement back;

    public EdgeUI(){
        table.setFillParent(true);

    }

    public void init(Skin skin, Stage stage, AssetsSystem assets, World world){
        stage.clear();
        for (int y = 0, i = 0; y < 3; y++){
            for (int x = 0; x < 3; x++, i++){
                if (sides[i] != null){
                    sides[i].init(skin, assets, world, this);


                    sides[i].addTo(this, i==4?true:false);

                    //Gdx.app.log(TAG, "face");
                }
            }
            table.row();
        }
        //table.layout();

        //for screen touches
        Table backTable = new Table();
        backTable.setFillParent(true);
        back.init(skin, assets, world, null);
        back.addTo(backTable);

        stage.addActor(backTable);
        stage.addActor(table);

    }
    public void setMiddleScreen(UITable table){
        if (middleScreen != null){
            middleScreen.onMinimize();
        }
        sides[4].table.clear();
        table.maximizeTo(sides[4].table);
        table.onMaximize();
    }

    public void unsetMiddleScreen() {
        if (middleScreen != null){
            middleScreen.onMinimize();
        }
        middleScreen = null;
        sides[4].table.clear();
    }
}
