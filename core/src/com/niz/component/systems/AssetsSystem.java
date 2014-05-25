package com.niz.component.systems;

import com.artemis.systems.VoidEntitySystem;
import com.badlogic.gdx.assets.AssetManager;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.TextureAtlas;
import com.badlogic.gdx.graphics.g3d.Model;
import com.niz.factories.GameFactory;

/**
 * Created by niz on 09/05/2014.
 */
public class AssetsSystem extends VoidEntitySystem {

    private final AssetManager assets;

    public AssetsSystem(AssetManager assets) {
        this.assets = assets;
    }

    @Override
    protected void processSystem() {

    }

    public TextureAtlas getTextureAtlas(String tiles) {
        return assets.get(GameFactory.path+tiles+".pack", TextureAtlas.class);
    }

    public Texture getTexture(String tiles) {
        return assets.get(GameFactory.path+tiles+".png", Texture.class);
    }

    public Model getModel(String tiles) {
        return assets.get(GameFactory.path+tiles+".g3db", Model.class);
    }
}
