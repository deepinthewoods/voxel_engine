package com.niz.factories;

import com.artemis.World;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.assets.AssetManager;
import com.badlogic.gdx.graphics.Pixmap;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.TextureAtlas;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.graphics.g3d.Model;
import com.badlogic.gdx.utils.GdxRuntimeException;
import com.badlogic.gdx.utils.IntMap;

public class AssetDefinition{
	public String[] textures = {"tiles"}
	, pixmaps = {"fades"} , models = {"humanmodel"}, atlasses = {"tiles"};
	public int foo = 4;
	
	public static IntMap<TextureRegion> textureMap = new IntMap<TextureRegion>();
	public static IntMap<Model> modelMap = new IntMap<Model>();
	public static IntMap<Pixmap> pixmapMap = new IntMap<Pixmap>();
    public static IntMap<TextureAtlas> atlasMap = new IntMap<TextureAtlas>();
	
	public void process(World world, AssetManager assets, String path) {
		
		for (String s : textures){
			assets.load(path+s+".png", Texture.class);
		}
		for (String s : pixmaps){
			assets.load(path+s+".png", Pixmap.class);
		}
		for (String s : models){
			assets.load(path+s+".g3db", Model.class);
		}
        for (String s : atlasses){
            assets.load(path+s+".pack", TextureAtlas.class);
        }
		
		//assets.load("data/tiles.png", Texture.class);
		//assets.load("data/fades.png", Pixmap.class);
		//assets.load("data/humanmodel.g3db", Model.class);
	}
    /*
    @Deprecated
	public void postProcess(AssetManager assets){
		for (String s : textures){
			Texture t = assets.get("data/"+s+".png", Texture.class);
			textureMap.put(key(s), new TextureRegion(t));
			Gdx.app.log("assets", "put "+s);

		}
		for (String s : pixmaps){
			Pixmap t = assets.get("data/"+s+".png", Pixmap.class);
			pixmapMap.put(key(s), t);
			Gdx.app.log("assets", "put "+s);

		}
		for (String s : models){
			Model t = assets.get("data/"+s+".g3db", Model.class);
			modelMap.put(key(s), t);
			Gdx.app.log("assets", "put "+s);

		}

        for (String s : atlasses){
            TextureAtlas t = assets.get("data/"+s+".pack", TextureAtlas.class);
            atlasMap.put(key(s), t);
            Gdx.app.log("assets", "put "+s);

        }
	}
	private static int key(String s) {
		return s.hashCode();
	}
	public static TextureRegion getTexture(String string) {//"tiles"
		TextureRegion r = textureMap.get(key(string));
        if (r == null) {throw new GdxRuntimeException("texture not found"+string);}
		return r;
	}
	public static Model getModel(String string) {//"tiles"
		Model r = modelMap.get(key(string));
        if (r == null) {throw new GdxRuntimeException("mpdel not found"+string);}
		return r;
	}
	public static Pixmap getPixmap(String string) {//"tiles"
		Pixmap r = pixmapMap.get(key(string));
        if (r == null) {throw new GdxRuntimeException("pixmap not found"+string);}
		return r;
	}
	public static TextureAtlas getAtlas(String string){
        TextureAtlas r = atlasMap.get(key(string));

        if (r == null) {throw new GdxRuntimeException("atlas not found"+string);}
        return r;
    }
	*/
}
