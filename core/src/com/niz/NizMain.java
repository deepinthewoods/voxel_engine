package com.niz;

import com.badlogic.gdx.ApplicationAdapter;
import com.badlogic.gdx.Game;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;


public class NizMain extends Game {
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
}