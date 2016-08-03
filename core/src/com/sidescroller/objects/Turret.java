package com.sidescroller.objects;

import com.badlogic.gdx.physics.box2d.*;
import com.badlogic.gdx.physics.box2d.joints.RevoluteJoint;
import com.sidescroller.game.Direction;
import com.sidescroller.game.GameObject;
import com.sidescroller.game.TypeOfGameObject;

/**
 * Created by daniel on 2016-07-24.
 */
public class Turret implements GameObject {

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
		barrel.getBody().setFixedRotation(false);

		barrel.getBody().setUserData(this);
		turretBase.getBody().setUserData(this);

		for (Fixture fixture : barrel.getBody().getFixtureList()){
			fixture.setDensity(0.5f);
			fixture.setSensor(true);
		}
		barrelJoint.setMotorSpeed(0);
	}

	protected void rotateBarrel(Direction direction){
		if (direction == Direction.RIGHT) {
			barrelJoint.setMotorSpeed(Math.abs(motorSpeed));
		}
		else if (direction == Direction.LEFT) {
			barrelJoint.setMotorSpeed(-Math.abs(motorSpeed));
		}
		else{
			barrelJoint.setMotorSpeed(0);
		}
	}

	protected void shoot(){
	}

	public long getId(){
		return id;
	}

	/**
	 * Returns wich type of gameobject this specific object is.
	 * @return The type of gameobject
	 */
	public TypeOfGameObject getTypeOfGameObject(){
		return TypeOfGameObject.OTHER;
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
			return gameObject.getId() == this.getId();
		}
		catch (Exception e){
			return false;
		}
	}
}
