package com.sidescroller.objects.actions;

import com.badlogic.gdx.utils.Array;
import com.sidescroller.game.GameObject;
import com.sidescroller.game.InteractGameObject;
import com.sidescroller.game.TypeOfGameObject;
import com.sidescroller.objects.GameShape;
import com.sidescroller.objects.RubeSprite;
import com.sidescroller.Character.Player;

/**
 * Creates a trigger that triggers when a player collides with it and presses the 'Interact' key (default 'E')
 */
public class ButtonTrigger extends Trigger implements InteractGameObject
{
	private GameShape gameShape;
	private boolean hasTriggered;

	/**
	 * Default constructor.
	 * @param iD The id of this object.
	 * @param targetActionID The id of the target action used in actionmanager.
	 */
	protected ButtonTrigger(long iD, int targetActionID) {
		super(iD, targetActionID);
		gameShape = null;
	}

	/**
	 * Creates a 'ButtonTrigger'.
	 * @param iD The unique id of the object.
	 * @param targetActionID The id of the target action/actions.
	 * @param gameShape The gameShape of the button.
     */
	public ButtonTrigger(long iD, int targetActionID, GameShape gameShape) {
		super(iD, targetActionID);
		this.gameShape = gameShape;
		gameShape.getBody().setUserData(this);
		hasTriggered = false;
	}

	/**
	 * Used to check if the trigger has triggered.
	 * @return True if triggered, else false.
     */
	public boolean hasTriggered(){
		return hasTriggered;
	}

	/**
	 * Function called when a interaction starts. A interaction is started when the player presses the key mapped to interact
	 * with other objects.
	 * @param object: Can only be a GameCharacter or a subclass of it. Use the 'GameObject.getTypeOfGameObject' to check
	 *              for specific types then cast to said type (safe casting).
	 */
	public void startInteract(GameObject object){
		hasTriggered = !hasTriggered;
		if (gameShape != null) {
			if (gameShape.getRubeSprites() != null) {
				Array<RubeSprite> rubeSprites = gameShape.getRubeSprites();
				for (RubeSprite rubeSprite : rubeSprites) {
					rubeSprite.getSprite().flip(true, false);
				}
			}
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
	 * Does nothing since the lever/button should still be pressable.
     */
	public void destroyTrigger(){}

	@Override
	public TypeOfGameObject getTypeOfGameObject(){
		return TypeOfGameObject.INTERACTOBJECT;
	}
}
