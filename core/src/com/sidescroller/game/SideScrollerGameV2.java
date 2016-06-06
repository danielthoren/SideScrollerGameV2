package com.sidescroller.game;

import com.badlogic.gdx.ApplicationAdapter;
import com.badlogic.gdx.Game;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.graphics.OrthographicCamera;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;

public class SideScrollerGameV2 extends ApplicationAdapter {
	private static Map currentMap;
	private SpriteBatch batch;
	private OrthographicCamera camera;
	private Texture texture;
	private static final float pixPerMeter = 1;
	private InputHandler inputHandler;
	private long nanoTimeLastUpdate;
	private int velocityIterations, positionIterations;     //Values deciding the accuracy of velocity and position
	public static final int NANOS_PER_SECOND = 1000000000;
	
	@Override
	public void create () {
		nanoTimeLastUpdate = System.nanoTime();
		int width = 500;
		camera = new OrthographicCamera(width, width/2);
		batch = new SpriteBatch();
		texture = new Texture(Gdx.files.internal("badlogic.jpg"));
		inputHandler = new InputHandler();
		Gdx.input.setInputProcessor(inputHandler);
		LoadMap.getInstance().loadMap(0);
		currentMap = LoadMap.getInstance().getMap(0);

		velocityIterations = 6;  //Accuracy of jbox2d velocity simulation
		positionIterations = 3;  //Accuracy of jbox2d position simulation
	}

	@Override
	public void render () {
		long deltaTime = System.nanoTime() - nanoTimeLastUpdate;

		currentMap.getWorld().step(deltaTime / NANOS_PER_SECOND, velocityIterations, positionIterations);

		currentMap.removeStagedOBjects();
		currentMap.addStagedObjects();

		camera.update();
		Gdx.gl.glClearColor(0,0,0.2f,1);
		Gdx.gl.glClear(GL20.GL_COLOR_BUFFER_BIT);

		batch.setProjectionMatrix(camera.combined);
		batch.begin();

		for (Draw obj : currentMap.getDrawObjects()){
			obj.draw(batch);
		}

		batch.end();
	}

	/**
	 * Converts the input meters (world coordinates) to pixels (java Fx coordinates) using the scale factor loaded in
	 * the 'MapLoader' singgelton.
	 * @param meters The amount of meters to be converted
	 * @return The amount of pixels the meters corresponds to
	 */
	public static float metersToPix(float meters){
		return meters * pixPerMeter ;
	}


	/**
	 * Converts the input pixels (java Fx coordinates) to meters (world coordinates) using the scale factor loaded in
	 * the 'MapLoader' singgelton.
	 * @param pix The amount of pixels to be converted
	 * @return The amount of meters the pixels corresponds to
	 */
	public static float pixToMeters(float pix){
		return pix / pixPerMeter;
	}

	public static Map getCurrentMap() {return currentMap;}

	public OrthographicCamera getCamera() {return camera;}
}
