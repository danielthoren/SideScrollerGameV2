package com.sidescroller.objects.turret;

import com.badlogic.gdx.physics.box2d.joints.RevoluteJoint;
import com.sidescroller.Character.GameCharacter;
import com.sidescroller.game.*;
import com.sidescroller.game.Update;
import com.sidescroller.objects.GameShape;

/**
 * A subclass to turret that enables players to control the turret.
 */
public class PlayerTurret extends Turret implements InteractGameObject, Update
{
    private boolean isActivated;
	private boolean hasReset;
	private GameCharacter character;

	private boolean upPressed, interactPressed;

	/**
     * Creates a turret that players can control.
     * @param id The id of the turret.
     * @param barrel The gameShape representing the barrel.
     * @param turretBase The gameShape representing the base of the turret.
     * @param barrelJoint The joint joining the barrel and the turretbase together, needs to have motor enabled and must hav
     *                    a set speed/torque.
     */
    public PlayerTurret(final long id, SideScrollGameV2 sideScrollGameV2, final GameShape barrel, final GameShape turretBase, final RevoluteJoint barrelJoint) {
        super(id, sideScrollGameV2, barrel, turretBase, barrelJoint);
        turretBase.getBody().setUserData(this);
        barrel.getBody().setUserData(this);
		character = null;
        isActivated = false;
		hasReset = true;
		upPressed = false;
		interactPressed = false;
    }

    /**
     * Function called when a interaction starts. A interaction is started when the player presses the key mapped to interact
     * with other objects.
	 * @param object: Can only be a GameCharacter or a subclass of it. Use the 'GameObject.getTypeOfGameObject' to check
	 *              for specific types then cast to said type (safe casting).
     */
    public void startInteract(GameObject object){
		isActivated = !isActivated;
		if (isActivated && hasReset && character == null) {
			character = (GameCharacter) object;
			character.setDisableLeftKey(true);
			character.setDisableRightKey(true);
			character.setDisableUpKey(true);
			character.setDisableInteractKey(true);

			character.setIsRunning(false);
		} else if (character != null) {
			character.setDisableLeftKey(false);
			character.setDisableRightKey(false);
			character.setDisableUpKey(false);
			character.setDisableInteractKey(false);

			this.character = null;
			hasReset = false;
		}
    }

    /**
     * Function called when a interaction ends. A interaction ends when the player releases the key mapped to interact with
     * other objects.
	 * @param object: Can only be a GameCharacter or a subclass of it. Use the 'GameObject.getTypeOfGameObject' to check
	 *              for specific types then cast to said type (safe casting).
     */
    public void endInteract(GameObject object){}

	/**
	 * checks for the characters keys to be pressed and taking appropriate action.
	 */
	private void checkKeys(){
		//Resetting toggle-booleans if keys are not pressed anymore.
		if (!character.isUpKey()){upPressed = false;}
		if (!character.isInteractKey()){interactPressed = false;}

		if (character.isLeftKey()){
			rotateBarrel(Direction.LEFT);
		}
		else if (character.isRightKey()){
			rotateBarrel(Direction.RIGHT);
		}
		else{
			rotateBarrel(Direction.NONE);
		}

		if (character.isUpKey() && !upPressed){
			upPressed = true;
			shoot(10);
		}
		if (character.isInteractKey() && !interactPressed){
			interactPressed = true;
			startInteract(character);
		}
	}

	/**
	 * The function that updates the object every frame
	 */
	public void update(){
		hasReset = true;
		if(character != null) {
			checkKeys();
		}
	}

    /**
   	 * Returns wich type of gameobject this specific object is.
   	 * @return The type of gameobject
   	 */
    @Override
   	public TypeOfGameObject getTypeOfGameObject(){
   		return TypeOfGameObject.INTERACTOBJECT;
   	}
}
