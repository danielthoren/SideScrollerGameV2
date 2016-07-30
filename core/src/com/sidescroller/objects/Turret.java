package com.sidescroller.objects;

import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.physics.box2d.*;
import com.badlogic.gdx.physics.box2d.joints.RevoluteJoint;
import com.sidescroller.game.Direction;
import com.sidescroller.game.Draw;
import com.sidescroller.game.GameObject;
import com.sidescroller.game.TypeOfGameObject;
import com.sidescroller.game.Update;

/**
 * Created by daniel on 2016-07-24.
 */
public class Turret {

	private Shape barrel;
	private Shape turretBase;
	private RevoluteJoint barrelJoint;
	private float motorSpeed;
	private final long id;

	/**
	 * Default contructor, never supposed to be used!!
	 */
	public Turret() {
		id = -1;
	}

	public Turret(long id, Shape barrel, Shape turretBase, RevoluteJoint barrelJoint) {
		this.barrel = barrel;
		this.barrelJoint = barrelJoint;
		this.turretBase = turretBase;
		this.id = id;
		motorSpeed = barrelJoint.getMotorSpeed();

		turretBase.getBody().setType(BodyDef.BodyType.DynamicBody);
		barrel.getBody().setType(BodyDef.BodyType.DynamicBody);
		barrel.getBody().setFixedRotation(false);

		barrel.getBody().setUserData(this);
		turretBase.getBody().setUserData(this);
	}

	public void rotateBarrel(Direction direction){
		if (direction == Direction.RIGHT) {
			barrelJoint.setMotorSpeed(-Math.abs(motorSpeed));
		}
		else if (direction == Direction.LEFT) {
			barrelJoint.setMotorSpeed(Math.abs(motorSpeed));
		}
		else{
			barrelJoint.setMotorSpeed(0);
		}
	}

	public TypeOfGameObject getTypeOfGameObject(){
		return TypeOfGameObject.OTHER;
	}

	public long getiD(){
		return id;
	}

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
			return gameObject.getiD() == this.getiD();
		}
		catch (Exception e){
			return false;
		}
	}
}
