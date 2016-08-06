package com.sidescroller.objects.Turret;

import com.badlogic.gdx.Input;
import com.badlogic.gdx.physics.box2d.joints.RevoluteJoint;
import com.sidescroller.game.Direction;
import com.sidescroller.game.InputListener;
import com.sidescroller.game.InteractGameObject;
import com.sidescroller.game.TypeOfGameObject;
import com.sidescroller.objects.GameShape;
import com.sidescroller.player.Player;

/**
 * A subclass to turret that enables players to control the turret.
 */
public class PlayerTurret extends Turret implements InteractGameObject, InputListener
{
    private int leftKey;
    private int rightKey;
    private int upKey;
    private boolean isActivated;

	/**
     * Creates a turret that players can control.
     * @param id The id of the turret.
     * @param barrel The gameShape representing the barrel.
     * @param turretBase The gameShape representing the base of the turret.
     * @param barrelJoint The joint joining the barrel and the turretbase together, needs to have motor enabled and must hav
     *                    a set speed/torque.
     */
    public PlayerTurret(final long id, final GameShape barrel, final GameShape turretBase, final RevoluteJoint barrelJoint) {
        super(id, barrel, turretBase, barrelJoint);
        turretBase.getBody().setUserData(this);
        barrel.getBody().setUserData(this);
        isActivated = false;
    }

    /**
     * Function called when a interaction starts. A interaction is started when the player presses the key mapped to interact
     * with other objects.
     * @param player The player that interacts with the object.
     */
    public void startInteract(Player player){
        isActivated = !isActivated;
        if (isActivated) {
            player.setLeftKey(false);
            player.setRightKey(false);
            player.setUpKey(false);

            upKey = player.getUpKey();
            leftKey = player.getLeftKey();
            rightKey = player.getRightKey();
        }
        else{
            player.setLeftKey(true);
            player.setRightKey(true);
            player.setUpKey(true);

            upKey = Input.Keys.UNKNOWN;
            leftKey = Input.Keys.UNKNOWN;
            rightKey = Input.Keys.UNKNOWN;
        }
    }

    /**
     * Function called when a interaction ends. A interaction ends when the player releases the key mapped to interact with
     * other objects.
     * @param player
     */
    public void endInteract(Player player){}

    /** Called when a key was pressed
     *
     * @param keycode one of the constants in {@link Keys}
     * @return whether the input was processed */
    public void keyDown(int keycode){
        if (keycode == leftKey){
            rotateBarrel(Direction.LEFT);
        }
        else if (keycode == rightKey){
            rotateBarrel(Direction.RIGHT);
        }
        else if (keycode == upKey){
            shoot(10);
        }
    }

    /** Called when a key was released
     *
     * @param keycode one of the constants in {@link Keys}
     * @return whether the input was processed */
    public void keyUp (int keycode){
        if (keycode == leftKey || keycode == rightKey){
            super.rotateBarrel(Direction.NONE);
        }
    }

    /**
   	 * Returns wich type of gameobject this specific object is.
   	 * @return The type of gameobject
   	 */
   	public TypeOfGameObject getTypeOfGameObject(){
   		return TypeOfGameObject.INTERACTOBJECT;
   	}
}