package com.sidescroller.objects.actions;

import com.sidescroller.game.GameObject;
import com.sidescroller.game.TypeOfGameObject;

/**
 * Abstract class that needs to be implemented by any object that wants to be added to the Map as a action.
 */
public abstract class Action implements GameObject
{
    protected int actionID;
    protected long id;

	/**
     * Used to init values belonging to this superclass.
     * @param actionID The id of the action used in the actionmanager.
     * @param id The id of this object, used when checking collisions etc.
     */
    protected Action(final long id, final int actionID) {
        this.actionID = actionID;
        this.id = id;
    }

    /**
     *Function that is called when the designated trigger/triggers (with the same id as this action) is triggered.
     */
    public abstract void act();

	/**
	 * Used when removing a Action. The action performes the necessary operation before being removed.
     */
    public abstract void destroyAction();

    /**
     * Returns the actions id.
     * @return The id of the action.
     */
    public int getActionID(){
	return actionID;
    }

    /**
     * returns the individual id for the specific object.
     * @return int id
     */
    public long getId(){
        return id;
    }

    /**
     * Returns wich type of gameobject this specific object is.
     * @return The type of gameobject
     */
    public TypeOfGameObject getTypeOfGameObject(){
        return TypeOfGameObject.ACTION;
    }

    /**
     * Overridden version of Equals that ensures that both object pointers are the exact same instantiation of
     * its class.
     * @param obj The object to compare to.
     * @return True if they are equal, else false.
     */
    @Override
    public boolean equals(Object obj) {
        try{
            GameObject gameObject = (GameObject) obj;
            return gameObject.getId() == id;
        }
        catch (Exception e){
            return false;
        }
    }
}