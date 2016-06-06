package com.sidescroller.game;

import com.badlogic.gdx.ApplicationAdapter;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.graphics.OrthographicCamera;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.physics.box2d.Box2DDebugRenderer;
import com.badlogic.gdx.utils.viewport.FillViewport;
import com.badlogic.gdx.utils.viewport.Viewport;

public class SideScrollerGameV2 extends ApplicationAdapter {
	private static Map currentMap;
	private SpriteBatch batch;
	private OrthographicCamera camera;
	private Texture texture;
	private Box2DDebugRenderer box2DDebugRenderer;
	private InputHandler inputHandler;
	private Viewport viewport;

	private long nanoTimeLastUpdate;
	private int velocityIterations, positionIterations;             //Values deciding the accuracy of velocity and position
	private static float pixPerMeter = 10;
	private static float aspectRatio;
	public static final Vector2 windowView = new Vector2(16f, 9f);  //The constant camera size (the window in to the world)
	public static final int NANOS_PER_SECOND = 1000000000;
	
	@Override
	public void create () {
		nanoTimeLastUpdate = System.nanoTime();
		camera = new OrthographicCamera(windowView.x, windowView.y);
		//camera.translate(camera.viewportWidth/2, camera.viewportHeight/2);
		viewport = new FillViewport(16, 9, camera);
		viewport.apply();

		batch = new SpriteBatch();
		aspectRatio = (float)Gdx.graphics.getHeight() / (float)Gdx.graphics.getWidth();
		inputHandler = new InputHandler();
		Gdx.input.setInputProcessor(inputHandler);
		LoadMap.getInstance().loadMap(0);
		currentMap = LoadMap.getInstance().getMap(0);
		//Setting the worlds contactlistener
		ContactListenerGame contactListenerGame = new ContactListenerGame();
		currentMap.getWorld().setContactListener(contactListenerGame);

		velocityIterations = 6;  //Accuracy of jbox2d velocity simulation
		positionIterations = 3;  //Accuracy of jbox2d position simulation
	}

	@Override
	public void render () {
		float deltaTime = System.nanoTime() - nanoTimeLastUpdate;
		nanoTimeLastUpdate = System.nanoTime();

		//currentMap.getWorld().step(1/60, velocityIterations, positionIterations);
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

	@Override
	public void resize (int width, int height) {
		viewport.update(width,height);
		//camera.position.set(camera.viewportWidth/2,camera.viewportHeight/2,0);
		camera.position.set(0,0,0);
	}

	/**
	 * Converts the input meters (world coordinates) to pixels (java Fx coordinates) using the scale factor loaded in
	 * the 'MapLoader' singgelton.
	 * @param meters The amount of meters to be converted
	 * @return The amount of pixels the meters corresponds to
	 */
	public static float metersToPix(float meters){
		return meters * pixPerMeter;
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

	public static float getAspectRatio(){return aspectRatio;}


}
