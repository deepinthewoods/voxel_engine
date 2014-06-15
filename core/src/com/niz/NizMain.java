package com.niz;

import com.badlogic.gdx.Game;
import com.badlogic.gdx.Gdx;


public class NizMain extends Game {
    public static CoreInfo coreInfo;
    public EngineScreen engine;

    @Override
    public void pause() {
    }

    @Override
    public void resume() {
    }

    @Override
    public void create() {
        engine = new EngineScreen(this);
        setScreen(engine);
    }

    @Override
    public void resize(int width, int height) {
        getScreen().resize(width, height);
    }

    @Override
    public void render() {
        getScreen().render(Gdx.graphics.getDeltaTime());
    }

    @Override
    public void dispose() {
        getScreen().dispose();
    }

    public NizMain(CoreInfo coreInfo){
        this.coreInfo = coreInfo;
    }
}