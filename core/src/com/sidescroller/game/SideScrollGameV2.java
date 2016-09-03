package com.sidescroller.game;

import com.badlogic.gdx.ApplicationAdapter;
import com.badlogic.gdx.Gdx;
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
import com.badlogic.gdx.utils.viewport.FillViewport;
import com.badlogic.gdx.utils.viewport.Viewport;
import com.sidescroller.Map.Map;
import com.sidescroller.Map.MapLoader;
import com.sidescroller.Character.Player;

@SuppressWarnings("InstanceVariableMayNotBeInitialized")
//Ignore warnings regarding fields not being initialized. Theese occur because there is no constructor. All fields are
//initialized in the 'create' method instead. This is because libGDX works this way.
public class SideScrollGameV2 extends ApplicationAdapter {
	private Map currentMap;
	private SpriteBatch batch;
	private OrthographicCamera camera;
	private Viewport viewport;
	private Vector2 cameraPosition;
	private AssetManager assetManager;
	private MapLoader mapLoader;
	private Player playerCameraLock;

	private static float updateInterval;
	public static final Vector2 WINDOW_VIEW = new Vector2(10, 5);  //The constant camera size (the window in to the world)

	private static final boolean DEBUGRENDERER = false;
	public static final short ENVIROMENT_CATEGORY = 0x0001;
	public static final short BACKGROUND_ENVIROMENT_CATEGORY = 0x0002;
	public static final short PLAYER_CATEGORY = 0x0004;
	public static final short ENEMY_CATEGORY = 0x0008;

	public static final int FOREGROUND_DRAW_LAYER = 4;
	public static final int PLAYER_DRAW_LAYER = 3;
	public static final int BACKGROUND_DRAW_LAYER = 1;

	@Override
	public void create () {
		cameraPosition = new Vector2(0,0);
		camera = new OrthographicCamera(WINDOW_VIEW.x, WINDOW_VIEW.y);
		viewport = new FillViewport(WINDOW_VIEW.x, WINDOW_VIEW.y, camera);
		viewport.apply();
		assetManager = new AssetManager();

		mapLoader = new MapLoader(this);
		batch = new SpriteBatch();
		loadMap("jsonFiles/world2.json");
		playerCameraLock = mapLoader.loadPlayer(currentMap);
	}

	@Override
	public void render () {
		currentMap.stepWorld(updateInterval);

		camera.update();
		camera.position.set(playerCameraLock.getBody().getPosition().x, playerCameraLock.getBody().getPosition().y, 0);
		Gdx.gl.glClearColor(0,0,0.2f,1);
		Gdx.gl.glClear(GL20.GL_COLOR_BUFFER_BIT);

		batch.setProjectionMatrix(camera.combined);

		//Updating the updateobjects
		currentMap.update();

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
	public Vector2 getTrojectoryPoint(Vector2 startPosition, Vector2 startVelocity, float n){
		//The velocity each step of the world.
		Vector2 stepVelocity = new Vector2(updateInterval * startVelocity.x, updateInterval * startVelocity.y);
		//The gravity each step of the world.
		Vector2 stepGravity = new Vector2(currentMap.getGravity().x * updateInterval * updateInterval, currentMap.getGravity().y *
																									   updateInterval *
																									   updateInterval);

		return new Vector2(startPosition.x + n * stepVelocity.x + 0.5f * (n*n + n) * stepGravity.x, startPosition.y + n * stepVelocity.y + 0.5f * (n*n + n) * stepGravity.y);
	}

	/**
	 * Loads a new map.
	 * @param mapFilePath The path to the map file.
	 * @return Returns true if the map was already loaded, else false.
	 */
	public boolean loadMap(String mapFilePath){
		boolean preloaded = true;
		if (!mapLoader.isLoaded(mapFilePath)){
			mapLoader.loadMap(mapFilePath);
			preloaded = false;
		}
		currentMap = mapLoader.getMap(mapFilePath);
		Gdx.input.setInputProcessor(currentMap.getInputHandler());
		updateInterval = currentMap.getUpdateTime();

		return preloaded;
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

	public MapLoader getMapLoader() {return mapLoader;}

	public void setCurrentMap(Map map){currentMap = map;}

	public Map getCurrentMap() {return currentMap;}

	public AssetManager getAssetManager(){return assetManager;}

	public OrthographicCamera getCamera() {return camera;}
}
