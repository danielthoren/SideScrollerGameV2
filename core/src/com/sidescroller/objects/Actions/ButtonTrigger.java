package com.sidescroller.objects.Actions;

import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.Sprite;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.physics.box2d.Body;
import com.badlogic.gdx.utils.Array;
import com.sidescroller.game.Draw;
import com.sidescroller.game.InteractGameObject;
import com.sidescroller.game.SideScrollerGameV2;
import com.sidescroller.game.TypeOfGameObject;
import com.sidescroller.objects.RubeSprite;
import com.sidescroller.objects.Shape;
import com.sidescroller.player.Player;

public class ButtonTrigger extends Trigger implements InteractGameObject
{
	private TypeOfGameObject typeOfGameObject = TypeOfGameObject.INTERACTOBJECT;
	private Shape shape;
	private boolean hasTriggered;
	private Sprite sprite;

	public ButtonTrigger(long iD, int targetActionID) {
		super(iD, targetActionID);
	}

	public ButtonTrigger(long iD, int targetActionID, Shape shape) {
		super(iD, targetActionID);
		this.shape = shape;
		shape.getBody().setUserData(this);
		hasTriggered = false;
	}

	public ButtonTrigger(long iD, int targetActionID, Shape shape, Texture texture) {
		super(iD, targetActionID);
		this.shape = shape;
		sprite = new Sprite(texture);
		shape.getBody().setUserData(this);
		hasTriggered = false;
	}

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
		Array<RubeSprite> rubeSprites = shape.getRubeSprites();
		for (RubeSprite rubeSprite : rubeSprites){
			rubeSprite.getSprite().flip(true, false);
		}
	}

	/**
	 * Function called when a interaction ends. A interaction ends when the player releases the key mapped to interact with
	 * other objects.
	 * @param player
	 */
	public void endInteract(Player player){
	}

	public TypeOfGameObject getTypeOfGameObject(){
		return typeOfGameObject;
	}
}
