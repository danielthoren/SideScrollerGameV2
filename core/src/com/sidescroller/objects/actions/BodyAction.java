package com.sidescroller.objects.actions;

import com.badlogic.gdx.physics.box2d.BodyDef.BodyType;
import com.badlogic.gdx.physics.box2d.Fixture;
import com.badlogic.gdx.utils.Array;
import com.sidescroller.game.SideScrollGameV2;
import com.sidescroller.objects.GameShape;

import java.util.ArrayList;
import java.util.List;

public class BodyAction extends Action
{
	/**
	 * An enum that describes wich type of 'BodyAction' is to be performed.
	 */
	public enum TypeOfBodyAction{
		SPAWN, REMOVE, MAKE_DYNAMIC
	}

    private GameShape gameShape;
    private BodyType bodyType;
    private List<Boolean> tempFixtureSensorData;
	private TypeOfBodyAction typeOfBodyAction;
	private SideScrollGameV2 sideScrollGameV2;

	/**
	 * Makes a BodyAction object that can either spawn a gameShape object, remove a gameShape object or make it dynamic.
	 * @param id The id of the action object.
	 * @param gameShape The gameShape that will be worked on.
	 * @param typeOfBodyAction The type of body action.
     */
    public BodyAction(final long id, SideScrollGameV2 sideScrollGameV2, final int actionId, final GameShape gameShape, TypeOfBodyAction typeOfBodyAction) {
		super(id, actionId);
		this.sideScrollGameV2 = sideScrollGameV2;
		this.gameShape = gameShape;
		this.typeOfBodyAction = typeOfBodyAction;
		tempFixtureSensorData = new ArrayList<Boolean>(1);
		bodyType = gameShape.getBody().getType();
		if (typeOfBodyAction ==TypeOfBodyAction.SPAWN) {
			gameShape.getBody().setType(BodyType.StaticBody);
			Array<Fixture> fixtures = gameShape.getBody().getFixtureList();
			for (Fixture fixture : fixtures) {
				tempFixtureSensorData.add(fixture.isSensor());
				fixture.setSensor(true);
			}
		}
		gameShape.getBody().setUserData(this);
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
			if (bodyType == BodyType.StaticBody) {
				gameShape.getBody().setType(BodyType.DynamicBody);
			}
			else{
				gameShape.getBody().setType(bodyType);
			}
			sideScrollGameV2.getCurrentMap().addDrawObject(gameShape);
		}
		else if (typeOfBodyAction == TypeOfBodyAction.MAKE_DYNAMIC){
			gameShape.getBody().setType(BodyType.DynamicBody);
		}
		else if(typeOfBodyAction == TypeOfBodyAction.REMOVE){
			sideScrollGameV2.getCurrentMap().removeBody(gameShape.getBody());
			sideScrollGameV2.getCurrentMap().removeDrawObject(gameShape);
		}
		sideScrollGameV2.getCurrentMap().getActionManager().removeAction(this);
	}

	/**
	 * No actions needs to be performed when removing a BodyAction
	 */
	public void destroyAction(){
		gameShape.getBody().setUserData(gameShape);
	}
}
