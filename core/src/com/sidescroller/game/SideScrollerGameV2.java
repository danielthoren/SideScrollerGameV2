package com.sidescroller.game;

import com.badlogic.gdx.ApplicationAdapter;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.graphics.OrthographicCamera;
import com.badlogic.gdx.graphics.Pixmap;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.Sprite;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.physics.box2d.Body;
import com.badlogic.gdx.physics.box2d.BodyDef;
import com.badlogic.gdx.physics.box2d.Box2DDebugRenderer;
import com.badlogic.gdx.physics.box2d.CircleShape;
import com.badlogic.gdx.physics.box2d.FixtureDef;
import com.badlogic.gdx.utils.viewport.FillViewport;
import com.badlogic.gdx.utils.viewport.Viewport;
import com.sidescroller.Map.Map;
import com.sidescroller.Map.MapLoader;
import com.sidescroller.objects.Shape;

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
	private static final float UPDATE_INTERVAL = 1f / 60.0f;
	public static final Vector2 WINDOW_VIEW = new Vector2(16f, 9f);  //The constant camera size (the window in to the world)
	public static final int NANOS_PER_SECOND = 1000000000;

	private static final boolean DEBUGRENDERER = true;

	private Body body;

	private Shape shapeObj;


	@Override
	public void create () {
		nanoTimeLastUpdate = System.nanoTime();
		cameraPosition = new Vector2(0,0);
		camera = new OrthographicCamera(WINDOW_VIEW.x, WINDOW_VIEW.y);
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

		CircleShape shape = new CircleShape();
		shape.setPosition(new Vector2(0,0));
		shape.setRadius(0.2f);

		FixtureDef fixtureDef = new FixtureDef();
		fixtureDef.density = 1;
		fixtureDef.friction = 0.1f;
		fixtureDef.restitution = 0.01f;
		fixtureDef.shape = shape;

		BodyDef bodyDef = new BodyDef();
		bodyDef.type = BodyDef.BodyType.DynamicBody;
		bodyDef.position.set(3,3);

		body = currentMap.createBody(bodyDef);
		body.createFixture(fixtureDef);

		shapeObj = new Shape(currentMap.getObjectID(), body);

	}

	@Override
	public void render () {
		float deltaTime = System.nanoTime() - nanoTimeLastUpdate;
		nanoTimeLastUpdate = System.nanoTime();
		currentMap.stepWorld(UPDATE_INTERVAL);

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

		drawTrojectory(batch, shapeObj.getBody(), new Vector2(-2, 10));

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
		Vector2 stepVelocity = new Vector2(UPDATE_INTERVAL * startVelocity.x, UPDATE_INTERVAL * startVelocity.y);
		//The gravity each step of the world.
		Vector2 stepGravity = new Vector2(currentMap.getGravity().x * UPDATE_INTERVAL * UPDATE_INTERVAL, currentMap.getGravity().y * UPDATE_INTERVAL * UPDATE_INTERVAL);

		return new Vector2(startPosition.x + n * stepVelocity.x + 0.5f * (n*n + n) * stepGravity.x, startPosition.y + n * stepVelocity.y + 0.5f * (n*n + n) * stepGravity.y);
	}

	public void drawTrojectory(SpriteBatch batch, Body body, Vector2 startVelocity){
		Pixmap pixmap = new Pixmap(1, 1, Pixmap.Format.RGB888);
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

	public static Map getCurrentMap() {return currentMap;}

	public OrthographicCamera getCamera() {return camera;}

	public static float getAspectRatio(){return aspectRatio;}
}
