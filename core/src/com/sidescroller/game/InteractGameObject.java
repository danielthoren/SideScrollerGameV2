package com.sidescroller.game;

import com.sidescroller.player.Player;

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
    public void startInteract(Player player);

    /**
     * Function called when a interaction ends. A interaction ends when the player releases the key mapped to interact with
     * other objects.
     * @param player
     */
    public void endInteract(Player player);
}
