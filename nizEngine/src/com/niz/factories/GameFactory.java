package com.niz.factories;

import com.artemis.Component;
import com.artemis.Entity;
import com.artemis.World;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.assets.AssetManager;
import com.badlogic.gdx.graphics.Camera;
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

public abstract void register(World world, AssetManager assets, Camera camera) ;

public abstract void doneLoading(float timeStep, World world, AssetManager assets, Camera camera, ModelBatch modelBatch, ShapeBatch shapeBatch);

public abstract void newGame(World world, Stage stage);

public abstract void load(World world);

public void init(float timeStep, World world, Environment env, AssetManager assets, Camera camera, ModelBatch modelBatch, ShapeBatch shapeBatch){
	
	
	doneLoading(timeStep, world, assets, camera, modelBatch, shapeBatch);
	
	world.initialize();
	world.initializeDraw(modelBatch, camera, env, shapeBatch);
}

	Array<Component> components = new Array<Component>();
	public void save(World world){
		Array<Entity> es = world.getEntityManager().getEntities();
		for (Entity e : es){
			e.getComponents(components);
			//write components
			
		}
	}

	public void initMenu(final World world, Skin skin, final Stage stage, Camera camera) {
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
		
		
		table.add(newGame);
		
		table.setFillParent(true);
		table.layout();
		
		stage.addActor(table);
		
		//stage.addActor(group);
		
		
		
	}

	

	
}
