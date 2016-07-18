package com.sidescroller.objects.Actions;

import com.badlogic.gdx.physics.box2d.BodyDef;
import com.badlogic.gdx.physics.box2d.Fixture;
import com.badlogic.gdx.utils.Array;
import com.sidescroller.game.SideScrollerGameV2;
import com.sidescroller.objects.Shape;
import java.util.ArrayList;

public class BodyAction extends Action
{
	/**
	 * An enum that describes wich type of 'BodyAction' is to be performed.
	 */
	public enum TypeOfBodyAction{
		SPAWN, REMOVE, MAKE_DYNAMIC
	}

    private Shape shape;
    private BodyDef.BodyType bodyType;
    private ArrayList<Boolean> tempFixtureSensorData;
	private TypeOfBodyAction typeOfBodyAction;

	/**
	 * Makes a BodyAction object that can either spawn a shape object, remove a shape object or make it dynamic.
	 * @param id The id of the action object.
	 * @param shape The shape that will be worked on.
	 * @param typeOfBodyAction The type of body action.
     */
    public BodyAction(final int id, final Shape shape, TypeOfBodyAction typeOfBodyAction) {
		this.shape = shape;
		this.typeOfBodyAction = typeOfBodyAction;
		this.actionID = id;
		tempFixtureSensorData = new ArrayList<Boolean>(1);
		bodyType = shape.getBody().getType();
		if (typeOfBodyAction ==TypeOfBodyAction.SPAWN) {
			shape.getBody().setType(BodyDef.BodyType.StaticBody);
			Array<Fixture> fixtures = shape.getBody().getFixtureList();
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
			Array<Fixture> fixtures = shape.getBody().getFixtureList();
			for (int x = 0; x < fixtures.size; x++) {
				if (!tempFixtureSensorData.get(x)) {
					fixtures.get(x).setSensor(false);
				}
			}
			if (bodyType == BodyDef.BodyType.StaticBody) {
				shape.getBody().setType(BodyDef.BodyType.DynamicBody);
			}
			else{
				shape.getBody().setType(bodyType);
			}
			SideScrollerGameV2.getCurrentMap().addDrawObject(shape);
		}
		else if (typeOfBodyAction == TypeOfBodyAction.MAKE_DYNAMIC){
			shape.getBody().setType(BodyDef.BodyType.DynamicBody);
		}
		else if(typeOfBodyAction == TypeOfBodyAction.REMOVE){
			SideScrollerGameV2.getCurrentMap().removeBody(shape.getBody());
			SideScrollerGameV2.getCurrentMap().removeDrawObject(shape);
		}
		SideScrollerGameV2.getCurrentMap().getActionManager().removeAction(this);
	}
}
