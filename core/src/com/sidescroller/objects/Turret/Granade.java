package com.sidescroller.objects.Turret;

import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.physics.box2d.Body;
import com.badlogic.gdx.physics.box2d.BodyDef;
import com.badlogic.gdx.physics.box2d.CircleShape;
import com.badlogic.gdx.physics.box2d.Contact;
import com.badlogic.gdx.physics.box2d.FixtureDef;
import com.badlogic.gdx.physics.box2d.Shape;
import com.badlogic.gdx.utils.Array;
import com.sidescroller.Map.RubeLoader.gushikustudios.loader.serializers.utils.RubeImage;
import com.sidescroller.game.CollisionListener;
import com.sidescroller.game.Draw;
import com.sidescroller.game.SideScrollerGameV2;
import com.sidescroller.game.SpriteAnimation;
import com.sidescroller.game.TypeOfGameObject;
import com.sidescroller.objects.GameShape;
import com.sidescroller.objects.RubeSprite;

public class Granade implements CollisionListener, Draw
{
	private final long id;
	private GameShape gameShape;
	private SpriteAnimation explosion;
	private Texture explosionTexture;
	private float radious;

	public Granade(final long id, Vector2 position, Texture explosionTexture, Texture granadeTexture) {
		this.id = id;
		this.explosionTexture = explosionTexture;

		radious = 0.1f;
		createBody(position, granadeTexture);
	}

	private void createBody(Vector2 position, Texture granadeTexture){
		Shape fixtureShape = new CircleShape();
		fixtureShape.setRadius(radious);

		FixtureDef fixtureDef = new FixtureDef();
		fixtureDef.restitution = 0.2f;
		fixtureDef.shape = fixtureShape;
		fixtureDef.density = 1f;
		fixtureDef.friction = 1f;

		BodyDef bodyDef = new BodyDef();
		bodyDef.bullet = true;
		bodyDef.type = BodyDef.BodyType.DynamicBody;
		bodyDef.position.y = position.y;
		bodyDef.position.x = position.x;

		Body body;
		body = SideScrollerGameV2.getCurrentMap().createBody(bodyDef);

		body.createFixture(fixtureDef);

		RubeImage rubeImage = new RubeImage();
		rubeImage.width = radious * 2;
		rubeImage.height = radious * 2;
		rubeImage.renderOrder = 1;
		Array<RubeSprite> rubeSprites = new Array<RubeSprite>(1);
		rubeSprites.add(new RubeSprite(rubeImage, granadeTexture));

		gameShape = new GameShape(id, body, rubeSprites);
		SideScrollerGameV2.getCurrentMap().addDrawObject(gameShape);
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
			if ((contact.getFixtureA().getBody().equals(gameShape.getBody()) && !contact.getFixtureB().isSensor()) ||
				(contact.getFixtureB().getBody().equals(gameShape.getBody()) && !contact.getFixtureA().isSensor())) {
				Vector2 bodyPos = gameShape.getBody().getPosition();
				Vector2 explosionPos = new Vector2(bodyPos.x - radious, bodyPos.y - radious);
				explosion =
						new SpriteAnimation(5, 8, 6, explosionPos, new Vector2(radious * 2, radious * 2), 0, explosionTexture);
				SideScrollerGameV2.getCurrentMap().removeBody(gameShape.getBody());
				SideScrollerGameV2.getCurrentMap().removeDrawObject(gameShape);
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
	 System.out.println(explosion);
	 if (explosion != null){
		 explosion.draw(batch, layer);
		 if (explosion.isDone()){
			 SideScrollerGameV2.getCurrentMap().removeDrawObject(this);
			 SideScrollerGameV2.getCurrentMap().removeCollisionListener(this);
		 }
	 }
 }

	/**
	 * returns the individual iD for the specific object.
	 * @return int iD
	 */
	public long getId(){return id;}

	/**
	 * Returns wich type of gameobject this specific object is.
	 * @return The type of gameobject
	 */
	public TypeOfGameObject getTypeOfGameObject(){
		return TypeOfGameObject.OTHER;
	}

	public GameShape getGameShape() {return gameShape;}
}
