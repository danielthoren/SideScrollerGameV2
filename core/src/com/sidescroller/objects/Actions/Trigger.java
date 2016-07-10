package com.sidescroller.objects.Actions;

import com.sidescroller.game.GameObject;
import com.sidescroller.game.InteractGameObject;
import com.sidescroller.game.TypeOfGameObject;

import java.util.ArrayList;

public abstract class Trigger implements GameObject
{
    protected long iD;
    protected int targetActionID;

    public abstract boolean hasTriggered();

    public int getTargetActionID(){
	return targetActionID;
    }

    public long getID(){
	return iD;
    }
}
