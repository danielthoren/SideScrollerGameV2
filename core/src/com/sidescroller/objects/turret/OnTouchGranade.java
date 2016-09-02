package com.sidescroller.objects.turret;

import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.physics.box2d.Body;
import com.badlogic.gdx.physics.box2d.BodyDef.BodyType;
import com.badlogic.gdx.physics.box2d.BodyDef;
import com.badlogic.gdx.physics.box2d.CircleShape;
import com.badlogic.gdx.physics.box2d.Contact;
import com.badlogic.gdx.physics.box2d.Filter;
import com.badlogic.gdx.physics.box2d.FixtureDef;
import com.badlogic.gdx.physics.box2d.Shape;
import com.badlogic.gdx.utils.Array;
import com.sidescroller.Map.RubeLoader.gushikustudios.loader.serializers.utils.RubeImage;
import com.sidescroller.game.CollisionListener;
import com.sidescroller.game.Draw;
import com.sidescroller.game.SideScrollGameV2;
import com.sidescroller.game.SpriteAnimation;
import com.sidescroller.game.TypeOfGameObject;
import com.sidescroller.objects.GameShape;
import com.sidescroller.objects.RubeSprite;

/**
 * A basic granade that explodes upon impact.
 */
public class OnTouchGranade implements CollisionListener, Draw
{
	private final long id;
	private GameShape granadeShape;
	private SpriteAnimation explosion;
	private SideScrollGameV2 sideScrollGameV2;
	private Texture explosionSpriteTexture;
	private int explosionTextureRows;
	private int explosionTextureColumns;
	private int layer;
	private float radious;
	private float blastRatio;
	private float explosionTime;

	private static final float EXPLOTIONTIME_DEFAULT = 0.2f;
	private static final int EXPLOSION_RATIO_DEFAULT = 5;

	/**
	 * Creates a granade that explodes on the first impact with another body that is not a sensor.
	 * @param id The id of the granade.
	 * @param position The position that the granade will spawn on.
	 * @param explosionSpriteTexture The spriteSheet of the explosion. If not using the default value, the fields 'explosionTextureRows' and
	 *                         'explosionTextureColumns' must be set accordingly.
	 * @param granadeTexture The texture of the granade.
	 * @param layer The layer at wich the granade will be drawn.
	 * @param radious The radious of the granade.
	 */
	public OnTouchGranade(final long id, SideScrollGameV2 sideScrollGameV2, Vector2 position, Texture explosionSpriteTexture, int explosionTextureColumns, int explosionTextureRows, Texture granadeTexture, int layer, float radious, Filter filter) {
		this.id = id;
		this.sideScrollGameV2 = sideScrollGameV2;
		this.layer = layer;
		this.explosionSpriteTexture = explosionSpriteTexture;
		this.radious = radious;
		this.explosionTextureColumns = explosionTextureColumns;
		this.explosionTextureRows = explosionTextureRows;
		blastRatio = EXPLOSION_RATIO_DEFAULT;
		explosionTime = EXPLOTIONTIME_DEFAULT;
		explosion = null;

		createBody(position, granadeTexture, filter);
	}

	/**
	 * Creates the body of the granade. This is just a simple circle.
	 * @param position The position at wich the body will be created.
	 * @param granadeTexture The texture to represent the body with (creating a GameShape object with it).
	 */
	private void createBody(Vector2 position, Texture granadeTexture, Filter filter){
		Shape fixtureShape = new CircleShape();
		fixtureShape.setRadius(radious);

		FixtureDef fixtureDef = new FixtureDef();
		fixtureDef.restitution = 0.2f;
		fixtureDef.shape = fixtureShape;
		fixtureDef.density = 1;
		fixtureDef.friction = 1;
		fixtureDef.filter.maskBits = filter.maskBits;
		fixtureDef.filter.categoryBits = filter.categoryBits;

		BodyDef bodyDef = new BodyDef();
		bodyDef.bullet = true;
		bodyDef.type = BodyType.DynamicBody;
		bodyDef.position.y = position.y;
		bodyDef.position.x = position.x;

		Body body;
		body = sideScrollGameV2.getCurrentMap().createBody(bodyDef);

		body.createFixture(fixtureDef);

		RubeImage rubeImage = new RubeImage();
		rubeImage.width = radious * 2;
		rubeImage.height = radious * 2;
		rubeImage.renderOrder = SideScrollGameV2.PLAYER_DRAW_LAYER;
		Array<RubeSprite> rubeSprites = new Array<RubeSprite>(1);
		rubeSprites.add(new RubeSprite(rubeImage, granadeTexture));

		granadeShape = new GameShape(id, body, rubeSprites);
	}

	/**
	 * Handles event that needs to happen when contact between objects begins. Do note that this function
	 * is called every time any contact occurs, thus each object implementing this interface must check if they
	 * are part of the contact or not.
	 * @param contact A object containing the two bodies and fixtures that made contact. It also contains collisiondata
	 *                such as point of contact and so on.
	 */
	public void beginContact(Contact contact){
		if (explosion == null) {
			if ((contact.getFixtureA().getBody().equals(granadeShape.getBody()) && !contact.getFixtureB().isSensor()) ||
				(contact.getFixtureB().getBody().equals(granadeShape.getBody()) && !contact.getFixtureA().isSensor())) {
				Vector2 bodyPos = granadeShape.getBody().getPosition();
				Vector2 explosionPos = new Vector2(bodyPos.x - radious * blastRatio, bodyPos.y - radious * blastRatio);
				Float framesPerSek = ((explosionTextureColumns * explosionTextureRows) / explosionTime);
				explosion = new SpriteAnimation(framesPerSek.intValue(), explosionTextureColumns, explosionTextureRows, explosionPos, new Vector2(radious * 2 * blastRatio, radious * 2 * blastRatio), 0,
												explosionSpriteTexture);
				sideScrollGameV2.getCurrentMap().removeBody(granadeShape.getBody());
			}
		}
	}

	/**
	 * Handles event that needs to happen when contact between objects ends. Do note that this function
	 * is called every time any contact occurs, thus each object implementing this interface must check if they
	 * are part of the contact or not.
	 * @param contact A object containing the two bodies and fixtures that made contact. It also contains collisiondata
	 *                such as point of contact and so on.
	 */
	public void endContact(Contact contact){

	}

	/**
  * The function that draws the object every frame
  * @param batch The SpriteBatch with wich to draw
  * @param layer The draw layer that is supposed to be drawn
  */
 public void draw(SpriteBatch batch, int layer){
	 if (this.layer == layer) {
		 if (explosion != null) {
			 explosion.draw(batch, layer);
			 if (explosion.isDone()) {
				 sideScrollGameV2.getCurrentMap().removeDrawObject(this);
				 sideScrollGameV2.getCurrentMap().removeCollisionListener(this);
			 }
		 }
		 else {
			 granadeShape.draw(batch, layer);
		 }
	 }
 }

	/**
	 * returns the individual id for the specific object.
	 * @return int id
	 */
	public long getId(){return id;}

	/**
	 * Returns wich type of gameobject this specific object is.
	 * @return The type of gameobject
	 */
	public TypeOfGameObject getTypeOfGameObject(){
		return TypeOfGameObject.OTHER;
	}

	public GameShape getGranadeShape() {return granadeShape;}

	public void setBlastRatio(final float blastRatio) {this.blastRatio = blastRatio;}

	public void setGroudIndex(short index){
		granadeShape.setGroupIndex(index);
	}
}
