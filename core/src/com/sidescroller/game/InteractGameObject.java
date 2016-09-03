package com.sidescroller.game;

import com.sidescroller.Character.Player;

/**
 * Interface that should be implemented by any object that should be able to interact with a player.
 */
public interface InteractGameObject extends GameObject
{
    /**
     * Function called when a interaction starts. A interaction is started when the player presses the key mapped to interact
     * with other objects.
     * @param player The player that interacts with the object.
     */
    void startInteract(Player player);

    /**
     * Function called when a interaction ends. A interaction ends when the player releases the key mapped to interact with
     * other objects.
     * @param player the player that interacts with the object.
     */
    void endInteract(Player player);
}
