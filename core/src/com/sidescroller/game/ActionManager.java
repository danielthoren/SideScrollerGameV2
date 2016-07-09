package com.sidescroller.game;

import com.sidescroller.objects.Actions.Action;
import com.sidescroller.objects.Actions.Trigger;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;

public class ActionManager
{
    private HashMap<Integer, Action> actions;
    private ArrayList<Trigger> triggers;

    public ActionManager() {
	actions = new HashMap<Integer, Action>(5);
	triggers = new ArrayList<Trigger>(5);
    }

    public void addAction(Action action){
	actions.put(action.getActionID(), action);
    }

    public void addTrigger(Trigger trigger){
	triggers.add(trigger);
    }

    public void removeAction(Action action){
	for (Trigger trigger : triggers){
	    trigger.getTargetActionIDs().remove(action.getActionID());
	    if (trigger.getTargetActionIDs().size() == 0){
		removeTrigger(trigger);
	    }
	}
	actions.remove(action.getActionID());
    }

    public void removeTrigger(Trigger trigger){
	for (Iterator<Trigger> iterator = triggers.iterator(); iterator.hasNext();){
	    Trigger trig = iterator.next();
	    if (trig.getiD() == trigger.getiD()){
		iterator.remove();
	    }
	}
    }

    /**
     * The function that updates the object every frame
     */
    public void update(){
	for (Trigger trigger : triggers){
	    if (trigger.hasTriggered()){
		for (int iD : trigger.getTargetActionIDs()) {
		    actions.get(iD).act();
		}
	    }
	}
    }
}
