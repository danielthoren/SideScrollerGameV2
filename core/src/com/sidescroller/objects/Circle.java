package com.sidescroller.objects;

import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.Sprite;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.physics.box2d.Body;
import com.badlogic.gdx.physics.box2d.BodyDef;
import com.badlogic.gdx.physics.box2d.CircleShape;
import com.badlogic.gdx.physics.box2d.FixtureDef;
import com.badlogic.gdx.physics.box2d.World;
import com.sidescroller.game.Draw;
import com.sidescroller.game.GameObject;
import com.sidescroller.game.SideScrollerGameV2;

/**
 * Class creating a circlebody and drawing a texture on that body.
 */
public class Circle implements Draw
{
    private Body body;
    private Sprite sprite;
    private long iD;
    private float radious;

    /**
     * Creates an instance of circle
     * @param iD The id of the circle
     * @param world The world in wich to add the circle
     * @param position The position in the world at wich to add the circle
     * @param isStatic True if the body should be static, esle false
     * @param radious The radious of the circle
     * @param density The density of the circlebody
     * @param friction The friction of the circlebody
     * @param restitution The restitution of the circlebody
     * @param texture The texture of the circlebody
     */
    public Circle(final long iD, World world, Vector2 position, boolean isStatic, float radious, float density, float friction, float restitution, Texture texture) {
	this.iD = iD;
	this.radious = radious;

	Sprite sprite = new Sprite(texture);
	sprite.setSize(radious*2, radious*2);
	sprite.setOrigin(radious, radious);
	this.sprite = sprite;

	createbody(world, position, radious, density, friction, restitution, isStatic);
    }

    /**
     * Creates the body of the circle using the 'BodyEditorLoader'.
     * @param world The world in wich to create the body
     * @param position The position at wich to create the body
     * @param radious The radious of the body
     * @param density The density of the body
     * @param friction The friction of the body
     * @param restitution The restitution of the body
     * @param isStatic If true then the body will be static, otherwise dynamic
     */
    private void createbody(World world, Vector2 position, float radious, float density, float friction, float restitution, boolean isStatic){
	FixtureDef fixtureDef = new FixtureDef();
	CircleShape circleShape = new CircleShape();
	circleShape.setRadius(radious);

	fixtureDef.shape = circleShape;
	fixtureDef.density = density;
	fixtureDef.friction = friction;
	fixtureDef.restitution = restitution;

	BodyDef bodyDef = new BodyDef();
	bodyDef.position.set(position);
	bodyDef.allowSleep = true;
	if (isStatic){
	    bodyDef.type = BodyDef.BodyType.StaticBody;
	}
	else {
	    bodyDef.type = BodyDef.BodyType.DynamicBody;
	}
	bodyDef.active = true;

	body = world.createBody(bodyDef);
	body.createFixture(fixtureDef);
		body.setUserData(this);
    }

    public void draw(SpriteBatch batch, int layer){
	sprite.setPosition(body.getPosition().x - radious, body.getPosition().y - radious);
	sprite.setRotation(SideScrollerGameV2.radToDeg(body.getAngle()));
	sprite.draw(batch);
    }

    public long getId(){return iD;}

    /**
     * Overridden version of Equals that ensures that both object pointers are the exact same instantiation of
     * its class.
     * @param obj The object to compare to.
     * @return True if they are equal, else false.
     */
    @Override
    public boolean equals(Object obj) {
        try{
            GameObject gameObject = (GameObject) obj;
            return gameObject.getId() == this.getId();
        }
        catch (Exception e){
            return false;
        }
    }
}
