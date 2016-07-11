package com.sidescroller.objects.Actions;

public abstract class Action
{
    protected int actionID;

    public abstract void act();

    public int getActionID(){
	return actionID;
    }
}
