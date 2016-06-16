package com.sidescroller.game;

import com.badlogic.gdx.physics.box2d.Contact;

/**
 * Interface specifying functions that all objects that want to listen to collisiondata needs to implement.
 */
public interface CollisionListener extends GameObject
{
    /**
     * Handles event that needs to happen when contact between objects begins. Do note that this function
     * is called every time any contact occurs, thus each object implementing this interface must check if they
     * are part of the contact or not.
     * @param contact A object containing the two bodies and fixtures that made contact. It also contains collisiondata
     *                such as point of contact and so on.
     */
    public abstract void beginContact(Contact contact);

    /**
     * Handles event that needs to happen when contact between objects ends. Do note that this function
     * is called every time any contact occurs, thus each object implementing this interface must check if they
     * are part of the contact or not.
     * @param contact A object containing the two bodies and fixtures that made contact. It also contains collisiondata
     *                such as point of contact and so on.
     */
    public abstract void endContact(Contact contact);
}
