package com.sidescroller.game;

import com.sidescroller.Character.GameCharacter;

/**
 * Interface that should be implemented by any object that should be able to interact with a player.
 */
public interface InteractGameObject extends GameObject
{
    /**
     * Function called when a interaction starts. A interaction is started when the player presses the key mapped to interact
     * with other objects.
	 * @param object: Can only be a GameCharacter or a subclass of it. Use the 'GameObject.getTypeOfGameObject' to check
	 *              for specific types then cast to said type (safe casting).* @param character The character that interacts with the object.
     */
    void startInteract(GameObject object);

    /**
     * Function called when a interaction ends. A interaction ends when the player releases the key mapped to interact with
     * other objects.
     * @param object: Can only be a GameCharacter or a subclass of it. Use the 'GameObject.getTypeOfGameObject' to check
	 *              for specific types then cast to said type (safe casting).
     */
    void endInteract(GameObject object);
}
