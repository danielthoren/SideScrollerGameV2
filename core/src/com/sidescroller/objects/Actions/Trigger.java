package com.sidescroller.objects.Actions;

import com.sidescroller.game.GameObject;
import com.sidescroller.game.TypeOfGameObject;

/**
 * Abstract class that any other object that wants to trigger actions needs to implement.
 */
public abstract class Trigger implements GameObject
{
    protected long iD;
    protected int targetActionID;

    /**
     * Default constructor of all trigger objects.
     * @param iD The idividual id of the trigger object.
     * @param targetActionID The target action id.
     */
    public Trigger(final long iD, final int targetActionID) {
        this.iD = iD;
        this.targetActionID = targetActionID;
    }

    /**
     * Returns true if the trigger has triggered.
     * @return True if triggered, else false.
     */
    public abstract boolean hasTriggered();

	/**
     * Gets the id of the target action.
     * @return The id.
     */
    public int getTargetActionID(){
	return targetActionID;
    }

	/**
	 * Called before the trigger is destroyed. This is done so that the trigger can perform necessary operations before
     * being destroyed.
     */
    public abstract void destroyTrigger();

    /**
     * returns the individual iD for the specific object.
     * @return int iD
     */
    public long getId(){return iD;}

    /**
     * Returns wich type of gameobject this specific object is.
     * @return The type of gameobject
     */
    public TypeOfGameObject getTypeOfGameObject(){return TypeOfGameObject.OTHER;}

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
