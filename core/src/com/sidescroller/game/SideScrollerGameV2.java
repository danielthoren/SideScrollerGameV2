package com.sidescroller.game;

import com.badlogic.gdx.ApplicationAdapter;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.InputProcessor;
import com.badlogic.gdx.assets.AssetManager;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.graphics.OrthographicCamera;
import com.badlogic.gdx.graphics.Pixmap;
import com.badlogic.gdx.graphics.Pixmap.Format;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.Batch;
import com.badlogic.gdx.graphics.g2d.Sprite;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.physics.box2d.Body;
import com.badlogic.gdx.physics.box2d.ContactListener;
import com.badlogic.gdx.utils.viewport.FillViewport;
import com.badlogic.gdx.utils.viewport.Viewport;
import com.sidescroller.map.Map;
import com.sidescroller.map.MapLoader;

@SuppressWarnings("InstanceVariableMayNotBeInitialized")
//Ignore warnings regarding fields not being initialized. Theese occur because there is no constructor. All fields are
//initialized in the 'create' method instead. This is because libGDX works this way.
public class SideScrollerGameV2 extends ApplicationAdapter {
	private static Map currentMap;
	private SpriteBatch batch;
	private OrthographicCamera camera;
	private Viewport viewport;
	private Vector2 cameraPosition;
	private AssetManager assetManager;
	private MapLoader mapLoader;

	private static float updateInterval;
	public static final Vector2 WINDOW_VIEW = new Vector2(16, 9);  //The constant camera size (the window in to the world)

	private static final boolean DEBUGRENDERER = true;

	@Override
	public void create () {
		cameraPosition = new Vector2(0,0);
		camera = new OrthographicCamera(WINDOW_VIEW.x, WINDOW_VIEW.y);
		viewport = new FillViewport(16, 9, camera);
		viewport.apply();
		assetManager = new AssetManager();

		mapLoader = new MapLoader(this);
		batch = new SpriteBatch();
		InputProcessor inputHandler = new InputHandler();
		Gdx.input.setInputProcessor(inputHandler);
		currentMap = mapLoader.loadMap("world1.json");
		updateInterval = currentMap.getUpdateTime();
		//Setting the worlds contactlistener
		ContactListener contactListenerGame = new ContactListenerGame();
		currentMap.setContactListener(contactListenerGame);
	}

	@Override
	public void render () {
		currentMap.stepWorld(updateInterval);

		camera.update();
		Gdx.gl.glClearColor(0,0,0.2f,1);
		Gdx.gl.glClear(GL20.GL_COLOR_BUFFER_BIT);

		batch.setProjectionMatrix(camera.combined);

		//Updating the updateobjects
		currentMap.update();
		currentMap.getActionManager().update();

		batch.begin();

		//Drawing the drawobjects
		currentMap.draw(batch);

		batch.end();

		if (DEBUGRENDERER){
			currentMap.debugDraw(camera);
		}
	}

	@Override
	public void resize (int width, int height) {
		viewport.update(width,height);
		camera.position.set(cameraPosition, 0);
	}

	/**
	 * Calculates the approximate position of a moving body n steps in the future. Does not take other objects into account.
	 * @param startPosition The starting position of the body.
	 * @param startVelocity The starting velocity of the body.
	 * @param n The step at wich to calculate the approximate posotion.
     * @return A vector with the calculated position.
     */
	public static Vector2 getTrojectoryPoint(Vector2 startPosition, Vector2 startVelocity, float n){
		//The velocity each step of the world.
		Vector2 stepVelocity = new Vector2(updateInterval * startVelocity.x, updateInterval * startVelocity.y);
		//The gravity each step of the world.
		Vector2 stepGravity = new Vector2(currentMap.getGravity().x * updateInterval * updateInterval, currentMap.getGravity().y *
																									   updateInterval *
																									   updateInterval);

		return new Vector2(startPosition.x + n * stepVelocity.x + 0.5f * (n*n + n) * stepGravity.x, startPosition.y + n * stepVelocity.y + 0.5f * (n*n + n) * stepGravity.y);
	}

	public void drawTrojectory(Batch batch, Body body, Vector2 startVelocity){
		Pixmap pixmap = new Pixmap(1, 1, Format.RGB888);
		Texture texture = new Texture(pixmap);
		Sprite sprite = new Sprite(texture);
		sprite.setSize(0.05f, 0.05f);

		for (int x = 1; x < 180; x++){
			Vector2 position = getTrojectoryPoint(body.getPosition(), startVelocity, x);
			sprite.setPosition(position.x, position.y);
			sprite.draw(batch);
		}
	}

	/**
	 * Converts radians to degrees. Used when drawing bodies.
	 * @param rad The angle in radians.
	 * @return The angle in degrees
	 */
	public static float radToDeg(float rad){
		return (float) (rad * (180/Math.PI));
	}

	public static float degToRad(float deg) {return (float) (deg * (Math.PI/180f));}

	public static Map getCurrentMap() {return currentMap;}

	public AssetManager getAssetManager(){return assetManager;}

	public OrthographicCamera getCamera() {return camera;}
}
