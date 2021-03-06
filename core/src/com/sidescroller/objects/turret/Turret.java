package com.sidescroller.objects.turret;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.physics.box2d.Filter;
import com.badlogic.gdx.physics.box2d.Fixture;
import com.badlogic.gdx.physics.box2d.joints.RevoluteJoint;
import com.sidescroller.game.Direction;
import com.sidescroller.game.GameObject;
import com.sidescroller.game.SideScrollGameV2;
import com.sidescroller.game.SpriteAnimationObject;
import com.sidescroller.game.TypeOfGameObject;
import com.sidescroller.objects.*;
import javafx.geometry.Side;

/**
 * Basic turret object
 */
public class Turret implements GameObject {

	protected GameShape barrel;
	protected GameShape turretBase;
	protected RevoluteJoint barrelJoint;
	private SideScrollGameV2 sideScrollGameV2;
	private Filter granadeFilter;
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
	private static final String BARREL_SMOKE_TEXTURE_DEFAULT = "textures/barrelSmoke.png";
	private static final String EXPLOTION_TEXTURE_DEFAULT = "textures/explosion.png";
	private static final String GRANADE_TEXTURE_DEFAULT = "textures/circler.png";
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
	public Turret(long id, SideScrollGameV2 sideScrollGameV2, GameShape barrel, GameShape turretBase, RevoluteJoint barrelJoint) {
		this.barrel = barrel;
		this.sideScrollGameV2 = sideScrollGameV2;
		this.barrelJoint = barrelJoint;
		this.turretBase = turretBase;
		this.id = id;

		granadeFilter = new Filter();
		granadeFilter.categoryBits = SideScrollGameV2.ENVIROMENT_CATEGORY;
		granadeFilter.maskBits = SideScrollGameV2.ENVIROMENT_CATEGORY | SideScrollGameV2.ENEMY_CATEGORY | SideScrollGameV2.PLAYER_CATEGORY;

		barrelSmokeRows = BARREL_SMOKE_ROWS_DEFAULT;
		barrelSmokeColumns = BARREL_SMOKE_COLUMNS_DEFAULT;
		barrelSmokeTime = BARREL_SMOKE_TIME_DEFAULT;
		explosionColumns = EXPLOSION_TEXTURE_COLUMNS_DEFAULT;
		explosionRows = EXPLOSION_TEXTURE_ROWS_DEFAULT;
		granadeRadious = GRANADE_RADIOUS_DEFAULT;
		//Checking if default textures are loaded, if not then loading them
		if (!sideScrollGameV2.getAssetManager().isLoaded(GRANADE_TEXTURE_DEFAULT)){
			sideScrollGameV2.getAssetManager().load(GRANADE_TEXTURE_DEFAULT, Texture.class);
			sideScrollGameV2.getAssetManager().update();
		}
		if (!sideScrollGameV2.getAssetManager().isLoaded(EXPLOTION_TEXTURE_DEFAULT)){
			sideScrollGameV2.getAssetManager().load(EXPLOTION_TEXTURE_DEFAULT, Texture.class);
			sideScrollGameV2.getAssetManager().update();
		}
		if (!sideScrollGameV2.getAssetManager().isLoaded(BARREL_SMOKE_TEXTURE_DEFAULT)){
			sideScrollGameV2.getAssetManager().load(BARREL_SMOKE_TEXTURE_DEFAULT, Texture.class);
			sideScrollGameV2.getAssetManager().update();
		}
		sideScrollGameV2.getAssetManager().finishLoading();

		barrelSmokeSpriteTexture = sideScrollGameV2.getAssetManager().get(BARREL_SMOKE_TEXTURE_DEFAULT);
		granadeTexture = sideScrollGameV2.getAssetManager().get(GRANADE_TEXTURE_DEFAULT);
		explosionTexture = sideScrollGameV2.getAssetManager().get(EXPLOTION_TEXTURE_DEFAULT);

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
		int layer = SideScrollGameV2.PLAYER_DRAW_LAYER;
		Vector2 granadePos = new Vector2(barrel.getBody().getPosition().x + (float) (barrelLength * Math.cos(barrel.getBody().getAngle())),
										 barrel.getBody().getPosition().y + (float) (barrelLength * Math.sin(barrel.getBody().getAngle())));
		OnTouchGranade onTouchGranade = new OnTouchGranade(sideScrollGameV2.getCurrentMap().getObjectID(), sideScrollGameV2, granadePos,
														   explosionTexture, explosionColumns, explosionRows, granadeTexture, layer,
														   granadeRadious, granadeFilter);

		Float barreleSmokeFrames = barrelSmokeRows * barrelSmokeColumns / barrelSmokeTime;
		Vector2 barrelSmokeSize = new Vector2(granadeRadious * 10, granadeRadious * 10);
		Vector2 barrelSmokePos = new Vector2(granadePos.x - (granadeRadious * 5), granadePos.y - (granadeRadious * 5));
		SpriteAnimationObject barrelSmoke = new SpriteAnimationObject(barreleSmokeFrames.intValue(), barrelSmokeColumns, barrelSmokeRows, barrelSmokePos, barrelSmokeSize,
																	  barrel.getBody().getAngle() - (float) (Math.PI/2),
																	  barrelSmokeSpriteTexture, sideScrollGameV2.getCurrentMap().getObjectID(),
																	  layer, sideScrollGameV2);
		barrelSmoke.setLoopAnimation(false);
		barrelSmoke.reverseAnimation(false);
		sideScrollGameV2.getCurrentMap().addDrawObject(barrelSmoke);

		//Calculating the relative x and y forces when applying the angle of the barrelbody.
		Vector2 appliedForce = new Vector2((float) (force * Math.cos(barrel.getBody().getAngle())) , (float) (force * Math.sin(barrel.getBody().getAngle())));
		onTouchGranade.getGranadeShape().getBody().applyForce(appliedForce, onTouchGranade.getGranadeShape().getBody().getLocalCenter(), true);
		sideScrollGameV2.getCurrentMap().addDrawObject(onTouchGranade);
		sideScrollGameV2.getCurrentMap().addCollisionListener(onTouchGranade);
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
