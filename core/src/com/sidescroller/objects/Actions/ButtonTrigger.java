package com.sidescroller.objects.Actions;

import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.Sprite;
import com.badlogic.gdx.utils.Array;
import com.sidescroller.game.InteractGameObject;
import com.sidescroller.game.TypeOfGameObject;
import com.sidescroller.objects.RubeSprite;
import com.sidescroller.objects.Shape;
import com.sidescroller.player.Player;

/**
 * Creates a trigger that triggers when a player collides with it and presses the 'Interact' key (default 'E')
 */
public class ButtonTrigger extends Trigger implements InteractGameObject
{
	private Shape shape;
	private boolean hasTriggered;

	public ButtonTrigger(long iD, int targetActionID) {
		super(iD, targetActionID);
	}

	/**
	 * Creates a 'ButtonTrigger'.
	 * @param iD The unique id of the object.
	 * @param targetActionID The id of the target action/actions.
	 * @param shape The shape of the button.
     */
	public ButtonTrigger(long iD, int targetActionID, Shape shape) {
		super(iD, targetActionID);
		this.shape = shape;
		shape.getBody().setUserData(this);
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
	 * @param player The player that interacts with the object.
	 */
	public void startInteract(Player player){
		hasTriggered = !hasTriggered;
		if (shape != null) {
			if (shape.getRubeSprites() != null) {
				Array<RubeSprite> rubeSprites = shape.getRubeSprites();
				for (RubeSprite rubeSprite : rubeSprites) {
					rubeSprite.getSprite().flip(true, false);
				}
			}
		}
	}

	/**
	 * Function called when a interaction ends. A interaction ends when the player releases the key mapped to interact with
	 * other objects.
	 * @param player
	 */
	public void endInteract(Player player){}

	/**
	 * Changes the userdata of the body belonging to the shape from 'this' to the actual shape. This is done so that
     * the JVM Garbagecollector removes 'this' (the garbagecollector removes objects with no references).
     */
	public void destroyTrigger(){
		shape.getBody().setUserData(shape);
	}

	@Override
	public TypeOfGameObject getTypeOfGameObject(){
		return TypeOfGameObject.INTERACTOBJECT;
	}
}
