package com.sidescroller.game;

/**
 * All objects that will be added in any of the gameworld lists (the lists contained in the map) or in the physics 'World'
 * must implement this interface. The iD of each object is used to safely remove items while not iterating
 * through a list of any kind.
 */
public interface GameObject
{
    /**
     * returns the individual iD for the specific object.
     * @return int iD
     */
    public long getiD();

    /**
     * Returns wich type of gameobject this specific object is.
     * @return The type of gameobject
     */
    public TypeOfGameObject getTypeOfGameObject();
}
