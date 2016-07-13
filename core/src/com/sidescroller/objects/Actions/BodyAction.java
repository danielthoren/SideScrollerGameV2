package com.sidescroller.objects.Actions;

import com.badlogic.gdx.physics.box2d.BodyDef;
import com.badlogic.gdx.physics.box2d.Fixture;
import com.badlogic.gdx.utils.Array;
import com.sidescroller.game.SideScrollerGameV2;
import com.sidescroller.objects.Shape;

import java.util.ArrayList;

public class BodyAction extends Action
{
	public enum TypeOfBodyAction{
		SPAWN, REMOVE, MAKE_DYNAMIC
	}

    private Shape shape;
    private BodyDef.BodyType bodyType;
    private ArrayList<Boolean> tempFixtureSensorData;
	TypeOfBodyAction typeOfBodyAction;

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

    public void act() {
		if (typeOfBodyAction == TypeOfBodyAction.SPAWN) {
			Array<Fixture> fixtures = shape.getBody().getFixtureList();
			for (int x = 0; x < fixtures.size; x++) {
				if (!tempFixtureSensorData.get(x)) {
					fixtures.get(x).setSensor(false);
				}
			}
			shape.getBody().setType(bodyType);
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
