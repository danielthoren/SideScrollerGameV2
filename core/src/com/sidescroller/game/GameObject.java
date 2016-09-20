package com.sidescroller.game;

/**
 * All objects that will be added in any of the gameworld lists (the lists contained in the Map) or in the physics 'World'
 * must implement this interface. The id of each object is used to safely remove items while not iterating
 * through a list of any kind.
 */
public interface GameObject
{
    /**
     * returns the individual id for the specific object.
     * @return int id
     */
    long getId();

    /**
     * Returns wich type of gameobject this specific object is.
     * @return The type of gameobject
     */
    TypeOfGameObject getTypeOfGameObject();
}
