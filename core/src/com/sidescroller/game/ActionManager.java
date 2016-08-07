package com.sidescroller.game;

import com.sidescroller.objects.actions.Action;
import com.sidescroller.objects.actions.Trigger;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

/**
 * The actionmanager manages all of the actions and triggers of the map. It updates the triggers and calls the act method
 * on the actions if the trigger with that id number is triggered.
 */
public class ActionManager
{
	private Map<Integer, ArrayList<Action>> actions;
	private List<Trigger> triggers;
	private List<Action> actionsStagedForRemoval;

	/**
	 * Creates a actionmanager.
	 */
	public ActionManager() {
		actions = new HashMap<Integer, ArrayList<Action>>(5);
		triggers = new ArrayList<Trigger>(5);
		actionsStagedForRemoval = new ArrayList<Action>(1);
	}

	/**
	 * Adds a 'Action' to the actions of the actionmanager.
	 * @param action The action to be added.
     */
	public void addAction(Action action){
		if (!actions.containsKey(action.getActionID())){
			actions.put(action.getActionID(), new ArrayList<Action>(1));
		}
		actions.get(action.getActionID()).add(action);
	}

	/**
	 * Adds a trigger to the actionmanager.
	 * @param trigger The trigger to be added.
     */
	public void addTrigger(Trigger trigger){
		triggers.add(trigger);
	}

	/**
	 * Removes a 'Action' from the actionmanager.
	 * @param action The action to be removed.
     */
	public void removeAction(Action action){
		actionsStagedForRemoval.add(action);
	}

	/**
	 * Removes a trigger from the actionmanager.
	 * @param trigger The trigger to be removed.
     */
	public void removeTrigger(GameObject trigger){
		for (Iterator<Trigger> iterator = triggers.iterator(); iterator.hasNext();){
			Trigger trig = iterator.next();
			if (trig.getId() == trigger.getId()){
				trig.destroyTrigger();
				iterator.remove();
			}
		}
	}

	/**
	 * Checks if any trigger is triggered, if so then calls act on the specified actions. Also removes the actions that are
	 * staged for removal.
	 */
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
		for (Action actionRemove : actionsStagedForRemoval) {
			for (Iterator<Action> iterator = actions.get(actionRemove.getActionID()).iterator(); iterator.hasNext();){
				Action action = iterator.next();
				if (action.equals(actionRemove)){

					//If this is the last action with this action id then remove all triggers with that id.
					if (actions.get(actionRemove.getActionID()).size() == 1){
						for (Iterator<Trigger> triggerIterator = triggers.iterator(); triggerIterator.hasNext();){
							Trigger trigger = triggerIterator.next();
							if (trigger.getTargetActionID() == actionRemove.getActionID()){
								trigger.destroyTrigger();
								triggers.remove(trigger);
							}
						}
					}

					//Then remove the action.
					action.destroyAction();
					iterator.remove();
				}
			}
		}
		actionsStagedForRemoval.clear();
	}
}
