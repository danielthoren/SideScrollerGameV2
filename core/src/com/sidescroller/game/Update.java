package com.sidescroller.game;

/**
 * Every object that needs to be updated needs to implement this interface
 */
public interface Update extends GameObject
{
    /**
     * The function that updates the object every frame
     */
    void update();
}
