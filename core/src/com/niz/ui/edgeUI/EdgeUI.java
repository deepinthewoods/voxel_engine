package com.niz.ui.edgeUI;

import com.artemis.World;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.scenes.scene2d.*;
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
    protected transient Table table = new Table(), disablerTable = new Table();
    transient UITable middleScreen = null;
    transient Stage stage;
    public UITable[] sides = new UITable[9];
    public UIElement back;
    private transient boolean touchDisabled = false;

    private static EdgeUI current;

    public EdgeUI(){
        table.setFillParent(true);
        disablerTable.setFillParent(true);
        disablerTable.setTouchable(Touchable.enabled);
        disablerTable.addListener(new InputListener(){
            @Override
            public boolean handle(Event e) {
                Gdx.app.log(TAG, "disabled");
                return super.handle(e);
            }

            @Override
            public boolean touchDown(InputEvent event, float x, float y, int pointer, int button) {
                Gdx.app.log(TAG, "disabled");return true;
            }

            @Override
            public void touchUp(InputEvent event, float x, float y, int pointer, int button) {
                Gdx.app.log(TAG, "disabled");
                event.cancel();
            }

            @Override
            public void touchDragged(InputEvent event, float x, float y, int pointer) {
                Gdx.app.log(TAG, "disabled");
                event.cancel();
            }

            @Override
            public boolean mouseMoved(InputEvent event, float x, float y) {
                Gdx.app.log(TAG, "disabled");
                return true;
            }

            @Override
            public void enter(InputEvent event, float x, float y, int pointer, Actor fromActor) {
                Gdx.app.log(TAG, "disabled");
                event.cancel();
            }

            @Override
            public void exit(InputEvent event, float x, float y, int pointer, Actor toActor) {
                Gdx.app.log(TAG, "disabled");
                event.cancel();
            }

            @Override
            public boolean scrolled(InputEvent event, float x, float y, int amount) {
                Gdx.app.log(TAG, "disabled");
                return true;
            }

            @Override
            public boolean keyDown(InputEvent event, int keycode) {
                Gdx.app.log(TAG, "disabled");
                return true;
            }

            @Override
            public boolean keyUp(InputEvent event, int keycode) {
                Gdx.app.log(TAG, "disabled");
                return true;
            }

            @Override
            public boolean keyTyped(InputEvent event, char character) {
                Gdx.app.log(TAG, "disabled");
                return true;
            }
        });
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
        this.stage = stage;
        //for screen touches
        Table backTable = new Table();
        backTable.setFillParent(true);
        back.init(skin, assets, world, null);
        back.addTo(backTable);

        stage.addActor(backTable);

        stage.addActor(table);
        current = this;
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

    public void disableTouches() {
        if (!touchDisabled){
            touchDisabled = true;
            stage.addActor(disablerTable);
        }
    }

    public void enableTouches(){
        if (touchDisabled){
            touchDisabled = false;
            stage.getActors().removeValue(disablerTable, true);
        }
    }

    public static EdgeUI getCurrentlyEnabled(){
        return current;
    }
}
