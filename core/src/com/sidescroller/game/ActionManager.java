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

	private ArrayList<Integer> actionsStagedForRemoval;
	private ArrayList<Trigger> triggersStagedForRemoval;

	public ActionManager() {
		actions = new HashMap<Integer, Action>(5);
		triggers = new ArrayList<Trigger>(5);
		actionsStagedForRemoval = new ArrayList<Integer>(1);
		triggersStagedForRemoval = new ArrayList<Trigger>(1);
	}

	public void addAction(Action action){
		actions.put(action.getActionID(), action);
	}

	public void addTrigger(Trigger trigger){
		triggers.add(trigger);
	}

	public void removeAction(Action action){
		actionsStagedForRemoval.add(action.getActionID());
	}

	public void removeTrigger(Trigger trigger){
		for (Iterator<Trigger> iterator = triggers.iterator(); iterator.hasNext();){
			Trigger trig = iterator.next();
			if (trig.getiD() == trigger.getiD()){
				iterator.remove();
			}
		}
	}

	public void update(){
		for (Trigger trigger : triggers){
			if (trigger.hasTriggered()){
				if (actions.containsKey(trigger.getTargetActionID())) {
					actions.get(trigger.getTargetActionID()).act();
				}
			}
		}

		for (Integer integer : actionsStagedForRemoval) {
			for (Iterator<Trigger> iterator = triggers.iterator(); iterator.hasNext(); ) {
				Trigger trigger = iterator.next();
				if (trigger.getTargetActionID() == integer) {
					iterator.remove();
				}
			}
			actions.remove(integer);
		}
	}
}
