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
import com.sidescroller.Map.Map;
import com.sidescroller.Map.MapLoader;
import com.sidescroller.player.Player;

public class SideScrollerGameV2 extends ApplicationAdapter {
	private static Map currentMap;
	private SpriteBatch batch;
	private OrthographicCamera camera;
	private Texture texture;
	private Box2DDebugRenderer box2DDebugRenderer;
	private InputHandler inputHandler;
	private Viewport viewport;
	private Vector2 cameraPosition;

	private long nanoTimeLastUpdate;
	private int velocityIterations, positionIterations;             //Values deciding the accuracy of velocity and position
	private static float pixPerMeter = 10;
	private static float aspectRatio;
	public static final Vector2 windowView = new Vector2(16f, 9f);  //The constant camera size (the window in to the world)
	public static final int NANOS_PER_SECOND = 1000000000;

	private static final boolean DEBUGRENDERER = true;

	SpriteAnimation spriteAnimation;

	@Override
	public void create () {
		nanoTimeLastUpdate = System.nanoTime();
		cameraPosition = new Vector2(0,0);
		camera = new OrthographicCamera(windowView.x, windowView.y);
		viewport = new FillViewport(16, 9, camera);
		viewport.apply();

		box2DDebugRenderer = new Box2DDebugRenderer();
		batch = new SpriteBatch();
		aspectRatio = (float)Gdx.graphics.getHeight() / (float)Gdx.graphics.getWidth();
		inputHandler = new InputHandler();
		Gdx.input.setInputProcessor(inputHandler);
		currentMap = MapLoader.getInstance().loadMap("world1.json");
		//Setting the worlds contactlistener
		ContactListenerGame contactListenerGame = new ContactListenerGame();
		currentMap.setContactListener(contactListenerGame);

		spriteAnimation = new SpriteAnimation(10, 6, 4, new Texture(Gdx.files.internal("walkAnimation.png")));
	}

	@Override
	public void render () {
		float deltaTime = System.nanoTime() - nanoTimeLastUpdate;
		nanoTimeLastUpdate = System.nanoTime();
		currentMap.stepWorld(1f/60f);

		currentMap.removeStagedOBjects();
		currentMap.addStagedObjects();

		camera.update();
		Gdx.gl.glClearColor(0,0,0.2f,1);
		Gdx.gl.glClear(GL20.GL_COLOR_BUFFER_BIT);

		batch.setProjectionMatrix(camera.combined);

		//Updating the updateobjects
		for (Update obj : currentMap.getUpdateObjects()){
			obj.update();
		}
		currentMap.getActionManager().update();

		batch.begin();

		//Drawing the drawobjects
		for (int layer = currentMap.getLayerCount(); layer >= 0; layer--) {
			for (Draw obj : currentMap.getDrawObjects()) {
				obj.draw(batch, layer);
			}
		}

		spriteAnimation.draw(batch, 0);

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
	 * Converts radians to degrees. Used when drawing bodies.
	 * @param rad The angle in radians.
	 * @return The angle in degrees
	 */
	public static float radToDeg(float rad){
		return (float) (rad * (180/Math.PI));
	}

	public static Map getCurrentMap() {return currentMap;}

	public OrthographicCamera getCamera() {return camera;}

	public static float getAspectRatio(){return aspectRatio;}
}
