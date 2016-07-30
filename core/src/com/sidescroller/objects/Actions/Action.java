package com.sidescroller.objects.Actions;

import com.sidescroller.game.GameObject;
import com.sidescroller.game.TypeOfGameObject;

/**
 * Abstract class that needs to be implemented by any object that wants to be added to the map as a action.
 */
public abstract class Action implements GameObject
{
    protected int actionID;
    protected long id;

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
     * returns the individual iD for the specific object.
     * @return int iD
     */
    public long getId(){
        return id;
    }

    /**
     * Returns wich type of gameobject this specific object is.
     * @return The type of gameobject
     */
    public TypeOfGameObject getTypeOfGameObject(){
        return TypeOfGameObject.OTHER;
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
            return gameObject.getId() == this.getId();
        }
        catch (Exception e){
            return false;
        }
    }
}