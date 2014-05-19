package com.niz.factories;

import com.artemis.Component;
import com.artemis.Entity;
import com.artemis.World;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.assets.AssetManager;
import com.badlogic.gdx.files.FileHandle;
import com.badlogic.gdx.graphics.Camera;
import com.badlogic.gdx.graphics.g2d.Sprite;
import com.badlogic.gdx.graphics.g2d.TextureAtlas;
import com.badlogic.gdx.graphics.g3d.Environment;
import com.badlogic.gdx.graphics.g3d.ModelBatch;
import com.badlogic.gdx.scenes.scene2d.Action;
import com.badlogic.gdx.scenes.scene2d.InputEvent;
import com.badlogic.gdx.scenes.scene2d.Stage;
import com.badlogic.gdx.scenes.scene2d.actions.Actions;
import com.badlogic.gdx.scenes.scene2d.ui.Button;
import com.badlogic.gdx.scenes.scene2d.ui.Label;
import com.badlogic.gdx.scenes.scene2d.ui.Skin;
import com.badlogic.gdx.scenes.scene2d.ui.Table;
import com.badlogic.gdx.scenes.scene2d.utils.ClickListener;
import com.badlogic.gdx.utils.Array;
import com.niz.ShapeBatch;

public abstract class GameFactory {

public abstract void assets(World world, AssetManager assets) ;

public abstract void systems(float timeStep, World world, AssetManager assets, FileHandle file);

public abstract void newGame(World world, Stage stage);

public abstract void load(World world);

public void init(float timeStep, World world, AssetManager assets, FileHandle file){
	
	
	systems(timeStep, world, assets, file);
	
	world.initialize();
	world.initializeDraw();
}

	Array<Component> components = new Array<Component>();
	public void save(World world){
		Array<Entity> es = world.getEntityManager().getEntities();
		for (Entity e : es){
			e.getComponents(components);
			//write components
			
		}
	}

	public void initMenu(final World world, final Skin skin, final Stage stage, final AssetManager assets, final float timestep) {
		//Group group = new WidgetGroup();
		Gdx.input.setInputProcessor(stage);
		final Table table = new Table();
		final Button newGame = new Button(new Label("New", skin), skin);
		
		newGame.addListener(new ClickListener(){
            @Override
            public void clicked(InputEvent event, float x, float y) {

                table.addAction(
                        Actions.sequence(
                                Actions.fadeOut(.4f)
                                ,
                                Actions.parallel(
                                        Actions.removeActor(table)
                                        ,
                                        new Action(){

                                            @Override
                                            public boolean act(float delta) {
                                                FileHandle file = Gdx.files.internal("data/game.ini");
                                                init(timestep, world, assets, file);
                                                newGame(world, stage);
                                                return true;
                                            }



                                        }
                                )
                        )
                );

                newGame.removeListener(this);
            }
        });
        final Sprite btnSprite = assets.get("data/tiles.png", TextureAtlas.class).createSprite("button");
        final Button editorBtn = new Button(new Label("Editor", skin), skin);
        editorBtn.addListener(new ClickListener(){
            @Override
            public void clicked(InputEvent event, float x, float y) {

                table.addAction(
                        Actions.sequence(
                                Actions.fadeOut(.4f)
                                ,
                                Actions.parallel(
                                        Actions.removeActor(table)
                                        ,
                                        new Action(){

                                            @Override
                                            public boolean act(float delta) {
                                                FileHandle file = Gdx.files.internal("data/editor.ini");
                                                init(timestep, world, assets, file);
                                                return true;
                                            }

                                        },
                                        new Action(){

                                            @Override
                                            public boolean act(float delta) {
                                                editor(world, stage, skin, btnSprite);
                                                return true;
                                            }

                                        }
                                )
                        )
                );

                editorBtn.removeListener(this);
            }
        });


		table.add(newGame);
        table.row();
		table.add(editorBtn);
		table.setFillParent(true);
		table.layout();
		
		stage.addActor(table);



		//stage.addActor(group);
		
		
		
	}



    protected abstract void editor(World world, Stage stage, Skin skin, Sprite sprite);
}
