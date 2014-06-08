package com.niz.component.systems;

import com.artemis.systems.VoidEntitySystem;
import com.badlogic.gdx.assets.AssetManager;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.TextureAtlas;
import com.badlogic.gdx.graphics.g3d.Model;
import com.badlogic.gdx.utils.Array;
import com.niz.factories.AssetDefinition;

/**
 * Created by niz on 09/05/2014.
 */
public class AssetsSystem extends VoidEntitySystem {

    private final AssetManager assets;
    private AssetDefinition assetsDefMain = new AssetDefinition();
    private Array<AssetDefinition> asses = new Array<AssetDefinition>();
    public AssetsSystem(AssetManager assets) {
        this.assets = assets;
    }

    @Override
    protected void processSystem() {

    }

    public TextureAtlas getTextureAtlas(String tiles) {
        return assetsDefMain.getAtlas(tiles);
    }

    public Texture getTexture(String tiles) {
        return assetsDefMain.getTexture(tiles);
    }

    public Model getModel(String tiles) {
        return assetsDefMain.getModel(tiles);
    }

    public void addDef(AssetDefinition ass) {
        asses.add(ass);
    }
    public void processDefinitions(){
        for (AssetDefinition ass : asses){
            ass.postProcess(assets, assetsDefMain);
        }
        asses.clear();
    }

}
