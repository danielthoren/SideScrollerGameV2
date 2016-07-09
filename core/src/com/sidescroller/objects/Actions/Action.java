package com.sidescroller.objects.Actions;

public abstract class Action
{
    private int actionID;

    public abstract void act();

    public int getActionID(){
	return actionID;
    }
}
