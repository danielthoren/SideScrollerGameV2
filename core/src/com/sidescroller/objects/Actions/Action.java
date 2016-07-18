package com.sidescroller.objects.Actions;

/**
 * Abstract class that needs to be implemented by any object that wants to be added to the map as a action.
 */
public abstract class Action
{
    protected int actionID;

    /**
     *Function that is called when the designated trigger/triggers (with the same id as this action) is triggered.
     */
    public abstract void act();

    /**
     * Returns the actions id.
     * @return The id of the action.
     */
    public int getActionID(){
	return actionID;
    }
}