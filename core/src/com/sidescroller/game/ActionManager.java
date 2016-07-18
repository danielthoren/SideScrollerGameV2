package com.sidescroller.game;

import com.sidescroller.objects.Actions.Action;
import com.sidescroller.objects.Actions.Trigger;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;

public class ActionManager
{
	private HashMap<Integer, ArrayList<Action>> actions;
	private List<Trigger> triggers;

	private List<Action> actionsStagedForRemoval;
	private List<Trigger> triggersStagedForRemoval;

	public ActionManager() {
		actions = new HashMap<Integer, ArrayList<Action>>(5);
		triggers = new ArrayList<Trigger>(5);
		actionsStagedForRemoval = new ArrayList<Action>(1);
		triggersStagedForRemoval = new ArrayList<Trigger>(1);
	}

	public void addAction(Action action){
		if (!actions.containsKey(action.getActionID())){
			actions.put(action.getActionID(), new ArrayList<Action>(1));
		}
		actions.get(action.getActionID()).add(action);
	}

	public void addTrigger(Trigger trigger){
		triggers.add(trigger);
	}

	public void removeAction(Action action){
		actionsStagedForRemoval.add(action);
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
		//Checks if any of the triggers is triggered. If so then calles act on all 'Actions' with the specified id
		for (Trigger trigger : triggers){
			if (trigger.hasTriggered()){
				if (actions.containsKey(trigger.getTargetActionID())) {
					for (Action action : actions.get(trigger.getTargetActionID())){
						action.act();
					}
				}
			}
		}

		//Removing all of the actions staged for removal
		for (Action action : actionsStagedForRemoval) {
			actions.get(action.getActionID()).remove(action);
		}
	}
}
