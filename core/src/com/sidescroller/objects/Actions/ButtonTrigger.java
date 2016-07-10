package com.sidescroller.objects.Actions;

import com.badlogic.gdx.physics.box2d.Body;
import com.sidescroller.game.InteractGameObject;
import com.sidescroller.game.TypeOfGameObject;
import com.sidescroller.objects.Shape;
import com.sidescroller.player.Player;

public class ButtonTrigger extends Trigger implements InteractGameObject
{
    private TypeOfGameObject typeOfGameObject = TypeOfGameObject.INTERACTOBJECT;
    private Shape shape;
    private boolean hasTriggered;

    public ButtonTrigger(Shape shape) {
	this.shape = shape;
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
	hasTriggered = true;
    }

    /**
     * Function called when a interaction ends. A interaction ends when the player releases the key mapped to interact with
     * other objects.
     * @param player
     */
    public void endInteract(Player player){
	hasTriggered = false;
    }


    public TypeOfGameObject getTypeOfGameObject(){
	return typeOfGameObject;
    }
}
