package com.sidescroller.objects;

import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.physics.box2d.*;
import com.sidescroller.game.Draw;
import com.sidescroller.game.TypeOfGameObject;
import com.sidescroller.game.Update;

/**
 * Created by daniel on 2016-07-24.
 */
public class Turret implements Update {

	private Shape barrel;
	private Shape turretBase;
	private final long id;

	public Turret(long id, Shape barrel, Shape turretBase) {
		this.barrel = barrel;
		this.turretBase = turretBase;
		this.id = id;

		turretBase.getBody().setType(BodyDef.BodyType.DynamicBody);
		barrel.getBody().setType(BodyDef.BodyType.DynamicBody);
		barrel.getBody().setFixedRotation(false);
	}

	public void update(){

	}

	public TypeOfGameObject getTypeOfGameObject(){
		return TypeOfGameObject.OTHER;
	}

	public long getiD(){
		return id;
	}
}
