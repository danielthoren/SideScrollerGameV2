package com.sidescroller.game;

/**
 * Enum holding all of the types of gameobjects that is of interest when collision occurs. This is used to determine wich action to take when colliding
 * with another gameobject (this is based on the premesis that all bodies in the 'world' have userdata that implements
 * the interface 'GameObject')
 */
public enum TypeOfGameObject
{
    SHAPE, PLAYER, INTERACTOBJECT, DOOR, OTHER
}
