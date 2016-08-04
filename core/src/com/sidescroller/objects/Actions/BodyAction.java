package com.sidescroller.objects.Actions;

import com.badlogic.gdx.physics.box2d.BodyDef;
import com.badlogic.gdx.physics.box2d.Fixture;
import com.badlogic.gdx.utils.Array;
import com.sidescroller.game.SideScrollerGameV2;
import com.sidescroller.objects.GameShape;

import java.util.ArrayList;

public class BodyAction extends Action
{
	/**
	 * An enum that describes wich type of 'BodyAction' is to be performed.
	 */
	public enum TypeOfBodyAction{
		SPAWN, REMOVE, MAKE_DYNAMIC
	}

    private GameShape gameShape;
    private BodyDef.BodyType bodyType;
    private ArrayList<Boolean> tempFixtureSensorData;
	private TypeOfBodyAction typeOfBodyAction;

	/**
	 * Makes a BodyAction object that can either spawn a gameShape object, remove a gameShape object or make it dynamic.
	 * @param id The id of the action object.
	 * @param gameShape The gameShape that will be worked on.
	 * @param typeOfBodyAction The type of body action.
     */
    public BodyAction(final int id, final GameShape gameShape, TypeOfBodyAction typeOfBodyAction) {
		this.gameShape = gameShape;
		this.typeOfBodyAction = typeOfBodyAction;
		this.actionID = id;
		tempFixtureSensorData = new ArrayList<Boolean>(1);
		bodyType = gameShape.getBody().getType();
		if (typeOfBodyAction ==TypeOfBodyAction.SPAWN) {
			gameShape.getBody().setType(BodyDef.BodyType.StaticBody);
			Array<Fixture> fixtures = gameShape.getBody().getFixtureList();
			for (Fixture fixture : fixtures) {
				tempFixtureSensorData.add(fixture.isSensor());
				fixture.setSensor(true);
			}
		}
	}


	/**
	 * Act method of action. Makes an object spawn, dynamic or removes it.
	 */
    public void act() {
		if (typeOfBodyAction == TypeOfBodyAction.SPAWN) {
			Array<Fixture> fixtures = gameShape.getBody().getFixtureList();
			for (int x = 0; x < fixtures.size; x++) {
				if (!tempFixtureSensorData.get(x)) {
					fixtures.get(x).setSensor(false);
				}
			}
			if (bodyType == BodyDef.BodyType.StaticBody) {
				gameShape.getBody().setType(BodyDef.BodyType.DynamicBody);
			}
			else{
				gameShape.getBody().setType(bodyType);
			}
			SideScrollerGameV2.getCurrentMap().addDrawObject(gameShape);
		}
		else if (typeOfBodyAction == TypeOfBodyAction.MAKE_DYNAMIC){
			gameShape.getBody().setType(BodyDef.BodyType.DynamicBody);
		}
		else if(typeOfBodyAction == TypeOfBodyAction.REMOVE){
			SideScrollerGameV2.getCurrentMap().removeBody(gameShape.getBody());
			SideScrollerGameV2.getCurrentMap().removeDrawObject(gameShape);
		}
		SideScrollerGameV2.getCurrentMap().getActionManager().removeAction(this);
	}

	/**
	 * No actions needs to be performed when removing a BodyAction
	 */
	public void destroyAction(){}
}
