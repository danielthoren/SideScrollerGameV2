package com.sidescroller.objects;

import com.badlogic.gdx.Input;
import com.badlogic.gdx.physics.box2d.joints.RevoluteJoint;
import com.sidescroller.game.Direction;
import com.sidescroller.game.InputListener;
import com.sidescroller.game.InteractGameObject;
import com.sidescroller.game.TypeOfGameObject;
import com.sidescroller.player.Player;

public class PlayerTurret extends Turret implements InteractGameObject, InputListener
{
    private int leftKey;
    private int rightKey;
    private boolean isActivated;

    public PlayerTurret(final long id, final Shape barrel, final Shape turretBase, final RevoluteJoint barrelJoint) {
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

            leftKey = player.getLeftKey();
            rightKey = player.getRightKey();
        }
        else{
            player.setLeftKey(true);
            player.setRightKey(true);
            player.setUpKey(true);

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
            super.rotateBarrel(Direction.LEFT);
        }
        else if (keycode == rightKey){
            super.rotateBarrel(Direction.RIGHT);
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
