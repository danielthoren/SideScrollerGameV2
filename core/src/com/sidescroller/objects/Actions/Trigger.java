package com.sidescroller.objects.Actions;

import com.sidescroller.game.GameObject;

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

    public int getTargetActionID(){
	return targetActionID;
    }

    public long getiD(){
	return iD;
    }
}
