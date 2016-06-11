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
import com.sidescroller.game.SideScrollerGameV2;

public class DynamicCircle implements Draw
{
    private Body body;
    private Sprite sprite;
    private long iD;
    private float radious;


    public DynamicCircle(final long iD, World world, Vector2 position, float radious, float density, float friction, float restitution, Texture texture) {
	this.iD = iD;
	this.radious = radious;

	Sprite sprite = new Sprite(texture);
	sprite.setSize(radious*2, radious*2);
	sprite.setOrigin(radious,radious);
	this.sprite = sprite;

	createbody(world, position, radious, density, friction, restitution);
    }

    private void createbody(World world, Vector2 position, float radious, float density, float friction, float restitution){
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
	bodyDef.type = BodyDef.BodyType.DynamicBody;
	bodyDef.active = true;

	body = world.createBody(bodyDef);
	body.createFixture(fixtureDef);
    }

    public void draw(SpriteBatch batch){
	sprite.setPosition(body.getPosition().x/2, body.getPosition().y/2);
	sprite.setRotation(SideScrollerGameV2.radToDeg(body.getAngle()));
	sprite.draw(batch);
    }

    public long getId(){return iD; }
}
