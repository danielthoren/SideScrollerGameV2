package com.sidescroller.objects.turret;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.math.Vector2;
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
	private int explosionRows;
	private int explosionColumns;
	private float barrelSmokeTime;
	protected float motorSpeed;
	private float barrelLength;
	protected float force;
	protected float granadeRadious;
	private final long id;

	//@TODO Load texture from assetmanager when it is created.
	private static final Texture BARREL_SMOKE_TEXTURE_DEFAULT = new Texture(Gdx.files.internal("barrelSmoke.png"));
	private static final Texture EXPLOTION_TEXTURE_DEFAULT = new Texture(Gdx.files.internal("explosion.png"));
	private static final Texture GRANADE_TEXTURE_DEFAULT = new Texture(Gdx.files.internal("circler.png"));
	private static final int EXPLOSION_TEXTURE_ROWS_DEFAULT = 6;
	private static final int EXPLOSION_TEXTURE_COLUMNS_DEFAULT = 8;
	private static final float BARREL_SMOKE_TIME_DEFAULT = 0.5f;
	private static final float GRANADE_RADIOUS_DEFAULT = 0.1f;
	private static final int BARREL_SMOKE_COLUMNS_DEFAULT = 10;
	private static final int BARREL_SMOKE_ROWS_DEFAULT = 1;

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

		barrelSmokeSpriteTexture = BARREL_SMOKE_TEXTURE_DEFAULT;

		barrelSmokeRows = BARREL_SMOKE_ROWS_DEFAULT;
		barrelSmokeColumns = BARREL_SMOKE_COLUMNS_DEFAULT;
		barrelSmokeTime = BARREL_SMOKE_TIME_DEFAULT;
		barrelSmokeSpriteTexture = BARREL_SMOKE_TEXTURE_DEFAULT;
		granadeTexture = GRANADE_TEXTURE_DEFAULT;
		explosionTexture = EXPLOTION_TEXTURE_DEFAULT;
		explosionColumns = EXPLOSION_TEXTURE_COLUMNS_DEFAULT;
		explosionRows = EXPLOSION_TEXTURE_ROWS_DEFAULT;
		granadeRadious = GRANADE_RADIOUS_DEFAULT;

		motorSpeed = barrelJoint.getMotorSpeed();
		barrel.getBody().setFixedRotation(false);

		barrel.getBody().setUserData(this);
		turretBase.getBody().setUserData(this);

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
		Vector2 granadePos = new Vector2(barrel.getBody().getPosition().x + (float) (barrelLength * Math.cos(barrel.getBody().getAngle())),
										 barrel.getBody().getPosition().y + (float) (barrelLength * Math.sin(barrel.getBody().getAngle())));
		OnTouchGranade onTouchGranade = new OnTouchGranade(SideScrollerGameV2.getCurrentMap().getObjectID(), granadePos, explosionTexture, explosionColumns, explosionRows, granadeTexture, layer, granadeRadious);
		//@Todo fix layers with maskbits!
		onTouchGranade.setGroudIndex((short)-1);

		Float barreleSmokeFrames = barrelSmokeRows * barrelSmokeColumns / barrelSmokeTime;
		SpriteAnimationObject barrelSmoke = new SpriteAnimationObject(barreleSmokeFrames.intValue(), barrelSmokeColumns,
																	  barrelSmokeRows, new Vector2(granadePos.x - (granadeRadious * 5), granadePos.y - (granadeRadious * 5)), new Vector2(granadeRadious * 10, granadeRadious * 10),
																	  barrel.getBody().getAngle() - (float) (Math.PI/2), barrelSmokeSpriteTexture, SideScrollerGameV2.getCurrentMap().getObjectID(), layer);
		barrelSmoke.setLoopAnimation(false);
		barrelSmoke.reverseAnimation(false);
		SideScrollerGameV2.getCurrentMap().addDrawObject(barrelSmoke);

		//Calculating the relative x and y forces when applying the angle of the barrelbody.
		Vector2 appliedForce = new Vector2((float) (force * Math.cos(barrel.getBody().getAngle())) , (float) (force * Math.sin(barrel.getBody().getAngle())));
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

	public void setBarrelSmokeSpriteTexture(final Texture barrelSmokeSpriteTexture) {this.barrelSmokeSpriteTexture = barrelSmokeSpriteTexture;}

	public void setBarrelSmokeRows(final int barrelSmokeRows) {this.barrelSmokeRows = barrelSmokeRows;}

	public void setBarrelSmokeColumns(final int barrelSmokeColumns) {this.barrelSmokeColumns = barrelSmokeColumns;}

	public void setBarrelSmokeTime(final float barrelSmokeTime) {this.barrelSmokeTime = barrelSmokeTime;}

	public int getExplosionRows() {return explosionRows;}

	public int getExplosionColumns() {return explosionColumns;}

	public Texture getExplosionTexture() {return explosionTexture;}

	public Texture getGranadeTexture() {return granadeTexture;}

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
			return gameObject.getId() == id;
		}
		catch (Exception e){
			return false;
		}
	}
}
