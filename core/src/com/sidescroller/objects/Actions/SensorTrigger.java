package com.sidescroller.objects.Actions;

import com.badlogic.gdx.physics.box2d.Contact;
import com.sidescroller.game.CollisionListener;
import com.sidescroller.game.GameObject;
import com.sidescroller.game.TypeOfGameObject;
import com.sidescroller.objects.Shape;

public class SensorTrigger extends Trigger implements CollisionListener
{
    private Shape shape;
    private boolean isTriggered;

    public SensorTrigger(long iD, int targetActionID, Shape shape) {
        super(iD, targetActionID);
        this.shape = shape;
        shape.getBody().setUserData(this);
    }

    /**
     * Handles event that needs to happen when contact between objects begins. Do note that this function
     * is called every time any contact occurs, thus each object implementing this interface must check if they
     * are part of the contact or not.
     * @param contact A object containing the two bodies and fixtures that made contact. It also contains collisiondata
     *                such as point of contact and so on.
     */
    public void beginContact(Contact contact){
        checkForPlayerCollision(contact);
    }

    /**
     * Handles event that needs to happen when contact between objects ends. Do note that this function
     * is called every time any contact occurs, thus each object implementing this interface must check if they
     * are part of the contact or not.
     * @param contact A object containing the two bodies and fixtures that made contact. It also contains collisiondata
     *                such as point of contact and so on.
     */
    public void endContact(Contact contact){
        checkForPlayerCollision(contact);
    }

    /**
     * Checks if this objects collides with a player, if so then sets triggered to true, otherwise false.
     * @param contact
     */
    private void checkForPlayerCollision(Contact contact){
        TypeOfGameObject fixA;
        TypeOfGameObject fixB;
        try{
            fixA = ((GameObject) contact.getFixtureA().getBody().getUserData()).getTypeOfGameObject();
            fixB = ((GameObject) contact.getFixtureB().getBody().getUserData()).getTypeOfGameObject();
        }
        catch (ClassCastException e){
            fixA = null;
            fixB = null;
        }

        isTriggered = (fixA == TypeOfGameObject.PLAYER || fixB == TypeOfGameObject.PLAYER);
    }

	/**
	 * Changes the userdata of the body belonging to the shape from 'this' to the actual shape. This is done so that
     * the JVM Garbagecollector removes 'this' (the garbagecollector removes objects with no references).
     */
    public void destroyTrigger(){
        shape.getBody().setUserData(shape);
    }

    public boolean hasTriggered(){
        return isTriggered;
    }

    public TypeOfGameObject getTypeOfGameObject(){
        return TypeOfGameObject.SHAPE;
    }
}
