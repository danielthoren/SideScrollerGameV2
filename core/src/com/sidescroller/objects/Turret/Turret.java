package com.sidescroller.objects.Turret;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.physics.box2d.*;
import com.badlogic.gdx.physics.box2d.joints.RevoluteJoint;
import com.sidescroller.game.Direction;
import com.sidescroller.game.GameObject;
import com.sidescroller.game.SideScrollerGameV2;
import com.sidescroller.game.SpriteAnimationObject;
import com.sidescroller.game.TypeOfGameObject;
import com.sidescroller.objects.*;

/**
 * Basic turret object
 */
public class Turret implements GameObject {

	protected GameShape barrel;
	protected GameShape turretBase;
	protected RevoluteJoint barrelJoint;
	protected Texture granadeTexture;
	protected Texture explosionTexture;
	private Texture barrelSmokeSpriteTexture;
	private int barrelSmokeRows;
	private int barrelSmokeColumns;
	private float barrelSmokeTime;
	protected float motorSpeed;
	private float barrelLength;
	protected float force;
	private final long id;

	/**
	 * Default contructor, never supposed to be used!!
	 */
	public Turret() {
		id = -1;
	}

	/**
	 * Creates a basic turret.
	 * @param id The id of the turret.
	 * @param barrel The gameShape representing the barrel.
	 * @param turretBase The gameShape representing the base of the turret.
	 * @param barrelJoint The joint joining the turretbase and the barrel to eachother. Must have motor enabled and a set
	 *                    speed/tourque.
	 */
	public Turret(long id, GameShape barrel, GameShape turretBase, RevoluteJoint barrelJoint) {
		this.barrel = barrel;
		this.barrelJoint = barrelJoint;
		this.turretBase = turretBase;
		this.id = id;

		//@TODO Request textures from assetmanager when it is created, saving memmory and loadingtime.
		granadeTexture = new Texture(Gdx.files.internal("circler.png"));
		explosionTexture = new Texture(Gdx.files.internal("explosion.png"));
		barrelSmokeSpriteTexture = new Texture(Gdx.files.internal("barrelSmoke.png"));

		barrelSmokeRows = 1;
		barrelSmokeColumns = 10;
		barrelSmokeTime = 0.5f;

		motorSpeed = barrelJoint.getMotorSpeed();
		barrel.getBody().setFixedRotation(false);

		barrel.getBody().setUserData(this);
		turretBase.getBody().setUserData(this);

		for (Fixture fixture : barrel.getBody().getFixtureList()){
			fixture.setDensity(0.5f);
			fixture.setSensor(true);
		}
		barrelJoint.setMotorSpeed(0);

		barrelLength = 0;
		//Getting the longest side of any of the images attached to the barrel. This will be the barrel itselfe since it is
		//the longest (In 99% of the cases anyway. There are no other easy ways to do this)
		for (RubeSprite rubeSprite : barrel.getRubeSprites()){
			if (rubeSprite.getRubeImage().height > barrelLength){
				barrelLength = rubeSprite.getRubeImage().height;
			}
			else if (rubeSprite.getRubeImage().width > barrelLength){
				barrelLength = rubeSprite.getRubeImage().width;
			}
		}
	}

	/**
	 * Rotates the barrel in the given direction using the speed specified by the field 'motorSpeed'.
	 * @param direction The direction in wich to rotate, if not set to either 'RIGHT' or 'LEFT' then the barrel will stop rotating.
	 */
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

	/**
	 * Shoots a granade from the turret and spawns a smoke effekt at the barrels opening.
	 * @param force The force with wich to shoot.
	 */
	protected void shoot(float force){
		//@TODO get drawlayer from SideScrollerGame class. Create specific playerlayer osv
		int layer = 1;
		float radious = 0.1f;
		Vector2 granadePos = new Vector2(barrel.getBody().getPosition().x + (float) (barrelLength * Math.cos((double) barrel.getBody().getAngle())),
										 barrel.getBody().getPosition().y + (float) (barrelLength * Math.sin((double) barrel.getBody().getAngle())));
		OnTouchGranade onTouchGranade = new OnTouchGranade(SideScrollerGameV2.getCurrentMap().getObjectID(), granadePos, explosionTexture, granadeTexture, layer, radious);
		//@Todo fix layers with maskbits!
		onTouchGranade.setGroudIndex((short)-1);

		Float barreleSmokeFrames = barrelSmokeRows * barrelSmokeColumns / barrelSmokeTime;
		SpriteAnimationObject barrelSmoke = new SpriteAnimationObject(barreleSmokeFrames.intValue(), barrelSmokeColumns,
																	  barrelSmokeRows, new Vector2(granadePos.x - (radious * 5), granadePos.y - (radious * 5)), new Vector2(radious * 10, radious * 10),
																	  barrel.getBody().getAngle() - (float) (Math.PI/2), barrelSmokeSpriteTexture, SideScrollerGameV2.getCurrentMap().getObjectID(), layer);
		barrelSmoke.setLoopAnimation(false);
		barrelSmoke.reverseAnimation(false);
		SideScrollerGameV2.getCurrentMap().addDrawObject(barrelSmoke);

		//Calculating the relative x and y forces when applying the angle of the barrelbody.
		Vector2 appliedForce = new Vector2((float) (force * Math.cos((double) barrel.getBody().getAngle())) , (float) (force * Math.sin((double) barrel.getBody().getAngle())));
		onTouchGranade.getGameShape().getBody().applyForce(appliedForce, onTouchGranade.getGameShape().getBody().getLocalCenter(), true);
		SideScrollerGameV2.getCurrentMap().addDrawObject(onTouchGranade);
		SideScrollerGameV2.getCurrentMap().addCollisionListener(onTouchGranade);
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
