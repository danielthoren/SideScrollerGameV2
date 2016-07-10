package com.sidescroller.objects.Actions;

import com.sidescroller.game.GameObject;
import com.sidescroller.game.InteractGameObject;
import com.sidescroller.game.TypeOfGameObject;

import java.util.ArrayList;

public abstract class Trigger implements GameObject
{
    private long iD;
    private ArrayList<Integer> targetActionIDs;

    public abstract boolean hasTriggered();

    public ArrayList<Integer> getTargetActionIDs(){
	return targetActionIDs;
    }

    public long getiD(){
	return iD;
    }
}
